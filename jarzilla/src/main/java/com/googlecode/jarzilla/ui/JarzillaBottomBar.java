/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.ui;

import javax.swing.JLabel;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;

/**
 * Jarzilla bottom bar with label in the center
 *
 * @author rayvanderborght
 */
public class JarzillaBottomBar extends BottomBar
{
    /** */
    private final JLabel itemsIndicator = MacWidgetFactory.createEmphasizedLabel("");

    /** */
    public void setItemCount(Integer itemCount)
    {
        String text = (itemCount == null) ? "" : itemCount + " items";
        this.itemsIndicator.setText(text);
    }

    /** */
    public JarzillaBottomBar(BottomBarSize size)
    {
        super(size);
        this.addComponentToCenter(itemsIndicator);
    }
}
