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

import com.ridderware.jrandom.MersenneTwisterFast;
import com.ridderware.jrandom.RandomNumberGenerator;

/**
 * A Universe defines time boundaries and a scheduling policy for a collection
 * of agents which participate in a simulation run. Interfaces are provided to
 * define the time boundaries and to control the simulation's march through
 * virtual time (e.g. reset, run, step, stepTo).
 *
 * @author Jeff Ridder
 */
public abstract class Universe {

    /**
     * The default RandomNumberGenerator available to all occupants of this
     * Universe.
     */
    protected RandomNumberGenerator default_random_number_generator = MersenneTwisterFast.getInstance();

    /**
     * The time tick value which represents the start of this simulation run.
     */
    protected double start_time = 0;

    /**
     * The time tick value which represents the end of this simulation run.
     */
    protected double end_time = 0;

    /**
     * The time tick value which represents "now" in the current simulation run.
     */
    protected double current_time = 0.;

    /**
     * A count of the total number of Behavior perform methods invoked in this
     * Universe. A statistic which is gathered for diagnostic and benchmarking
     * performance and which has no material effect on the simulation.
     */
    protected long performed_behavior_count = 0;

    /**
     * A count of the behaviors performed in the most recently completed
     * simulation step.
     */
    protected long step_behavior_count = 0;

    /**
     * A count of the total number of agent state transitions that occurred
     * between simulation steps in this universe for this simulation run. A
     * statistic which is gathered for diagnostic and benchmarking performance
     * and which has no material effect on the simulation.
     */
    protected long state_transition_count = 0;

    /**
     * A flag which indicates that some external entity has requested the
     * simulation to stop at the completion of the current simulation step. No
     * simulation step will execute until this flag is cleared by an external
     * entity.
     */
    protected boolean stopped = false;

    /**
     * A simulation run may complete if there are no more behaviors to perform
     * or if the current simulation time exceeds the simulation end time.
     */
    protected boolean done = false;

    /**
     * While a Universe is running within the context of a Scenario execute
     * method, the notion of a "current scenario" is defined.
     */
    protected Scenario current_scenario = null;
    
    /**
     * An optional Space object, which is useful for spatial sims and GUI
     */
    protected Space space = null;

    /**
     */
    public Universe() {
    }
    
    public Universe(Space space)
    {
        this.space = space;
    }

    /**
     * Invoke this method to request the simulation to stop at the completion of
     * the current simulation step. NOTE: You must invoke setStopped(false)
     * before continuing. A stopped state is not implicitly cleared by any other
     * method.
     *
     * @param stopped True to request simulation to halt
     */
    public void setStopped(boolean stopped) {
        this.stopped = stopped;
    }

    /**
     * Sets the DefaultRandomNumberGenerator attribute of the Universe object
     *
     * @param default_random_number_generator
     */
    public void setDefaultRandomNumberGenerator(RandomNumberGenerator default_random_number_generator) {
        if (default_random_number_generator != null) {
            this.default_random_number_generator = default_random_number_generator;
        }
    }
    
   /**
   *  Gets the Space attribute of the Universe object
   *
   * @return    space
   */
  public Space getSpace()
  {
    return this.space;
  }   

    /**
     * The time tick value which represents "now" in the current simulation run.
     *
     * @return double time ticks.
     */
    public double getCurrentTime() {
        return this.current_time;
    }

    /**
     * Get the simulation time window start.
     *
     * @return Simulation time window start
     */
    public double getStartTime() {
        return this.start_time;
    }

    /**
     * Get the current scenario for this universe, if any. The current scenario
     * is defined while a Universe is running within the scope of a Scenario
     * execute method.
     *
     * @return current scenario
     */
    public Scenario getCurrentScenario() {
        return this.current_scenario;
    }

    /**
     * Get the simulation time window end.
     *
     * @return Simulation time window end.
     */
    public double getEndTime() {
        return this.end_time;
    }

    /**
     * A count of the total number of Behavior perform methods invoked so far in
     * this Universe. A statistic which is gathered for diagnostic and
     * benchmarking performance and which has no material effect on the
     * simulation.
     *
     * @return long number of behaviors executed.
     */
    public long getPerformedBehaviorCount() {
        return this.performed_behavior_count;
    }

    /**
     * A count of the behaviors performed in the most recently completed
     * simulation step.
     *
     * @return long number of behaviors executed.
     */
    public long getStepBehaviorCount() {
        return this.step_behavior_count;
    }

    /**
     * A count of the total number of agent state transitions that occurred
     * between simulation steps in this universe for this simulation run. A
     * statistic which is gathered for diagnostic and benchmarking performance
     * and which has no material effect on the simulation.
     *
     * @return long number of between step state transitions.
     */
    public long getStateTransitionCount() {
        return this.state_transition_count;
    }

    /**
     * Gets the DefaultRandomNumberGenerator attribute of the Universe object
     *
     * @return RandomNumberGenerator
     */
    public RandomNumberGenerator getDefaultRandomNumberGenerator() {
        return this.default_random_number_generator;
    }

    /**
     * Get the number of agents in this universe.
     *
     * @return int number of agents.
     */
    public abstract int census();

    /**
     * Clear out the universe in preparation for a new simulation run. Reset the
     * simulation time boundaries. Set the simulation current time to the start
     * time. Reset the performed behavior count and other bookkeeping
     * statistics.
     *
     * @param start_time
     * @param end_time
     */
    public void resetSimulation(double start_time, double end_time) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.current_time = 0.;
        this.performed_behavior_count = 0;
        this.state_transition_count = 0;
        this.stopped = false;
        this.done = false;
    }

    /**
     * Add a new agent to this Universe. The behaviors associated with the
     * specified agent will be considered for scheduling prior to the next
     * simulation step.
     *
     * @param agent The agent to add.
     */
    public void addAgent(Agent agent) {
        if (agent != null) {
            agent.associateWithUniverse(this);
        }
    }

    /**
     * Inform the Universe that a behavior has changed in some way. Typically,
     * this is an indication that the behavior enable/disable state has toggled.
     * Changed behaviors will have their schedule status reevaluated prior to
     * execution of the next simulation step.
     *
     * @param behavior The behavior which has changed.
     */
    public abstract void behaviorChanged(Behavior behavior);

    /**
     * Execute behaviors until we are stopped or the scheduling algorithm
     * indicates that the simulation is done.
     */
    public void run() {
        while ((!stopped) && (!done)) {
            step();
        }
    }

    /**
     * Execute the set of behaviors associated with the next scheduled time
     * tick.
     */
    public abstract void step();

    /**
     * Execute sets of behaviors associated with the time ticks from the current
     * time up to those associated with the time tick of equal or lesser value
     * than the time specified.
     *
     * @param time The time tick value at which to pause the simulation.
     */
    public void stepTo(double time) {
        while ((current_time <= time) && (!stopped) && (!done)) {
            step();
        }
    }

    /**
     * Used internally by the Fast Universal Simulation Engine (FUSE) framework
     * to define the current scenario when known.
     *
     * @param current_scenario
     */
    public void setCurrentScenario(Scenario current_scenario) {
        this.current_scenario = current_scenario;
    }
    
    /**
     *  Inform the Universe that an agent has made a state transition request.
     *
     * @param  agent
     */
    abstract void stateTransitionRequestedBy(Agent agent);
}
