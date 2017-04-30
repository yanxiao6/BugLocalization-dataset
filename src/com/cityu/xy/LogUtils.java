package com.cityu.xy;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class LogUtils {
	private static boolean mOpen = false;
	
	private static BufferedWriter cBW;


	public static boolean isOpen() {
		return mOpen;
	}

	public static void setOpen(boolean open) {
		mOpen = open;
		System.out.println("log print is " + (open?"open":"close"));
	}
	
	/**
	 * print log
	 * @param content
	 */
	public static void log(String TAG, String content){
		if(mOpen){
			System.out.printf("%s: %s\n", TAG, content);
		}
	}
	
	/**
	 * write log to file
	 * @param content
	 */
	public static void logFile(String TAG, String content){
		if(!mOpen){
			return;
		}
		
		try{
			if(cBW == null){
				File logFile = new File(TAG + "_log.txt"); 
				if(logFile.exists()){
					logFile.delete();
				}
				cBW = new BufferedWriter(new FileWriter(logFile));
				
			}
			
			cBW.write(content);
			cBW.write("\r\n");
			cBW.flush();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
