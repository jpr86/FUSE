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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;
import org.apache.logging.log4j.*;

/**
 * Aggregates and executes agent behaviors in order of time step and behavior
 * order. Multiple behaviors scheduled for the same time step will be executed
 * in the order specified if they implement IOrderedBehavior, and executed last
 * otherwise.
 *
 * @author Jeff Ridder
 */
public class OrderedTimeStepUniverse extends Universe
{

    private static final Logger logger = LogManager.getLogger(OrderedTimeStepUniverse.class);
    private final static DecimalFormat double_formatter = (DecimalFormat) DecimalFormat.getNumberInstance();
    /**
     * All of the agents in this Universe.
     */
    private final LinkedHashSet<IAgent> agents = new LinkedHashSet<>(2500);

    /**
     * Agents that have been added to the Universe but which have not yet had
     * their behaviors considered for scheduling.
     */
    private final LinkedHashSet<IAgent> new_agents = new LinkedHashSet<>(
            2500);
    /**
     * Behaviors that have been enabled that will be reevaluated prior to the
     * next simulation step.
     */
    private final LinkedHashSet<IBehavior<? extends IAgent>> recentlyEnabledBehaviors
            = new LinkedHashSet<>(5000);

    private final ArrayList<PriorityQueue<IBehavior<? extends IAgent>>> behaviors;

    private int step_index; //time step index

    static
    {
        double_formatter.applyPattern("###,###,###,###,##0.000");
    }

    /**
     */
    public OrderedTimeStepUniverse()
    {
        this.behaviors = new ArrayList<PriorityQueue<IBehavior<? extends IAgent>>>();
        this.step_index = 0;
    }

    @Override
    public int census()
    {
        return agents.size();
    }

    @Override
    public void resetSimulation(double start_time, double end_time)
    {
        super.resetSimulation(start_time, end_time);

        agents.clear();
        new_agents.clear();
        recentlyEnabledBehaviors.clear();
        behaviors.clear();
        this.step_index = 0;

        //  Allocate behaviors array
        for (double time = start_time; time <= end_time; time += 1.0)
        {
            behaviors.add(new PriorityQueue<IBehavior<? extends IAgent>>(2, orderComparator));
        }
    }

    @Override
    public void behaviorChanged(Behavior behavior)
    {
        if (behavior.isEnabled())
        {
            recentlyEnabledBehaviors.add(behavior);
        }
        else
        {
            //  Find where the disabled behavior was scheduled and remove it
            int index = (int) Math.ceil(behavior.getScheduledTime());
            if (!behaviors.get(index).remove(behavior))
            {
                recentlyEnabledBehaviors.remove(behavior);
            }
        }
    }

    @Override
    public void step()
    {
        step_behavior_count = 0;

        // New agents may have been added to the
        // universe since the last step (or this
        // may be the first step).  Each new
        // agent is reset.  Finally, the behaviors
        // associated with each agent are reset
        // and added to the collection of all
        // behaviors to be considered for scheduling.
        if (new_agents.size() > 0)
        {
            for (IAgent agent : new_agents)
            {
                agent.reset();
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
                    scheduleBehavior(behavior);
                }
            }

            new_agents.clear();

        }

        if (step_index >= behaviors.size())
        {
            done = true;
        }
        else
        {
            current_time = start_time + step_index;
            int numBehaviorsExecuted = 0;

            PriorityQueue<IBehavior<? extends IAgent>> stepBehaviors = behaviors.get(step_index);

            while (true)
            {
                IBehavior<? extends IAgent> toExecute = stepBehaviors.poll();
                if (toExecute == null)
                {
                    break;
                }
                toExecute.perform(current_time);
                updateScheduledTime(toExecute, current_time);
                scheduleBehavior(toExecute);
                numBehaviorsExecuted++;
            }

            step_behavior_count += numBehaviorsExecuted;

            // Update the schedules of any
            // behaviors that have been reenabled
            // since the last step.
            if (!recentlyEnabledBehaviors.isEmpty())
            {
                for (IBehavior<? extends IAgent> reschedule : recentlyEnabledBehaviors)
                {
                    updateScheduledTime(reschedule, current_time);
                    scheduleBehavior(reschedule);
                }
                recentlyEnabledBehaviors.clear();
            }
        }

        step_index++;
        performed_behavior_count += step_behavior_count;
    }

    private void scheduleBehavior(IBehavior<? extends IAgent> behavior)
    {
        if (behavior.isEnabled())
        {
            //  Must add to appropriate queue
            int index = (int) Math.ceil(behavior.getScheduledTime());
            if (index < behaviors.size())
            {
                behaviors.get((int) Math.ceil(behavior.getScheduledTime())).add(behavior);
            }
        }
    }

    private static void updateScheduledTime(IBehavior<? extends IAgent> behavior, double current_time)
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
     * Add a new agent to this Universe. The behaviors associated with the
     * specified agent will be considered for scheduling prior to the next
     * simulation step.
     *
     * @param agent The agent to add.
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

    public static Comparator<IBehavior<? extends IAgent>> orderComparator = new Comparator<IBehavior<? extends IAgent>>()
    {
        @Override
        public int compare(IBehavior<? extends IAgent> o1, IBehavior<? extends IAgent> o2)
        {
            IOrderedBehavior<? extends IAgent> b1 = null;
            IOrderedBehavior<? extends IAgent> b2 = null;

            if (o1 instanceof IOrderedBehavior)
            {
                b1 = (IOrderedBehavior<? extends IAgent>) o1;
            }
            if (o2 instanceof IOrderedBehavior)
            {
                b2 = (IOrderedBehavior<? extends IAgent>) o2;
            }

            if (b1 != null && b2 != null)
            {
                return (int) (b1.getOrder() - b2.getOrder());
            }
            else if (b1 != null && b2 == null)
            {
                return -1;
            }
            else if (b1 == null && b2 != null)
            {
                return 1;
            }
            return 0;
        }
    };

    @Override
    void stateTransitionRequestedBy(Agent agent) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
