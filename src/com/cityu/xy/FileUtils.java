package com.cityu.xy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
	public static File copyTo(File sourceFile, File copyToFile){
		if(sourceFile == null || !sourceFile.exists()){
			return null;
		}
		
		if(null == copyToFile){
			return null;
		}
		
		File copyToParent = copyToFile.getParentFile();
		if(copyToParent == null){
			return null;
		}
		
		if(!copyToParent.exists()){
			copyToParent.mkdirs();
		}
		
		if(!copyToFile.exists()){
			try {
				copyToFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		 // 复制文件  
        int byteread = 0; // 读取的字节数  
        InputStream in = null;  
        OutputStream out = null;  
  
        try {  
            in = new FileInputStream(sourceFile);  
            out = new FileOutputStream(copyToFile);  
            byte[] buffer = new byte[1024];  
  
            while ((byteread = in.read(buffer)) != -1) {  
                out.write(buffer, 0, byteread);  
            }  
            return copyToFile;  
        } catch (FileNotFoundException e) {  
            return null;  
        } catch (IOException e) {  
            return null;  
        } finally {  
            try {  
                if (out != null)  
                    out.close();  
                if (in != null)  
                    in.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        } 
	}
	
	
}
