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
package com.ridderware.fuse.examples.SimpleAgent;

import org.apache.logging.log4j.*;
import com.ridderware.fuse.Behavior;
import com.ridderware.fuse.Agent;

/**
 *  This agent demonstrates the ability to use the configuration scripting
 *  facility to change Agent attributes. This agent defines named constants for
 *  COLOR values and provides a setColor method which is invoked from the
 *  scenario configuration script.
 */
public class SimpleAgent extends Agent {

  /**
   *  Red.
   */
  public static final int COLOR_RED = 1;

  /**
   *  Green.
   */
  public static final int COLOR_GREEN = 2;

  /**
   *  Blue.
   */
  public static final int COLOR_BLUE = 3;

  private static final Logger logger = LogManager.getLogger(SimpleAgent.class);

  private int color = COLOR_RED;


  /**
   *  Constructor for the SimpleAgent object
   *
     * @param name
   */
  public SimpleAgent(String name)
  {
    super(name);
    this.addBehavior(new Life());
  }


  /**
   *  Sets the Color attribute of the SimpleAgent object
   *
   * @param  color
   */
  public void setColor(int color)
  {
    switch (color)
    {
      case COLOR_RED:
      case COLOR_GREEN:
      case COLOR_BLUE:
      {
        this.color = color;
        break;
      }
    }
  }


  /**
   *  A minimalist Behavior.
   */
  private class Life extends Behavior {

    /**
     *  The simulation framework invokes this method to ask this behavior when
     *  it would like to be scheduled.
     *
     * @param  current_time
     * @return               double second time value at which to run.
     */
    @Override
    public double getNextScheduledTime(double current_time)
    {
      double result = 0.0;
      if (current_time < 10.0)
      {
        result = current_time + 1.0;
      }
      return result;
    }


    /**
     *  This method is invoked by the simulation framework when the scheduled
     *  time for this behavior is reached. In this simple example, the dice are
     *  rolled to determine the amount of X,Y bug movement that should occur.
     *
     * @param  current_time
     */
    @Override
    public void perform(double current_time)
    {
      logger.info(SimpleAgent.this + ": perform [" + current_time + "].");
    }

  }

}
