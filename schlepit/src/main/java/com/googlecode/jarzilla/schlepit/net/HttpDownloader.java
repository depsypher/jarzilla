/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.schlepit.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Manages downloads over http
 *
 * @author rayvanderborght
 */
public class HttpDownloader extends Downloader
{
	/** */
	public HttpDownloader(String currentVersion, URL url, String updateDir, DownloadObserver observer)
	{
		this.currentVersion = currentVersion;
		this.url = url;
		this.updateDir = updateDir;
		this.observer = observer;
	}

	/**
	 * {@inheritdoc}
	 */
	@Override
	protected void doDownload(URL url) throws DownloadException
	{
		HttpURLConnection connection = null;
		try
		{
			this.totalDownloadSize = this.getFileSize(url);
			connection = (HttpURLConnection)url.openConnection();
			connection.connect();

			if (connection == null || connection.getResponseCode() != HttpURLConnection.HTTP_OK)
				throw new DownloadException("Error connecting to " + url);
		}
		catch(IOException e)
		{
			throw new DownloadException("Error getting connection", e);
		}

		InputStream in = null;
		FileOutputStream out = null;
		try
		{
			File tempZip = File.createTempFile("update", "zip");
			in = connection.getInputStream();
			out = new FileOutputStream(tempZip);
			int read;

			if (!this.isValidHeader(in))
				throw new DownloadException("Invalid header in update file");

			int versionLength = new DataInputStream(in).readInt();

			if (versionLength > 255)
				throw new DownloadException("Invalid version in update file");

			byte[] versionBytes = new byte[versionLength];
			in.read(versionBytes);

			String newVersion = new String(versionBytes, "UTF-8");

			System.out.println("Current Version=" + currentVersion);
			System.out.println("New Version=" + newVersion);

			if (newVersion.equals(currentVersion))
				throw new DownloadException("Already up to date");

			this.observer.onUpdateAvailable(this);
			try
			{
				synchronized(this)
				{
					this.wait();
				}
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
			System.out.println("proceeding with download");

			long crc = this.getCrc(in);

			while ((read = in.read(this.buffer)) != -1)
			{
				out.write(this.buffer, 0, read);

				currentSize += read;
				this.updateObserver();
			}
			HttpDownloader.unZip(tempZip, this.updateDir, crc);
		}
		catch(IOException e)
		{
			throw new DownloadException("Error downloading", e);
		}
		finally
		{
			if (in != null)
			{
				try
				{
					in.close();
				}
				catch(IOException e)
				{
					throw new DownloadException("Error closing stream", e);
				}
			}
			if (out != null)
			{
				try
				{
					out.close();
				}
				catch(IOException e)
				{
					throw new DownloadException("Error closing stream", e);
				}
			}
		}
	}

	/** */
	protected long getFileSize(URL url) throws IOException
	{
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("HEAD");
		connection.connect();

		if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
			throw new IOException("Unable to check up-to-date for " +	url + ": " + connection.getResponseCode());

		try
		{
			return connection.getContentLength();
		}
		finally
		{
			connection.disconnect();
		}
	}

	/** */
	public static void unZip(File zipFile, String path, long crc)
	{
		try
		{
			final int BUFFER = 2048;
			byte data[] = new byte[BUFFER];
			FileInputStream fis = new FileInputStream(zipFile);

			CheckedInputStream checksum = new CheckedInputStream(new BufferedInputStream(fis), new Adler32());
			while (checksum.read(data, 0, BUFFER) != -1)
				;

			long fileCrc = checksum.getChecksum().getValue();
			fis.close();

			System.out.println("Checksum:" + crc + " fileCrc: " + fileCrc);
			if (crc != fileCrc)
				throw new RuntimeException("Corrupt zip");

			BufferedOutputStream dest = null;
			fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null)
			{
				System.out.println("Extracting: " + path + entry);
				int count;
				if (entry.isDirectory())
				{
					new File(path + entry.getName()).mkdir();
				}
				else
				{
					// write the files to the disk
					FileOutputStream fos = new FileOutputStream(path + entry.getName());
					dest = new BufferedOutputStream(fos, BUFFER);
					while ((count = zis.read(data, 0, BUFFER)) != -1)
					{
						dest.write(data, 0, count);
					}
					dest.flush();
					fos.close();
				}
			}
			zis.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/** */
	public static void copyFile(File sourceFile, File destFile) throws IOException
	{
		if (!destFile.exists())
		{
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try
		{
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		}
		finally
		{
			if (source != null)
			{
				source.close();
			}
			if (destination != null)
			{
				destination.close();
			}
		}
	}
}
