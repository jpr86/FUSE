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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import org.apache.logging.log4j.*;

/**
 * SimpleView requests all painting and contains the actual drawing area
 * (the geoPanel).
 * TODO: take Insets into account
 * Will be upgraded with JAVA 1.6
 * @author Jason C. HandUber
 */
public class SimpleView extends ViewFrame
{
    private static final Logger logger = LogManager.getLogger(SimpleView.class);
    
    private GeoPanel geoPanel;
    SimpleView sv = this;
    
    /**
     * Creates a new instance of SimpleView
     * @param name The name of this SimpleView
     */
    public SimpleView(String name)
    {
        super(name);
    }
    
    /**
     * Initializes Swing components.
     */
    public void initialize()
    {
        super.initialize();
        geoPanel = new GeoPanel();
        super.getFrame().add(geoPanel);
    }
    
    /**
     * Causes the JPanel to call it's repaint() method, which in turn
     * calls and prompts all the Paintables to repaint themselves.
     */
    public void paintPaintables()
    {
        this.geoPanel.repaint();
        /*
        geoPanel.paintImmediately((int)geoPanel.getBounds().getX(),
                (int)geoPanel.getBounds().getY(),
                geoPanel.getWidth(),
                geoPanel.getHeight());
         **/
    }
    
    /**
     * Returns the drawing area window bounds
     * @return A rectangle containing the Bounds of the JPanel containing the drawing area
     */
    public Rectangle getWindowBounds()
    {
        return geoPanel.getWindowBounds();
    }
    
    class GeoPanel extends JPanel implements MouseListener
    {
        BufferedImage bImg;
        
        public GeoPanel()
        {
            setBackground(new java.awt.Color(255, 255, 255));
            setLayout(new java.awt.BorderLayout());
            setVisible(true);
            setDoubleBuffered(true);
            addMouseListener(this);
        }
        
        public Rectangle getWindowBounds()
        {
            Insets insets = getInsets();
            Rectangle bounds = this.getBounds();
            bounds.setRect(bounds.getX()-insets.left,
                    bounds.getY()+insets.top,
                    bounds.getWidth()-insets.left-insets.right,
                    bounds.getHeight()-insets.top-insets.bottom);
            return bounds;
        }
        
        /**
         * public BufferedImage getDrawingImage()
         * {
         * bImg = new BufferedImage((int)getWindowBounds().getWidth(),
         * (int)getWindowBounds().getHeight(),
         * BufferedImage.OPAQUE);
         * bImg.getGraphics().setColor(java.awt.Color.YELLOW);
         * bImg.getGraphics().fillRect(0,0,bImg.getWidth(),bImg.getHeight());
         *
         * return bImg;
         * }
         *
         * public void drawImage(BufferedImage image)
         * {
         * bImg = image;
         * //insets TODO
         * //           getGraphics().clearRect(0,0, getWidth(),getHeight());
         * getGraphics().drawImage(image, 0, 0, null);
         * this.paintImmediately(0,0,image.getWidth(), image.getHeight());
         * }
         */
        
        /**
         * Invoked when the mouse button has been clicked (pressed
         * and released) on a component.
         */
        public void mouseClicked(MouseEvent e)
        {
            defaultMouseClicked(e);
        }
        
        /**
         * Invoked when a mouse button has been pressed on a component.
         */
        public void mousePressed(MouseEvent e)
        {
            //defaultMouseClicked(e);
        }
        
        /**
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(MouseEvent e)
        {
        }
        
        /**
         * Invoked when the mouse enters a component.
         */
        public void mouseEntered(MouseEvent e)
        {
        }
        
        /**
         * Invoked when the mouse exits a component.
         */
        public void mouseExited(MouseEvent e)
        {
        }
        
        
        /**
         * Paints the view
         * As of JAVA 6 (1.6), this code should be placed in it's superclass,
         * A bufferedImage should be repainted and set here. Future architecture
         * should allow a user to specify the component's name in which to draw
         * each GUIable within the actual ViewFrame.
         * @param Graphics g
         */
        public synchronized void paint(Graphics g)
        {
            super.paint(g);
            
            Painter.getPainter().prePaint(buffer, space, (Graphics2D)g, getBounds());
            
            synchronized (paintables)
            {
                for (Paintable paintable : paintables)
                {
                    //currently we don't optimize painting and thus don't record
                    //the shapes returned by PaintViewables
                    if (paintable.getPaintType() == Paintable.PaintType.Simple && p2a.containsKey(paintable))
                    {
                        paintable.paintAgent(p2a.get(paintable));
                    }
                    else if (paintable.getPaintType() == Paintable.PaintType.Simple)
                    {
                        paintable.paintAgent();
                    }
                    
                    if (paintable.getPaintType() == Paintable.PaintType.Clickable && p2a.containsKey(paintable))
                    {
                        p2b.put( paintable, paintable.paintAgent(p2a.get(paintable)) );
                    }
                    else if (paintable.getPaintType() == Paintable.PaintType.Clickable)
                    {
                        p2b.put( paintable, paintable.paintAgent() );
                    }
                }
                paintables.notifyAll();
            }
        }
    }
}
