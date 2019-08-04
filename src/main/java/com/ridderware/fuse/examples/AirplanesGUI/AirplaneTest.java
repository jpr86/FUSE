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
package com.ridderware.fuse.examples.AirplanesGUI;

import com.ridderware.fuse.*;
import com.ridderware.fuse.gui.*;
import java.awt.Font;

/**
 *
 * @author Jeff Ridder
 */
public class AirplaneTest {

    public static void main(String[] args) {
        Scenario scenario = new Scenario();

        scenario.setStartTime(0.0);
        scenario.setEndTime(9963600.0);

        Cartesian3DSpace space = new Cartesian3DSpace(1000, 1000, 1000);
        GUIUniverse universe = new GUIUniverse(space);
        scenario.setUniverse(universe);

        SimpleView mainView = new SimpleView("Main View");
        universe.addView(mainView);

        FontManager fm = new FontManager();
        Font airplanesTopView = fm.getDefaultFont();

        Airplane maverick = new Airplane("Maverick", airplanesTopView.deriveFont(30F), "\u0046");
        Airplane goose = new Airplane("Goose", airplanesTopView.deriveFont(30F), "\u0047");
        Airplane iceman = new Airplane("Iceman", airplanesTopView.deriveFont(30F), "\u0045");
        Airplane rocky = new Airplane("Rocky", airplanesTopView.deriveFont(30F), "\u0044");
        Airplane bullwinkle = new Airplane("Bullwinkle", airplanesTopView.deriveFont(30F), "\u0042");

        universe.addPaintableAgent(maverick, new ViewFrame[]{mainView});
        universe.addPaintableAgent(goose, new ViewFrame[]{mainView});
        universe.addPaintableAgent(iceman, new ViewFrame[]{mainView});
        universe.addPaintableAgent(rocky, new ViewFrame[]{mainView});
        universe.addPaintableAgent(bullwinkle, new ViewFrame[]{mainView});

        scenario.addAgent(maverick);
        scenario.addAgent(goose);
        scenario.addAgent(iceman);
        scenario.addAgent(rocky);
        scenario.addAgent(bullwinkle);


        scenario.execute();
    }
}
