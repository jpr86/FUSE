/* %%
 *
 * Fast Universal Simulation Engine (FUSE)
 *
 * Copyright 2014 Jeff Ridder
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ridderware.fuse;

/**
 * The interface implemented by all producers of Agent instances.
 * 
 * @author Jeff Ridder
 */
public interface IAgentFactory
{

    /**
     * Populate the specified universe with any and all agent instances for
     * which this agent factory has responsibility. The simulation framework may
 perform multiple simulation runs. The populateUniverse method may be
 called for each of these runs. The IAgentFactory may populate the universe
 with the same or a different set of agents for each simulation run for
 reasons of its own choosing.
     *
     * @param universe An instance of a simulation Universe.
     */
    public void populateUniverse(Universe universe);

}
