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

package com.ridderware.fuse.examples.Bug;

import java.util.Random;

import org.apache.logging.log4j.*;
import com.ridderware.fuse.Behavior;
import com.ridderware.fuse.Agent;

/**
 *  The Bug class is an example of how the Agent base class is typically
 *  extended to add additional attributes and Behaviors. In this case, (X, Y)
 *  coordinate attributes are added and a simple "Move" Behavior is added.
 */
public class Bug extends Agent {

  private static final Logger logger = LogManager.getLogger(Bug.class);

  private long position_x = 0;
  private long position_y = 0;


  /**
   *  Constructor for the Bug object
   *
     * @param name
     * @param position_x
     * @param position_y
   */
  public Bug(String name, long position_x, long position_y)
  {
    super(name);
    this.position_x = position_x;
    this.position_y = position_y;
    this.addBehavior(new Move());
  }


  /**
   *  A Behavior which defines how bugs move.
   */
  private class Move extends Behavior {

    private final Random random = new Random(System.currentTimeMillis() + getName().hashCode());


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
      double delta_time = random.nextDouble() * 100.;
      double result = current_time + delta_time;
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

      if (random.nextDouble() > 0.5)
      {
        position_x = (random.nextDouble() < 0.5) ? (position_x + 1) : (position_x - 1);
      }
      else
      {
        position_y = (random.nextDouble() < 0.5) ? (position_y + 1) : (position_y - 1);
      }

        logger.info("Bug [" + getName() + "] is at position (" +
            position_x + ", " + position_y + ") at time " + current_time + ".");
    }

  }

}
