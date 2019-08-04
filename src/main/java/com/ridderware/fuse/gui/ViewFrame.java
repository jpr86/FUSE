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
import com.ridderware.fuse.Double2D;
import com.ridderware.fuse.Space;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JInternalFrame;
import org.apache.logging.log4j.*;

/**
 * This class should not extend anything in case future simulations
 * require it to extend Agent and insert artificial events in order to
 * update the locations of Agents on a regular, non-event driven basis.
 *
 * In order to extend this class there are two key obligations that must
 * be met.
 * (1) Any mouse click on the actual drawing component (ex. JPanel) should
 *     have a MouseListener that calls this classes "defaultMouseClicked" method.
 * (2) Whereever the paintView method is implemented, the g2b data structure
 *     which maps GUIables to their bounding Shape objects, must be updated.
 * These requirements will be eliminated in the next version.
 *
 * @author Jason C. HandUber
 */
public abstract class ViewFrame
{
    private static final Logger logger = LogManager.getLogger(ViewFrame.class);
    
    private JInternalFrame topLevelJInternalFrame = new JInternalFrame();
    
    /**
     * The amount of space to buffer between the edge of the drawable window
     * and the space which is actually drawn in.
     */
    protected int buffer;
    
    /**
     * The space object
     */
    protected Space space;
    
    /**
     * Will be used in future versions to replace the canvas.
     */
    protected BufferedImage drawingImage;
    
    /**
     * all Paintables that are part of the view
     */
    protected HashSet<Paintable> paintables = new HashSet<Paintable>();
    
    /**
     * all paintables implementing the clickable interface (subset of p2b.keySet)
     */
    protected HashSet<Clickable> clickables = new HashSet<Clickable>();
    
    /**
     * paintables that implement Reflector to their data frames
     */
    protected HashMap<Reflector, ReflectorFrame> r2rFrame = new HashMap<Reflector,ReflectorFrame>();
    
    /**
     * paintables to their arguments (if n/a, they are not in this HashMap)
     */
    protected HashMap<Paintable, Object[]> p2a = new HashMap<Paintable, Object[]>();
    
    /**
     * paintables to their collection of bounding boxes in the device space (window space)
     */
    protected HashMap<Paintable, Collection<Shape>> p2b = new HashMap<Paintable, Collection<Shape>>();
    
    /**
     * Constructor that sets the title/name of this view.
     * @param title the title/name of this ViewFrame
     */
    public ViewFrame(String title)
    {
        topLevelJInternalFrame.setTitle(title);
        topLevelJInternalFrame.setName(title);
    }
    
    /**
     * Future versions will replace this functionality and do a overridable
     * default version of painting in this class
     */
    public abstract void paintPaintables();
    
    /**
     * Returns the bounds of the canvas.
     * TODO: Take Insets into account
     * @return A Rectangle representing the Bounds of the paintable area
     *         of the canvas.
     */
    public abstract Rectangle getWindowBounds();
    
    /**
     * Initializes this Frame's visual components and makes the Frame
     * visible.
     */
    public void initialize()
    {
        initComponents();
    }
    
    /**
     * Sets the space
     * @param space the space
     */
    public void setSpace(Space space)
    {
        this.space = space;
    }
    
    /**
     * Sets the visibility
     * @param visible true to make this ViewFrame visible, false to hide it
     */
    public void setVisible(boolean visible)
    {
        topLevelJInternalFrame.setVisible(visible);
    }
    
    /**
     * Test whether or not this ViewFrame's top level container is visible
     * @return boolean indicating whether the ViewFrame is drawable
     */
    public boolean isDrawable()
    {
        return topLevelJInternalFrame.isVisible();
    }
    
    /**
     * Gets the name/title of this ViewFrame
     * @return the name (or title, they are interchangable) of this ViewFrame
     */
    public String getName()
    {
        return topLevelJInternalFrame.getTitle();
    }
    
    /**
     * Returns the top-level JInternalFrame in which the View is contained
     * @return the frame
     */
    public JInternalFrame getFrame()
    {
        return topLevelJInternalFrame;
    }
    
    /**
     * Adds a class that implements paintable to this view.
     * @param paintable the Paintable to add to this View.
     */
    public void addPaintable(Paintable paintable)
    {
        addPaintable(paintable, new Object[0]);
    }
    
    /**
     * Adds a class that implements paintable to this View and records the specified
     * arguments, calling that classe's paint method with those arguments.
     * @param paintable the paintable to add
     * @param args the arguments that will be passed when paiting the paintable
     */
    public void addPaintable(Paintable paintable, Object... args)
    {      
        synchronized (paintables)
        {
            paintables.add(paintable);
            
            if (paintable.getMaxBufferSize() > buffer)
            {
                buffer = paintable.getMaxBufferSize();
            }
            
            if (args!=null && args.length!=0)
            {
                p2a.put(paintable, args);
            }
            
            if (paintable instanceof Reflector)
            {
                r2rFrame.put((Reflector)paintable, null);
            }
            
            if (paintable.getPaintType() == Paintable.PaintType.Clickable)
            {
                p2b.put(paintable, null);
            }
            
            if (paintable instanceof Clickable)
            {
                clickables.add((Clickable)paintable);
            }
            paintables.notifyAll();
        }     
    }
    
    /**
     * Called prior to any painting (hook)
     */
    protected void prePaint()
    {}
    
    
    /**
     * Call after any painting (hook)
     */
    protected void postPaint()
    {}
    
    private void paintAll()
    {
        prePaint();
        paintPaintables();
        postPaint();
    }
    
    /**
     * Called prior to any updating
     */
    public void preUpdate()
    {
    }
    
    /**
     * This is the main method of ViewFrame. It is called anytime we need
     * to paint or update our displays. Note: this can happen in reaction to
     * a new event, a user-clicks on the screen, anything that could change
     * what the GUI displays.
     */
    public void update()
    {
        //logger.info("----- Pre-Update -----");
        preUpdate();
        paintAll();
        updateDFrames();
        postUpdate();
        //logger.info("----- Post-Update -----");
    }
    
    /**
     * Called after any update (repainting, hook equivalent to postpaint)
     */
    public void postUpdate()
    {}
    
    /*
     TODO: Add initialize auto-determine buffer size
     */
    
    
    /**
     * Updates the getter fields of all visible GUIableDataFrames by invoking
     * their getter methods and updating their respective JTextFields.
     * Then updates the g2b HashMap by calling the abstract paintView method.
     */
    private void updateDFrames()
    {
        for (ReflectorFrame dframe : r2rFrame.values())
        {
            if (dframe!=null && dframe.isVisible())
            {
                dframe.updateSimpleReceiveFields();
            }
        }
    }
    
    
    /**
     * Hides all windows and then sets all class data to null.
     * Currently not used.
     */
    public void cleanUp()
    {
        synchronized (paintables)
        {
            paintables = null;
            paintables.notifyAll();
        }
        clickables = null;
        p2a = null;
        p2b = null;
        drawingImage = null;
        space = null;
        
        for (ReflectorFrame dframe : r2rFrame.values())
        {
            dframe.setVisible(false);
        }
        
        setVisible(false);
        
        r2rFrame = null;
        
        update();
    }
    
    
    /**
     * Makes certain that each GUIable has a visisble GUIableDataFrame.
     */
    public void viewAllGUIablesDataFrames()
    {
        for (Reflector reflector : r2rFrame.keySet())
        {
            displayDataIFrame(reflector);
        }
    }
    
    /**
     * Initializes some Swing components.
     */
    private void initComponents()
    {
        topLevelJInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        topLevelJInternalFrame.setClosable(true);
        topLevelJInternalFrame.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        topLevelJInternalFrame.setMaximizable(true);
        topLevelJInternalFrame.setResizable(true);
        topLevelJInternalFrame.setDoubleBuffered(true);
        topLevelJInternalFrame.setBounds(0, 70, 500, 500);
        //topLevelJInternalFrame.addComponentListener(new MyCompListener());
    }
    
    /**
     * ViewFrame provides this as a default MouseClicked method. You can use
     * it or build your own.
     * @param evt The mouse event
     */
    protected void defaultMouseClicked(java.awt.event.MouseEvent evt)
    {
        double clickX = evt.getPoint().getX();
        double clickY = evt.getPoint().getY();
        
        Paintable target = null;
        Shape bounding = null;
        
        search_paint_clickables:
            for (Paintable paintable : p2b.keySet())
            {
                if (paintable.getPaintType()==Paintable.PaintType.Clickable &&
                        p2b.get(paintable)!=null)
                {
                    for (Shape bounds : p2b.get(paintable))
                    {
                        if (bounds.contains(clickX, clickY))
                        {
                            target = paintable;
                            bounding = bounds;
                            break search_paint_clickables;
                        }
                    }
                }
            }
            
            if (target!=null && target instanceof Reflector)
            {
                displayDataIFrame((Reflector)target);
            }
            
            //space getWindowBounds()
            
            //lets get the cartesian coordinates without any
            //buffers, in simulation space
            double m_fact = (getWindowBounds().getWidth()- 2*buffer)/(space.getDeltaX());
            m_fact = Math.min(m_fact, ((getWindowBounds().getHeight()-2*buffer)/space.getDeltaY()) );
            
            double sim_x = space.getXmin() + ((clickX - buffer) / m_fact);
            double sim_y = space.getYmin() + (( (getWindowBounds().getHeight()-clickY) - buffer) / m_fact);
            
            Double2D click = new Double2D(sim_x,sim_y);
            
            for (Clickable clickable : clickables)
            {
                clickable.mouseClick(evt, click, bounding);
            }
            
            update();
    }
    
    /*********************************
     **
     *  BEWARE: REFLECTION CODE FOLLOWS
     **
     *********************************/
    
    /**
     * Displays the Data Frame belonging to the specified Reflector
     * @param reflector The Reflector
     */
    public void displayDataIFrame(Reflector reflector)
    {
        if (r2rFrame.containsKey(reflector) && r2rFrame.get(reflector)!=null)
        {
            r2rFrame.get(reflector).setVisible(true);
        }
        else
        {
            ArrayList<Method> methods = getSpecifiedMethods(reflector);
            ReflectorFrame reflectorFrame = new ReflectorFrame(methods, reflector);
            
            reflectorFrame.setBounds((int)FUSEGUI.getGUI().getBounds().getWidth() - 210,10, 200, 400);
            FUSEGUI.getGUI().addReflectionFrame(reflectorFrame);
            reflectorFrame.setVisible(true);
            r2rFrame.put(reflector, reflectorFrame);
        }
    }
    
    /**
     *  A little helper method which takes an object and an ArrayList<String>
     *  of method names in that object and returns an Array[Method] containing
     *  those specified methods.
     *  Note: we can't look up method individually, because while we know the
     *  method names we are looking for a-priori, we do not know their
     *  signatures. So we scan all the methods looking for name matches
     *  with valid parameters & returns.
     */
    private ArrayList<Method> getSpecifiedMethods(Reflector reflector)
    {
        ArrayList<String> specifiedStringMethods = reflector.getGUIMethods();
        Method[] allMethods = reflector.getClass().getMethods();
        ArrayList<Method> specifiedMethods = new ArrayList<Method>(allMethods.length);
        
        for (Method method : allMethods)
        {
            logger.debug("Scanning method: "+ method.getName()+" # parameters: "+ method.getParameterTypes().length);
            if (specifiedStringMethods.contains(method.getName()))
            {
                logger.debug("Checking method: "+ method.getName()+" # parameters: "+ method.getParameterTypes().length);
                if (isParameterListValid(method))
                {
                    logger.debug("Recognized method: "+ method.getName());
                    specifiedMethods.add(method);
                }
            }
        }
        return specifiedMethods;
    }
    
    /**
     *  If there are no parameters, return true. If there are parameters,
     *  loop over each of them and make sure they are an instanceof one
     *  of the Main Wrapper classes (Integer, Double, etc.) as well as
     *  String. If they are a custom object (Date), then return false.
     *  Otherwise return true.
     */
    private boolean isParameterListValid(Method method)
    {
        boolean invalidMethod = false;
        Class[] parameters = method.getParameterTypes();
        // Object[] parameters = method.getGenericParameterTypes();
        
        //if none of these are true, I cannot allow the user to input
        //arguments to this method from the GUI.
        for (Class parameter : parameters)
        {
            if (   !(Number.class.isAssignableFrom(parameter))
            && !(String.class.isAssignableFrom(parameter))
            && !(parameter.isPrimitive()))
            {
                invalidMethod = true;
                logger.debug("Invalid parameter found: "+ parameter);
            }
        }
        
        //TODO: Support this method type.
        if (method.getParameterTypes().length == 0 && method.getReturnType() == null)
        {
            invalidMethod = true;
            logger.debug("isParameterListValid [deadbeat]: "+method.getName()+" # args: "+ method.getParameterTypes().length);
        }
        
        int modifier = method.getModifiers();
        
        //furthermore, I will only read public methods.
        if (!Modifier.isPublic(modifier))
        {
            invalidMethod = true;
            //logger.info("isParameterListValid [public method check]: "+method.getName()+" # args: "+ method.getParameterTypes().length);
        }
        
        /*
         *  Overloaded methods like setLocation(Double3D loc) and setLocation(x,
         *  y,z) will throw this warning. Being that user need not specify
         *  method argument types, this functions matches only method names and
         *  ignores non-sensical method arguments (i.e. java.util.Date)
         */
        if (invalidMethod)
        {
            logger.warn("Invalid reflection method ["+ method.getName()+ "] will be ignored.");
            logger.warn("Assuming another method exists with the same name but with a valid signature.");
        }
        
        return !invalidMethod;
    }
}