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


import java.util.ArrayList;
import java.util.Random;

import com.ridderware.fuse.IAgentFactory;
import com.ridderware.fuse.Universe;

/**
 *  A simple example of an AgentFactory. In this example, the populateUniverse
 *  method of the agent factory simply creates an instance of the Bug agents which populate this simulation Universe.
 */
public class BugSim implements IAgentFactory {

  private static final Random random = new Random(System.currentTimeMillis());

  private ArrayList<Bug> bugs = new ArrayList<>();


  /**
   *  Constructor for the BugSim object
   */
  public BugSim()
  {

    // A collection of Bugs (Agents) which are placed at some randomized
    // (X, Y) coordinate when constructed.

    bugs.add(new Bug("Manny", randomCoordinate(), randomCoordinate()));
    bugs.add(new Bug("Moe", randomCoordinate(), randomCoordinate()));
    bugs.add(new Bug("Jack", randomCoordinate(), randomCoordinate()));

  }


  /**
   *  Populate the universe from the bug list.
   *
   * @param  universe
   */
  public void populateUniverse(Universe universe)
  {
    for (int i = 0; i < bugs.size(); i++)
    {
      universe.addAgent((Bug) bugs.get(i));
    }
  }


  private long randomCoordinate()
  {
    return (long) random.nextInt(100);
  }

}
