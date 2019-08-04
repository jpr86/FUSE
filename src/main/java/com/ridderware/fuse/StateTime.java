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

/**
 *  A timestamped state.
 */
public class StateTime {

  private IAgentState state = null;
  private double time = 0;
  private String description = null;


  /**
   *  Constructor for the StateTime object
   *
     * @param state
     * @param time
   */
  public StateTime(IAgentState state, double time)
  {
    this.state = state;
    this.time = time;
    this.description = "[" + state + " : " + time + "]";
  }


  /**
   *  Gets the State attribute of the StateTime object
   *
   * @return    The state.
   */
  public IAgentState getState()
  {
    return this.state;
  }


  /**
   *  Gets the Time attribute of the StateTime object
   *
   * @return    The time.
   */
  public double getTime()
  {
    return this.time;
  }


  /**
   *  Returns true if the state is non-null and the time is non-negative.
   *
   * @return    True if not egregiously invalid.
   */
  public boolean isValid()
  {
    return ((state != null) && (time >= 0));
  }


  /**
   *  Return a nicely formatted string representation suitable for debug traces.
   *
   * @return    String representation of state and time.
   */
  public String toString()
  {
    return this.description;
  }


  /**
   *  A comparator which compares StateTime instances by time value only.
   */
  public static class TimeComparator implements java.util.Comparator<StateTime> {

    /**
     *  Compare by time stamp only.
     *
     * @param  o1
     * @param  o2
     * @return     int indication of comparison.
     */
    @Override
    public int compare(StateTime o1, StateTime o2)
    {

      int result = 0;

      if (o1.time < o2.time)
      {
        result = -1;
      }
      else if (o1.time == o2.time)
      {
        result = 0;
      }
      else
      {
        result = 1;
      }

      return result;
    }

  }

}
