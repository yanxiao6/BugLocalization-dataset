package com.cityu.xy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author xiaoyan
 *
 */
public class BR_BugItemGenerator {
	public final String TAG = BR_BugItemGenerator.class.getSimpleName();
//	public final String BUG_REPORT_FILE = "dataset/SWT.txt";
//	public final String BUG_REPORT_FILE = "dataset/JDT.txt";
//	public final String BUG_REPORT_FILE = "dataset/AspectJ.txt";
//	public final String BUG_REPORT_FILE = "dataset/Eclipse_Platform_UI.txt";
	public final String BUG_REPORT_FILE = "dataset/Tomcat.txt";



	public SourceCodeManager sourceCodeManager;

	public BR_BugItemGenerator() {
		sourceCodeManager = SourceCodeManager.getInstance();
	}

	/**
	 * 
	 * @param file bugReport file
	 * @return  bugItem list
	 */
	public List<BugItem> generateBugItemsFromFile(String file) {
		if (file == null || file.length() < 0) {
			return null;
		}

		List<BugItem> items = new ArrayList<BugItem>();

		BufferedReader br = null;
		try {
			LogUtils.log(TAG, "try to open bugReport file: " + file);
			br = new BufferedReader(new FileReader(new File(file)));
			LogUtils.log(TAG, "open bugReport file successfully!");

			LogUtils.log(TAG, "Parsing bugReport file...");
			String line = null;
			while ((line = br.readLine()) != null) {
				// skip headline
				char firstChar = line.charAt(0);
				if (firstChar > '9' || firstChar < '0') {
					LogUtils.log(TAG, "Skip line: " + line);
					continue;
				}

				items.add(new BugItem(line));
			}
			LogUtils.log(TAG, "parsing bugReport file finished!");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return items;
	}

	/**
	 * generate bugItem label, this function must be invoke after {@link #sourceCodeManager.initSourFileContext}
	 * @param items
	 */
	public void generateFileLabel(List<BugItem> items) {
		LogUtils.log(TAG, "start generate BugItem label...");
		for (BugItem bugItem : items) {
			List<String> files = bugItem.files;
			if (files != null && files.size() > 0) {
				bugItem.bugFileLable = sourceCodeManager.getBugReportLabel(files);
			}
		}
		LogUtils.log(TAG, "generating label finished!");
	}
}
