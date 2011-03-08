/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.ui;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.macwidgets.plaf.ITunesTableUI;
import com.googlecode.jarzilla.Jarzilla;
import com.googlecode.jarzilla.core.ArchiveFileEntry;
import com.googlecode.jarzilla.core.Utils;

/**
 * Shows the files contained in an archive
 *
 * @author rayvanderborght
 */
@SuppressWarnings("serial")
public class ResultsPanel extends JPanel
{
	/** */
	private final JList resultList = new JList(new DefaultListModel());
	private final JTable table;
	private final JarzillaHeader header;

	/** */
	public ResultsPanel(final Jarzilla jarzilla)
	{
		this.setLayout(new BorderLayout());

		this.header = new JarzillaHeader("Archive Contents");
		this.add(header, BorderLayout.NORTH);

		table = new JTable(new ResultsTableModel(new ArrayList<ArchiveFileEntry>()))
		{
			@Override
			public TableCellRenderer getCellRenderer(int row, int column)
			{
				switch (column)
				{
					case 0:
						return new ResultNameRenderer();

					case 1:
						return new ResultDateRenderer();

					case 2:
						return new ResultSizeRenderer();

					default:
						return super.getCellRenderer(row, column);
				}
			}
		};
		table.setUI(new ITunesTableUI());

		table.getColumnModel().getColumn(0).setPreferredWidth(280);
		table.getColumnModel().getColumn(1).setPreferredWidth(85);
		table.getColumnModel().getColumn(2).setPreferredWidth(70);

		JScrollPane scrollPane = new JScrollPane(table);
		IAppWidgetFactory.makeIAppScrollPane(scrollPane);
		this.add(scrollPane, BorderLayout.CENTER);

		this.addActionListeners();

		// listen to files being dragged over the editor area
		DropTarget dropTarget = new DropTarget(this, new DropTargetListener()
		{
			/** */
			@Override
			public void dragOver(DropTargetDragEvent dtde)
			{
				// check if a file is being dragged over and if anybody can process it
				if (dtde != null && true)
				{
					dtde.acceptDrag(DnDConstants.ACTION_COPY);
				}
				else
				{
					dtde.rejectDrag();
				}
			}

			/** */
			@Override
			@SuppressWarnings("unchecked")
			public void drop(DropTargetDropEvent evt)
			{
				int action = evt.getDropAction();
				evt.acceptDrop(action);
				try
				{
					Transferable data = evt.getTransferable();
					if (data.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
					{
						List<File> files = (List<File>)data.getTransferData(DataFlavor.javaFileListFlavor);
						for (File file : files)
						{
							jarzilla.scanPath(file);
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					evt.dropComplete(true);
				}
			}
			public void dragEnter(DropTargetDragEvent dtde) {  }
			public void dragExit(DropTargetEvent dte) {  }
			public void dropActionChanged(DropTargetDragEvent dtde) {  }
		});
		this.setDropTarget(dropTarget);
	}

	/** */
	public void setBusy(boolean busy)
	{
		this.header.setBusy(busy);
	}

	/** */
	private void addActionListeners()
	{
		final JPopupMenu menu = new JPopupMenu();
		JMenuItem copyMI = new JMenuItem("Copy");
		copyMI.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StringSelection stringSelection = new StringSelection(resultList.getSelectedValue().toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
			}
		});
		menu.add(copyMI);

		this.table.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
					ResultsPanel.this.exploreSelected();
			}

			@Override
			public void mousePressed(MouseEvent e)
			{
				this.maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				this.maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					if (ResultsPanel.this.resultList.getSelectedValue() != null)
						menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		resultList.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyTyped(KeyEvent e)
			{
			   if(e.getKeyChar() == '\n')
			   {
				   ResultsPanel.this.exploreSelected();
			   }
			}
		});
	}

	/** */
	private void exploreSelected()
	{
		try
		{
			JTable t = ResultsPanel.this.table;
			ArchiveFileEntry classInfo = (ArchiveFileEntry)t.getModel().getValueAt(t.getSelectedRow(), t.getSelectedColumn());

			String jarFileName = classInfo.getArchiveFilePath();
			String resourceName = classInfo.getEntryFilePath();
			String normalizedName = classInfo.getEntryFilePath().toLowerCase();
	        if (normalizedName.endsWith(".ear") || normalizedName.endsWith(".jar") ||
            	normalizedName.endsWith(".sar") || normalizedName.endsWith(".war") ||
            	normalizedName.endsWith(".zip"))
            {
    			URL url = new URL("jar:file:" + jarFileName + "!/" + resourceName);

    			File tempDir = File.createTempFile("jarzilla", normalizedName.substring(normalizedName.length() - 4));
    			this.extract(url, tempDir.getCanonicalPath());
    			new ProcessBuilder("open", "-n", tempDir.getCanonicalPath()).start();
            }
	        else
	        {
				ResourceExplorer resourceExplorerDialog = new ResourceExplorer(Jarzilla.getFrame(), classInfo);
				resourceExplorerDialog.getJDialog().setSize(640, 480);
				resourceExplorerDialog.getJDialog().setLocationRelativeTo(null);
				resourceExplorerDialog.getJDialog().setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				resourceExplorerDialog.getJDialog().setVisible(true);
	        }
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Jarzilla.messageBoxWithDetails("Error", e.getMessage(), Utils.getStackTrace(e), JOptionPane.ERROR_MESSAGE);
		}
	}

	/** */
	public void setResults(List<ArchiveFileEntry> results)
	{
		table.setModel(new ResultsTableModel(results));
		table.getColumnModel().getColumn(0).setPreferredWidth(280);
		table.getColumnModel().getColumn(1).setPreferredWidth(85);
		table.getColumnModel().getColumn(2).setPreferredWidth(70);

		this.repaint();
	}

	/** */
	public void clean()
	{
		table.setModel(new ResultsTableModel());
	}

	/** */
	private static class ResultNameRenderer extends DefaultTableCellRenderer
	{
		@Override
		public void setValue(Object value)
		{
			String name = (value == null) ? "" : ((ArchiveFileEntry)value).getEntryFilePath();
			this.setText(name);
		}
	}

	/** */
	private static class ResultDateRenderer extends DefaultTableCellRenderer
	{
		@Override
		public void setValue(Object value)
		{
			ArchiveFileEntry info = (ArchiveFileEntry)value;
			if (info == null || info.getFileTime() < 0)
			{
				this.setText("--");
			}
			else
			{
				Date date = new Date(info.getFileTime());
				this.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(date));
			}
		}
	}

	/** */
	private static class ResultSizeRenderer extends DefaultTableCellRenderer
	{
		private static final double ONE_KB = 1024;
		private static final double ONE_MB = 1024 * 1024;
		private static final double ONE_GB = 1024 * 1024 * 1024;

		/** */
		public ResultSizeRenderer()
		{
			this.setHorizontalAlignment(SwingConstants.RIGHT);
		}

		/** */
		@Override
		public void setValue(Object value)
		{
			ArchiveFileEntry info = (ArchiveFileEntry)value;
			if (info == null)
			{
				this.setText("");
			}
			else
			{
				if (info.getFileSize() < 1024)
				{
					this.setText(info.getFileSize() + " bytes");
				}
				else if (info.getFileSize() < 1024 * 1024)
				{
					this.setText(new DecimalFormat("#.##").format(info.getFileSize() / ONE_KB) + " KB");
				}
				else if (info.getFileSize() < 1024 * 1024 * 1024)
				{
					this.setText(new DecimalFormat("#.##").format(info.getFileSize() / ONE_MB) + " MB");
				}
				else
				{
					this.setText(new DecimalFormat("#.##").format(info.getFileSize() / ONE_GB) + " GB");
				}
			}
		}
	}

	/** */
	private static class ResultsTableModel extends AbstractTableModel
	{
		private final List<ArchiveFileEntry> datalist = new ArrayList<ArchiveFileEntry>();
		private final String[] columns = { "Name", "Date", "Size" };

		/** */
		public ResultsTableModel() {  }

		/** */
		public ResultsTableModel(List<ArchiveFileEntry> l)
		{
			datalist.addAll(l);
		}

		@Override
		public int getRowCount() {
			return datalist.size();
		}

		@Override
		public String getColumnName(int i)
		{
			return columns[i];
		}

		@Override
		public int getColumnCount()
		{
			return columns.length;
		}

		@Override
		public Class<?> getColumnClass(int columnIndex)
		{
			return ArchiveFileEntry.class;
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			return datalist.get(row);
		}

		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}
	}

	/** */
	public void extract(URL url, String path)
	{
		System.out.println("Extracting: " + path);
		try
		{
			BufferedInputStream ins = new BufferedInputStream(url.openConnection().getInputStream());

			final int BUFFER = 1024;
			byte data[] = new byte[BUFFER];

			BufferedOutputStream dest = null;
			int count;
			FileOutputStream fos = new FileOutputStream(path);
			dest = new BufferedOutputStream(fos, BUFFER);
			while ((count = ins.read(data, 0, BUFFER)) != -1)
			{
				dest.write(data, 0, count);
			}
			dest.flush();
			fos.close();
			ins.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
