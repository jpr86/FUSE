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
package com.ridderware.fuse.examples.SimpleAgent;

import com.ridderware.fuse.*;

/**
 *
 * @author Jeff Ridder
 */
public class SimpleAgentTest 
{

    public static void main(String[] args) 
    {
        
        Scenario scenario = new Scenario();

        scenario.setUniverse(new SimplerUniverse());
        scenario.setStartTime(0.0);
        scenario.setEndTime(3600.0);

        for (int i = 0; i < 7; i++) 
        {
            SimpleAgent agent = new SimpleAgent("A-" + i);
            if ((i % 2) == 0) {
                agent.setColor(SimpleAgent.COLOR_RED);
            } else {
                agent.setColor(SimpleAgent.COLOR_BLUE);
            }
            scenario.addAgent(agent);
        }

        scenario.execute();
    }
}
