/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.schlepit.build;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/**
 * Ant task for creating schlepit update files
 *
 * @author rayvanderborght
 */
public class SchlepItTask extends Task
{
	private static final byte[] HEADER = new byte[] { 0x01, 0x05, 0x0E, 0x0E, 0x0C, 0x00, 0x0D, 0x0E };
	private static final int BUFFER = 2048;

	/** */
	protected String fileName;
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }

	/** */
	protected File source;
	public File getSource() { return source; }
	public void setSource(File dir) { this.source = dir; }

	/** */
	protected File dest;
	public File getDest() { return dest; }
	public void setDest(File dest) { this.dest = dest; }

	/** */
	public String version;
	public String getVersion() { return version; }
	public void setVersion(String version) { this.version = version; }

	/**
	 * {@inheritdoc}
	 */
	@Override
	public void execute() throws BuildException
	{
		if (source == null)
			throw new BuildException("Must specify the zip containing your update.");

		if (!dest.isDirectory())
			throw new BuildException("Must provide a destination directory.");

		File result = new File(this.getDest(), fileName);
		FileOutputStream out = null;
		try
		{
			FileInputStream in = new FileInputStream(this.source);
			CheckedInputStream checkedStream = new CheckedInputStream(in, new Adler32());

			byte data[] = new byte[BUFFER];
			while (checkedStream.read(data, 0, BUFFER) != -1)
				;

			long crcValue = checkedStream.getChecksum().getValue();
			checkedStream.close();

			System.out.println("CRC=" + crcValue);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(bos);
			dos.writeLong(crcValue);
			dos.flush();
			byte[] crcBytes = bos.toByteArray();
			bos.close();

			byte[] versionBytes = version.getBytes("UTF-8");

			// write out version length and version itself in UTF-8 bytes
			bos = new ByteArrayOutputStream();
			dos = new DataOutputStream(bos);
			System.out.println("len=" + versionBytes.length);
			dos.writeInt(versionBytes.length);
			dos.flush();
			byte[] versionLengthBytes = bos.toByteArray();
			bos.close();

			out = new FileOutputStream(result);
			out.write(HEADER);
			out.flush();
			out.write(versionLengthBytes);
			out.write(versionBytes);
			out.write(crcBytes);

			in = new FileInputStream(this.source);

			int count;
			while ((count = in.read(data, 0, BUFFER)) != -1)
				out.write(data, 0, count);

			in.close();
		}
		catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				out.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}
