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
 * Provides a Mutable version of the Double3D class. Beware, using this class
 * as a key in a HashMap is very error-prone.
 *
 * @see com.ridderware.fuse.Double3D
 *
 * @author Jason C. HandUber
 */
public class MutableDouble3D extends Double3D
{
    private static final Logger logger = LogManager.getLogger(MutableDouble3D.class);
    
    /**
     * In general this constructor should not be used. It is here for JINI
     * purposes.
     */
    public MutableDouble3D()
    {
        super();
    }
    
    
    /**
     *  Creates a new instance of MutableDouble3D
     * @param source The Double3D whose location will be used to set this
     * MutableDouble3D's x,y, and z coordinates.
     */
    public MutableDouble3D(Double3D source)
    {
        super(source);
    }
    
    /**
     *  Creates a new instance of MutableDouble3D
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public MutableDouble3D(double x, double y, double z)
    {
        super(x,y,z);
    }
    
    /**
     * Convinience constructor to convert the x and y values from a Double2D into
     * a MutableDouble3D (z is set to 0.0).
     * @param double2d The Double2D which provides the x and y values for this 
     * MutableDouble3D object - z is set to 0.0.
     */
    public MutableDouble3D(Double2D double2d)
    {
        super(double2d);
    }
    
    /**
     * A convinience constructor that takes in the Double2D and uses its x and y values
     * to set this MutableDouble3D's x and y values. Z is expliclity set.
     * @param double2d Contains the x and y value of this new MutableDouble3D object
     * @param z the z-value of this new MutableDouble3D object
     */
    public MutableDouble3D(Double2D double2d, double z)
    {
        super(double2d, z);
    }
    
    /**
     *  Sets the location of this Double3D object with the provided x,y,and
     *  z values. Resets the hashCode back to zero to force a recompute
     *  with these new values.
     * @param x new X location
     * @param y new Y location
     * @param z new Z location
     */
    public void setXYZ(double x, double y, double z)
    {
        hashCode = 0;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     *  Sets the location of this Double3D object with the provided values
     *  from the passed in Double3D object. Resets the hashCode back to zero to
     *  force a recompute with these new values.
     * @param foreign The Double3D object whose values you wish to copy to this
     * Double3D object.
     */
    public void setXYZ(Double3D foreign)
    {
        hashCode = 0;
        this.x = foreign.x;
        this.y = foreign.y;
        this.z = foreign.z;
    }
    
    /**
     *  Sets the X attribute of the Double3D object. Resets the hashCode back
     *  to zero to force a recompute with these new values.
     * @param x the x-component (by convention, east/west)
     */
    public void setX(double x)
    {
        hashCode = 0;
        this.x = x;
    }
    
    
    /**
     *  Sets the Y attribute of the Double3D object. Resets the hashCode back
     *  to zero to force a recompute with these new values.
     * @param y The y-component (by convention, north/south)
     */
    public void setY(double y)
    {
        hashCode = 0;
        this.y = y;
    }
    
    /**
     *  Sets the Z attribute of the Double3D object. Resets the hashCode back
     * to zero to force a recompute with these new values.
     * @param z The z-component (by convention, depth/height)
     */
    public void setZ(double z)
    {
        hashCode = 0;
        this.z = z;
    }
}
