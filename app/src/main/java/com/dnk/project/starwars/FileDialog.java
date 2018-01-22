package com.dnk.project.starwars;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.dnk.project.starwars.ListenerList.FireHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Environment;
import android.util.Log;

/**
 * This code is from <br>
 * http://stackoverflow.com/questions/3592717/choose-file-dialog Provided by<br>
 * Kirill Mikhailov on 2012-12-21<br>
 * <br>
 * Modified by Huang Neng Geng on 2013-09-15<br>
 * <br>
 * The usage is:<br>
 * <br>
 * 
 * <pre>
 * 
 * final String TAG = &quot;File dialog&quot;;
 * String path = Environment.getExternalStorageDirectory().toString();
 * fileDialog = new FileDialog(this);
 * 
 * fileDialog.setFileEndsWith(new String[] { &quot;.txt&quot; });
 * fileDialog.setShowDirectoryOnly(false);
 * fileDialog.setSortedBy(FileDialog.SORTED_BY_DATE);
 * fileDialog.setListFileFirst(true);
 * 
 * fileDialog.initDirectory(path);
 * 
 * fileDialog.addFileListener(new FileDialog.FileSelectedListener() {
 * 	public void fileSelected(File file) {
 * 		Log.d(TAG, &quot;selected file &quot; + file.toString());
 * 	}
 * });
 * fileDialog.addDirectoryListener(new FileDialog.DirSelectedListener() {
 * 	public void directorySelected(File directory) {
 * 		Log.d(TAG, &quot;selected dir &quot; + directory.toString());
 * 	}
 * });
 * fileDialog.showDialog();
 * 
 * </pre>
 * 
 * @author Kirill Mikhailov
 * 
 */
public class FileDialog {
	public static final int SORTED_BY_NAME = 1;
	public static final int SORTED_BY_SIZE = 2;
	public static final int SORTED_BY_DATE = 3;

	private static final String PARENT_DIR = "..";
	private File currentPath;
	private String[] fileList;

	private String[] dirs;
	private String[] files;

	private int fileSortedBy = SORTED_BY_NAME;
	private String[] fileEndsWith; // = { ".bat", ".sys" };
	private boolean showDirectoryOnly;
	private boolean listFileFirst;

	private final String TAG = getClass().getName();

	public interface FileSelectedListener {
		void fileSelected(File file, String[] dirs, String[] files);
	}

	public interface DirSelectedListener {
		void directorySelected(File directory, String[] dirs, String[] files);
	}

	private ListenerList<FileSelectedListener> fileListenerList = new ListenerList<FileDialog.FileSelectedListener>();
	private ListenerList<DirSelectedListener> dirListenerList = new ListenerList<FileDialog.DirSelectedListener>();
	private final Activity activity;

	public FileDialog(Activity activity) {
		super();
		this.activity = activity;
	}

	public void initDirectory(File dir) {
		if (!dir.exists())
			dir = Environment.getExternalStorageDirectory();
		loadDirFileList(dir);
	}

	public void initDirectory(String path) {
		File dir = new File(path);
		if (!dir.exists())
			dir = Environment.getExternalStorageDirectory();
		loadDirFileList(dir);
	}

	/**
	 * @return file dialog
	 */
	public Dialog createFileDialog() {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setTitle(currentPath.getPath());
		if (showDirectoryOnly) {
			builder.setPositiveButton("Select directory",
					new OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Log.d(TAG, currentPath.getPath());
							fireDirectorySelectedEvent(currentPath);
						}
					});
		}

		builder.setItems(fileList, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String fileChosen = fileList[which];
				if (fileChosen.startsWith("<")) {
					// convert from <dir> into dir
					fileChosen = fileChosen.substring(1,
							fileChosen.length() - 1);
				}
				File chosenFile = getChosenFile(fileChosen);
				if (chosenFile.isDirectory()) {
					fireDirectorySelectedEvent(chosenFile);
					loadDirFileList(chosenFile);
					dialog.cancel();
					dialog.dismiss();
					showDialog();
				} else
					fireFileSelectedEvent(chosenFile);
			}
		});

		dialog = builder.show();
		return dialog;
	}

	/**
	 * Show file dialog
	 */
	public void showDialog() {
		createFileDialog().show();
	}

	public void addDirectoryListener(DirSelectedListener listener) {
		dirListenerList.add(listener);
	}

	public void removeDirectoryListener(DirSelectedListener listener) {
		dirListenerList.remove(listener);
	}

	public void addFileListener(FileSelectedListener listener) {
		fileListenerList.add(listener);
	}

	public void removeFileListener(FileSelectedListener listener) {
		fileListenerList.remove(listener);
	}

	/**
	 * List files that ends with filename suffix, which is a string array. It is
	 * case sensitive. <br>
	 * Examples are { ".jpg", ".JPG", ".png", ".PNG" }<br>
	 * 
	 * Default is null, means list all files.
	 * 
	 * @param fileEndsWith
	 */
	public void setFileEndsWith(String[] fileEndsWith) {
		this.fileEndsWith = fileEndsWith;
	}

	/**
	 * Sort file name by file name, file size, or last modified date. Default is
	 * by name.
	 * 
	 * Note: Directory name is always sorted by name.
	 * 
	 * @param sortedBy
	 *            SORTED_BY_NAME, SORTED_BY_SIZE, or SORTED_BY_DATE
	 */
	public void setFileSortedBy(int fileSortedBy) {
		this.fileSortedBy = fileSortedBy;
	}

	/**
	 * 
	 * Show directory only if set to true. Default is false. Default is false.
	 * 
	 * @param showDirectoryOnly
	 */
	public void setShowDirectoryOnly(boolean showDirectoryOnly) {
		this.showDirectoryOnly = showDirectoryOnly;
	}

	/**
	 * List file first, then directory second if true. Default is false.
	 * 
	 * @param listFileFirst
	 */

	public void setListFileFirst(boolean listFileFirst) {
		this.listFileFirst = listFileFirst;
	}

	/**
	 * List files ended with fileEndsWith and sort it by what set by
	 * setFileSortedBy(int);
	 * 
	 * @return
	 */
	public String[] getFileList() {
		List<String> r = new ArrayList<String>();
		FilenameFilter filter = new FilenameFilter() {
			@SuppressLint("DefaultLocale")
			@Override
			public boolean accept(File dir, String filename) {
				File sel = new File(dir, filename);
				if (!sel.canRead())
					return false;
				if (sel.isDirectory())
					return false;
				else {
					if (fileEndsWith == null) {
						return true;
					}
					for (String ends : fileEndsWith) {
						if (filename.endsWith(ends)) {
							return true;
						}
					}
					return false;
				}
			}
		};
		File[] fileList = currentPath.listFiles(filter);

		// sorted it by name, size, or date
		Arrays.sort(fileList, new Comparator<File>() {
			@SuppressLint("DefaultLocale")
			@Override
			public int compare(File o1, File o2) {
				long l;
				switch (fileSortedBy) {
				case SORTED_BY_NAME:
					return o1.getName().toLowerCase()
							.compareTo(o2.getName().toLowerCase());
				case SORTED_BY_SIZE:
					l = o1.length() - o2.length();
					return l == 0 ? 0 : l > 0 ? 1 : -1;
				case SORTED_BY_DATE:
					l = o1.lastModified() - o2.lastModified();
					return l == 0 ? 0 : l > 0 ? 1 : -1;
				default:
					throw new RuntimeException("Sort type was missing.");
				}
			}
		});

		for (File file : fileList) {
			r.add(file.getName());
		}
		files = (String[]) r.toArray(new String[] {});
		return files;
	}

	/**
	 * List all directory and sort it by directory name
	 * 
	 * @return
	 */
	public String[] getDirList() {
		List<String> r = new ArrayList<String>();
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				File sel = new File(dir, filename);
				if (!sel.canRead()) {
					return false;
				}
				return sel.isDirectory();
			}
		};
		String[] dirList = currentPath.list(filter);

		// sort it by name only for dirctory name
		Arrays.sort(dirList, String.CASE_INSENSITIVE_ORDER);

		for (String file : dirList) {
			r.add(file);
		}
		dirs = (String[]) r.toArray(new String[] {});
		return dirs;
	}

	private void fireFileSelectedEvent(final File file) {
		fileListenerList
				.fireEvent(new FireHandler<FileDialog.FileSelectedListener>() {
					public void fireEvent(FileSelectedListener listener) {
						listener.fileSelected(file, dirs, files);
					}
				});
	}

	private void fireDirectorySelectedEvent(final File directory) {
		dirListenerList
				.fireEvent(new FireHandler<FileDialog.DirSelectedListener>() {
					public void fireEvent(DirSelectedListener listener) {
						listener.directorySelected(directory, dirs, files);
					}
				});
	}

	private void loadDirFileList(File path) {
		this.currentPath = path;
		List<String> r = new ArrayList<String>();
		if (path.exists()) {
			if (path.getParentFile() != null) {
				r.add(PARENT_DIR);
			}
			if (listFileFirst) {
				if (!showDirectoryOnly) {
					for (String file : getFileList()) {
						r.add(file);
					}
				}
				for (String file : getDirList()) {
					r.add(file);
				}
			} else {
				for (String file : getDirList()) {
					r.add(file);
				}
				if (!showDirectoryOnly) {
					for (String file : getFileList()) {
						r.add(file);
					}
				}
			}
		}
		fileList = (String[]) r.toArray(new String[] {});
	}

	private File getChosenFile(String fileChosen) {
		if (fileChosen.equals(PARENT_DIR))
			return currentPath.getParentFile();
		else
			return new File(currentPath, fileChosen);
	}
}

class ListenerList<L> {
	private List<L> listenerList = new ArrayList<L>();

	public interface FireHandler<L> {
		void fireEvent(L listener);
	}

	public void add(L listener) {
		listenerList.add(listener);
	}

	public void fireEvent(FireHandler<L> fireHandler) {
		List<L> copy = new ArrayList<L>(listenerList);
		for (L l : copy) {
			fireHandler.fireEvent(l);
		}
	}

	public void remove(L listener) {
		listenerList.remove(listener);
	}

	public List<L> getListenerList() {
		return listenerList;
	}
}
