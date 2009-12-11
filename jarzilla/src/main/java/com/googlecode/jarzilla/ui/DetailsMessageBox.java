package com.googlecode.jarzilla.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.Border;

/**
 * Displays a dialog with a message and a button to display extra details
 *
 * @author Igor Polevoy
 * Date: Jan 3, 2003
 * Time: 12:58:43 PM
 */
@SuppressWarnings("serial")
public class DetailsMessageBox extends JDialog
{
    private JButton detailsButton;
    private JButton okButton;
    private boolean open;
    private final int type;

    /**
     * Constructor
     *
     * @param parent - parent frame
     * @param title - title of dialog
     * @param message - message text to be displayed on the dialog
     * @param details - details text to be displayed if "Details" button is pressed
     * @param type - type of icon to display on the dialog. Possible options:
     * <code>
     * javax.swing.JOptionPane.WARNING_MESSAGE
     * javax.swing.JOptionPane.ERROR_MESSAGE
     * javax.swing.JOptionPane.INFORMATION_MESSAGE
     * </code>
     */
    public DetailsMessageBox(Frame parent, String title, String message, String details, int type)
    {
        super(parent, title, true);
        Action closeAction = new AbstractAction()
        {
            public void actionPerformed(ActionEvent event)
            {
            	DetailsMessageBox.this.dispose();
            }
        };
        // close on Command-W
        ((JComponent)this.getContentPane()).registerKeyboardAction(closeAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        ((JComponent)this.getContentPane()).registerKeyboardAction(closeAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        ((JComponent)this.getContentPane()).registerKeyboardAction(closeAction,
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_FOCUSED);

        this.type = type;
        this.init(message);
        this.addListeners(details);
    }

    /**
     * initializes gui
     *
     * @param message - text message
     */
    private void init(String message)
    {
        detailsButton = new JButton("Details >>");
        okButton = new JButton("OK");

        //this is a panel that holds icon, text and the control panel with buttons
        JPanel messagePanel = new JPanel(new BorderLayout(20, 20));
        messagePanel.setPreferredSize(new Dimension(400, 200));
        JTextArea ta = new JTextArea(message);
        ta.setEditable(false);
        ta.setBackground(messagePanel.getBackground());

        messagePanel.add(ta, BorderLayout.CENTER);
        messagePanel.add(new JLabel(" "), BorderLayout.NORTH);

        //get the specified icon from the UIDefaults
        Icon icon;
        if(JOptionPane.ERROR_MESSAGE == type)
        {
            icon = (Icon)UIManager.get("OptionPane.errorIcon");
        }
        else if(JOptionPane.INFORMATION_MESSAGE == type)
        {
            icon = (Icon)UIManager.get("OptionPane.informationIcon");
        }
        else if(JOptionPane.WARNING_MESSAGE == type)
        {
            icon = (Icon)UIManager.get("OptionPane.warningIcon");
        }
        else
        {
            throw new IllegalArgumentException("Incorrect type of message box: " + type + ". Correct types are: javax.swing.JOptionPane.WARNING_MESSAGE, javax.swing.JOptionPane.ERROR_MESSAGE, javax.swing.JOptionPane.INFORMATION_MESSAGE");
        }
        JLabel l = new JLabel(icon);
        //the following code is to more or less center the stupid icon.
        //setHorizontalAlignment() does not do much, apparently it only affects text
        l.setBorder(new Border()
        {
            public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {  }

            public Insets getBorderInsets(Component c)
            {
                return new Insets(10, 20, 10,10);
            }

            public boolean isBorderOpaque()
            {
                return false;
            }
        });
        messagePanel.add(l,BorderLayout.WEST);
        messagePanel.add(new JLabel(" "), BorderLayout.EAST);//this is just a margin on the right

        //controlPanel holds buttons and has a FlowLayout
        JPanel controlPanel = new JPanel();
        controlPanel.add(okButton);
        controlPanel.add(detailsButton);
        messagePanel.add(controlPanel, BorderLayout.SOUTH);
        this.getContentPane().setLayout(new BorderLayout());
        //messagePanel is in the CENTER, detailesPane gets added to the SOUTH of content pane
        this.getContentPane().add(messagePanel, BorderLayout.CENTER);

        this.pack();
    }

    /**
     * Adds listeners to buttons
     *
     * @param details - detals
     */
    private void addListeners(final String details)
    {
        detailsButton.addActionListener(new ActionListener()
        {
            private JScrollPane pane;

            public void actionPerformed(ActionEvent e)
            {
                if (!open)
                {
                    JTextArea detailsTA = new JTextArea(details);
                    detailsTA.setEditable(false);
                    pane = new JScrollPane(detailsTA);
                    pane.setPreferredSize(new Dimension(400, 200));
                    DetailsMessageBox.this.getContentPane().add(pane, BorderLayout.SOUTH);
                    detailsButton.setText("Details <<");
                    DetailsMessageBox.this.setSize(DetailsMessageBox.this.getSize().width,  DetailsMessageBox.this.getSize().height + 50);
                    DetailsMessageBox.this.pack();
                    open = true;
                }
                else
                {
                    DetailsMessageBox.this.getContentPane().remove(pane);
                    DetailsMessageBox.this.setSize(DetailsMessageBox.this.getSize().width,  DetailsMessageBox.this.getSize().height - 50);
                    detailsButton.setText("Details >>");
                    DetailsMessageBox.this.pack();
                    open = false;
                }
            }
        });

        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                DetailsMessageBox.this.dispose();
            }
        });
    }

    /**
     * Overrides Component.setVisible(). Contains code for centering.
     *
     * @param visible true to display, false to hide
     */
    @Override
    public void setVisible(boolean visible)
    {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension dialogSize = this.getSize();
        this.setLocation(screenSize.width / 2 - dialogSize.width / 2, screenSize.height / 2 - dialogSize.height / 2);

        super.setVisible(visible);
    }
}
