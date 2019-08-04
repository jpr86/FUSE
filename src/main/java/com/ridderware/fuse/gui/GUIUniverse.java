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

package com.ridderware.fuse.gui;

import com.ridderware.fuse.Agent;
import com.ridderware.fuse.Scenario;
import com.ridderware.fuse.SimpleUniverse;
import com.ridderware.fuse.Space;
import java.util.HashSet;
import java.util.Stack;
import org.apache.logging.log4j.*;

/**
 * A subclass of SimpleUniverse that provides event-by-event cues
 to FUSEGUI which then filters these queues as repaint events
 for all attached ViewFrames added using GUIUniverse.addView
 *
 * @author Jason HandUber 
 */
public class GUIUniverse extends SimpleUniverse
{
    private static final Logger logger = LogManager.getLogger(GUIUniverse.class);
    private Scenario latest_scenario;
    private HashSet<Killable> killables = new HashSet<>();
    
    private Stack<ToPaint> newPaintables = new Stack<>();
    
    /**
     *  Creates a new instance of GUI_Universe
     * @param space The Space object is abstract, create an appropriate subclass to define your
     * space and pass it into the GUI_Universe so that we have bounded time and space.
     */
    public GUIUniverse(Space space)
    {
        super(space);
        try
        {
            FUSEGUI.getGUI().initialize(this);
        }
        catch(Exception e)
        {
            logger.error("the exception happened here.");
            e.printStackTrace();
        }
    }
    
    /**
     * Adds support for the restart functionality behind the GUI.
     * @param current_scenario The current scenario
     */
    public void setCurrentScenario(Scenario current_scenario)
    {
        logger.debug("Setting currentScenario");
        super.setCurrentScenario(current_scenario);
        
        if ( current_scenario != null )
        {
            latest_scenario = current_scenario;
        }
    }
    
    /**
     * Creates the specified View, queing it to be displayed
     * @param view The instance of ViewFrame to display
     */
    public void addView(ViewFrame view)
    {
        FUSEGUI.getGUI().createWindow(view);
    }
    
    /**
     */
    public void addAgent(Agent agent)
    {
        super.addAgent(agent);
        
        if (agent instanceof Killable)
        {
            killables.add((Killable)agent);
        }
    }
    
    public void addPaintableAgent(Paintable paintable, ViewFrame ... views)
    {
        addPaintableAgent(paintable, new Object[0], views);
    }
    
    /**
     * Adds a paintable, with specified args, to all views specified
     */    
    public void addPaintableAgent(Paintable paintable, Object[] args, ViewFrame ... views)
    {
        for (ViewFrame view : views)
        {
            ToPaint tp = new ToPaint(paintable, args, view);
            newPaintables.push(tp);
        }
    }
    
    public void addPaintableAgent(Paintable paintable, String ... viewNames)
    {
        addPaintableAgent(paintable, new Object[0], viewNames);
    }
    
    /**
     * Adds a paintable, with specified args, to all views specified by their 
     * title (ie. window title).
     */
    public void addPaintableAgent(Paintable paintable, Object[] args, String ... viewNames)
    {
        for (String title : viewNames)
        {
            ToPaint tp = new ToPaint(paintable, args, FUSEGUI.getGUI().getViewFrameByTitle(title));
            newPaintables.push(tp);
        }
    }
    
    /**
     * Executes the last-known scenario. This is necessary because ewsim sets the
     * scenario to null as soon as a done condition is reached, here we have saved
     * the last scenario and will execute it on start. Again, this is to support
     * the GUI's restart functionality.
     */
    public void executeScenario()
    {
        logger.debug("Executing scenario: "+ latest_scenario);
        latest_scenario.execute();
    }
    
    /**
     * In this event driven simulation we don't count ticks of the clock. Everything
     * is relative. Everytime an 'event' happens, we call step() to deal with the
     * events and register any new events that may spawn from such event.
     */
    public void step()
    {
        while (!newPaintables.isEmpty())
        {
            ToPaint tp = newPaintables.pop();
            this.addAgent((Agent)tp.paintable);
            tp.viewFrame.addPaintable(tp.paintable, tp.args);
        }
        super.step();
        FUSEGUI.getGUI().prestep();
        FUSEGUI.getGUI().step();
    }
    
    /**
     * A simulation is done if there are no more behavior to execute or the
     * simulation has passed its end time.
     * @return boolean indicating doneness
     */
    public Boolean isDone()
    {
        return done;
    }
    
    /**
     */
    public void killAll()
    {
        for (Killable k : killables)
        {
            k.kill();
        }
    }
}

class ToPaint
{
    public final Object[] args;
    public final ViewFrame viewFrame;
    public final Paintable paintable;
    
    public ToPaint(Paintable paintable, Object[] args, ViewFrame viewFrame)
    {
        this.paintable = paintable;
        this.viewFrame = viewFrame;
        this.args = args;
    }
}
