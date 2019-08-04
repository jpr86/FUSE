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

package com.ridderware.fuse.examples.BugGui;

import java.util.Random;

import org.apache.logging.log4j.*;
import com.ridderware.fuse.Behavior;
import com.ridderware.fuse.Agent;
import com.ridderware.fuse.Double3D;
import com.ridderware.fuse.MutableDouble3D;
import com.ridderware.fuse.gui.Paintable;
import com.ridderware.fuse.gui.Painter;
import com.ridderware.fuse.gui.Reflector;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Collection;

import java.util.HashSet;



/**
 *  The Bug class is an example of how the Agent base class is typically
 *  extended to add additional attributes and Behaviors. In this case, (X, Y)
 *  coordinate attributes are added and a simple "Move" Behavior is added. 
 *  This Bug implements Paintable and Reflector in order to provide
 *  some GUI functionality. 
 */
public class Bug extends Agent implements Paintable, Reflector
{
    private static final Logger logger = LogManager.getLogger(Bug.class);
    
    private final MutableDouble3D position = new MutableDouble3D();
    
    /**
     *  Constructor for the Bug object
     *
     * @param name
     */
    public Bug(String name)
    {
        super(name);
        addBehavior(new Move());
    }
    
    /**
     * Resets this bugs location
     */
    @Override
    public void reset()
    {
        super.reset();
        position.setXYZ(getUniverse().getSpace().getRandomCoordinate());
    }
    
    /**
     * Gets this bug's position in the simulation space
     * @return 
     */
    public Double3D getSimLocation()
    {
        return position;
    }
    
    
    /**
     * This method allows the user to set the Position via the GUI-setter.
     * @param x
     * @param y
     * @param z
     * @return 
     */
    public boolean setPosition(double x, double y, double z)
    {
        boolean error = false;
        if (getUniverse().getSpace().contains(new Double3D(x,y,z)))
        {
            position.setXYZ(x, y, z);
        }
        else
        {
            error = true;
            logger.error("Illegal modification requested [OutOfDefinedSpaceBounds-1]");
            logger.error("Current: "+ this.position.toString()+" requested: "+ position.toString());
        }
        return error;
    }
    
    
    
    @Override
    public String getTitle()
    {
        return getName();
    }
    
    /**
     * Returns the names of the methods we wish to allow the GUI-user to set/get.
     * @return methods ArrayList<String> all method names for GUI interaction
     */
    @Override
    public ArrayList<String> getGUIMethods()
    {
        ArrayList<String> methods = new ArrayList<String>(2);
        methods.add("getPosition");
        methods.add("setPosition");
        return methods;
    }
    
    /**
     * The size between each side and the drawable window. A buffer of size 2,
     * and a window of size 10x10 (area 100), will return a drawable window
     * area of 8x8.
     *
     * @return A buffering size between the window and the drawing space within.
     */
    @Override
    public int getMaxBufferSize()
    {
        return 10;
    }
    
    /**
     *  Gets the Location attribute of the Bug object
     *
     * @return    TBD
     */
    public Double3D getPosition()
    {
        return position;
    }
    
    /**
     * Use Painter to paint your Shape, Painter will return a bounding
     * Shape you have to return to the fuse subsystem.
     * -- Interface docs, return collection of bounding Shapes
     * provided by Painter.
     */
    @Override
    public Collection<Shape> paintAgent(Object... args)
    {
        HashSet<Shape> bounds = new HashSet<>(1);
        bounds.add(Painter.getPainter().paintEllipse2D_Double(5,5, position, true));
        return bounds;
    }

    /**
     * Returns the type of painting we require.
     */
    @Override
    public Paintable.PaintType getPaintType()
    {
        return Paintable.PaintType.Clickable;
    }
    
    /**
     *  A Behavior which defines how bugs move.
     */
    private class Move extends Behavior
    {
        private final Random random = new Random(System.currentTimeMillis() + getName().hashCode());
        
        /**
         *  The simulation framework invokes this method to ask this behavior when
         *  it would like to be scheduled.
         *
         * @param  current_time
         *
         * @return               double second time value at which to run.
         */
        @Override
        public double getNextScheduledTime(double current_time)
        {
            double result = current_time + random.nextDouble() * 100.;
            logger.debug(getName() +" requested next scheduled time @: "+ result +" Current SimTime: "+ getUniverse().getCurrentTime());
            return result;
        }
        
        /**
         *  This method is invoked by the simulation framework when the scheduled
         *  time for this behavior is reached. In this simple example, the dice are
         *  rolled to determine the amount of X,Y bug movement that should occur.
         *
         * @param  current_time
         */
        @Override
        public void perform(double current_time)
        {
            assert(getUniverse().getSpace().contains(position)):"BUG has invalid location (position)";
            
            Double3D temp = position.getCopy();
            
            if (random.nextDouble() > 0.5)
            {
                position.setX((random.nextDouble() < 0.5) ? (position.getX() + 1) : (position.getX() - 1));
            }
            else
            {
                position.setY((random.nextDouble() < 0.5) ? (position.getY() + 1) : (position.getY() - 1));
            }
            
            if (!getUniverse().getSpace().contains(position))
            {
                position.setXYZ(temp);
            }
        }
    }
}
