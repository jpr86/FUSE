/*
 * 
 * Fast Universal Simulation Engine (FUSE)
 *
 * Copyright 2019 Jeff Ridder.
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
package com.ridderware.fuse.examples.AirplanesGUI;

import java.util.Random;

import org.apache.logging.log4j.*;
import com.ridderware.fuse.Behavior;
import com.ridderware.fuse.Agent;
import com.ridderware.fuse.Double3D;
import com.ridderware.fuse.MutableDouble3D;
import com.ridderware.fuse.gui.Paintable;
import java.awt.Color;
import com.ridderware.fuse.gui.Painter;
import com.ridderware.fuse.gui.Reflector;
import java.awt.Font;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


/**
 * A more complicated example of a GUI'd Agent. This Agent is clickable and
 * uses the default clicking behavior when added to a SimpleView. That is,
 * whenever the Agent is clicked on (determined by the Collection of Shapes
 * returned in paintClickable) a ReflectorFrame containing the methods defined
 * in getGUIMethods() is made available for user-interaction via the GUI.
 */
public class Airplane extends Agent implements Paintable, Reflector
{
    private static final Logger logger = LogManager.getLogger(Airplane.class);
    
    private MutableDouble3D position = new MutableDouble3D();
    private double heading = 0;
    private Font airplaneTTF;
    private String fontSymbol;
    
    /**
     * Constructor for the Airplane object
     * @param name The name of the Airplane.
     * @param airplaneTTF You can use FontManager to get the default true-type font.
     * @param fontSymbol The character or unicode value that maps to the symbol that you wish this
     * airplane to be viewed as.
     */
    public Airplane(String name, Font airplaneTTF, String fontSymbol)
    {
        super(name);
        this.airplaneTTF = airplaneTTF;
        this.fontSymbol = fontSymbol;
        addBehavior(new Move());
    }
    
    /**********************************************************************/
    /** These Methods exist primarily to support the Reflector Interface **/
    /**********************************************************************/
    
    /**
     * A simple getter for use with this Agent's ReflectorFrame.
     * @return a string-representation of this Agent's location
     */
    public String getPosition()
    {
        return position.toString();
    }
    
    /**
     * A simple getter for use with this Agent's ReflectorFrame.
     * @return this agent's heading
     */
    public double getHeading()
    {
        return heading;
    }
    
    /**
     * A simple setter for use with this Agent's ReflectorFrame.
     * Careful! No error checking, you could set his location way out
     * of the defined Space and thus he would not be visible in the GUI!
     * @param x The agent's new x-location
     * @param y The agent's new y-location
     * @param z The agent's new z-location
     */
    public void setLocation(double x, double y, double z)
    {
        position.setXYZ(x, y, z);
    }
    
    /**
     * Returns a list of the actual method names available for user-interaction
     * in this Agent's ReflectorFrame.
     */
    public java.util.ArrayList<String> getGUIMethods()
    {
        ArrayList<String> methodNames = new ArrayList<String>(3);
        methodNames.add("getPosition");
        methodNames.add("getHeading");
        methodNames.add("setLocation");
        return methodNames;
    }
    
    /**
     * Returns the title to name this Agent's ReflectorFrame.
     *
     * @return the title of this agent's ReflectorFrame.
     */
    public String getTitle()
    {
        return getName()+"'s ReflectorFrame";
    }
    
    
    /**
     * Resets the airplanes position.
     */
    public void reset()
    {
        position.setXYZ(getUniverse().getSpace().getRandomCoordinate());
    }
    
    /**************************************************************************/
    /* These Methods exist primarily to support the PaintClickable Interface  */
    /* and the abstract Paintable Interface                                   */
    /**************************************************************************/
    
    /**
     * Future versions of FUSEGUI will not require this method. Currently it
     * just returns the maximum space between the center of the Symbol and it's
     * extremity or a high-ball estimate thereof.
     * @return 35
     */
    public int getMaxBufferSize()
    {
        return 35;
    }
    
    /**
     *
     * @param args Any arguments supplied when this Paintable was assigned to
     *             the ViewFrame in which it is now being painted.
     * @return a Collection of Shapes that bound any area we wish to represent
     *         this clickable in the GUI subsystem.
     */
    public Collection<Shape> paintAgent(Object... args)
    {
        HashSet<Shape> boundingShapes = new HashSet<Shape>(1);
        boundingShapes.add(Painter.getPainter().paintText(fontSymbol, position, Color.BLACK, airplaneTTF, heading));
        return boundingShapes;
    }

    /**
     * Return the type of painting this agent should perform.
     * returning Clickable results in this Agent's ReflectorFrame to be
     * displayed when the Agent is clicked on.
     * @return Paintable.PaintType The painting type.
     */
    public com.ridderware.fuse.gui.Paintable.PaintType getPaintType()
    {
        return Paintable.PaintType.Clickable;
    }
    
    /**********************************************************************/
    /** This class exists to support the basic Agent functionality       **/
    /** required by the FUSE  framework.                                 **/
    /**********************************************************************/
    
    /**
     *  A Behavior which defines how bugs move.
     */
    private class Move extends Behavior
    {
        
        private Random random = new Random(System.currentTimeMillis() + getName().hashCode());
        
        
        /**
         *  The simulation framework invokes this method to ask this behavior when
         *  it would like to be scheduled.
         *
         * @param  current_time
         * @return               double second time value at which to run.
         */
        public double getNextScheduledTime(double current_time)
        {
            //set deltatime = universe.delay
            double delta_time = random.nextDouble() * 100.;
            double result = current_time + delta_time;
            return result;
        }
        
        
        /**
         *  This method is invoked by the simulation framework when the scheduled
         *  time for this behavior is reached. In this simple example, the dice are
         *  rolled to determine the amount of X,Y plane movement that should occur.
         *  Note: A Dynamic Space is used, so there is no need to enforce checks
         *  to make certain that this agent remains within any limits.
         *
         * @param  current_time
         */
        public void perform(double current_time)
        {
            Double3D temp = position.getCopy();
            
            if (random.nextDouble() > 0.5)
            {
                position.setX((random.nextDouble() < 0.5) ? (position.getX() + 10) : (position.getX() - 10));
            }
            else
            {
                position.setY((random.nextDouble() < 0.5) ? (position.getY() + 10) : (position.getY() - 10));
            }
            
            double origHeading = heading;
            double deltaHeading = random.nextDouble() * 25;
            heading = ((random.nextDouble() < .5) ? (heading + deltaHeading) : (heading - deltaHeading));
            
            if (heading < 0.0 || heading >= 360.0)
                heading = origHeading;
            
            if (!getUniverse().getSpace().contains(position))
            {
                position.setXYZ(temp);
            }
        }
    }
}
