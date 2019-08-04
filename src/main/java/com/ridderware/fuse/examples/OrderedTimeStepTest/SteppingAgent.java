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

import com.ridderware.fuse.Agent;
import com.ridderware.fuse.IAgent;
import com.ridderware.fuse.IBehavior;

/**
 *
 * @author Jeff Ridder
 */
public class SteppingAgent extends Agent
{

    public SteppingAgent(String name)
    {
        super(name);
        
        for ( int i = 0; i < 5; i++ )
        {
            OrderedTimeStepBehavior t = new OrderedTimeStepBehavior();
            t.setOrder(5-i);
            this.addBehavior(t);
        }
        UnorderedBehavior b = new UnorderedBehavior();
        this.addBehavior(b);
    }

    @Override
    public void reset()
    {
        super.reset(); 
        
        for (IBehavior<? extends IAgent> b : this.getBehaviors())
        {
            b.setEnabled(true);
        }
    }
    
    
    
}
