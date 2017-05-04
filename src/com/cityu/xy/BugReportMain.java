package com.cityu.xy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BugReportMain {
	private final static String TAG = BugReportAstMain.class.getSimpleName();

	public static void main(String[] args) throws Exception {
		// open log
		LogUtils.setOpen(true);

		BufferedWriter bw = null;

		// generate BugItem
		LogUtils.log(TAG, "generate bugItem ...");
		BR_BugItemGenerator generator = new BR_BugItemGenerator();
		List<BugItem> items = generator.generateBugItemsFromFile(generator.BUG_REPORT_FILE);

		SourceCodeManager sourceCodeManager = SourceCodeManager.getInstance();
		sourceCodeManager.initSourFileContext(items);

		// if the source code of different versions need to be pulled from github, open this switch
		 if (SourceCodeManager.GIT_CHECKOUT) {
			 sourceCodeManager.gitAllSourceCodeFile(items);
			 return;
		 }

		ArrayList<String> bugFiles = new ArrayList<String>();
		for (BugItem item : items) {
			List<String> files = sourceCodeManager.getLocalSourceFile(item);

			if (files == null) {
				continue;
			}

			for (String filePath : files) {
				File file = new File(filePath);
				if (!file.exists()) {
					LogUtils.log(TAG, item.id + " " + item.commit + " " + file.getAbsolutePath());
				} else {
					if(!bugFiles.contains(file.getAbsolutePath())){
						bugFiles.add(file.getAbsolutePath());
					}
				}
			}
		}

		sourceCodeManager.setSourceFiles(bugFiles);
		// sourceCodeManager.initAllSourceFile();

		// according sourceManager to generate bugLabel
		generator.generateFileLabel(items);

		// write bugItem into text files
		try {
			bw = new BufferedWriter(new FileWriter(new File(SourceCodeManager.LOCAL_FILE_PREFIX + "bugItem.txt")));

			LogUtils.log(TAG, "write bugItem into txt files");
			for (BugItem item : items) {
				if (item != null && item.bugFileLable != null && item.bugFileLable.length() > 0) {
					if (item.description != null && item.description.length() > 0) {
						bw.write(item.description);
					} 
					
					if(item.summary != null && item.summary.length() > 0){
						bw.write(" ");
						bw.write(item.summary);
					}
					bw.write("$|$");
					bw.write(item.bugFileLable);
					bw.write("$#$");
					bw.write(item.reportTimes);
					bw.write("\r\n");
				}else{
					LogUtils.log("BugReportMain", item.commit + ", " + item.id);
				}
			}
			LogUtils.log(TAG, "bugItem written successifully");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bw != null) {
				try {
					bw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		sourceCodeManager.saveSourceFileList();

		LogUtils.log(TAG, "Task finished !!! enjoy^.^");
	}
}
