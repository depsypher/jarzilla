/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.core;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Holds useful info about a java class
 *
 * @author rayvanderborght
 */
public class ClassFileInfo
{
    /** */
    @Getter @Setter private List<String> constructors = new ArrayList<String>();

    /** */
    @Getter @Setter private String[] fields = new String[0];

    /** */
    @Getter @Setter private String modifiers = "";

    /** */
    @Getter @Setter private String superclass = "";

    /** */
    @Getter @Setter private String[] interfaces = new String[0];

    /** */
    @Getter @Setter private List<String> methods = new ArrayList<String>();

    /** */
    @Getter @Setter private String jarFileName;

    /** */
    @Getter @Setter private String classPath;

    /** */
    public String getClassName()
    {
        String tmp = classPath.replace('/', '.');
        return tmp.substring(0, tmp.lastIndexOf('.'));
    }
}
