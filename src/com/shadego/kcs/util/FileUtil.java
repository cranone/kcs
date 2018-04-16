package com.shadego.kcs.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
* @author Maclaine E-mail:deathencyclopedia@gmail.com
* 
*/
public class FileUtil {

	public static void write(String path,String data) throws IOException{
		File file=new File(path);
		if(file.getParentFile()!=null&&!file.getParentFile().exists()){
			file.getParentFile().mkdirs();
		}
		if(!file.exists()){
			file.createNewFile();
		}
		try (FileOutputStream fs = new FileOutputStream(file); FileChannel fc = fs.getChannel();) {
			ByteBuffer bb = ByteBuffer.wrap(data.getBytes());
			bb.put(data.getBytes());
			bb.flip();
			fc.write(bb);
		}
	}
}
