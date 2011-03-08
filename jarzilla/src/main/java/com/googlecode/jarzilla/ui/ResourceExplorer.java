package com.googlecode.jarzilla.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import jsyntaxpane.DefaultSyntaxKit;
import jsyntaxpane.SyntaxStyles;

import com.explodingpixels.macwidgets.HudWidgetFactory;
import com.explodingpixels.macwidgets.HudWindow;
import com.googlecode.jarzilla.core.ArchiveFileEntry;
import com.googlecode.jarzilla.core.ClassFileInfo;
import com.googlecode.jarzilla.core.Utils;

/**
 * This dialog shows resources, such as HTML, text files and images (JPEG, GIF, PNG).
 * It will show contents of a resource read from a jar file. Text resource
 * view alows to switch font from monospaced to default font. Property files
 * are easier to read with monospaced font. HTML view renders HTML as in
 * browser, but also provides a second tab to see HTML source.
 *
 * @author rayvanderborght
 * @author Igor Polevoy
 */
public class ResourceExplorer extends HudWindow
{
    private JCheckBox monospacedCheckbox;
    private JTextArea textArea;
    private Font font;

    /**
     * Creates a dialog, and reads a resource in the process.
     *
     * @param owner        - top level window
     * @param jarFileName  - name of jar file
     * @param resourceName - internal path to resource
     * @throws IOException - thrown in case there is a problem reading the resource.
     */
    @SuppressWarnings("serial")
    public ResourceExplorer(Frame owner, ArchiveFileEntry fileInfo) throws IOException
    {
        super(fileInfo.getEntryFilePath(), owner);

        this.getContentPane().setLayout(new BorderLayout());

        String jarFileName = fileInfo.getArchiveFilePath();
        String resourceName = fileInfo.getEntryFilePath();

        String normalizedName = resourceName.toLowerCase();
        if (normalizedName.endsWith(".html"))
        {
            this.buildForHTML(jarFileName, resourceName);
        }
        else if (normalizedName.endsWith(".class"))
        {
			URL url = new URL("jar:file:" + jarFileName + "!/" + resourceName);
			BufferedInputStream in = new BufferedInputStream(url.openConnection().getInputStream());
			File f = File.createTempFile("decompile", ".class");

			try
			{
			    OutputStream os = new FileOutputStream(f);
			    try
			    {
			        byte[] buffer = new byte[4096];
			        for (int n; (n = in.read(buffer)) != -1;)
			        {
			        	os.write(buffer, 0, n);
			        }
			    }
			    finally
			    {
			    	os.close();
			    }
			}
			finally
			{
				in.close();
			}

			this.buildForClass(fileInfo, this.decompile(f));
        }
        else if (this.isImage(resourceName))
        {
            this.buildForImage(Utils.readResourceAsBytes(jarFileName, resourceName));
        }
        else
        {
            this.buildForSimpleResource(Utils.readResourceAsString(jarFileName, resourceName));
        }

        // build south panel
        JPanel southPanel = new JPanel();
        JButton closeB = new JButton("Close");
        southPanel.add(closeB);

        Action closeAction = new AbstractAction()
        {
            public void actionPerformed(ActionEvent event)
            {
                ResourceExplorer.this.getJDialog().dispose();
            }
        };
        closeB.addActionListener(closeAction);

        if (Utils.isMac())
        {
            // close on Command-W
            (this.getContentPane()).registerKeyboardAction(closeAction,
                    KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

            this.getContentPane().getRootPane().putClientProperty("Window.alpha", new Float(0.97));
        }

        // close on Escape
        (this.getContentPane()).registerKeyboardAction(closeAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.getContentPane().add(southPanel, BorderLayout.SOUTH);

        this.makeResizeable();
    }

    /**
     * Builds a center panel for text resource
     *
     * @param resource content of resourse (property file, manifest, etc.)
     */
    private void buildForSimpleResource(String resource)
    {
        // north panel - toolbar
        monospacedCheckbox = HudWidgetFactory.createHudCheckBox("Monospaced Font");
        monospacedCheckbox.setSelected(true);
        monospacedCheckbox.setForeground(new Color(0xdddddd));

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(monospacedCheckbox, BorderLayout.WEST);
        monospacedCheckbox.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                if (monospacedCheckbox.isSelected())
                {
                    textArea.setFont(new Font("Monospaced", font.getStyle(), font.getSize()));
                }
                else
                {
                    textArea.setFont(new Font("Default", font.getStyle(), font.getSize()));
                }
            }
        });

        // center panel
        textArea = new JTextArea(resource);
        font = textArea.getFont();
        textArea.setFont(new Font("Monospaced", font.getStyle(), font.getSize()));
        textArea.setEditable(false);
        this.getContentPane().add(northPanel, BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    /**
     *
     * @param jarFileName name of jar file (fully qualified)
     * @param resourceName path to resource within the jar file
     * @throws IOException
     */
    private void buildForHTML(String jarFileName, String resourceName) throws IOException
    {
        JEditorPane htmlPane = new JEditorPane(new URL("jar:file:" + jarFileName + "!/" + resourceName));
        htmlPane.setEditable(false);

        JTextArea sourceArea = new JTextArea(Utils.readResourceAsString(jarFileName, resourceName));
        sourceArea.setEditable(false);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("HTML", new JScrollPane(htmlPane));
        tabbedPane.add("Source", new JScrollPane(sourceArea));

        sourceArea.setFont(new Font("Monospaced", Font.PLAIN, sourceArea.getFont().getSize()));
        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Builds UI for displaying class files
     *
     * @param fileInfo
     * @param source
     * @throws IOException
     */
    private void buildForClass(ArchiveFileEntry fileInfo, String source) throws IOException
    {
    	ClassFileInfo classInfo = Utils.createFully(fileInfo.getArchiveFilePath(), fileInfo.getEntryFilePath(), fileInfo.getFileTime(), fileInfo.getFileSize());

        String nodeString = "<html><font color='#007fae'>%s</font></html>";

        DefaultMutableTreeNode classNode        = new DefaultMutableTreeNode(String.format(nodeString, "Class Information"));
        DefaultMutableTreeNode interfacesNode   = new DefaultMutableTreeNode(String.format(nodeString, "Implemented Interfaces"));
        DefaultMutableTreeNode constructorsNode = new DefaultMutableTreeNode(String.format(nodeString, "Constructors"));
        DefaultMutableTreeNode fieldsNode       = new DefaultMutableTreeNode(String.format(nodeString, "Fields"));
        DefaultMutableTreeNode methodsNode      = new DefaultMutableTreeNode(String.format(nodeString, "Methods"));

        nodeString = "<html><font color='#0000aa'>%s:</font> %s</html>";

        DefaultMutableTreeNode root = new DefaultMutableTreeNode(String.format(nodeString, "Class", classInfo.getClassName()));
        root.add(classNode);
        root.add(interfacesNode);
        root.add(constructorsNode);
        root.add(fieldsNode);
        root.add(methodsNode);

        JTree tree = new JTree();

        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer)tree.getCellRenderer();
        renderer.setFont(new Font("Monospaced", Font.PLAIN, 14));

        classNode.add(new DefaultMutableTreeNode(String.format(nodeString, "Jar File", classInfo.getJarFileName())));
        classNode.add(new DefaultMutableTreeNode(String.format(nodeString, "Superclass", classInfo.getSuperclass())));
        classNode.add(new DefaultMutableTreeNode(String.format(nodeString, "Modifiers", classInfo.getModifiers())));

        ((DefaultTreeModel)tree.getModel()).setRoot(root);

        // interfaces
        String[] interfacesArray = classInfo.getInterfaces();
        for (int i = 0; i < interfacesArray.length; i++)
        {
            String interfaceName = interfacesArray[i];
            interfacesNode.add(new DefaultMutableTreeNode(interfaceName));
        }

        // constructors
        List<String> constructorList = classInfo.getConstructors();
        for (int i = 0; i < constructorList.size(); i++)
        {
            String constructor = constructorList.get(i);
            constructorsNode.add(new DefaultMutableTreeNode(constructor));
        }

        // methods
        List<String> methodsList = classInfo.getMethods();
        for (int i = 0; i < methodsList.size(); i++)
        {
            String method = methodsList.get(i);
            methodsNode.add(new DefaultMutableTreeNode(method));
        }

        // fields
        String[] fieldsList = classInfo.getFields();
        for (int i = 0; i < fieldsList.length; i++)
        {
            String field = fieldsList[i];
            fieldsNode.add(new DefaultMutableTreeNode(field));
        }

        for (int i = 0; i < tree.getRowCount(); i++)
        {
            tree.expandRow(i);
        }

        DefaultSyntaxKit.initKit();

        // TODO: read from a properties file
        Properties styleProps = new Properties();
        styleProps.put("KEYWORD", "0x7F0055, 0");
        styleProps.put("KEYWORD2", "0x7F0055, 3");
        styleProps.put("STRING", "0x2A00FF, 0");
        styleProps.put("STRING2", "0x2A00FF, 1");
        styleProps.put("NUMBER", "0x000000, 1");
        SyntaxStyles.getInstance().mergeStyles(styleProps);

        final JEditorPane sourceArea = new JEditorPane();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Source", new JScrollPane(sourceArea));
        tabbedPane.add("Outline", new JScrollPane(tree));

        sourceArea.setEditable(false);
        sourceArea.setContentType("text/java");
        sourceArea.setText(source);
        sourceArea.setCaretPosition(0);

        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Builds UI for display of image.
     *
     * @param image image content.
     */
    private void buildForImage(byte[] image)
    {
        JLabel l = new JLabel();
        Icon icon = new ImageIcon(image);
        l.setIcon(icon);
        JPanel center = new JPanel();
        center.add(l);
        this.getContentPane().add(new JScrollPane(center), BorderLayout.CENTER);
    }

    /**
     * Checks if resource is image. Test is done based on extension only.
     *
     * @param selectedValue - path to resource
     * @return true is extension is: gif, jpg, jpeg, png (case insensitive)
     */
    private boolean isImage(String selectedValue)
    {
        String tmp = selectedValue.toLowerCase();
        return tmp.endsWith(".gif") || tmp.endsWith(".jpg") || tmp.endsWith(".jpeg")  || tmp.endsWith(".png");
    }

	/** */
    public String decompile(File file)
    {
        String result = null;
        try
        {
        	String path = this.getClass().getResource("/" + this.getClass().getName().replace('.', '/') + ".class").toURI().toString();
        	path = path.replaceFirst("file:", "").replaceFirst("jar:", "");
        	path = path.substring(0, path.lastIndexOf("Jarzilla") + 8);
        	path = path + ".app/Contents/Resources/jad";

            ProcessBuilder p = new ProcessBuilder(path, "-p", file.getCanonicalPath());
            Process proc = null;

            try
            {
            	proc = p.start();
            }
            catch (IOException e)
            {
            	System.out.println("could not run jad, setting permissions to executable");

            	// the first time after the user has auto-updated the jad
            	// executable will not have executable permissions yet
            	Process chmod = new ProcessBuilder("chmod", "755", path).start();
            	chmod.waitFor();

            	proc = p.start();
            }

            ProcessReader reader = new ProcessReader(proc.getInputStream());
            reader.start();
            proc.waitFor();
            result = reader.getString();

            if (result != null && result.indexOf("\n\n") != -1)
            {
            	result = result.substring(result.indexOf("\n\n") + "\n\n".length(), result.length());
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return result;
    }

	/** */
    private class ProcessReader extends Thread
    {
        private BufferedReader reader;
        private String string;
        private boolean done;

        /** */
        public ProcessReader(InputStream stream)
        {
            super();
            done = false;
            reader = new BufferedReader(new InputStreamReader(stream));
        }

        @Override
		public void run()
        {
        	StringBuilder sb = new StringBuilder();
            try
            {
            	String line = null;
                while ((line = reader.readLine()) != null)
                {
                    if ("{".equals(line.trim()) && sb.charAt(sb.length() - 1) == '\n')
                    {
                        line = line.trim();
                        sb.deleteCharAt(sb.length() - 1);
                        sb.append(" ");
                    }
                    sb.append(line);
                    sb.append("\n");
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            string = sb.toString();
            done = true;
        }

        /** */
        public String getString()
        {
            while (!done)
            {
                try
                {
                    Thread.sleep(50L);
                }
                catch(Exception e) { }
            }
            return string;
        }
    }
}