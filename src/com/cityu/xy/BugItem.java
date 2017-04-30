package com.cityu.xy;

import java.util.ArrayList;
import java.util.List;

public class BugItem {
	// private final String TAG = BugItem.class.getSimpleName();

	public int id;
	public int bugId;
	public String summary;
	public String description;
	public String reportTime;
	public String reportTimes;
	public String status;
	public String commit;
	public String commintTime;
	public List<String> files;

	public String bugFileLable;

	private final int ITEM_SIZE = 10; // content count£¬ eg£ºID£¬bugId¡£¡£¡£

	public BugItem(String itemStr) {
		if (itemStr == null || itemStr.length() <= 0) {
			return;
		}

		String[] contents = itemStr.split("\t");
		if (contents.length < ITEM_SIZE) {
			// when count is not meet, throw exception
			throw new IllegalArgumentException();
		}

		int index = 0;
		this.id = Integer.valueOf(contents[index++]);

		this.bugId = Integer.valueOf(contents[index++]);
		this.summary = contents[index++];
		this.description = contents[index++];
		this.reportTime = contents[index++];
		this.reportTimes = contents[index++];
		this.status = contents[index++];
		this.commit = contents[index++];
		this.commintTime = contents[index++];

		files = new ArrayList<String>();
		String[] files = contents[index++].split(".java ");
		for (String file : files) {
			if (!file.endsWith(".java")) {
				this.files.add(file + ".java");
			}else{
				this.files.add(file);
			}
		}
	}

	public String getBugFileLable() {
		return bugFileLable;
	}

	public void setBugFileLable(String bugFileMap) {
		this.bugFileLable = bugFileMap;
	}
}
