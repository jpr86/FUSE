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
import java.util.HashMap;
import org.apache.logging.log4j.*;

/**
 * A Scenario establishes a relationship between a Universe, a number of
 * IAgentFactory instances, additional Agents, and simulation control
 * parametrics. A Scenario may be further extended by invoking the configure
 * method which allows for execution of an external configuration script.
 * 
 * @author Jeff Ridder
 */
public class Scenario implements IAgentFactory
{

    private static final Logger logger = LogManager.getLogger(Scenario.class);

    private static DecimalFormat double_formatter = (DecimalFormat) DecimalFormat.getNumberInstance();
    private static DecimalFormat long_formatter = (DecimalFormat) DecimalFormat.getNumberInstance();
    private final HashMap<String, Object> user_objects = new HashMap<>();
    /**
     * The Universe associated with this scenario.
     */
    private Universe universe = null;

    /**
     * The list of additional agents that will be added to the Universe by this
     * scenario. A Scenario is an IAgentFactory.
     */
    private final ArrayList<IAgent> agents = new ArrayList<>();

    /**
     * The agent factories which will populate the Universe.
     */
    private final ArrayList<IAgentFactory> agent_factories = new ArrayList<>();

    /**
     * The start of the simulation time window.
     */
    private double start_time = 0.0;

    /**
     * The end of the simulation time window.
     */
    private double end_time = 0.0;

    static
    {
        double_formatter.applyPattern("###,###,###,###,##0.000");
        long_formatter.applyPattern("###,###,###,###,###");
    }
    
    /**
     *  Store the specified user object in the Scenario cataloged by the specified
     *  name. Null names are not allowed. Null user object values are allowed.
     * @param name  The name of the user object (non-null)
     * @param object  The Object itself
     */
    public void setUserObject(String name, Object object)
    {
        if (name != null)
        {
            user_objects.put(name, object);
        }
    }
    
    /**
     * Sets the Universe attribute of the Scenario object
     *
     * @param universe The Universe
     */
    public void setUniverse(Universe universe)
    {
        this.universe = universe;
    }

    /**
     * Sets the IAgentFactory attribute of the Scenario object
     *
     * @param agent_factory The agent factory.
     */
    public void setAgentFactory(IAgentFactory agent_factory)
    {
        if (agent_factory != null)
        {
            agent_factories.clear();
            agent_factories.add(agent_factory);
        }
    }

    /**
     * Sets the IAgentFactory attribute of the Scenario object
     *
     * @param start_time The start time
     */
    public void setStartTime(double start_time)
    {
        this.start_time = start_time;
    }

    /**
     * Sets the IAgentFactory attribute of the Scenario object
     *
     * @param end_time The end time
     */
    public void setEndTime(double end_time)
    {
        this.end_time = end_time;
    }
    
    /**
     *  Retrieve the specified user object. If the name is unknown to the scenario
     *  then the result will be null. If the value of the stored object is null
     *  then the result will also be null.
     * @param name The name of the user object
     * @return Object user_object
     */
    public Object getUserObject(String name)
    {
        Object result = null;
        if (name != null)
        {
            result = user_objects.get(name);
        }
        return result;
    }
    
    /**
     * Get the associated Universe.
     *
     * @return Universe
     */
    public Universe getUniverse()
    {
        return this.universe;
    }

    /**
     * Get current list of IAgentFactory instances in the scenario.
     *
     * @return List of agent factories.
     */
    public IAgentFactory[] getAgentFactories()
    {
        IAgentFactory[] result = new IAgentFactory[agent_factories.size()];
        agent_factories.toArray(result);
        return result;
    }

    /**
     * Gets the StartTime attribute of the Scenario object
     *
     * @return The start of the simulation time window
     */
    public double getStartTime()
    {
        return this.start_time;
    }

    /**
     * Gets the EndTime attribute of the Scenario object
     *
     * @return The end of the simulation time window
     */
    public double getEndTime()
    {
        return this.end_time;
    }

    /**
     * Adds any non-null agent that hasn't already been added to the Scenario's
     * Agent ArrayList
     *
     * @param agent The Agent to add
     */
    public void addAgent(IAgent agent)
    {
        if ((agent != null) && (!agents.contains(agent)))
        {
            agents.add(agent);
        }
    }

    /**
     * Adds any non-null IAgentFactory that hasn't already been added to the
     * Scenario's ArrayList of type IAgentFactory
     *
     * @param agent_factory The instance of IAgentFactory to add
     */
    public void addAgentFactory(IAgentFactory agent_factory)
    {
        if ((agent_factory != null) && (!agent_factories.contains(agent_factory)))
        {
            agent_factories.add(agent_factory);
        }
    }

    /**
     * A Scenario may also be an IAgentFactory. Populate the Universe from the
     * list of agents held by the Scenario. The Universe instance passed must be
     * the same as the Universe currently set for the Scenario.
     *
     * @param universe The Universe
     */
    @Override
    public void populateUniverse(Universe universe)
    {
        if ((universe != null) && (this.universe == universe))
        {
            for (int i = 0; i < agents.size(); i++)
            {
                universe.addAgent((Agent) agents.get(i));
            }
        }
    }

    /**
     * Run the scenario.
     */
    public void execute()
    {

        universe.setCurrentScenario(this);

        try
        {

            universe.resetSimulation(start_time, end_time);

            logger.info("Simulation run started at virtual time " + double_formatter.format(universe.getCurrentTime()) + ".");

            logger.debug("Populating universe from " + agent_factories.size() + " agent factories.");

            // We are an IAgentFactory.
            // Invoke our own populateUniverse
            logger.debug("  Population before [" + this.getClass().getName() + "] is " + long_formatter.format(universe.census()) + " agents.");
            populateUniverse(this.universe);
            logger.debug("  Population after [" + this.getClass().getName() + "] is " + long_formatter.format(universe.census()) + " agents.");

            // Then invoke populateUniverse
            // on each of the additional IAgentFactory
            // instances known to this Scenario.
            for (int i = 0; i < agent_factories.size(); i++)
            {
                IAgentFactory agent_factory = (IAgentFactory) agent_factories.get(i);
                logger.debug("  Population before [" + agent_factory.getClass().getName() + "] is " + long_formatter.format(universe.census()) + " agents.");
                agent_factory.populateUniverse(universe);
                logger.debug("  Population after [" + agent_factory.getClass().getName() + "] is " + long_formatter.format(universe.census()) + " agents.");
            }

            logger.debug("  Agent factories produced " + long_formatter.format(universe.census()) + " agents.");

            long t0 = System.currentTimeMillis();
            universe.run();
            long t1 = System.currentTimeMillis();

            long wall_time = (t1 - t0);
            double sim_time = (universe.getCurrentTime() - universe.getStartTime());

            logger.info("Simulation run ended at virtual time " + double_formatter.format(universe.getCurrentTime()) + ".");
            logger.info("  After run, universe contains " + long_formatter.format(universe.census()) + " agents.");
            logger.info("  Performed " + long_formatter.format(universe.getPerformedBehaviorCount()) + " behaviors.");
            logger.info("  Elapsed virtual time = " + double_formatter.format(sim_time) + " seconds.");
            logger.info("  Elapsed Wall time = " + long_formatter.format(wall_time) + " milliseconds.");

            if (wall_time > 0)
            {
                double behavior_rate = (double) universe.getPerformedBehaviorCount() / (double) wall_time;
                double real_time_percent = 100. * 1000. * sim_time / (double) wall_time;
                logger.info("  Simulation behavior rate = " + double_formatter.format(behavior_rate) + " behaviors/millisecond.");
                logger.info("  Simulation virtual/wall time = " + double_formatter.format(real_time_percent) + "%.");
            }

        } catch (Exception e)
        {
            logger.error("Simulation exiting with exception.", e);
        }

        universe.setCurrentScenario(null);

    }

}
