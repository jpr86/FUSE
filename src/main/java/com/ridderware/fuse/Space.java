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

package com.ridderware.fuse;

import com.ridderware.jrandom.MersenneTwisterFast;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashSet;
import org.apache.logging.log4j.*;


/**
 * The Space class. All classes that define a physical space in the simulation
 * should extend, directly or indirectly, this class. Spaces are currently
 * and, for the foreseeable future, limited to taking the form of an arbitrary
 * cube.
 * @see com.ridderware.fuse.Cartesian2DSpace
 * @see com.ridderware.fuse.Cartesian3DSpace
 * @author Jason C. HandUber
 */
public abstract class Space implements java.io.Serializable
{
    private static final Logger logger = LogManager.getLogger(Space.class);
    
    /**
     * The name of this space - entirely optional.
     */
    protected String name;
    
    /**
     * The minimum x-value of this space.
     */
    private double xmin;
    
    /**
     * The maximum x-value of this space.
     */
    private double xmax;
    
    /**
     * The minimum y-value of this space.
     */
    private double ymin;
    
    /**
     * The minimum y-value of this space.
     */
    private double ymax;
    
    /**
     * The minimum z-value of this space.
     */
    private double zmin;
    
    /**
     * The maximum z-value of this space.
     */
    private double zmax;
    
    /**
     * The random number generator available to be used by all subclasses.
     */
    protected MersenneTwisterFast random = new MersenneTwisterFast();
    
    /**
     * The default no-args, no-effects constructor.
     * Typically not used by the programmer.
     */
    public Space()
    {}
    
    /**
     * Creates a Space object where the minimum X,Y, and Z values are 0.0 and the
     * maximum X,Y, and Z values are taken as arguments to this constructor.
     * @param xmax This space's maximum X-value
     * @param ymax This space's maximum Y-value
     * @param zmax This space's maximum Z-value
     */
    public Space(double xmax, double ymax, double zmax)
    {
        setXmin(0.0);
        setXmax(xmax);
        setYmin(0.0);
        setYmax(ymax);
        setZmin(0.0);
        setZmax(zmax);
    }
    
    /**
     * Creates a Space object with a name, where the minimum X,Y, and Z values are 0.0
     * and the maximum X,Y, and Z values are taken as arguments to this constructor.
     * @param name The name of this space object
     * @param xmax The maximum x-value of this space object
     * @param ymax The maximum y-value of this space object
     * @param zmax The maximum z-value of this space object
     */
    public Space(String name, double xmax, double ymax, double zmax)
    {
        setName(name);
        setXmin(0.0);
        setXmax(xmax);
        setYmin(0.0);
        setYmax(ymax);
        setZmin(0.0);
        setZmax(zmax);
    }
    
    /**
     * Creates a Space object where the minimum and maximum X,Y, and Z values are taken
     * as arguments to this constructor.
     * @param xmin This space's minimum x-value
     * @param xmax This space's maximum x-value
     * @param ymin This space's minimum y-value
     * @param ymax This space's maximum y-value
     * @param zmin This space's minimum z-value
     * @param zmax This space's maximum z-value
     */
    public Space(double xmin, double xmax,
            double ymin, double ymax,
            double zmin, double zmax)
    {
        setXmin(xmin);
        setXmax(xmax);
        setYmin(ymin);
        setYmax(ymax);
        setZmin(zmin);
        setZmax(zmax);
    }
    
    /**
     * Creates a Space object with the specified name in which the minimum and
     * maximum X,Y, and Z values are taken as arguments to this constructor.
     * @param name The name of the space
     * @param xmin This space's minimum x-value
     * @param xmax This space's maximum x-value
     * @param ymin This space's minimum y-value
     * @param ymax This space's maximum y-value
     * @param zmin This space's minimum z-value
     * @param zmax This space's maximum z-value
     */
    public Space(String name,
            double xmin, double xmax,
            double ymin, double ymax,
            double zmin, double zmax)
    {
        setName(name);
        setXmin(xmin);
        setXmax(xmax);
        setYmin(ymin);
        setYmax(ymax);
        setZmin(zmin);
        setZmax(zmax);
    }
    
    /**
     * Returns the bounds of this space in the cartesian coordinate system.
     * Used primarily for drawing / scaling purposes.
     * @return Rectangle2D.Double representing this space's bounds on a
     * Cartesian plane
     */
    public abstract Rectangle2D.Double getCartesianBounds();
    
    /**
     * Convert from Cartesian into this space's coordinate system.
     * Ex. If X represents latitude and Y represents longitude then
     * this method would return cartesian.X as Y and cartesian.Y as X.
     * @param cartesian A point in the standard cartesian coordinate system
     * @return The equivalent point in this space's coordinate system
     */
    public abstract Double3D cartesian2space(Double3D cartesian);
    
    /**
     * Converts a point in this space's coordinate system to a point in the standard
     * cartesian coordinate system.
     * @see #cartesian2space
     * @param spacePoint The point in this space's coordinate system
     * @return The equivalent cartesian point of the passed in space point
     */
    public abstract Double3D space2cartesian(Double3D spacePoint);
    
    /**
     * Is this point contained within this space.
     * @return boolean indicating whether point is contained in this space.
     * @param point The point to test
     */
    public abstract boolean contains(Double3D point);
    
    
    /**
     * Returns a Double3D object representing a random coordinate within the space
     * @return The random Double3D point within the space
     */
    public abstract Double3D getRandomCoordinate();
    
    /**
     * Test whether a Collection of points is contained within this space.
     * All violating points are returned in a HashSet<Double3D>.
     *
     * @param points : the Collection of points to test
     * @return HashSet<Double3D> the HashSet of violating out-of-space
     *  points.
     */
    public HashSet<Double3D> contains(Collection<Double3D> points)
    {
        HashSet<Double3D> violators = new HashSet<Double3D>();
        for (Double3D point : points)
        {
            if (!this.contains(point))
            {
                violators.add(point);
            }
        }
        return violators;
    }
    
    /**
     * The difference between getXmax() and getXmin()
     * @return getXmax()-getXmin()
     */
    public double getDeltaX()
    {
        return getXmax()-getXmin();
    }
    
    /**
     * Returns the difference between getYmax() and getYmin()
     * @return getYmax()-getYmin()
     */
    public double getDeltaY()
    {
        return getYmax()-getYmin();
    }
    
    /**
     * Returns the difference between getZmax and getZmin
     * @return getZmax() - getZmin()
     */
    public double getDeltaZ()
    {
        return getZmax() - getZmin();
    }
    
    /**
     * Returns a string representing this space that is easy to parse.
     * @return String, easy-to-parse using String.split["\t"]. String is
     * composed of getName(), getXmin(), getXmax(), getYmin(), getYmax(),
     * getZmin(), getZmax(). Tabs seperating all entries.
     */
    public String ezParseString()
    {
        return getName()+"\t"+
                getXmin() +"\t"+
                getXmax() +"\t"+
                getYmin() +"\t"+
                getYmax() +"\t"+
                getZmin() +"\t"+
                getZmax();
    }
    
    /**
     * Returns an easy to read string
     * @return String, human-readable description of this Space, in the form:
     * Name[] { (Xmin[] Xmax[]) , (Ymin[] Ymax[]) , (Zmin[] Zmax[]) }
     */
    public String toString()
    {
        return "Name["+getName()+"] { (Xmin["+getXmin()+"] Xmax["+getXmax()+
                "]) , (Ymin["+ getYmin() +"] Ymax["+ getYmax()+"]) , (Zmin["+
                getZmin()+"] Zmax["+ getZmax() +"]) }";
    }
    
    /**
     * Returns the size of the largest dimension (width, height, depth)
     * @return returns the size of the largest dimension
     */
    public double getMaxDimensionSize()
    {
        return Math.max(getDeltaZ(),Math.max(getDeltaY(), getDeltaX()));
    }
    
    /**
     * Sets Name
     * @param name This space's new name.
     */
    public void setName( String name )
    {
        this.name = name;
    }
    
    /**
     * Returns Name
     *
     * @return    the name of this Space, if defined, otherwise, null.
     */
    public String getName( )
    {
        return name;
    }
    
    /**
     * Sets Xmin
     * @param xmin The minimum x-value
     */
    public void setXmin( double xmin )
    {
        this.xmin = xmin;
    }
    
    /**
     * Returns Xmin
     *
     * @return    a  double
     */
    public double getXmin( )
    {
        return xmin;
    }
    
    /**
     * Sets Xmax
     * @param xmax The maximum X value
     */
    public void setXmax( double xmax )
    {
        this.xmax = xmax;
    }
    
    /**
     * Returns Xmax
     *
     * @return    a  double
     */
    public double getXmax( )
    {
        return xmax;
    }
    
    /**
     * Sets Ymin
     * @param ymin The minimum y-value
     */
    public void setYmin( double ymin )
    {
        this.ymin = ymin;
    }
    
    /**
     * Returns Ymin
     *
     * @return    a  double
     */
    public double getYmin( )
    {
        return ymin;
    }
    
    /**
     * Sets Ymax
     * @param ymax The maximum y-value
     */
    public void setYmax( double ymax )
    {
        this.ymax = ymax;
    }
    
    /**
     * Returns Ymax
     *
     * @return    a  double
     */
    public double getYmax()
    {
        return ymax;
    }
    
    /**
     * Sets Zmin
     * @param zmin The minimum z-value
     */
    public void setZmin( double zmin )
    {
        this.zmin = zmin;
    }
    
    /**
     * Returns Zmin
     *
     * @return    a  double
     */
    public double getZmin( )
    {
        return zmin;
    }
    
    /**
     * Sets Zmax
     * @param zmax The maximum z-value.
     */
    public void setZmax( double zmax )
    {
        this.zmax = zmax;
    }
    
    /**
     * Returns Zmax
     *
     * @return    a  double
     */
    public double getZmax( )
    {
        return zmax;
    }
}
