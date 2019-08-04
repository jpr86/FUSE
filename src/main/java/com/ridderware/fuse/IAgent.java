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

import java.util.Set;

/**
 * Interface for a basic agent with behaviors.
 *
 * @author Jeff Ridder
 */
public interface IAgent {

    /**
     * Adds a behavior to the set of agent behaviors.
     *
     * @param behavior behavior
     */
    public void addBehavior(IBehavior<? extends IAgent> behavior);

    /**
     * Add the specified state to this agent.
     *
     * @param state The State to add.
     */
    public void addState(IAgentState<? extends IAgent> state);

    /**
     * Resets the agent to its initial, t=0 state prior to each simulation run.
     */
    public void reset();
    
    /**
     * Used internally by simulation framework to clear bookkeeping state of
     * agent prior to its participation in a simulation run.
     */
    public void init();
    
    /**
     * Returns the set of agent behaviors.
     *
     * @return behavior set
     */
    public Set<IBehavior<? extends IAgent>> getBehaviors();

    /**
     * Returns the set of agent states defined for this agent.
     *
     * @return set of States.
     */
    public Set<IAgentState<? extends IAgent>> getStates();
    
    /**
     * Get agent initial state time. Package scope. Available for internal use
     * by FUSE package only.
     *
     * @return StateTime
     */
    public StateTime getInitialStateTime();
        
    /**
     * Agents should invoke this method zero or one times during reset to define
     * the initial state of the agent and the simulated time at which the agent
     * entered that state. The state must be non-null and the time can not be in
     * the future.
     *
     * @param initial_state_time The initial state time defined by StateTime.
     * The StateTime object must not be null and it must satisfy the
     * StateTime.isValid() test.
     * @see #reset
     */
    public void setInitialStateTime(StateTime initial_state_time);
    
    /**
     * A callback to this agent to tell it that a previous state transition
     * request from old_state to new_state has been completed. Agents request
     * state transitions but the actual transitions are controlled and
     * coordinated by the simulation framework.
     *
     * @param old_state The old state defined by IAgentState
     * @param new_state The new state defined by IAgentState
     * @see #requestNextState
     */
    public void stateChanged(IAgentState old_state, IAgentState new_state);
    
    /**
     * Forces the agent to a particular state. Package scope method to be used
     * only by the simulation framework. Please do not invoke for any other
     * purpose.
     *
     * @param forced_state
     */
    public void transitionToState(IAgentState forced_state);
        
    /**
     * Causes a pending state transition to be realized by copying the pending
     * "next_state" to the "current_state" and then firing a state change event
     * to all registered listeners. This method is reserved for exclusive use by
     * a Universe. Please do not invoke it for any other purpose.
     */
    public void transitionToNextState();
    
    /**
     * Get the name of this agent.
     *
     * @return String name
     */
    public String getName();

    /**
     * The current state of this agent.
     *
     * @return The current State.
     */
    public IAgentState getState();

    /**
     * Returns the universe associated with this agent.
     *
     * @return universe
     */
    public Universe getUniverse();
}
