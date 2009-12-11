/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import lombok.Getter;

/**
 * Represents an archive file
 *
 * @author rayvanderborght
 */
public class ArchiveFile
{
	/** */
	@Getter
	private final String archiveFilePath;

	/** */
	@Getter
    private final List<ArchiveFileEntry> entries = new LinkedList<ArchiveFileEntry>();

    /** */
	public ArchiveFile(String archiveFilePath)
	{
		this.archiveFilePath = archiveFilePath;
	}

	/** */
	public void add(ArchiveFileEntry entry)
	{
		if (!entries.contains(entry))
			entries.add(entry);
	}

	/** */
    public List<ArchiveFileEntry> search(String name)
    {
        List<ArchiveFileEntry> results = new ArrayList<ArchiveFileEntry>();

        for (ArchiveFileEntry entry : entries)
        {
            if (entry.getEntryFilePath().toLowerCase().contains(name.toLowerCase()))
                results.add(entry);
        }
        Collections.sort(results);

        return results;
    }
}
