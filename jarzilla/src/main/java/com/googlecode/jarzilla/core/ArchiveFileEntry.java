/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.core;

import lombok.Getter;

/**
 * Represents a file inside of an archive
 *
 * @author rayvanderborght
 */
public class ArchiveFileEntry implements Comparable<ArchiveFileEntry>
{
    /** */
    @Getter private final String archiveFilePath;

    /** */
    @Getter private final String entryFilePath;

    /** */
    @Getter private final long fileTime;

    /** */
    @Getter private final long fileSize;

    /**
     *
     * @param entryFilePath name of class (really a resource, including a ".class")
     */
    public ArchiveFileEntry(String archiveFilePath, String entryFilePath, long fileTime, long fileSize)
    {
        this.archiveFilePath = archiveFilePath;
        this.entryFilePath = entryFilePath;
        this.fileTime = fileTime;
        this.fileSize = fileSize;
    }

    /** {@inheritdoc} */
    @Override
    public String toString()
    {
        return this.getEntryFilePath();
    }

    /** {@inheritdoc} */
    @Override
    public int compareTo(ArchiveFileEntry o)
    {
        return (o == null) ? -1 : this.getEntryFilePath().compareTo(o.getEntryFilePath());
    }
}
