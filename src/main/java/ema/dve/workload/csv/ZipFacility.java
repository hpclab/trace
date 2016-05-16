package ema.dve.workload.csv;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipFacility 
{
	
	public void createZip(String filename, Map<String,String> data) throws Exception
	{
		ZipOutputStream zos = null;
		
		try
		{
			zos = new ZipOutputStream(new FileOutputStream(filename));
			
			for (String key: data.keySet())
			{
				String content = data.get(key);
				zos.putNextEntry(new ZipEntry(key));
				zos.write(content.getBytes());
				zos.closeEntry();	
			}
		}
		finally
		{
			if (zos != null)
				zos.close();
		}
		
	}
	
	public Map<String,String> readZip(String filename) throws IOException
	{
		HashMap<String, String> map = new HashMap<String, String>();
		
		ZipInputStream zis = null;
		ZipEntry ze = null;
		
		try
		{
			zis = new ZipInputStream(new FileInputStream(filename));
			
			while ((ze = zis.getNextEntry()) != null)
			{				
				StringBuilder sb = new StringBuilder();
				int c = -1;
				while ((c = zis.read()) != -1)
				{
					sb.append((char)c);
				}
				
				map.put(ze.getName(), sb.toString());
			}
		}
		finally
		{
			if (zis != null)
				zis.close();
		}
		
		return map;
	}
	
	/* For testing purposes only */
	public static void main (String[] args) throws Exception
	{
		ZipFacility zip = new ZipFacility();
		HashMap<String, String> data = new HashMap<String, String>();
		data.put("avatars", "some content");
		data.put("configuration", "some content");
		data.put("objects", "some content");
		data.put("hotspots", "some content");
		
		zip.createZip("archive.zip", data);
		
		Map<String,String> map = zip.readZip("archive.zip");
		System.out.println(map.get("configuration"));
	}
}
