/*
 * 
 * Fast Universal Simulation Engine (FUSE)
 *
 * Copyright 2006 Jeff Ridder.
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

package com.ridderware.fuse;

import org.apache.logging.log4j.*;

/**
 * Provides a Mutable version of the Double2D class. Beware, using this class
 * as a key in a HashMap is very error-prone.
 *
 * @see com.ridderware.fuse.Double2D
 *
 * @author Jason C. HandUber
 */
public class MutableDouble2D extends Double2D
{
    private static final Logger logger = LogManager.getLogger(MutableDouble2D.class);
    
    /**
     * In general this constructor should not be used. It is here for JINI
     * purposes.
     */
    public MutableDouble2D()
    {
        super();
    }
    
    /**
     * Creates a new instance of MutableDouble2D
     * @param x The initial x-coordinate of this MutableDouble2D
     * @param y The initial y-coordinate of this MutableDouble2D
     */
    public MutableDouble2D(double x, double y)
    {
        super(x,y);
    }
    
    /**
     * Creates a new instance of MutableDouble2D
     * @param source The source of the inital values for this MutableDouble2D
     */
    public MutableDouble2D(Double2D source)
    {
        super(source);
    }
    
    /**
     * Creates a new instance of MutableDouble2D
     * @param point The source of the inital values for this MutableDouble2D
     */
    public MutableDouble2D(java.awt.geom.Point2D point)
    {
        super(point);
    }
    
    /**
     *  Sets the X attribute of the Double2D object
     * @param x the x-component
     */
    public void setX(double x)
    {
        hashCode = 0;
        this.x = x;
    }
    
    /**
     *  Sets the Y attribute of the Double2D object
     * @param y The y-component (
     */
    public void setY(double y)
    {
        hashCode = 0;
        this.y = y;
    }
    
    /**
     *  Sets the X and Y coordinates of this Double2D.
     * @param x new X location
     * @param y new Y location
     */
    public void setXY(double x, double y)
    {
        hashCode = 0;
        this.x = x;
        this.y = y;
    }
    
    /**
     *  Sets the X and Y coordinates of this Double2D using the coordinates
     *  provided by the specified Double2D.
     * @param foreign The Double2D whose X and Y coordinates will be used to
     * update this Double2D's X and Y coordinates.
     */
    public void setXY(Double2D foreign)
    {
        hashCode = 0;
        this.x = foreign.x;
        this.y = foreign.y;
    }
}
