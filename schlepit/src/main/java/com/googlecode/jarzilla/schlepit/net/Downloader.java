/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.schlepit.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Abstract class for managing file download.
 *
 * @author rayvanderborght
 */
public abstract class Downloader extends Thread
{
	/**
	 * milliseconds between notifying progress observers of file download progress.
	 */
	protected static final long UPDATE_INTERVAL = 100L;

	/** */
	protected DownloadObserver observer;

	/** */
	protected String currentVersion;

	/** */
	protected URL url;

	/** */
	protected String updateDir;

	/** */
	protected long currentSize;

	/** */
	protected long downloadStartTime;

	/** */
	protected long lastProgressTime;

	/** */
	protected boolean downloadComplete;

	/** */
	protected long totalDownloadSize;

	/** */
	protected byte[] buffer = new byte[4096];

	/**
	 * @return Returns true on successful download
	 */
	public boolean download()
	{
		try
		{
			this.doDownload(this.url);
		}
		catch (DownloadException e)
		{
			return false;
		}

		return true;
	}

	/** */
	protected abstract void doDownload(URL url) throws DownloadException;

	/**
	 * {@inheritdoc}
	 */
	@Override
	public void run()
	{
		this.download();
	}

	/** */
	protected boolean isValidHeader(InputStream in) throws IOException
	{
		byte[] bytes = new byte[8];
		in.read(bytes);

		return bytes[0] == 0x1
			&& bytes[1] == 0x5
			&& bytes[2] == 0xE
			&& bytes[3] == 0xE
			&& bytes[4] == 0xC
			&& bytes[5] == 0x0
			&& bytes[6] == 0xD
			&& bytes[7] == 0xE;
	}

	/** */
	protected long getCrc(InputStream in) throws IOException
	{
		byte[] bytes = new byte[8];
		in.read(bytes);

		return ((long)(0xff & bytes[0]) << 56  |
				(long)(0xff & bytes[1]) << 48  |
				(long)(0xff & bytes[2]) << 40  |
				(long)(0xff & bytes[3]) << 32  |
				(long)(0xff & bytes[4]) << 24  |
				(long)(0xff & bytes[5]) << 16  |
				(long)(0xff & bytes[6]) << 8   |
				(long)(0xff & bytes[7]) << 0);
	}

	/**
	 * Periodically called by downloaders to update their progress.
	 */
	protected void updateObserver() throws DownloadException
	{
		long now = System.currentTimeMillis();
		if ((now - lastProgressTime) >= UPDATE_INTERVAL)
		{
			lastProgressTime = now;

			long seconds = (now - downloadStartTime) / 1000L;
			long bytesPerSecond = (seconds == 0) ? 0 : (currentSize / seconds);

			int percentDone = (totalDownloadSize == 0) ? 0 : (int)((currentSize * 100f) / totalDownloadSize);
			long secondsLeft = (bytesPerSecond <= 0 || totalDownloadSize == 0) ? -1 : (totalDownloadSize - currentSize) / bytesPerSecond;

			if (percentDone < 100 || !downloadComplete)
			{
				downloadComplete = (percentDone == 100);
				if (!observer.downloadProgress(percentDone, secondsLeft))
					throw new DownloadException("Download aborted");
			}
		}
	}
}
