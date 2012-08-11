package com.alk.battleScheduler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;


/**
 * 
 * @author alkarin
 *
 */
public class Util {

    //Changes the & to color code sign
    public static String colorChat(String msg) {
        String langChar = Character.toString((char) 167);
        msg = msg.replaceAll("&", langChar);
        return msg;
    }

    public static String deColorChat(String msg) {
    	msg = msg.replaceAll("\\&[0-9a-zA-Z]", "");
    	return msg;
    }    

	public static File load(InputStream inputStream, String config_file) {
		File file = new File(config_file);
		if (!file.exists()){ /// Create a new config file from our default
			try{
				OutputStream out=new FileOutputStream(config_file);
				byte buf[]=new byte[1024];
				int len;
				while((len=inputStream.read(buf))>0){
					out.write(buf,0,len);}
				out.close();
				inputStream.close();
			} catch (Exception e){
			}
		}
		return file;
	}
}
