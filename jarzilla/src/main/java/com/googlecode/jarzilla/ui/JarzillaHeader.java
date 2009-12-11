/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.SpringLayout;

import org.jdesktop.swingx.JXBusyLabel;

/**
 * Jarzilla header with busy indicator
 *
 * @author rayvanderborght
 */
@SuppressWarnings("serial")
public class JarzillaHeader extends HeaderComponent
{
    /** */
    private final JXBusyLabel busyIndicator;

    /** */
    public JarzillaHeader(String title)
    {
        super(title);

        SpringLayout layout = new SpringLayout();
        this.setLayout(layout);

        this.busyIndicator = new JXBusyLabel(new Dimension(16, 16));
        this.busyIndicator.getBusyPainter().setBaseColor(new Color(0x2e2e2e));
        this.busyIndicator.getBusyPainter().setHighlightColor(new Color(0xeeeeee));
        layout.putConstraint(SpringLayout.EAST, busyIndicator, -11, SpringLayout.EAST, this);
        layout.putConstraint(SpringLayout.NORTH, busyIndicator, 4, SpringLayout.NORTH, this);
        this.add(busyIndicator);
    }

    /** */
    public void setBusy(boolean busy)
    {
        this.busyIndicator.setVisible(busy);
        this.busyIndicator.setBusy(busy);
    }
}
