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

import org.apache.logging.log4j.*;

/**
 * A convenient encapsulation of an agent's state. An instance of a state
 * belongs to one and only one agent. States have names which, in general,
 * should only be used for logging and traceability purposes. Multiple state
 * instances with the same name are NOT the same state and should be avoided.
 *
 * @author Jeff Ridder
 */
public class AgentState implements IAgentState {

    /**
     */
    public static final AgentState UNDEFINED = new AgentState("UNDEFINED");

    private static final Logger logger = LogManager.getLogger(AgentState.class);

    private String name = null;
    private IAgent agent = null;
    private String description = null;

    public String getStateDesc() {
        return description;
    }

    /**
     * Constructor for the State object
     *
     * @param name
     */
    public AgentState(String name) {
        this.name = name;
        this.description = "[" + name + "]";
    }

    /**
     * The name of this state.
     *
     * @return String representation of state name.
     */
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public IAgent getAgent() {
        return this.agent;
    }

    @Override
    public String toString() {
        return this.description;
    }

    @Override
    public void associateWithAgent(IAgent agent) {
        this.agent = agent;
        String agent_prefix = (agent == null) ? "" : agent.getName() + ": ";
        this.description = "[" + agent_prefix + name + "]";
    }

}
