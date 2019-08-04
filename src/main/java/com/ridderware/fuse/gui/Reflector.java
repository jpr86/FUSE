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

import java.util.ArrayList;

/**
 * Any object wishing to use reflection to allow the GUI-user to interact with
 * its methods needs to implement this interface
 * @see com.ridderware.fuse.examples.airplanes_gui.Airplane
 * @author Jason C. HandUber
 */
public interface Reflector
{
    /**
     * Gets the title of the window representing this Reflector
     * @return the title
     */
    public String getTitle();
    
    /**
     * Should return a list of the names of any methods the user desires to be
     * reflected in the GUI.
     * @return list of names of methods
     */
    public ArrayList<String> getGUIMethods();
}
