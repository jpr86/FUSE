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

package com.ridderware.fuse.examples.OrderedTimeStepTest;

import com.ridderware.fuse.OrderedTimeStepUniverse;
import com.ridderware.fuse.Scenario;

/**
 *
 * @author Jeff Ridder
 */
public class OrderedTimeStepTest
{
        public static void main(String[] args)
    {


        Scenario scenario = new Scenario();
        
        scenario.setUniverse(new OrderedTimeStepUniverse());
        scenario.setStartTime(0.0);
        scenario.setEndTime(10.0);
        
        //  Create and add agents
        SteppingAgent a = new SteppingAgent("Manny");
        scenario.addAgent(a);
        
        SteppingAgent b = new SteppingAgent("Jack");
        scenario.addAgent(b);
        
        SteppingAgent c = new SteppingAgent("Moe");
        scenario.addAgent(c);
//        scenario.setAgentFactory(world);


        for (long i = 0; i < 1; i++)
        {
            //  This is where randomization would happen
            //  Inflation rate, returns, other events, etc.
            //  Execute
            scenario.execute();

        }
    }
}
