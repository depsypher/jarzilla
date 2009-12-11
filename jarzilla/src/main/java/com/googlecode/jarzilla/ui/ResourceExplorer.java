package com.googlecode.jarzilla.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.URL;

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
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.explodingpixels.macwidgets.HudWidgetFactory;
import com.explodingpixels.macwidgets.HudWindow;
import com.googlecode.jarzilla.core.Utils;

/**
 * This dialog shows resources, such as HTML, text files and images (JPEG, GIF, PNG).
 * It will show contents of a resource read from a jar file. Text resource
 * view alows to switch font from monospaced to default font. Property files
 * are easier to read with monospaced font. HTML view renders HTML as in
 * browser, but also provides a second tab to see HTML source.
 *
 * @auther rayvanderborght
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
    public ResourceExplorer(Frame owner, String jarFileName, String resourceName) throws IOException
    {
        super(resourceName, owner);

        this.getContentPane().setLayout(new BorderLayout());

        if (resourceName.toLowerCase().endsWith(".html"))
        {
            this.buildForHTML(jarFileName, resourceName);
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
        //north panel - toolbar
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

        //center panel
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
}