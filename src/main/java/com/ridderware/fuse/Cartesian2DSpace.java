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

import java.awt.geom.Rectangle2D;
import org.apache.logging.log4j.*;



/**
 * An class representing a static 2-dimensional space, contained within
 * a rectangle. This space is essentially a 3-D space with the z-value forced
 * to be equal to 0.
 */
public class Cartesian2DSpace extends Space
{
    private static final Logger logger = LogManager.getLogger(Cartesian2DSpace.class);
    
    /**
     * Default no-arg, no effects constructor.
     */
    public Cartesian2DSpace()
    {}
    
    /**
     *  Constructor for the Static2DSpace object
     * @param xmax The maximum x-value in the 2D space
     * @param ymax The maximum y-value in the 2D space
     */
    public Cartesian2DSpace(double xmax, double ymax)
    {
        super(0.0,xmax,0.0,ymax,0.0,0.0);
    }
    
    /**
     *  Constructor for the Static2DSpace object
     * @param name The name of the space object
     * @param xmax The maximum x-value in the 2D space
     * @param ymax The maximum y-value in the 2D space
     */
    public Cartesian2DSpace(String name, double xmax, double ymax)
    {
        super(name, 0.0,xmax,0.0,ymax,0.0,0.0);
    }
    
    /**
     *  Constructor for the Simple2DSpace object
     * @param xmin The minimum x-value in the 2D Cartesian space
     * @param ymin The minimum y-value in the 2D Cartesian space
     * @param xmax The maximum x-value in the 2D space
     * @param ymax The maximum y-value in the 2D space
     */
    public Cartesian2DSpace(double xmin, double xmax, double ymin, double ymax)
    {
        super(xmin,xmax,ymin,ymax,0.0,0.0);
    }
    
    /**
     *  Constructor for the Static2DSpace object
     * @param name The name of the space object
     * @param xmin The minimum x-value in the 2D space
     * @param xmax The maximum x-value in the 2D space
     * @param ymin The minimum y-value in the 2D space
     * @param ymax The maximum y-value in the 2D space
     */
    public Cartesian2DSpace(String name, double xmin, double xmax, double ymin, double ymax)
    {
        super(name, xmin,xmax,ymin,ymax,0.0,0.0);
    }
    
    
    /**
     * Returns a random coordinate in the 2D Space as a Double3D (so z=0)
     * @return A Double3D object representing a random coordinate within this space
     */
    public Double3D getRandomCoordinate()
    {
        return new Double3D(random.nextDouble() * getDeltaX() + getXmin(),
                random.nextDouble() * getDeltaY() + getYmin(), 0);
    }
    
    /**
     * Returns the bounds of this space in the cartesian coordinate system.
     * Used primarily for drawing / scaling purposes.
     * @return Rectangle representing this space's bounds on a Cartesian plane
     */
    public Rectangle2D.Double getCartesianBounds()
    {
        return new Rectangle2D.Double(getXmin(), getYmin(), 
                getDeltaX(), getDeltaY());
    }
    
    /**
     * Convert from Cartesian into this space's coordinate system.
     * Ex. If X represents latitude and Y represents longitude then
     * this method would return cartesian.X as Y and cartesian.Y as X.
     * @param cartesian A point in the standard cartesian coordinate system
     * @return The equivalent point in this space's coordinate system
     */
    public Double3D cartesian2space(Double3D cartesian)
    {
        return cartesian;
    }
    
    /**
     * Converts a point in this space's coordinate system to a point in the 
     * standard cartesian coordinate system.
     * @see #cartesian2space
     * @param spacePoint The point in this space's coordinate system
     * @return The equivalent cartesian point of the passed in space point
     */
    public Double3D space2cartesian(Double3D spacePoint)
    {
        return spacePoint;
    }
    
    /**
     * Return false if the Double3D passed has a point outisde of this 
     * Static2DSpace
     * @param point The Double3D point to be tested
     * @return False if the passed Double3D point has a coordinate outside of 
     * this Space's defined bounds, true otherwise.
     */
    public boolean contains(Double3D point)
    {
        if (point.getX() < getXmin() || point.getX() > getXmax() || 
                point.getY() < getYmin() || point.getY() > getYmax() || 
                point.getZ()!=0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
}
