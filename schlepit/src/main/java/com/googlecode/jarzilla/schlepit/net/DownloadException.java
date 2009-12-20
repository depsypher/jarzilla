/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.schlepit.net;

/**
 * A download exception
 *
 * @author rayvanderborght
 */
@SuppressWarnings("serial")
public class DownloadException extends Exception
{
	/** */
	public DownloadException(String message)
	{
		super(message);
	}

	/** */
	public DownloadException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
