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

import java.text.DecimalFormat;

/**
 * Provides convenient (Immutable) data structure for a 2-D point and provides
 * various supporting methods (distance calculations, etc).
 *
 * @see com.ridderware.fuse.MutableDouble2D
 *
 * @author Jason C. HandUber
 * @author Jeff Ridder
 */
public class Double2D
{
    /**
     * The immutable x-coordinate of this Double2D
     */
    protected double x;
    
    /**
     * The immutable y-coordinate of this Double2D
     */
    protected double y;
    
    //round to 4 decimal places for the toString() method.
    private DecimalFormat fmt = new DecimalFormat("0.0000");
    
    /**
     * Computed in the hashCode method. Set this value to zero to
     * force a recompute in the hashCode() method. Otherwise, this
     * value remains cached after its first compute.
     * @see #hashCode
     */
    protected volatile int hashCode = 0;
    
    /**
     * No args, no effects constructor. Generally, programmers
     * will not use this constructor. It is here for JINI
     * purposes.
     */
    public Double2D()
    {}
    
    /**
     *  Creates a new instance of Double2D
     * @param source The Double2D whose location will be used to set this
     * Double2D's x and y coordinates.
     */
    public Double2D(Double2D source)
    {
        this.x = source.getX();
        this.y = source.getY();
    }
    
    /**
     *  Creates a new instance of Double2D
     * @param x x component
     * @param y y component
     */
    public Double2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Allows construction of a Double2D given a Double3D, where the z dimension
     * is lost.
     * @param double3d Defines the x and y values used to set this Double2D's location
     */
    public Double2D(Double3D double3d)
    {
        this.x = double3d.getX();
        this.y = double3d.getY();
    }
    
    /**
     * Creates a new instance of Double2D from this provided Point2D.
     * @param point Used to initalize this Double2D
     */
    public Double2D(java.awt.geom.Point2D point)
    {
        this.x = point.getX();
        this.y = point.getY();
    }
    
    /**
     * Computes the mid point between this Double2D and the passed in Double2D
     * @param foreign The Double2D point which we wish to compute to midpoint to
     * (relative to this Double2D point)
     * @return The midpoint between this Double2D point and the passed in
     * Double2D point, returned as a new Double2D
     */
    public Double2D midpoint(Double2D foreign)
    {
        double xC = (x + foreign.x) / 2;
        double yC = (y + foreign.y) / 2;
        return new Double2D(xC, yC);
    }
    
    /**
     * Returns the distance between this point and the specified point.
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The distance from this point to the specified point
     */
    public double distance(double x, double y)
    {
        double del_x = x-this.x;
        double del_y = y-this.y;
        return Math.sqrt(del_x*del_x+del_y*del_y);
    }
    
    /**
     * Returns the distance between this point and the specified point.
     * @param that The foreign point to compute to.
     * @return the distance between this point and the specified point.
     */
    public double distance(Double2D that)
    {
        double del_x = that.x-this.x;
        double del_y = that.y-this.y;
        return Math.sqrt(del_x*del_x+del_y*del_y);
    }
    
    /**
     * Returns the distance between this point and the specified 3D point.  The distance
     * returned will be the distance in the XY plane, not the slant range.  i.e.,
     * it will ignore the Z coordinate of the 3D point.
     * @param that 3D point to compute distance to.
     */
    public double distance(Double3D that)
    {
        double del_x = that.x-this.x;
        double del_y = that.y-this.y;
        return Math.sqrt(del_x*del_x+del_y*del_y);
    }
    
    /**
     * Returns the angle between here to there relative to the y axis.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return angle between here and there in radians relative to y axis.
     */
    public double angle(double x, double y)
    {
        double delta_x = x - this.x;
        double delta_y = y - this.y;
        
        return Math.atan2(delta_x, delta_y);
    }
    
    /**
     * Returns the angle between here to there relative to the y axis.
     *
     * @param that the point with which to compute the angle relative to here.
     * @return angle between here and there in radians relative to y axis.
     */
    public double angle(Double2D that)
    {
        double delta_x = that.x - this.x;
        double delta_y = that.y - this.y;
        
        return Math.atan2(delta_x, delta_y);
    }
      
    /**
     * Returns the angle between here to there relative to the y axis, ignoring the
     * Z coordinate.
     *
     * @param that the point with which to compute the angle relative to here.
     * @return angle between here and there in radians relative to y axis.
     */
    public double angle(Double3D that)
    {
        double delta_x = that.x - this.x;
        double delta_y = that.y - this.y;
        
        return Math.atan2(delta_x, delta_y);
    }
    
    /**
     * Returns the square of the distance between this point and the
     * specified x,y coordinates.
     * @return The distance between this Double2D and the foreign point, squared.
     * @param z The z-coordinate of the foreign point
     * @param x The x-coordinate of the foreign point
     * @param y The y-coordinate of the foreign point
     */
    public double distanceSq(double x, double y, double z)
    {
        double del_x = x-this.x;
        double del_y = y-this.y;
        return del_x*del_x+del_y*del_y;
    }
    
    /**
     * Returns the square of the distance between this Double2D object and the
     * passed in Double2D object.
     * @param that The foreign Double2D object
     * @return The distance between this Double2D and the foreign point, squared.
     */
    public double distanceSq(Double2D that)
    {
        double del_x = that.x-this.x;
        double del_y = that.y-this.y;
        return del_x*del_x+del_y*del_y;
    }
    
    /**
     * Returns the square of the distance between this Double2D object and the
     * passed in Double3D object, ignoring the Z coordinate of the latter.
     * @param that The foreign Double3D object
     * @return The distance between this Double2D and the foreign point, squared.
     */
    public double distanceSq(Double3D that)
    {
        double del_x = that.x-this.x;
        double del_y = that.y-this.y;
        return del_x*del_x+del_y*del_y;
    }
    
    /**
     * Returns true if the given coordinate are within the given range of this
     * point
     * @param range range to check (double)
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return true iff the given point is within the range of the specified coordinates
     */
    public boolean isWithinRange(double range, double x, double y)
    {
        if (this.distance(x,y) <= range)
            return true;
        else
            return false;
    }
    
    /**
     * Provided to phase MASON out.
     * @return String containing coordinates of this point.
     */
    public String toCoordinates()
    {
        return toString();
    }
    
    /**
     * Return true iff the point you pass in is within the range you pass in of
     * this Double2D object
     * @param range range to check (double)
     * @param foreign The point to check against
     * @return True iff the point passed is within the specified range of this
     * point object
     */
    public boolean isWithinRange(double range, Double2D foreign)
    {
        if (this.distance(foreign) <= range)
            return true;
        else
            return false;
    }
    
    /**
     * Return true iff the Double3D point you pass in is within the range you pass in of
     * this Double2D object, ignoring the Z coordinate of the passed in point.
     * @param range range to check (double)
     * @param foreign The point to check against
     * @return True iff the point passed is within the specified range of this
     * point object
     */
    public boolean isWithinRange(double range, Double3D foreign)
    {
        if (this.distance(foreign) <= range)
            return true;
        else
            return false;
    }
    
    /**
     * Returns the coordinates of this point in the following format:
     * (x,y) using DecimalFormat("0.0000") (i.e. one point to the left
     * and four to the right of the decimal will be displayed).
     * @return String containing a formatted representation of the 2 coordinates
     */
    public String toString()
    {
        return ("("+fmt.format(x)+","+ fmt.format(y)+")");
    }
    
    /**
     * Returns a new Double2D object (new memory location) with the same values
     * as the this Double2D object.
     * @return the new Double2D object
     */
    public Double2D getCopy()
    {
        return new Double2D(x,y);
    }
    
    /**
     * Returns true iff the Object passed is a Double2D object and it's
     * coordinates are equal to this objects coordinates (we use
     * Double.doubleToLongBits in order to check for NaN)
     * @param obj Check if the passed Object (obj) is equal to this Double2D object
     * @return True iff the passed Object is a Double2D and has the same
     *         coordinates as this point
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Double2D)
        {
            Double2D that = (Double2D) obj;
            
            return( (Double.doubleToLongBits(getX())==Double.doubleToLongBits(that.getX()))&&
                    (Double.doubleToLongBits(getY())==Double.doubleToLongBits(that.getY())) );
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Effective JAVA, Item #8, Always override hashCode when you override equals
     * We cache our hash code to not have to recompute it. Any mutable extension
     * of this class needs to overwrite this method to avoid caching or reset the
     * hashCode to 0 to force a recompute.  Of course, if you do that you had
     * better make certain this object is not being used as a key in any hash
     * map or it'll be lost.
     *
     * @return int hashCode
     */
    public int hashCode()
    {
        if (hashCode == 0)
        {
            hashCode = 17;
            
            long e = Double.doubleToLongBits(x);
            int a = (int) (e^(e>>>32));
            
            long f = Double.doubleToLongBits(y);
            int b = (int) (f^(f>>>32));
            
            hashCode = 37 * hashCode + a;
            hashCode = 37 * hashCode + b;
        }
        
        return hashCode;
    }
    
    /**
     *  Gets the X attribute of the Double2D object
     * @return the x-component of this object
     */
    public double getX()
    {
        return x;
    }
    
    
    /**
     *  Gets the Y attribute of the Double2D object
     * @return the y-component of this object
     */
    public double getY()
    {
        return y;
    }
}
