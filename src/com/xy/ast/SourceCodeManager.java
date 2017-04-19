package com.xy.ast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.Git;

public class SourceCodeManager {
	private final String TAG = SourceCodeManager.class.getSimpleName();
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
	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/org.aspectj/";
	public static final String LOCAL_FILE_PREFIX = "sourceFile_aspectj/";
	
	/**
	 * eclipseUI
	 */
//	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/eclipse.platform.ui/";
//	public static final String LOCAL_FILE_PREFIX = "sourceFile_eclipseUI/";
	
	/**
	 * tomcat
	 */
//	public static final String SOURCE_CODE_DIR = "/Users/xiaoyan/tomcat/";
//	public static final String LOCAL_FILE_PREFIX = "sourceFile_tomcat/";
	
	
	
	

	public static final String LOCAL_COPY_SOURCE_CODE = LOCAL_FILE_PREFIX + "sourceFile/";

	private static SourceCodeManager mInstance = null;

	private Map<String, String> mSourceFileAstMap;
	private List<String> mSourceFileList;
	private Map<String, SourceFile> mSourceFileMapping = new HashMap<String, SourceFile>();

	private SourceCodeManager() {
	};

	// 单例模式
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
	 * 生成源代码对应的AST map。
	 * 
	 * @return
	 */
	public Map<String, String> generateSourceCodeAstMap() {
		if (mSourceFileAstMap != null && mSourceFileAstMap.size() > 0) {
			return mSourceFileAstMap;
		}

		Map<String, String> sourceCodeAstMap = new LinkedHashMap<String, String>();

		for (int index = 0; index < mSourceFileList.size(); index++) {
			String fileName = mSourceFileList.get(index);
			String astKeyWord = generateAstWords(fileName);
			
			int indes = fileName.lastIndexOf(File.separator);
			String simpleFileName = fileName.substring(indes+1);
			String commit = simpleFileName.split(" ")[0];
			
			String repName = SOURCE_CODE_DIR.substring(15);
			String tempFileName = fileName.substring(46);
			tempFileName = tempFileName.replace(LOCAL_COPY_SOURCE_CODE, "");
			tempFileName = tempFileName.replaceFirst(repName, "");
			tempFileName = tempFileName.replace(commit + " ", "");
			SourceFile sourceFile = mSourceFileMapping.get(tempFileName);
			Set<Long> times = sourceFile.commintTimeMapping.keySet();
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for(Long time : times){
				sb.append(String.valueOf(time));
				count++;
				if(count < times.size()){
					sb.append(" ");
				}
			}
			astKeyWord += "$%$" + sb.toString();
			sourceCodeAstMap.put(fileName, astKeyWord);

			// 打印进度日志
			if (index % 100 == 0) {
				LogUtils.log(TAG, String.format("进度：%.2f", (((float) index) / mSourceFileList.size()) * 100));
			}
		}

		return sourceCodeAstMap;
	}

	private String generateAstWords(String filePath) {
		ASTNode astNode = JdtAstUtil.getCompilationUnit(filePath);
		StringBuffer buffer = new StringBuffer();
		try {
			TypeDeclaration type = (TypeDeclaration) ((CompilationUnit) astNode).types().get(0);
			// 当前类名
			buffer.append(type.getName());
			buffer.append(' ');

			MethodDeclaration[] methodDeclarations = type.getMethods();
			for (MethodDeclaration method : methodDeclarations) {
				buffer.append(method.getName());
				buffer.append(' ');
			}

			FieldDeclaration[] fieldDeclarations = type.getFields();
			for (FieldDeclaration field : fieldDeclarations) {
				Object object = field.fragments().get(0);
				if (object instanceof VariableDeclaration) {
					buffer.append(((VariableDeclaration) object).getName());
					buffer.append(' ');
				}
			}
		} catch (Exception e) {
			LogUtils.log(SourceCodeManager.class.getSimpleName(), filePath);
			String fileName = filePath.split(" ")[1];
			fileName = fileName.replace(".java", "");
			buffer.append(fileName);
			// e.printStackTrace();
		}

		return buffer.toString();
	}
	
	public void gitAllSourceCodeFile(List<BugItem> items) throws Exception {
		Git git = null;
		git = Git.open(new File(SOURCE_CODE_DIR));

		for (BugItem bugItem : items) {
			List<String> sourceFiles = bugItem.files;

			for (String fileName : sourceFiles) {
				if (!fileName.endsWith(".java")) {
					continue;
				}

				// 从git中拉取对应的版本文件
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
					// 有可能是新增的文件，需要将新增的文件加入进来
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

				// 拷贝文件到指定的地方
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
	 * 获取 对应BugReport的label
	 * 
	 * @param files
	 * @return
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
	 * 保存源代碼文件到本地。
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
