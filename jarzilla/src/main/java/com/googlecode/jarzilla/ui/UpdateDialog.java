/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import com.googlecode.jarzilla.Jarzilla;

/**
 * The update dialog displayed when there is a new version to download
 *
 * @author rayvanderborght
 */
@SuppressWarnings("serial")
public class UpdateDialog extends JDialog
{
	private JProgressBar progressBar;
	private JPanel controlPanel = new JPanel();
	private JLabel text = new JLabel();
	private JButton okButton = new JButton("OK");
	private JButton notNowButton = new JButton("Not Now");
	private JButton restartButton = new JButton("Restart");

	/** */
	public UpdateDialog()
	{
		super(Jarzilla.getFrame(), "Update");

		this.progressBar = new JProgressBar(0, 100);
		this.progressBar.setValue(0);
		this.progressBar.setVisible(false);

		JPanel messagePanel = new JPanel(new BorderLayout(20, 20));
		messagePanel.setPreferredSize(new Dimension(400, 200));

		text.setText("A new update is available.  Click OK to download.");
		text.setHorizontalAlignment(SwingConstants.CENTER);
		messagePanel.add(text, BorderLayout.NORTH);
		messagePanel.add(this.progressBar, BorderLayout.CENTER);

		controlPanel.add(notNowButton);
		controlPanel.add(okButton);

		messagePanel.add(controlPanel, BorderLayout.SOUTH);
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(messagePanel, BorderLayout.CENTER);

		messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		this.pack();
	}

	/** */
	public void setProgress(int percent)
	{
		this.progressBar.setValue(percent);
	}

	/** */
	public void setProgressVisible(boolean visible)
	{
		this.text.setText("Downloading...");
		this.progressBar.setVisible(visible);
		this.controlPanel.removeAll();
		this.validate();
		this.repaint();
	}

	/** */
	public void setOkActionListener(ActionListener actionListener)
	{
		this.okButton.addActionListener(actionListener);
	}

	/** */
	public void setNotNowActionListener(ActionListener actionListener)
	{
		this.notNowButton.addActionListener(actionListener);
	}

	/** */
	public void setRestartActionListener(ActionListener actionListener)
	{
		this.restartButton.addActionListener(actionListener);
	}

	/** */
	public void confirmRestart()
	{
		this.text.setText("Done");
		this.progressBar.setVisible(false);
		this.controlPanel.removeAll();
		this.controlPanel.add(this.restartButton);
		this.validate();
		this.repaint();
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
