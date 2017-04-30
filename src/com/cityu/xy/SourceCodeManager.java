package com.cityu.xy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;

public class SourceCodeManager {
//	private final String TAG = SourceCodeManager.class.getSimpleName();
//	public static final boolean GIT_CHECKOUT = true;
	public static final boolean GIT_CHECKOUT = false;



		
	/**
	 * swt
	 */
//	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/eclipse.platform.swt/";
//	public static final String LOCAL_FILE_PREFIX = "sourceFile_swt/";
	
	/**
	 * jdt
	 */
//	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/eclipse.jdt.ui/";
//	public static final String LOCAL_FILE_PREFIX = "sourceFile_jdt/";
	
	/**
	 * aspectj
	 */
//	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/org.aspectj/";
//	public static final String LOCAL_FILE_PREFIX = "sourceFile_aspectj/";
	
	/**
	 * eclipseUI
	 */
//	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/eclipse.platform.ui/";
//	public static final String LOCAL_FILE_PREFIX = "sourceFile_eclipseUI/";
	
	/**
	 * tomcat
	 */
	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/tomcat/";
	public static final String LOCAL_FILE_PREFIX = "sourceFile_tomcat/";
	public static final String LOCAL_COPY_SOURCE_CODE = LOCAL_FILE_PREFIX + "sourceFile/";

	private static SourceCodeManager mInstance = null;

	private List<String> mSourceFileList;
	private Map<String, SourceFile> mSourceFileMapping = new HashMap<String, SourceFile>();

	private SourceCodeManager() {
	};

	// Singleton
	public static SourceCodeManager getInstance() {
		if (mInstance == null) {
			synchronized (SourceCodeManager.class) {
				if (mInstance == null) {
					mInstance = new SourceCodeManager();
				}
			}
		}

		return mInstance;
	}

	public void setSourceFiles(List<String> sourceFiles) {
		mSourceFileList = sourceFiles;
	}
	
	public void initSourFileContext(List<BugItem> itemList){
		for(BugItem item : itemList){
			for(String path : item.files){
				SourceFile  sourceFile = mSourceFileMapping.get(path);
				if(sourceFile == null){
					SourceFile newSourceFile = new SourceFile();
					newSourceFile.path = path;
					newSourceFile.commintTimeMapping.put(Long.valueOf(item.commintTime), item.commit);
					mSourceFileMapping.put(path, newSourceFile);
				}else{
					sourceFile.commintTimeMapping.put(Long.valueOf(item.commintTime), item.commit);
//					LogUtils.log(TAG, path);
				}
				
			}
		}
	}
	
	/**
	 * git all file from github
	 * @param items
	 * @throws Exception
	 */
	public void gitAllSourceCodeFile(List<BugItem> items) throws Exception {
		Git git = null;
		git = Git.open(new File(SOURCE_CODE_DIR));

		for (BugItem bugItem : items) {
			List<String> sourceFiles = bugItem.files;

			for (String fileName : sourceFiles) {
				if (!fileName.endsWith(".java")) {
					continue;
				}

				// git version file
				try {
					CheckoutCommand checkoutCommand = git.checkout();
					checkoutCommand.addPath(fileName);
					checkoutCommand.setStartPoint(bugItem.commit + "~1");
					checkoutCommand.call();
					// }
				} catch (Exception e) {
					 e.printStackTrace();
				} finally {
					git.close();
				}

				File file = new File(SourceCodeManager.SOURCE_CODE_DIR, fileName);
				if (!file.exists()) {
					// some file maybe added, so git it
					try {
						CheckoutCommand checkoutCommand = git.checkout();
						checkoutCommand.addPath(fileName);
						checkoutCommand.setStartPoint(bugItem.commit);
						checkoutCommand.call();
						git.close();

						File addFile = new File(SourceCodeManager.SOURCE_CODE_DIR, fileName);
						if (!addFile.exists()) {
							System.out.println(bugItem.bugId + " " + bugItem.commit + " " + addFile.getAbsolutePath());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}finally {
						git.close();
					}
				}

				// copy file to target directory
				if (file.exists()) {
					String origineFileName = file.getName();
					String origineAbsoluteName = file.getAbsolutePath();
					String copyToFileName = origineAbsoluteName.substring(15, origineAbsoluteName.length() - origineFileName.length());
					File copyToFile = new File(LOCAL_COPY_SOURCE_CODE + copyToFileName + bugItem.commit + " " + origineFileName);
					FileUtils.copyTo(file, copyToFile);
				}
			}
		}
	}

	/**
	 * obtain local file according to bugItem files 
	 * @param bugItem
	 * @return
	 */
	public List<String> getLocalSourceFile(BugItem bugItem) {
		if (bugItem == null) {
			return null;
		}

		List<String> filePaths = bugItem.files;
		List<String> localFiles = new ArrayList<String>(filePaths.size());

		for (String path : filePaths) {
			SourceFile sourceFile = mSourceFileMapping.get(path);
			String commit = sourceFile.commintTimeMapping.lastEntry().getValue();
			File file = new File(SOURCE_CODE_DIR, path);
			String origineFileName = file.getName();
			String origineAbsoluteName = file.getAbsolutePath();
			String copyToFileName = origineAbsoluteName.substring(15, origineAbsoluteName.length() - origineFileName.length());
			File localFile = new File(LOCAL_COPY_SOURCE_CODE + copyToFileName + commit + " " + origineFileName);
			localFiles.add(localFile.getAbsolutePath());
		}
		bugItem.files = localFiles;

		return localFiles;
	}

	/**
	 * obtain label
	 * 
	 * @param files bugItem's files
	 * @return label
	 */
	public String getBugReportLabel(List<String> files) {
		int[] labelArray = new int[mSourceFileList.size()];

		boolean isValid = false;
		for (String fileName : files) {
			File file = new File(fileName);
			if (file.exists()) {
				String path = file.getAbsolutePath();
				int index = mSourceFileList.indexOf(path);
				if (index >= 0 && index < mSourceFileList.size()) {
					labelArray[index] = 1;
					isValid = true;
				}
			}
		}

		if (isValid) {
			StringBuffer buffer = new StringBuffer();
			for (int index = 0; index < labelArray.length; index++) {
				buffer.append(labelArray[index]);
			}

			return buffer.toString();
		} else {
			return null;
		}
	}

	/**
	 * save source file to text as list
	 */
	public void saveSourceFileList() {
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(SourceCodeManager.LOCAL_FILE_PREFIX + "source_list.txt"));
			for (String sourceFile : mSourceFileList) {
				fw.write(sourceFile);
				fw.write("\r\n");
			}
			fw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null) {
				try {
					fw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
