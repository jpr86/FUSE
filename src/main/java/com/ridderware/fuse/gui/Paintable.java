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

import java.awt.Shape;
import java.util.Collection;

/**
 * Any object wishing to be viewable in the fuse.gui system should implement
 * this interface.
 * @see com.ridderware.fuse.gui.Clickable
 * @author Jason C. HandUber
 */
public interface Paintable
{
    /**
     * If an Agent defines a paintType of Clickable, by default his 
     * ReflectorFrame will pop up when he's clicked on.
     * If an Agent defines a paintType of Simple, no system-level action
     * is taken if he's clicked on.
     */
    public static enum PaintType {
        /**
         * If an Agent is of type Simple, then the FUSE GUI system will not take
         * any action if it is clicked on.
         */
        Simple, 
        /**
         * If an agent is of type Clickable, the FUSE GUI will attempt to bring up its
         * Reflector frame if it has defined one
         */
        Clickable};

    /**
     * the max buffer size is a high-ball estimate of the maximum amount of 
     * space between the displayed image's center and its farthest extreme
     * @return the max buffer size
     */
    public int getMaxBufferSize();
    
    /**
     * Every Agent is required to define what type of painting he wishes
     * done.
     * @return An enum value indicating whether or not the FUSE GUI should care if this Agent
     * was clicked on
     */
    public PaintType getPaintType();
    
    /**
     * The Painter singleton should be used to do all painting and its 
     * methods automagically return a bounding Shape for any painted Object.
     * The collection of these bounding shapes should be returned to by any
     * implementor of this interface. This interface also passes any arguments
     * the Paintable was given when added to a particular ViewFrame. This makes
     * it possible to have a different paint method on a ViewFrame by ViewFrame
     * basis. This is particularly useful for Perception/Ground Truth painting.
     * @return A collection of Shapes bounding the painted Object(s)
     * @param args Any arguments specified when adding the Paintable to 
     *  a ViewFrame will be passed back to the Paintable here. 
     */
    public Collection<Shape> paintAgent(Object... args);
}
