/*
 * 
 * Fast Universal Simulation Engine (FUSE)
 *
 * Copyright 2014 Jeff Ridder.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ridderware.fuse.gui;

import com.ridderware.fuse.Double3D;
import com.ridderware.fuse.Space;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import org.apache.logging.log4j.*;


/**
 * Handles all painting for the FUSE GUI system
 * @author Jason C. HandUber
 */
public class Painter
{
    private static final Logger logger = LogManager.getLogger(Painter.class);
    private static final Painter one = new Painter();
    
    private int buffer;
    
    private Graphics2D g2d;
    private Rectangle windowBounds;
    private Space space;
    private double m_fact;
    
    private boolean showBuffer = false;
    private boolean showSpace = false;
    private boolean paintBounds = false;
    
    /**
     * To use this function you must pass in the actual bounding shape you want
     * to label (the parent Shape).
     * If the label would force off-screen writing, bounding will try
     */
    public static enum Bounded
    {North, Northeast, East, Southeast, South, Southwest, West, Northwest};
    
    /**
     * Singleton, thus a private Painter
     */
    private Painter()
    {
    }
    
    /**
     * Returns the singleton
     * @return Painter singleton.
     */
    public static Painter getPainter()
    {
        return one;
    }
    
    /**
     * Toggles whether or not to paint the bounding box determined by the max buffer
     * size
     */
    public void togglePaintBounds()
    {
        paintBounds = (paintBounds ? false : true);
    }
    
    /**
     * Toggles whether to display the legal drawing region
     */
    public void toggleBuffer()
    {
        showBuffer = (showBuffer ? false : true);
    }
    
    /**
     * Toggles whether to display the Space object
     */
    public void toggleSpace()
    {
        showSpace = (showSpace ? false : true);
    }
    
    /**
     * Prepaint MUST be called prior to any sequence of repaint events in order
     * to setup the conversion factor between simulation space and drawable
     * space.
     * @param g2d The Graphics2D object on which to paint
     * @param windowBounds The bounds of the enclosing JComponent (typically JPanel). These bounds should
     * have already accounted for the Insets.
     * @param buffer The amount of buffer space between the window space and the
     *               available drawing space.
     * @param space The space object being used in the simulation
     */
    public void prePaint(int buffer, Space space, Graphics2D g2d, Rectangle windowBounds)
    {
        this.space = space;
        this.buffer = buffer;
        this.windowBounds = windowBounds;
        this.g2d = g2d;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        if (showBuffer)
        {
            g2d.setColor(Color.LIGHT_GRAY);
            g2d.drawRect(buffer, buffer, (int)(windowBounds.getWidth() - 2*buffer), (int)(windowBounds.getHeight() - 2 * buffer));
        }
        
        if (showSpace)
        {
            logger.debug("Painting space: "+ space.toString());
            Double3D center = new Double3D(space.getCartesianBounds().getCenterX(),
                    space.getCartesianBounds().getCenterY(), 0.);
            paintRectangle2D_Double(space.getCartesianBounds(), new Color(235,235,235,110), center, true);
        }
        
        double xm_fact= (windowBounds.getWidth()- 2*buffer)/(space.getCartesianBounds().getWidth());
        double ym_fact = ((windowBounds.getHeight()-2*buffer)/space.getCartesianBounds().getHeight());
        m_fact = Math.min(xm_fact, ym_fact);
    }
    
    /**
     * Use this method to draw a String (or char) in the TimesRoman font with a
     * point size of 12.
     * @return a Shape that bounds the painted text.
     * @param text The actual string.
     * @param position The center of the bounds sorrounding the text.
     * @param color The Color of the text
     */
    public Shape paintText(String text, Double3D position, Color color)
    {
        Font fontRoman=new Font("TimesRoman", Font.PLAIN, 12);
        return paintText(text,position,color,fontRoman);
    }
    
    /**
     * Use this method to draw a String (or char) in the Times Roman font with a
     * specified point size.
     * @return a Shape that bounds the painted text.
     * @param text The actual string.
     * @param position The center of the bounds sorrounding the text.
     * @param color The color of the text
     * @param fontPointSize The point size of the font
     */
    public Shape paintText(String text, Double3D position, Color color, int fontPointSize)
    {
        Font fontRoman=new Font("TimesRoman", Font.PLAIN, fontPointSize);
        return paintText(text,position,color,fontRoman);
    }
    
    /**
     * Use this method to specify the Font you want to draw with.
     * @return a Shape that bounds the painted text.
     * @param text The actual String
     * @param position The center of the bounds sorrounding the text.
     * @param color The color of the text
     * @param font The font of the text
     */
    public Shape paintText(String text, Double3D position, Color color, Font font)
    {
        return paintText(text, position, color, font, null);
    }
    
    /**
     * Use this method to specify the font and a rotation angle of the font
     * @return a Shape that bounds the painted text.
     * @param text The actual String
     * @param position The center of the bounds sorrounding the text.
     * @param color The color of the text
     * @param font The font of the text
     * @param rotation The amount of degrees to rotate the font where 90 degrees
     *                 is north and 0 degrees east.
     */
    public Shape paintText(String text, Double3D position, Color color, Font font, Double rotation)
    {
        AffineTransform origAT = g2d.getTransform();
        Font origFont = g2d.getFont();
        Color origColor = g2d.getColor();
        
        double x=0.0,y =0.0;
        Double3D cartesian = space.space2cartesian(position);
        
        //center point (lat/long) on the screen in cartesian
        try
        {
            x = (cartesian.getX()- space.getCartesianBounds().getMinX()) * m_fact + buffer;
            y = windowBounds.getHeight() - ((cartesian.getY()-space.getCartesianBounds().getMinY()) * m_fact + buffer);
        }
        catch(NullPointerException e)
        {
            logger.error("Position: "+ position +" Space: "+ space +" WindowBounds: "+ windowBounds);
        }
        if (rotation!=null)
        {
            rotation = Math.toDegrees(rotation);
            
            if (rotation > 180 && rotation < 270)
            {
                rotation -= 180;
                rotation = 180 - rotation;
                rotation*=-1;
            }
        }
        
        //get the dimensions of the text
        FontRenderContext frc = new FontRenderContext(null,true,true);
        Rectangle2D stringBounds = font.getStringBounds(text, 0, text.length(), frc);
        
        //compute some points of the unrotated font
        double left_x = x - stringBounds.getWidth()/2;
        double right_x = x + stringBounds.getWidth()/2;
        double lower_y = y + stringBounds.getHeight()/2;
        double upper_y = y - stringBounds.getHeight()/2;
        
        AffineTransform at = new AffineTransform();
        if (rotation!=null)
        {
            logger.debug("Rotating by: "+ rotation);
            at.rotate(Math.toRadians(rotation),(int)x, (int)y);
        }
        
        Shape border = new Rectangle2D.Double(left_x,upper_y,stringBounds.getWidth(),stringBounds.getHeight());
        border = at.createTransformedShape(border);
        
        if (paintBounds)
        {
            g2d.setColor(Color.BLACK);
            g2d.draw(border);
        }
        
        //why this doesn't work I don't know
        //font = font.deriveFont(at);
        if (rotation!=null)
        {
            g2d.rotate(Math.toRadians(rotation), x, y);
        }
        
        logger.debug("Drawing text ["+text+"] @ ("+(left_x)+","+(lower_y)+") with font: "+ font.getFontName()+" in size: "+font.getSize()+" in color: "+ g2d.getColor()+" with rotation: "+ rotation +" Cartesian: "+ cartesian);
        g2d.setFont(font);
        g2d.setColor(color);
        g2d.drawString(text,(float)left_x,(float)lower_y);
        
        
        
        g2d.setTransform(origAT);
        g2d.setFont(origFont);
        g2d.setColor(origColor);
        return border;
    }
    
    /**
     * Paints the specified Ellipse2D.Double (fills the space painted with
     * the color Black).
     * @return a Shape that bounds the painted ellipse.
     * @param width The width of the ellipse
     * @param height The height of the ellipse
     * @param position The center of the position of the shape to be drawn.
     */
    public Shape paintEllipse2D_Double(double width, double height, Double3D position)
    {
        return paintEllipse2D_Double(width, height, position, true);
    }
    
    /**
     * Paints the specified Ellipse2D.Double in Black. This method
     * adds the Fill flag option, allowing the user to simply paint the outline
     * of the ellipse if false, or fill the ellipse with Black if true.
     * @return a Shape that bounds the painted ellipse.
     * @param width The width of the ellipse
     * @param height The height of the ellipse
     * @param position The center of the position of the shape to be drawn.
     * @param fill A boolean indicating whether to just paint the outline of the
     *             ellipse or actually fill it.
     */
    public Shape paintEllipse2D_Double(double width, double height, Double3D position, boolean fill)
    {
        return paintEllipse2D_Double(width, height, position, fill, Color.BLACK);
    }
    
    
    /**
     * Paints the specified Ellipse2D.Double in the specified color.
     * @return a Shape that bounds the painted ellipse.
     * @param width The width of the ellipse
     * @param height The height of the ellipse
     * @param position The center of the position of the shape to be drawn.
     * @param fill A boolean indicating whether to just paint the outline of the
     *             ellipse or actually fill it.
     * @param color The color to paint the Ellipse
     */
    public Shape paintEllipse2D_Double(double width, double height, Double3D position, boolean fill, Color color)
    {
        return paintEllipse2D_Double(width, height, position, fill, color, false);
    }
    
    /**
     * Paints the specified Ellipse2D.Double, adds maintainAspectRatio, allowing
     * the user to specify whether or not the the ellipse should grow/shrink
     * if the screen size changes.
     * @return a Shape that bounds the painted ellipse.
     * @param width The width of the ellipse
     * @param height The height of the ellipse
     * @param position The center of the position of the shape to be drawn.
     * @param fill A boolean indicating whether to just paint the outline of the
     *             ellipse or actually fill it.
     * @param color The color to paint the Ellipse
     * @param maintainAspectRatio If true, the same ellipse will be
     *      proportionally larger on a larger window and vice-versa. If false,
     *      the ellipse will always be the same size, regardless of window size.
     */
    public Shape paintEllipse2D_Double(double width, double height, Double3D position, boolean fill, Color color, boolean maintainAspectRatio)
    {
        return paintEllipse2D_Double(width, height, position, fill, color, maintainAspectRatio, 0F);
    }
    
    /**
     * Paints the specified Ellipse2D.Double, adds an alpha value, allowing
     * the user to specify the degree of transparency this ellipse should have.
     * @return a Shape that bounds the painted ellipse.
     * @param width The width of the ellipse
     * @param height The height of the ellipse
     * @param position The center of the position of the shape to be drawn.
     * @param fill A boolean indicating whether to just paint the outline of the
     *             ellipse or actually fill it.
     * @param color The color to paint the Ellipse
     * @param maintainAspectRatio If true, the same ellipse will be
     *      proportionally larger on a larger window and vice-versa. If false,
     *      the ellipse will always be the same size, regardless of window size.
     * @param alpha The level of transparency, 0 being fully opaque and 1.0
     *              being fully transparent.
     */
    public Shape paintEllipse2D_Double(double width, double height, Double3D position, boolean fill,  Color color, boolean maintainAspectRatio, float alpha)
    {
        Composite origComposite = g2d.getComposite();
        Color origColor = g2d.getColor();
        
        g2d.setColor(color);
        
        double x,y;
        
        //get the center in simulation space
        Double3D cartesian = space.space2cartesian(position);
        
        logger.debug("SimPosition: "+ position +" Space: "+ space.toString());
        
        x = (cartesian.getX()-space.getCartesianBounds().getMinX()) * m_fact + buffer;
        y = (cartesian.getY()-space.getCartesianBounds().getMinY()) * m_fact + buffer;
        
        logger.debug("Centered x,y in Cartesian (buffered & mfactored): "+ x +","+y);
        Ellipse2D.Double ellipse = new Ellipse2D.Double();
        
                /*
                 * Now we depart good ol Cartesian and dirty ourselves with
                 * Java's coordinate system. Note: buffers already incorporated.
                 */
        ellipse.y = windowBounds.getHeight() - y;
        
        if (maintainAspectRatio)
        {
            ellipse.width = width * m_fact;
            ellipse.height = height * m_fact;
        }
        else
        {
            ellipse.width = width;
            ellipse.height = height;
        }
        
        //now set the upper left corner of the box
        ellipse.x = x - (ellipse.width/2);
        ellipse.y = ellipse.y - (ellipse.height/2);
        
        
        logger.debug("m_fact"+m_fact+"] X,Y Cartesian: "+ x +","+y);
        logger.debug("Upper-left corner: "+ ellipse.x +","+ellipse.y +" ellipse.h, ellipse.w  "+ ellipse.height+","+ellipse.width+"  window height: "+ windowBounds.getHeight());
        
        logger.debug("Java Space: Ellipse (x,y,w,h): ("+ ellipse.x +","+ ellipse.y+","+ellipse.width+","+ellipse.height+")");
        
        if (alpha!=0)
        {
            AlphaComposite myAlpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha);
            g2d.setComposite(myAlpha);
        }
        
        if (fill)
            g2d.fill(ellipse);
        else
            g2d.draw(ellipse);
        
        if (paintBounds)
        {
            g2d.setColor(Color.BLACK);
            g2d.draw(ellipse);
        }
        
        g2d.setColor(origColor);
        g2d.setComposite(origComposite);
        
        return ellipse;
    }
    
    /**
     * Paints a text box much like a label. Location is relative to the specified
     * Shape.
     * @param parent The labelee
     * @param text The label itself (text)
     * @param bounded The desired location relative to the Shape. If this location would not yield a
     * fully visible label, all other possible anchors are attempted prior to giving
     * up.
     * @return The shape containing the label, or null if the label could not be drawn.
     */
    public Shape paintBoundingTextBox(Shape parent, String text, Bounded bounded)
    {
        Font fontRoman=new Font("TimesRoman", Font.PLAIN, 12);
        return paintBoundingTextBox(parent, text, bounded, fontRoman, Color.BLACK);
    }
    
    /**
     * To use this function you must pass in the actual bounding shape you want
     * to label (the parent Shape).
     * If the label would force off-screen writing, bounding will try
     * @return A shape that encloses the painted TextBox
     * @param font The font
     * @param color The color
     * @param parent The labelee
     * @param text The label itself (text)
     * @param bounded The desired location relative to the Shape. If this location would not yield a
     * fully visible label, all other possible anchors are attempted prior to giving up.
     */
    public Shape paintBoundingTextBox(Shape parent,
            String text,
            Bounded bounded,
            Font font,
            Color color)
    {
        Font origFont = g2d.getFont();
        Color origColor = g2d.getColor();
        
        boolean found = false;
        int tries = 0;
        int index = bounded.ordinal();
        
        double p_xmax = parent.getBounds2D().getMaxY();
        double p_xmin = parent.getBounds2D().getMinX();
        double p_ymax = parent.getBounds2D().getMaxY();
        double p_ymin = parent.getBounds2D().getMinY();
        
        Double left_x=null, lower_y=null;
        
        FontRenderContext frc = new FontRenderContext(null,true,true);
        Rectangle2D stringBounds = font.getStringBounds(text, 0, text.length(), frc);
        
        while (tries++ < Bounded.values().length && !found)
        {
            bounded = Bounded.values()[index++];
            
            if (index > Bounded.values().length-1)
                index = 0;
            
            switch(bounded)
            {
                case North:
                    left_x = parent.getBounds2D().getMinX();
                    lower_y = parent.getBounds2D().getMinY();
                    break;
                    
                case Northeast:
                    left_x = parent.getBounds2D().getMaxX();
                    lower_y = parent.getBounds2D().getMinY();
                    break;
                    
                case East:
                    left_x = parent.getBounds2D().getMaxX();
                    lower_y = parent.getBounds2D().getCenterY();
                    break;
                    
                case Southeast:
                    left_x = parent.getBounds2D().getMaxX();
                    lower_y = parent.getBounds2D().getMaxY() + stringBounds.getBounds2D().getHeight();
                    break;
                    
                case South:
                    left_x = parent.getBounds2D().getMinX();
                    lower_y = parent.getBounds2D().getMaxY() + stringBounds.getBounds2D().getHeight();
                    break;
                    
                case Southwest:
                    left_x = parent.getBounds2D().getMinX() - stringBounds.getBounds2D().getWidth();
                    lower_y = parent.getBounds2D().getMaxY() + stringBounds.getBounds2D().getHeight();
                    break;
                    
                case West:
                    left_x = parent.getBounds2D().getMinX() - stringBounds.getBounds2D().getWidth();
                    lower_y = parent.getBounds2D().getCenterY();
                    break;
                    
                case Northwest:
                    left_x = parent.getBounds2D().getMinX() - stringBounds.getBounds2D().getWidth();
                    lower_y = parent.getBounds2D().getMinY();
                    break;
                    
                default:
                    logger.warn("Unhandled anchor specification: "+ bounded);
                    break;
            }
            
            if (left_x >= windowBounds.getBounds2D().getMinX() &&
                    left_x + stringBounds.getBounds2D().getMaxX() <= windowBounds.getBounds2D().getMaxX() &&
                    lower_y <= windowBounds.getBounds2D().getMaxY() &&
                    lower_y - stringBounds.getBounds2D().getHeight() >= windowBounds.getBounds2D().getMinY())
            {
                found = true;
            }
            else
            {
                
                logger.warn((left_x >= windowBounds.getBounds2D().getMinX())+","+
                        ((left_x + stringBounds.getBounds2D().getMaxX()) <= windowBounds.getBounds2D().getMaxX())+","+
                        (lower_y <= windowBounds.getBounds2D().getMaxY())+","+
                        ((lower_y - stringBounds.getBounds2D().getHeight()) >= windowBounds.getBounds2D().getMinY()));
                
                
                if (index == 0)
                {
                    logger.warn("Unable to anchor @ : "+ Bounded.values()[Bounded.values().length-1]);
                }
                else
                {
                    logger.warn("Unable to anchor @ : "+ Bounded.values()[index - 1]);
                }
                
            }
        }
        
        if (found)
        {
            g2d.setColor(color);
            g2d.setFont(font);
            g2d.drawString(text,left_x.floatValue(),lower_y.floatValue());
            
            logger.debug("Drawing string @ lower_left,lower_y: "+left_x.floatValue()+","+lower_y.floatValue());
            
            stringBounds.setFrame(left_x,
                    lower_y-stringBounds.getHeight(),
                    stringBounds.getWidth(),
                    stringBounds.getHeight());
        }
        else
        {
            stringBounds = null;
            logger.warn("No suitable location found in which to paint: "+ text);
        }
        
        if (paintBounds)
        {
            g2d.setColor(Color.BLACK);
            g2d.draw(stringBounds);
        }
        
        g2d.setFont(origFont);
        g2d.setColor(origColor);
        
        return stringBounds;
    }
    
    
    /**
     * Paints the specified Rectangle.
     * @param rectangle The Rectangle to paint (width & height matter)
     * @param color The color in which to paint the Rectangle.
     * @param location The central location of the Rectangle.
     * @param scale Increase width/height propertional to window size?
     * @return A bounding shape containing the painted Rectangle.
     */
    public Shape paintRectangle(Rectangle rectangle,
            Color color,
            Double3D location,
            boolean scale)
    {
        Rectangle2D.Double r = new Rectangle2D.Double(rectangle.getX(),
                rectangle.getY(),
                rectangle.getWidth(),
                rectangle.getHeight());
        return paintRectangle2D_Double(r, color, location, scale);
    }
    
    /**
     * Paints the specified Rectangle2D.Double
     * @return rectangle bounding shape
     * @param rectangle The Rectangle to paint (width & height matter)
     * @param color The color which to paint the rectangle
     * @param location The center of the rectangle
     * @param scale Increase width/height propertional to window size?
     */
    public Shape paintRectangle2D_Double(Rectangle2D.Double rectangle,
            Color color,
            Double3D location,
            boolean scale)
    {
        logger.debug("Painting rectangle: "+ rectangle);
        
        //center of the rectangle on the screen in Cartesian coord space
        rectangle.x = (location.getX()-space.getCartesianBounds().getMinX()) * m_fact + buffer;
        rectangle.y = (location.getY()-space.getCartesianBounds().getMinY()) * m_fact + buffer;
        
        //center of the rectangle on the screen in JAVA coord space
        rectangle.y = windowBounds.getHeight() - rectangle.y;
        
        if (scale)
        {
            rectangle.width *= m_fact;
            rectangle.height *= m_fact;
        }
        
        logger.debug("Painting rectangle: "+ rectangle);
        
        //upper left corner of the rectangle
        rectangle.x = rectangle.x - (rectangle.width/2);
        rectangle.y = rectangle.y - (rectangle.height/2);
        
        g2d.setColor(color);
        g2d.fill(rectangle);
        
        if (paintBounds)
        {
            g2d.setColor(Color.BLACK);
            g2d.draw(rectangle);
        }
        
        return rectangle;
    }
}
