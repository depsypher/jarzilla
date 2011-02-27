/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.googlecode.jarzilla.Jarzilla;

/**
 * The help dialog
 *
 * @author rayvanderborght
 */
@SuppressWarnings("serial")
public class HelpDialog extends JDialog
{
	private JPanel controlPanel = new JPanel();
	private JLabel text = new JLabel();
	private JButton okButton = new JButton("OK");
	private final URI uri = makeUri();

	/** */
	public HelpDialog()
	{
		super(Jarzilla.getFrame(), "Help");

		JPanel messagePanel = new JPanel(new BorderLayout(20, 20));
		messagePanel.setPreferredSize(new Dimension(400, 200));

		text.setText("Jarzilla Help");
		text.setHorizontalAlignment(SwingConstants.CENTER);
		messagePanel.add(text, BorderLayout.NORTH);

        JButton button = new JButton();
        button.setText("<HTML>Visit the Jarzilla <FONT color=\"#000099\"><U>wiki</U></FONT> for the latest help documentation.</HTML>");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setOpaque(false);
        button.setBackground(Color.WHITE);
        button.setToolTipText(uri.toString());
        button.addActionListener(new ActionListener()
        {
        	@Override
        	public void actionPerformed(ActionEvent e)
        	{
        		open(uri);
        	}
        });
        messagePanel.add(button);

		controlPanel.add(okButton);

		messagePanel.add(controlPanel, BorderLayout.SOUTH);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(messagePanel, BorderLayout.CENTER);

		messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		this.pack();

		this.okButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				HelpDialog.this.dispose();
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

	/** */
	private static URI makeUri()
	{
		try
		{
			return new URI("http://code.google.com/p/jarzilla/wiki/Jarzilla");
		}
		catch (URISyntaxException e)
		{
			return null;
		}
	}

	/** */
    private static void open(URI uri)
    {
        if (Desktop.isDesktopSupported())
        {
        	try
        	{
        		Desktop.getDesktop().browse(uri);
        	}
        	catch (IOException e) {  }
        }
    }
}
