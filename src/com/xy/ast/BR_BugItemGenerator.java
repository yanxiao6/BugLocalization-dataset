package com.xy.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 根据BugReport生成AST树，并输出结果到文本。
 * 
 * @author hz
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
	 *            需要解析的bug report
	 * @return 解析后的bugItem 列表
	 */
	public List<BugItem> generateBugItemsFromFile(String file) {
		if (file == null || file.length() < 0) {
			return null;
		}

		List<BugItem> items = new ArrayList<BugItem>();

		BufferedReader br = null;
		try {
			LogUtils.log(TAG, "尝试打开BugReport文件：" + file);
			br = new BufferedReader(new FileReader(new File(file)));
			LogUtils.log(TAG, "打开BugReport文件成功!");

			LogUtils.log(TAG, "开始解析BugReport文件...");
			String line = null;
			while ((line = br.readLine()) != null) {
				// 跳过表头（标题栏）
				char firstChar = line.charAt(0);
				if (firstChar > '9' || firstChar < '0') {
					LogUtils.log(TAG, "Skip line：" + line);
					continue;
				}

				items.add(new BugItem(line));
			}
			LogUtils.log(TAG, "解析BugReport文件完毕!");
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

	// 匹配对应文件映射表。注意调用该方法之前，SouceManager需要先初始化列表
	public void generateFileLabel(List<BugItem> items) {
		LogUtils.log(TAG, "开始生成BugReport标签");
		for (BugItem bugItem : items) {
			List<String> files = bugItem.files;
			if (files != null && files.size() > 0) {
				bugItem.bugFileLable = sourceCodeManager.getBugReportLabel(files);
			}
		}
		LogUtils.log(TAG, "生成标签完成");
	}
}
