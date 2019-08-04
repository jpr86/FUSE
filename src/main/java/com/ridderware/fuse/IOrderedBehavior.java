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

/**
 * A behavior which will be executed in a specified order by an order executing universe. 
 * Note that ordinary universes will not honor the execution order.
 * 
 * @author Jeff Ridder
 * @param <T> type of agent owning this behavior
 */
public interface IOrderedBehavior<T extends IAgent> extends IBehavior<T>
{
    /**
     * Sets the execution order for this behavior.  Lower orders are executed before
     * higher numbers in the same time increment.  If multiple behaviors have the
     * same order, their execution order is not ensured.  For example, all order 0
     * behaviors are executed before order 1, and all order 1 before order 2, etc.  But
     * if there are multiple order 0 behaviors, their execution order is essentially random.
     * 
     * @param order
     */
    public void setOrder(int order);
    
    /**
     * Returns the execution order for this behavior.
     * @return execution order.
     */
    public int getOrder();
}
