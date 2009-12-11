/*
 * $Id$
 * $URL$
 */
package com.googlecode.jarzilla.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import javax.swing.JComponent;

import com.explodingpixels.macwidgets.MacFontUtils;

/**
 * Supplies a header component with a shiny black background
 *
 * @see http://explodingpixels.wordpress.com/2009/11/13/creating-the-itunes-navigation-header/
 * @author rayvanderborght
 */
@SuppressWarnings("serial")
public class HeaderComponent extends JComponent
{
    // the hard-coded preferred height. ideally this would be derived
    // from the font size.
    private static int HEADER_HEIGHT = 25;

    // the background colors used in the multi-stop gradient.
    private static Color BACKGROUND_COLOR_1 = new Color(0x393939);
    private static Color BACKGROUND_COLOR_2 = new Color(0x2e2e2e);
    private static Color BACKGROUND_COLOR_3 = new Color(0x232323);
    private static Color BACKGROUND_COLOR_4 = new Color(0x282828);

    // the color to use for the top and bottom border.
    private static Color BORDER_COLOR = new Color(0x171717);

    // the inner shadow colors on the top of the header.
    private static Color TOP_SHADOW_COLOR_1 = new Color(0x292929);
    private static Color TOP_SHADOW_COLOR_2 = new Color(0x353535);
    private static Color TOP_SHADOW_COLOR_3 = new Color(0x383838);

    // the inner shadow colors on the bottom of the header.
    private static Color BOTTOM_SHADOW_COLOR_1 = new Color(0x2c2c2c);
    private static Color BOTTOM_SHADOW_COLOR_2 = new Color(0x363636);

    private final String title;

    /** */
    public HeaderComponent(String title)
    {
        this.title = title;
    }

    /** */
    @Override
    public Dimension getPreferredSize()
    {
        return new Dimension(-1, HEADER_HEIGHT);
    }

    /** */
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D graphics = (Graphics2D) g.create();

        // calculate the middle of the area to paint.
        int midY = this.getHeight()/2;

        // paint the top half of the background with the corresponding
        // gradient. note that if we were using Java 6, we could use a
        // LinearGradientPaint with multiple stops.
        Paint topPaint = new GradientPaint(0, 0, BACKGROUND_COLOR_1, 0, midY, BACKGROUND_COLOR_2);
        graphics.setPaint(topPaint);
        graphics.fillRect(0, 0, this.getWidth(), midY);

        // paint the top half of the background with the corresponding
        // gradient.
        Paint bottomPaint = new GradientPaint(0, midY + 1, BACKGROUND_COLOR_3,
                0, this.getHeight(), BACKGROUND_COLOR_4);

        graphics.setPaint(bottomPaint);
        graphics.fillRect(0, midY, this.getWidth(), this.getHeight());

        // draw the top inner shadow.
        graphics.setColor(TOP_SHADOW_COLOR_1);
        graphics.drawLine(0, 1, this.getWidth(), 1);
        graphics.setColor(TOP_SHADOW_COLOR_2);
        graphics.drawLine(0, 2, this.getWidth(), 2);
        graphics.setColor(TOP_SHADOW_COLOR_3);
        graphics.drawLine(0, 3, this.getWidth(), 3);

        // draw the bottom inner shadow.
        graphics.setColor(BOTTOM_SHADOW_COLOR_1);
        graphics.drawLine(0, this.getHeight() - 3, this.getWidth(), this.getHeight() - 3);
        graphics.setColor(BOTTOM_SHADOW_COLOR_2);
        graphics.drawLine(0, this.getHeight() - 2, this.getWidth(), this.getHeight() - 2);

        // draw the top and bottom border.
        graphics.setColor(BORDER_COLOR);
        graphics.drawLine(0, 0, this.getWidth(), 0);
        graphics.drawLine(0, this.getHeight() - 1, this.getWidth(), this.getHeight() - 1);

        graphics.setFont(MacFontUtils.ITUNES_TABLE_HEADER_FONT);
        graphics.setColor(Color.WHITE);
        graphics.drawString(title, 11, 16);

        graphics.dispose();
    }
}