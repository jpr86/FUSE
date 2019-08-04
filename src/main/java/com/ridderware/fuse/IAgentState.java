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
package com.ridderware.fuse;

/**
 * AgentState interface. By making this generic we can ensure that agents and
 * states are paired together correctly.
 *
 * @param <T> type of agent owning the agent state.
 * @author Jeff Ridder
 */
public interface IAgentState<T extends IAgent> 
{

    /**
     *  Returns the agent that was registered when this state was created.
     *
     * @return    Agent which owns this state
     */
    public T getAgent();
        
    /**
     * The name of this state.
     *
     * @return String representation of state name.
     */
    public String getName();
    
    /**
     * A state can be associated with only one agent. For efficiency, there
     * is a bidirectional link from an agent to a state and from each
     * state back to its owning agent. This method is used internally by the
     * framework to complete the link.
     *
     * @param agent
     */
    public void associateWithAgent(T agent);
    
}
