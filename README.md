# FUSE
Fast Universal Simulation Engine

FUSE is a fast executing agent-based, discrete event simulation engine. It is the underlying simulation framework for
CHECKMATE and other derived works.


An agent has the option to define one or more associated Behaviors.
Each behavior is self-scheduling and can be dynamically enabled and
disabled as the simulation progresses. For example, as agents change state the
state change may result in disabling the behaviors associated with the previous
state and enabling those in the new state. FUSE supports construction
of finite state machines which govern behavior activation/deactivation.

Agents live in an instance of a Universe, which is the keeper of time. Several
universe classes are provided in this package as a reasonable implementation 
of the abstract Universe class. The Universe defines the window of time in 
which the simulation exists. The Universe provides APIs to reset, run, stop, 
and step through the simulation.

A Universe is populated with Agents by an implementation of an AgentFactory. 
The AgentFactory examples provided with this framework populate the Universe in 
code. An AgentFactory implementation could just as easily construct Agent 
instances from a text file, XML, database, URL data source, or any other 
connection to other systems or persistent storage mechanisms.

A Universe and one or more AgentFactory instances are associated within the 
context of a Scenario. A Universe is executed in a unique thread created by the 
Scenario class when its execute method is invoked.  


