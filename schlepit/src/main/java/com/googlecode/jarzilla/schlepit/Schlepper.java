/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.schlepit;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import com.googlecode.jarzilla.schlepit.net.DownloadObserver;
import com.googlecode.jarzilla.schlepit.net.Downloader;
import com.googlecode.jarzilla.schlepit.net.HttpDownloader;

/**
 * Main entry point for updating an application using schlepit
 *
 * @author rayvanderborght
 */
public class Schlepper
{
	/** */
	public boolean schlep(String currentVersion, String updateUrl, String updateDir, DownloadObserver observer)
	{
		URL url = null;
		try
		{
			url = new URL(updateUrl);
		}
		catch(MalformedURLException e)
		{
			e.printStackTrace();
		}
		Downloader dl = new HttpDownloader(currentVersion, url, updateDir, observer);
		return dl.download();
	}

	/** */
	public Properties getProperties(String filePath)
	{
		Properties properties = new Properties();
		try
		{
			properties.load(new FileInputStream(filePath));
			return properties;
		}
		catch(IOException e) {  }

		return null;
	}

	/** */
	public String getDirectory(Class<?> clazz)
	{
		try
		{
			URI uri = Schlepper.class.getResource("/" + Schlepper.class.getName().replace('.', '/') + ".class").toURI();
			String name = uri.toString().replaceFirst("file:", "").replaceFirst("jar:", "");
			return name.substring(0, name.lastIndexOf('/') + 1);
		}
		catch(Exception e)
		{
			// ignore
		}
		return null;
	}
}
