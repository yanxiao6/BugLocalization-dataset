package com.xy.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * generate AST based on bug reports and then output into files
 * 
 * @author xy
 *
 */
public class BR_BugItemGenerator {
	public final String TAG = BR_BugItemGenerator.class.getSimpleName();
//	public final String BUG_REPORT_FILE = "dataset/SWT.txt";
//	public final String BUG_REPORT_FILE = "dataset/JDT.txt";
	public final String BUG_REPORT_FILE = "dataset/AspectJ.txt";
//	public final String BUG_REPORT_FILE = "dataset/Eclipse_Platform_UI.txt";
//	public final String BUG_REPORT_FILE = "dataset/Tomcat.txt";



	public SourceCodeManager sourceCodeManager;

	public BR_BugItemGenerator() {
		sourceCodeManager = SourceCodeManager.getInstance();
	}

	/**
	 * 
	 * @param file 
	 *            bug report that needs parsing
	 * @return    bugItem list after parsing
	 */
	public List<BugItem> generateBugItemsFromFile(String file) {
		if (file == null || file.length() < 0) {
			return null;
		}

		List<BugItem> items = new ArrayList<BugItem>();

		BufferedReader br = null;
		try {
			LogUtils.log(TAG, "open bug report files£º" + file);
			br = new BufferedReader(new FileReader(new File(file)));
			LogUtils.log(TAG, "open BugReport files successifully!");

			LogUtils.log(TAG, "parse BugReport files...");
			String line = null;
			while ((line = br.readLine()) != null) {
				// skip the first line of tables (title bar)
				char firstChar = line.charAt(0);
				if (firstChar > '9' || firstChar < '0') {
					LogUtils.log(TAG, "Skip line£º" + line);
					continue;
				}

				items.add(new BugItem(line));
			}
			LogUtils.log(TAG, "parsing BugReport files finished!");
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

	// generate mapping labels between bug reports and source files
	// Note: SouceManager needs to be initialized before calling this function
	public void generateFileLabel(List<BugItem> items) {
		LogUtils.log(TAG, "begin generating BugReport labels");
		for (BugItem bugItem : items) {
			List<String> files = bugItem.files;
			if (files != null && files.size() > 0) {
				bugItem.bugFileLable = sourceCodeManager.getBugReportLabel(files);
			}
		}
		LogUtils.log(TAG, "generating labels finished!");
	}
}
