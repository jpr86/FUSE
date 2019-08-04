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
import org.apache.logging.log4j.*;

/**
 * Provides convenient immutable (good for Mappings) data structure for a 3-D
 * point and provides various support functions, such as distance calculations.
 *
 * @see com.ridderware.fuse.MutableDouble3D
 *
 * @author Jason C. HandUber
 */
public class Double3D implements java.io.Serializable
{
    private static final Logger logger = LogManager.getLogger(Double3D.class);
    
    /**
     * The x-coordinate of this Double3D
     */
    protected double x;
    /**
     * The y-coordinate of this Double3D
     */
    protected double y;
    /**
     * The z-coordinate of this Double3D
     */
    protected double z;
    
    //round to 4 decimal places for the toString() method.
    private DecimalFormat fmt = new DecimalFormat("0.0000");
    
    /**
     * The current hashcode of this object, volatile in case used in
     * multi-threaded applications.
     */
    protected volatile int hashCode = 0;
    
    /**
     * No args, no effects constructor. Generally, programmers
     * will not use this constructor (JINI).
     */
    public Double3D()
    {}
    
    /**
     *  Creates a new instance of Double3D
     * @param source The Double3D whose location will be used to set this
     * Double3D's x,y, and z coordinates.
     */
    public Double3D(Double3D source)
    {
        this.x = source.getX();
        this.y = source.getY();
        this.z = source.getZ();
    }
    
    /**
     *  Creates a new instance of Double3D
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public Double3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Convinience constructor to convert the x and y values from a Double2D into
     * a Double3D (z is set to 0.0).
     * @param double2d The Double2D which provides the x and y values for this Double3D object - z is
     * set to 0.0.
     */
    public Double3D(Double2D double2d)
    {
        this.x = double2d.getX();
        this.y = double2d.getY();
        this.z = 0.0;
    }
    
    /**
     * A convinience constructor that takes in the Double2D and uses its x and y values
     * to set this Double3D's x and y values. Z is expliclity set.
     * @param double2d Contains the x and y value of this new Double3D object
     * @param z the z-value of this new Double3D object
     */
    public Double3D(Double2D double2d, double z)
    {
        this.x = double2d.getX();
        this.y = double2d.getY();
        this.z = z;
    }
    
    /**
     * Returns the distance between this point and the specified point.
     * @param x x-coordinate (by convention, East/West)
     * @param y y-coordinate (by convention, North/South)
     * @param z z-coordinate (by convention Height/Depth)
     * @return The distance from this point to the specified point
     */
    public double distance(double x, double y, double z)
    {
        double del_x = x-this.x;
        double del_y = y-this.y;
        double del_z = z-this.z;
        return Math.sqrt(del_x*del_x+del_y*del_y+del_z*del_z);
    }
    
    /**
     *  Gets the Distance attribute of the Double3D object
     * @param that Gets the distance between this Point and the passed passed in (called "<B>that</B>" point)
     * @return Distance between the two points as a double.
     */
    public double distance(Double3D that)
    {        
        double del_x = that.x-this.x;
        double del_y = that.y-this.y;
        double del_z = that.z-this.z;
        return Math.sqrt(del_x*del_x+del_y*del_y+del_z*del_z);
    }
    
    /**
     * Returns the theta angle between here and there relative to the y axis.
     * This is the angle in the XY plane.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     *
     * @return angle between here and there in radians relative to y axis.
     */
    public double angleTheta(double x, double y, double z)
    {
        double delta_x = x - this.x;
        double delta_y = y - this.y;
        
        return Math.atan2(delta_x, delta_y);
    }
    
    /**
     * Returns the theta angle between here and there relative to the y axis.
     * This is the angle in the XY plane.
     *
     * @param that the point with which to compute the angle relative to here.
     * @return angle between here and there in radians relative to y axis.
     */
    public double angleTheta(Double3D that)
    {
        double delta_x = that.x - this.x;
        double delta_y = that.y - this.y;
        
        return Math.atan2(delta_x, delta_y);
    }
    
    /**
     * Returns the phi angle between here and there relative to the XY plane.
     * This is the elevation angle above the XY plane.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @param z z-coordinate
     *
     * @return phi angle between here and there in radians.
     */
    public double anglePhi(double x, double y, double z)
    {
        double del_x = x-this.x;
        double del_y = y-this.y;
        double del_z = z-this.z;
        double delta_xy = Math.sqrt(del_x*del_x+del_y*del_y);
        
        return Math.atan2(del_z, delta_xy);
    }
    /**
     * Returns the phi angle between here and there relative to the XY plane.
     * This is the elevation angle above the XY plane.
     *
     * @param that the point with which to compute the angle relative to here.
     * @return phi angle between here and there in radians.
     */
    public double anglePhi(Double3D that)
    {
        return anglePhi(that.getX(), that.getY(), that.getZ());
    }
    
    /**
     * Returns the square of the distance between this Double3D object and the
     * passed in x,y,z coordinates.
     * @param x The x-coordinate of the foreign point
     * @param y The y-coordinate of the foreign point
     * @param z The z-coordinate of the foreign point
     * @return The distance between this Double3D and the foreign point, squared.
     */
    public double distanceSq(double x, double y, double z)
    {
        double del_x = x-this.x;
        double del_y = y-this.y;
        double del_z = z-this.z;
        return (del_x*del_x+del_y*del_y+del_z*del_z);
    }
    
    /**
     * Returns the square of the distance between this Double3D object and the
     * passed in Double3D object.
     * @param that The foreign Double3D object
     * @return The distance between this Double3D and the foreign point, squared.
     */
    public double distanceSq(Double3D that)
    {
        double del_x = that.x-this.x;
        double del_y = that.y-this.y;
        double del_z = that.z-this.z;
        return (del_x*del_x+del_y*del_y+del_z*del_z);
    }
    
    
    /**
     * Returns true if the given coordinate are within the given range of this point
     * @param range range to check (double)
     * @param x the x-coordinate (by convetion, east/west)
     * @param y the y-coordinate (by convention, north/south)
     * @param z The z-coordinate (by convention, the height or depth)
     * @return true iff the given point is within the range of the specified coordinates
     */
    public boolean isWithinRange(double range, double x, double y, double z)
    {
        if (this.distance(x,y,z) <= range)
            return true;
        else
            return false;
    }
    
    /**
     * Return true iff the point you pass in is within the range you pass in of
     * this Double3D object
     * @param range range to check (double)
     * @param foreign The point to check against
     * @return True iff the point passed is within the specified range of this point object
     */
    public boolean isWithinRange(double range, Double3D foreign)
    {
        if (this.distance(foreign) <= range)
            return true;
        else
            return false;
    }
    
    /**
     * Computes the mid point between this Double3D and the passed in Double3D
     * @param foreign The Double3D point which we wish to compute to midpoint to (relative to this
     * Double3D point)
     * @return The midpoint between this Double3D point and the passed in Double3D point,
     * returned as a new Double3D
     */
    public Double3D midpoint(Double3D foreign)
    {
        double xC = (x + foreign.x) / 2;
        double yC = (y + foreign.y) / 2;
        double zC = (z + foreign.z) / 2;
        return new Double3D(xC, yC, zC);
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
     * Returns the coordinates of this point in the following format:
     * (x,y,z) using DecimalFormat("0.00") (i.e. one point to the left
     * and two to the right of the decimal will be displayed).
     * @return String containing a formatted representation of the 3 coordinates
     */
    public String toString()
    {
        return ("("+fmt.format(x)+","+ fmt.format(y)+","+ fmt.format(z)+")");
    }
    
    /**
     * Returns a formatted String reprsenting x as the latitude,
     * y as the longitude, and z as the altitude in NMi.
     * @return String lat/long/alt string
     */
    public String toLatLongNmiString()
    {
        return (fmt.format(x) +"/u00B0 Latitude "+
                fmt.format(y) + "/u00B0 Longitude "+
                fmt.format(z) + " NMi Altitude");
    }
    
    /**
     * Returns a new Double3D object (new memory location) with the same values
     * as the this Double3D object.
     * @return the new Double3D object
     */
    public Double3D getCopy()
    {
        return new Double3D(x,y,z);
    }
    
    /**
     * Returns true iff the Object passed is an instanceof Double3D and it's
     * coordinates are equal to this objects coordinates (thanks to Mason, we
     * use Double.doubleToLongBits in order to check for NaN)
     * @param obj Check if the passed Object (obj) is equal to this Double3D
     * object
     * @return True iff the passed point has the same coordinates as this point
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Double3D)
        {
            Double3D that = (Double3D) obj;
            
            return( (Double.doubleToLongBits(getX())==Double.doubleToLongBits(that.getX()))&&
                    (Double.doubleToLongBits(getY())==Double.doubleToLongBits(that.getY()))&&
                    (Double.doubleToLongBits(getZ())==Double.doubleToLongBits(that.getZ())) );
        }
        else
        {
            return false;
        }
    }
    
    /**
     * Effective JAVA, Item #8, Always override hashCode when you override equals
     * We cache our hash code to not have to recompute it. Any mutable extension
     * of this class needs overwrite this method to avoid caching or reset
     * the hashCode variable to zero on a change in the x, y, or z value. Of
     * course, if you do that you had better make certain this object is not
     * being used as a key in any hash map or it'll be lost.
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
            
            long g = Double.doubleToLongBits(z);
            int c = (int) (g^(g>>>32));
            
            hashCode = 37 * hashCode + a;
            hashCode = 37 * hashCode + b;
            hashCode = 37 * hashCode + c;
        }
        
        return hashCode;
    }
    
    /**
     *  Gets the X attribute of the Double3D object
     * @return the x-component of this object
     */
    public double getX()
    {
        return x;
    }
    
    
    /**
     *  Gets the Y attribute of the Double3D object
     * @return the y-component of this object
     */
    public double getY()
    {
        return y;
    }
    
    
    /**
     *  Gets the Z attribute of the Double3D object
     * @return the z-component of this object
     */
    public double getZ()
    {
        return z;
    }
}
