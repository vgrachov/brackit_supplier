package org.brackit.supplier.io.helper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

public class IOHelper {

	public String getContent(File file) throws IOException{
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[1000];
			int length = -1;
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			while ((length=fis.read(buffer))!=-1){
				content.write(buffer, 0, length);
			}
			return new String(content.toByteArray());
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			throw e;
		} finally{
			IOUtils.closeQuietly(fis);
		}
	}
	
}
