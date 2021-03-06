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

import com.ridderware.fuse.Behavior;

/**
 *
 * @author Jeff Ridder
 */
public class UnorderedBehavior extends Behavior
{

    @Override
    public double getNextScheduledTime(double current_time)
    {
        return current_time + 1.;
    }

    @Override
    public void perform(double current_time)
    {
        System.out.println("Time: "+current_time+" Agent: "+this.getAgent().toString()+" Order: n/a");
    }
}
