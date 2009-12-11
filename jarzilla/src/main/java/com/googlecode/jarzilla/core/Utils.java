package com.googlecode.jarzilla.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;

import javad.classfile.classFile;

/**
 * Utility class
 *
 * @author Igor Polevoy
 * @author rayvanderborght
 */
public class Utils
{
	/**
	 * Reads resource from a jar file fully
	 *
	 * @param jarFileName jar file name
	 * @param resourceName internal resource name
	 * @return full content of resource
	 */
	public static String readResourceAsString(String jarFileName, String resourceName) throws IOException
	{
		URL url = new URL("jar:file:" + jarFileName + "!/" + resourceName);
		BufferedInputStream in = new BufferedInputStream(url.openConnection().getInputStream());

		StringBuilder result = new StringBuilder(1024);
		for (int tmp = in.read(); tmp != -1; tmp = in.read())
		{
			result.append((char) tmp);
		}
		in.close();

		return result.toString();
	}

	/**
	 * Reads resource from jar fully
	 *
	 * @param jarFileName jar file path
	 * @param resourceName resource path
	 * @return bytes with resource content
	 */
	public static byte[] readResourceAsBytes(String jarFileName, String resourceName) throws IOException
	{
		ByteArrayOutputStream result = new ByteArrayOutputStream(1024);
		URL url = new URL("jar:file:" + jarFileName + "!/" + resourceName);
		BufferedInputStream in = new BufferedInputStream(url.openConnection().getInputStream());

		for (int tmp = in.read(); tmp != -1; tmp = in.read())
		{
			result.write(tmp);
		}
		in.close();

		return result.toByteArray();
	}

	/**
	 * Creates a fully filled instance of {@link ClassFileInfo} by parsing bytecode.
	 *
	 * @param jarName - name of jar file
	 * @param className - name of class (in format: pack1/pack2/pack3/ClassName.class)
	 * @return instance of {@link ClassFileInfo} filled with all values parsed from bytecode
	 * @throws IOException thrown in case there is a problem reading bytecode
	 */
	public static ClassFileInfo createFully(String jarName, String className, long fileTime, long fileSize) throws IOException
	{
		URL url = new URL("jar:file:" + jarName + "!/" + className);
		BufferedInputStream in = new BufferedInputStream(url.openConnection().getInputStream());
		DataInputStream din = new DataInputStream(in);

		ClassFileInfo classInfo = new ClassFileInfo();
		classInfo.setJarFileName(jarName);
		classInfo.setClassPath(className);

		if (className.endsWith(".class"))
		{
			classFile classFile = new classFile(din);
			classInfo.setModifiers(classFile.getClassModifiers());
			classInfo.setConstructors(classFile.getConstructors());
			classInfo.setFields(classFile.getFields());
			classInfo.setInterfaces(classFile.getInterfaces());
			classInfo.setMethods(classFile.getMethods());
			classInfo.setSuperclass(classFile.getSuperClassName());
		}
		din.close();

		return classInfo;
	}

	/** */
	public static String getStackTrace(Throwable e)
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		e.printStackTrace(new PrintStream(stream));
		return stream.toString();
	}

	/** */
	public static boolean isMac()
	{
		// TODO: implement this when we support something other than mac
		return true;
	}
}