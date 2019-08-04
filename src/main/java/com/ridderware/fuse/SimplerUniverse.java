/*
 * 
 * Fast Universal Simulation Engine (FUSE)
 *
 * Copyright 2014 Jeff Ridder.
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Iterator;

import java.util.PriorityQueue;
import java.util.Set;
import org.apache.logging.log4j.*;

/**
 * Aggregates and executes agent behaviors in time order. Multiple behaviors
 * which are scheduled for the same time tick will execute in an order that is
 * convenient to this scheduler. The order is not guaranteed nor is it nicely
 * randomized.
 * @author Jason C. HandUber 
 */
public class SimplerUniverse extends Universe
{

    private static final Logger logger = LogManager.getLogger(SimplerUniverse.class);
    private final static DecimalFormat double_formatter = (DecimalFormat) DecimalFormat.getNumberInstance();
    /**
     *  All of the agents in this Universe.
     */
    private final LinkedHashSet<IAgent> agents = new LinkedHashSet<>(2500);
    /**
     *  The universe is notified when an agent is requesting a state transition.
     *  The list of agents with pending state transitions grows during a time step
     *  and is then used to command the agents to perform the transition to a new
     *  state between simulation steps.
     */
    private final LinkedHashSet<IAgent> agents_with_pending_state_transitions = new LinkedHashSet<>(
            1000);
    /**
     *  Agents that have been added to the Universe but which have not yet had
     *  their behaviors considered for scheduling.
     */
    private final LinkedHashSet<IAgent> new_agents = new LinkedHashSet<>(
            2500);
    /**
     *  Behaviors that have been enabled that will be reevaluated prior to the next simulation step.
     */
    private final LinkedHashSet<IBehavior<? extends IAgent>> recentlyEnabledBehaviors = new LinkedHashSet<>(5000);
    
    /**
     *  All of the behaviors in this universe.
     */
    private final PriorityQueue<IBehavior<? extends IAgent>> behaviors = new PriorityQueue<>(
            100,
            new Comparator<IBehavior<? extends IAgent>>()
            {

                @Override
                public int compare(IBehavior o1, IBehavior o2)
                {
                    return Double.compare(o1.getScheduledTime(), o2.getScheduledTime());
                }
            });
    

    static
    {
        double_formatter.applyPattern("###,###,###,###,##0.000");
    }

    /**
     */
    public SimplerUniverse()
    {
    }

    /**
     *  Constructor for the SimpleUniverse object
     * @param space 
     */
    public SimplerUniverse(Space space)
    {
        super(space);
    }

    /**
     *  Get the number of agents in this universe.
     *
     * @return    int number of agents.
     */
    @Override
    public int census()
    {
        return agents.size();
    }

    /**
     * @see    com.ridderware.fuse.Universe#step
     */
    @Override
    public void step()
    {

        step_behavior_count = 0;

        // New agents may have been added to the
        // universe since the last step (or this
        // may be the first step).  Each new
        // agent is reset.  Then the collection of
        // initial StateTime values is sorted and
        // performed.  Finally, the behaviors
        // associated with each agent are reset
        // and added to the collection of all
        // behaviors to be considered for scheduling.

        if (new_agents.size() > 0)
        {

            for (IAgent agent : new_agents)
            {
                agent.init();
            }

            for (IAgent agent : new_agents)
            {
                agent.reset();
            }

            ArrayList<StateTime> initial_state_times = new ArrayList<>(new_agents.size());

            for (IAgent agent : new_agents)
            {
                StateTime state_time = agent.getInitialStateTime();
                if (state_time != null)
                {
                    initial_state_times.add(state_time);
                }
            }

            Collections.sort(initial_state_times, new StateTime.TimeComparator());

            for (StateTime state_time : initial_state_times)
            {
                if ((state_time.getTime() >= current_time) && (state_time.getTime() <= start_time))
                {
                    current_time = state_time.getTime();
                }
                IAgentState state = state_time.getState();
                IAgent agent = state.getAgent();
                agent.transitionToState(state);
                ++state_transition_count;
            }

            if (current_time == 0)
            {
                current_time = start_time;
            }

            for (IAgent agent : new_agents)
            {  
                Set<IBehavior<? extends IAgent>> agent_behaviors = agent.getBehaviors();

                for (IBehavior<? extends IAgent> behavior : agent_behaviors)
                {
                    behavior.reset();

                    updateScheduledTime(behavior, current_time);
                    if(behavior.isEnabled())
                    {
                        behaviors.add(behavior);
                    }
                }                    
            }

            new_agents.clear();

        }

        // Build a list of indexes of schedule items
        // that are next to run.

        if (behaviors.isEmpty() || behaviors.peek().getScheduledTime() == Double.MAX_VALUE)
        {
            logger.info("No more scheduled behaviors after time " + double_formatter.format(
                    current_time));
            done = true;
        }
        else
        {
            current_time = behaviors.peek().getScheduledTime();
            if (current_time > end_time)
            {
                logger.info("Simulation end time boundary reached.");
                done = true;
            }
            else
            {
                // Execute the ready behaviors.
                int numBehaviorsExecuted = 0;
                do
                {
                    final IBehavior toExecute = behaviors.remove();
                    toExecute.perform(current_time);
                    updateScheduledTime(toExecute, current_time);
                    behaviors.add(toExecute);
                    numBehaviorsExecuted++;
                }
                while (behaviors.peek().getScheduledTime() == current_time);

                step_behavior_count += numBehaviorsExecuted;

                // Flush pending state transitions.
                Iterator agent_iterator = agents_with_pending_state_transitions.iterator();
                while (agent_iterator.hasNext())
                {
                    Agent agent = (Agent) agent_iterator.next();
                    agent.transitionToNextState();
                    ++state_transition_count;
                    agent_iterator.remove();
                }

                // Update the schedules of any
                // behaviors that have been reenabled
                // since the last step.

                if (!recentlyEnabledBehaviors.isEmpty())
                {
                    for (IBehavior reschedule : recentlyEnabledBehaviors)
                    {
                        updateScheduledTime(reschedule, current_time);
                    }
                    behaviors.addAll(recentlyEnabledBehaviors);
                    recentlyEnabledBehaviors.clear();
                }
            }

        }

        performed_behavior_count += step_behavior_count;
    }

    /**
     *  Add a new agent to this Universe. The behaviors associated with the
     *  specified agent will be considered for scheduling prior to the next
     *  simulation step.
     *
     * @param  agent  The agent to add.
     */
    @Override
    public void addAgent(Agent agent)
    {
        super.addAgent(agent);
        if (agent != null)
        {
            agents.add(agent);
            new_agents.add(agent);
        }
    }

    /**
     *  Inform the Universe that a previously disabled behavior has been enabled or vice-versa.
     *  The behavior needs to be considered for scheduling before the next
     *  simulation step is performed.
     *
     * @param  behavior  The behavior which has been re-enabled.
     */
    @Override
    public final void behaviorChanged(final Behavior behavior)
    {
        if (behavior.isEnabled())
        {
            recentlyEnabledBehaviors.add(behavior);
        }
        else
        {
            if (!behaviors.remove(behavior))
            {
                recentlyEnabledBehaviors.remove(behavior);
            }
        }
    }

    private static void updateScheduledTime(IBehavior behavior, double current_time)
    {
        double scheduled_time = Double.MAX_VALUE;
        if (behavior.isEnabled())
        {
            double next_time = behavior.getNextScheduledTime(current_time);
            if (next_time > current_time)
            {
                scheduled_time = next_time;
            }
            else
            {
//                System.out.println("Disabling: " + behavior);
                behavior.setEnabled(false);
            }
        }
        behavior.setScheduledTime(scheduled_time);
    }

    /**
     *  Reset the universe in preparation for another run.
     *
     * @param  start_time
     * @param  end_time
     * @see                com.ridderware.fuse.Universe#resetSimulation
     */
    @Override
    public void resetSimulation(double start_time, double end_time)
    {

        super.resetSimulation(start_time, end_time);

        agents.clear();
        behaviors.clear();
        agents_with_pending_state_transitions.clear();
        new_agents.clear();
        recentlyEnabledBehaviors.clear();

    }

    /**
     *  Inform the Universe that an agent has made a state transition request.
     *
     * @param  agent
     */
    @Override
    void stateTransitionRequestedBy( Agent agent)
    {
        if (agent != null)
        {
            agents_with_pending_state_transitions.add(agent);
        }
    }
}