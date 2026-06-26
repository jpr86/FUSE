# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

**Build:**
```bash
mvn package
```

**Compile only:**
```bash
mvn compile
```

**Run an example (no formal test suite — examples have `main` methods):**
```bash
mvn compile exec:java -Dexec.mainClass="com.ridderware.fuse.examples.SimpleAgent.SimpleAgentTest"
mvn compile exec:java -Dexec.mainClass="com.ridderware.fuse.examples.Bug.BugTest"
mvn compile exec:java -Dexec.mainClass="com.ridderware.fuse.examples.OrderedTimeStepTest.OrderedTimeStepTest"
```

## Architecture

FUSE is an agent-based discrete event simulation (DES) engine. The core execution loop is:

**Scenario → Universe → Agents → Behaviors**

### Core abstractions

- **`Universe`** (abstract) — owns simulation time (`start_time`, `end_time`, `current_time`), holds agents, and drives the event loop via `run()` → `step()`. `run()` calls `step()` in a loop until `done` or `stopped`.
- **`Agent`** — the fundamental entity. Holds a set of `IBehavior` instances and a set of `IAgentState` instances. Subclass this to create domain objects. Access the RNG via `getRNG()` only after the agent is added to a universe.
- **`Behavior<T>`** (abstract) — self-scheduling unit of work. Implement `getNextScheduledTime(double current_time)` to return when it next fires, and `perform(double current_time)` to do the work. Behaviors are enabled by default; call `setEnabled(false)` to suppress them.
- **`AgentState` / `IAgentState`** — named state objects owned by an agent. Agents call `requestNextState(state)` internally; the universe batches and flushes transitions between steps via `transitionToNextState()`.
- **`Scenario`** — wires a `Universe` to one or more `IAgentFactory` implementations, sets `start_time`/`end_time`, and calls `execute()` to run the simulation. Also implements `IAgentFactory` so agents can be added directly via `scenario.addAgent(agent)`.
- **`IAgentFactory`** — single-method interface (`populateUniverse(Universe)`) for populating a universe with agents.

### Universe implementations

| Class | Scheduling | State transitions |
|---|---|---|
| `SimplerUniverse` | `PriorityQueue` by scheduled time | Supported |
| `SimpleUniverse` | `ArrayList` scanned each step | Supported |
| `ContinuousTimeUniverse` | `PriorityQueue` by scheduled time | Not supported (throws) |
| `OrderedTimeStepUniverse` | Integer time steps; `PriorityQueue` per step ordered by `IOrderedBehavior.getOrder()` | Not supported (throws) |
| `GUIUniverse` | Extends `SimplerUniverse`; hooks into `FUSEGUI` for Swing rendering after each step | Supported |

**`SimplerUniverse` is the recommended general-purpose universe** — it uses a priority queue (efficient for large behavior sets) and supports state transitions.

`OrderedTimeStepUniverse` is only appropriate when behaviors must fire in a defined order within a discrete integer time step (e.g., sensor before mover within the same tick). Behaviors implement `IOrderedBehavior` and return a priority from `getOrder()`.

### Behavior scheduling contract

After `perform()` is called, the universe immediately calls `getNextScheduledTime(current_time)` to reschedule. If the returned value is `<= current_time`, the behavior is auto-disabled. Returning `Double.MAX_VALUE` from `getNextScheduledTime` effectively stops the behavior without disabling it. Calling `setEnabled(false/true)` notifies the universe via `behaviorChanged(behavior)` so the schedule is updated before the next step.

### Spatial support

`Space` (abstract) defines a 3D bounding box. Concrete subclasses `Cartesian2DSpace` and `Cartesian3DSpace` provide coordinate mapping. Pass a `Space` to a `Universe` constructor to enable spatial simulations. `GUIUniverse` requires a `Space` in order to scale the view.

### GUI layer (`com.ridderware.fuse.gui`)

- `GUIUniverse` — extends `SimplerUniverse`, drives `FUSEGUI` repaint after each step.
- `FUSEGUI` — singleton Swing controller; initialized automatically by `GUIUniverse`.
- `ViewFrame` / `SimpleView` — Swing windows registered with the universe via `addView(view)`.
- `Paintable` — interface for agents that draw themselves; registered with a view via `addPaintableAgent(paintable, view)`.
- `Clickable` / `Killable` — optional interfaces for interactive GUI agents.

### Dependencies

- `com.github.jpr86:JRandom` (via JitPack) — `MersenneTwisterFast` RNG, used as the default RNG in every universe.
- `log4j-core` / `log4j-api` — logging throughout the framework; config at `src/main/resources/log4j2.properties`.
- `javax.jlfgr:jlfgr` (Freehep repo) — Java Look and Feel Graphics Repository, used by the GUI layer.

Java 8 source/target compatibility.
