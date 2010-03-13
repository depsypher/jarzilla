/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.schlepit.net;

/**
 * Interface for communicating download info.
 *
 * @author rayvanderborght
 */
public interface DownloadObserver
{
	/**
	 * Hook for handling user interaction prior to an actual update
	 *
	 * @param downloader The downloader that's managing the update
	 */
	public void onUpdateAvailable(Downloader downloader);

	/**
	 * Hook for handling user interaction after update is complete
	 *
	 * @param downloader
	 */
	public void onUpdateComplete(Downloader downloader);

	/**
	 * Report the download progress
	 *
	 * @param percent Percentage complete
	 * @param remaining Estimated time remaining, or -1 if unknown
	 *
	 * @return true if the download should continue, false if it should be aborted.
	 */
	public boolean downloadProgress(int percent, long remaining);
}