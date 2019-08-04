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

import com.ridderware.fuse.*;
import com.ridderware.fuse.gui.GUIUniverse;
import com.ridderware.fuse.gui.SimpleView;

/**
 *
 * @author Jeff Ridder
 */
public class BugGUITest {

    public static void main(String[] args) {
        Scenario scenario = new Scenario();

        Cartesian2DSpace space = new Cartesian2DSpace("Bug Space", 0., 100., 0., 200.);
        GUIUniverse universe = new GUIUniverse(space);
        scenario.setUniverse(universe);
        scenario.setStartTime(0.0);
        scenario.setEndTime(30000.0);
        SimpleView mainView = new SimpleView("Main View");
        universe.addView(mainView);

        Bug manny = new Bug("Manny");
        Bug moe = new Bug("Moe");
        Bug jack = new Bug("Jack");

        universe.addPaintableAgent(manny, "Main View");
        universe.addPaintableAgent(moe, "Main View");
        universe.addPaintableAgent(jack, "Main View");
        
        scenario.addAgent(manny);
        scenario.addAgent(moe);
        scenario.addAgent(jack);

        scenario.execute();
    }
}
