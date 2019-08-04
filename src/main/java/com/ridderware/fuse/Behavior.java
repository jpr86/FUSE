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

import org.apache.logging.log4j.*;

/**
 * Abstract base class for all Behaviors.
 * @param <T> type of agent this behavior can be associated with.
 * 
 * @author Jeff Ridder
 */
public abstract class Behavior<T extends IAgent> implements IBehavior<T>
{

    // Package scope.  Only used by simulation framework.
    private double scheduled_time;

    private static final Logger logger = LogManager.getLogger(Behavior.class);

    private T owner;
    private boolean is_enabled;

    /**
     * Behavior constructor.
     */
    public Behavior()
    {
        this.scheduled_time = Double.MAX_VALUE;
        this.is_enabled = true;
        this.owner = null;
        init();
    }

    public void setEnabled(boolean enabled)
    {
        if (enabled != is_enabled)
        {
            is_enabled = enabled;
            if (owner != null)
            {
                Universe universe = owner.getUniverse();
                if (universe != null)
                {
                    universe.behaviorChanged(this);
                }
            }
        }
    }

    public boolean isEnabled()
    {
        return is_enabled;
    }

    public T getAgent()
    {
        return this.owner;
    }

    public abstract double getNextScheduledTime(double current_time);

    public abstract void perform(double current_time);
    
    public void setScheduledTime(double scheduled_time)
    {
        this.scheduled_time = scheduled_time;
    }

    public double getScheduledTime()
    {
        return this.scheduled_time;
    }

    public void reset()
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Behavior " + this + " reset.");
        }
        init();
    }

    /**
     * A behavior can be associated with only one agent. For efficiency, there
     * is a bidirectional link from an agent to a behavior and from each
     * behavior back to its owning agent. This method is used internally by the
     * framework to complete the link.
     *
     * @param agent
     */
    public void associateWithAgent(T agent)
    {
        this.owner = agent;
    }

    private void init()
    {
        is_enabled = true;
    }


}
