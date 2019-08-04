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
 * Behavior interface. By making this generic we can ensure that agents and
 * behaviors are paired together correctly.
 *
 * @param <T> type of agent owning the behavior.
 * @author Jeff Ridder
 */
public interface IBehavior<T extends IAgent>
{

    /**
     * Get the agent that owns this behavior.
     *
     * @return Agent which owns this behavior.
     */
    public T getAgent();

    /**
     * Reset the state of the behavior to that prior to the start of simulation
     * time. A simulation may be executed multiple times. It is the
     * responsibility of the behavior author to ensure that the behavior returns
     * to a known consistent initial state and is prepared to participate in
     * another simulation run.
     */
    public void reset();

    /**
     * Enable or disable the behavior. If the behavior is disabled, its perform
     * method will not be invoked.
     *
     * @param enabled True to re/enable behavior.
     */
    public void setEnabled(boolean enabled);

    /**
     * Returns the enabled state of the behavior.
     *
     * @return true if enabled.
     */
    public boolean isEnabled();

    /**
     * The perform method represents the core logic of the behavior. The perform
     * method is invoked if the behavior is enabled and has been scheduled.
     *
     * @param current_time The current simulation time tick value.
     */
    public void perform(double current_time);

    /**
     * Given the specified current_time tick value, the behavior should return
     * the next future time tick at which the behavior "perform" method should
     * be invoked.
     *
     * A return value which is less than or equal to the current_time, causes
     * the behavior to be dropped from this scheduling interval but does not
     * disable the behavior. The return value must be a function of only the
     * specified current_time value and must explicitly not be a function of the
     * number of times this method is invoked.
     *
     * @param current_time The current simulation time tick value.
     * @return The time tick at which to run next.
     * @see #perform
     */
    public double getNextScheduledTime(double current_time);
    
    /**
     * Returns the currently scheduled time for this behavior
     * @return scheduled time
     */
    public double getScheduledTime();
    
    /**
     * Sets the scheduled time for the behavior.
     * @param scheduled_time
     */
    public void setScheduledTime(double scheduled_time);
        
    /**
     * A behavior can be associated with only one agent. For efficiency, there
     * is a bidirectional link from an agent to a behavior and from each
     * behavior back to its owning agent. This method is used internally by the
     * framework to complete the link.
     *
     * @param agent
     */
    public void associateWithAgent(T agent);
}
