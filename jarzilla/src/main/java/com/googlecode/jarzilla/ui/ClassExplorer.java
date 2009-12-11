/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

import com.explodingpixels.macwidgets.HudWindow;
import com.googlecode.jarzilla.core.ClassFileInfo;
import com.googlecode.jarzilla.core.Utils;

/**
 * Displays a tree view of class internals:
 * implemented interfaces, superclass, fields, methods, constructors, etc.
 *
 * @author rayvanderborght
 * @author Igor Polevoy
 * <br/>
 * August 22 2007
 */
public class ClassExplorer extends HudWindow
{
    /**
     * Constructs an instance
     *
     * @param owner top level frame
     * @param classInfo instance
     */
    @SuppressWarnings("serial")
    public ClassExplorer(Frame owner, ClassFileInfo classInfo)
    {
        super(classInfo.getClassPath(), owner);

        this.getContentPane().setLayout(new BorderLayout());

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

        // build south panel
        JPanel southPanel = new JPanel();
        JButton closeB = new JButton("Close");
        southPanel.add(closeB);
        closeB.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                ClassExplorer.this.getJDialog().dispose();
            }
        });

        JScrollPane pane = new JScrollPane(tree);
        this.getContentPane().add(pane, BorderLayout.CENTER);
        this.getContentPane().add(southPanel, BorderLayout.SOUTH);

        Action escape = new AbstractAction()
        {
            public void actionPerformed(ActionEvent event)
            {
                ClassExplorer.this.getJDialog().dispose();
            }
        };

        // close on Escape
        this.getContentPane().registerKeyboardAction(escape,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        if (Utils.isMac())
        {
            // close on Command-W
            this.getContentPane().registerKeyboardAction(escape,
                    KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                    JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

            pane.getRootPane().putClientProperty("Window.alpha", new Float(0.97));
        }

        this.makeResizeable();
    }
}

