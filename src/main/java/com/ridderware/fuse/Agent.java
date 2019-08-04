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

import com.ridderware.jrandom.RandomNumberGenerator;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.*;

/**
 * An Agent is the fundamental active entity in a simulation. An agent instance
 * may define and control its own States. An agent instance may define and
 * request the scheduling of Behaviors.
 *
 * @author Jeff Ridder
 */
public class Agent implements IAgent {

    private static final Logger logger = LogManager.getLogger(Agent.class);

    // The name of the agent
    protected String name = null;
    // The current state of the agent
    protected IAgentState current_state = null;
    // The next state of the agent (if one is pending)
    protected IAgentState next_state = null;

    private final HashSet<IBehavior<? extends IAgent>> behaviors = new HashSet<>();
    private final HashSet<IAgentState<? extends IAgent>> states = new HashSet<>();
    private Universe universe = null;
    private boolean is_state_transition_pending = false;
    private StateTime initial_state_time = null;
    private String description = null;

    /**
     * Agent constructor.
     *
     * @param name The name of the Agent
     */
    public Agent(String name) {
        this.name = name;
        description = "[" + name + "]";
    }

    @Override
    public void setInitialStateTime(StateTime initial_state_time) {
        if ((initial_state_time != null) && initial_state_time.isValid()) {
            this.initial_state_time = initial_state_time;
        } else {
            logger.warn("Rejected invalid setInitialStateTime(" + initial_state_time + ").");
        }
    }

    @Override
    public Set<IBehavior<? extends IAgent>> getBehaviors() {
        return behaviors;
    }

    @Override
    public IAgentState getState() {
        return this.current_state;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Universe getUniverse() {
        return this.universe;
    }

    @Override
    public Set<IAgentState<? extends IAgent>> getStates() {
        return this.states;
    }

    /**
     * A convenience method which returns the default RandomNumberGenerator from
     * the Universe in which this Agent exists. An exception will be thrown if
     * the agent is not yet associated with a Universe. Most notably, the random
     * number generator is not defined in an Agent's constructor.
     *
     * @return default random number generator
     */
    public RandomNumberGenerator getRNG() {
        RandomNumberGenerator result = null;
        try {
            result = universe.getDefaultRandomNumberGenerator();
        } catch (Exception e) {
            logger.error("Attempt to reference the default random number generator when none is defined.");
            logger.error("An agent must be associated with a Universe and that Universe must.");
            logger.error("define a default RandomNumberGenerator.");
            logger.error("For example, these conditions are not satisfied in an Agent's constructor.");
        }
        return result;
    }

    @Override
    public void reset() {
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public void stateChanged(IAgentState old_state, IAgentState new_state) {
        if (logger.isDebugEnabled()) {
            logger.debug("stateChanged(" + old_state + ", " + new_state + ") at time " + getUniverse().getCurrentTime() + ".");
        }
    }

    @Override
    public void addBehavior(IBehavior behavior) {
        if (behavior != null) {
            behaviors.add(behavior);
            behavior.associateWithAgent(this);
        }
    }

    @Override
    public void addState(IAgentState state) {
        if (state != null) {
            states.add(state);
            state.associateWithAgent(this);
        }
    }

    /**
     * The agent invokes this method on itself to declare what it desires its
     * next state to be. The actual state transition is synchronized with others
     * and may occur a significant time in the future. The callback method
     * "stateChanged(...)" will be invoked on this agent instance to indicate
     * that the actual transition to a new state has occurred.
     *
     * @param next_state The requested next state.
     * @see #stateChanged
     */
    protected void requestNextState(IAgentState next_state) {
        if ((next_state != null) && (next_state != current_state)) {
            this.next_state = next_state;
            if (!is_state_transition_pending) {
                is_state_transition_pending = true;
                universe.stateTransitionRequestedBy(this);
            }
        }
    }

    @Override
    public StateTime getInitialStateTime() {
        return initial_state_time;
    }

    /**
     * An agent can be associated with only one universe at a time. For
     * efficiency, there is a bidirectional link from a universe to its
     * population of agents and from each agent back to its owning universe.
     * This method is used internally by the framework to complete the link.
     *
     * @param universe
     */
    void associateWithUniverse(Universe universe) {
        this.universe = universe;
    }

    @Override
    public void transitionToNextState() {
        if (is_state_transition_pending) {
            IAgentState old_state = current_state;
            current_state = next_state;
            stateChanged(old_state, current_state);
            is_state_transition_pending = false;
        }
    }

    @Override
    public void transitionToState(IAgentState forced_state) {
        if ((forced_state != null) && (forced_state != current_state)) {
            IAgentState old_state = current_state;
            current_state = forced_state;
            stateChanged(old_state, current_state);
        }
    }

    @Override
    public void init() {
        current_state = AgentState.UNDEFINED;
        next_state = AgentState.UNDEFINED;
        is_state_transition_pending = false;
        initial_state_time = null;
    }
}
