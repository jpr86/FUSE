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

import java.util.Date;
import java.util.Random;

import org.apache.logging.log4j.*;

/**
 * A convenient set of time related constants and conversion methods. This class
 * defines time constants and convenience methods which make it easy to express
 * other units of time (e.g. days, weeks) as seconds. Methods are also provided
 * for the generation of random time values from a specified range.
 * 
 * @author Jeff Ridder
 */
public class TimeUtil
{

    /**
     */
    public static final Random random = new Random();

    /**
     */
    public static final int DAYS_IN_WEEK = 7;

    /**
     */
    public static final double SECONDS_PER_MILLISECOND = 0.001;

    /**
     */
    public static final double SECONDS_PER_MINUTE = 60.0;

    /**
     */
    public static final double SECONDS_PER_HOUR = 60.0 * SECONDS_PER_MINUTE;

    /**
     */
    public static final double SECONDS_PER_DAY = 24.0 * SECONDS_PER_HOUR;

    /**
     */
    public static final double SECONDS_PER_WEEK = DAYS_IN_WEEK * SECONDS_PER_DAY;

    private static final Logger logger = LogManager.getLogger(TimeUtil.class);

    /**
     * The current time as defined by the computer upon which this code executes
     * expressed as the number of seconds since the epoch 1-Jan-1970 midnight
     * GMT.
     *
     * @return double seconds since epoch
     */
    public static double now()
    {
        long milliseconds_since_epoch = new Date().getTime();
        logger.info("Milliseconds since epoch " + milliseconds_since_epoch + ".");
        return milliseconds(milliseconds_since_epoch);
    }

    /**
     * A number of seconds which represents a number of milliseconds chosen from
     * an even distribution of milliseconds in the specified range.
     *
     * @param milliseconds_range
     * @return double seconds.
     */
    public static double randomMilliseconds(double milliseconds_range)
    {
        double milliseconds = random.nextDouble() * milliseconds_range;
        return milliseconds(milliseconds);
    }

    /**
     * A number of seconds which represents a number of minutes chosen from an
     * even distribution of minutes in the specified range.
     *
     * @param minutes_range
     * @return double seconds.
     */
    public static double randomMinutes(double minutes_range)
    {
        double minutes = random.nextDouble() * minutes_range;
        return minutes(minutes);
    }

    /**
     * A number of seconds which represents a number of hours chosen from an
     * even distribution of hours in the specified range.
     *
     * @param hours_range
     * @return double seconds.
     */
    public static double randomHours(double hours_range)
    {
        double hours = random.nextDouble() * hours_range;
        return hours(hours);
    }

    /**
     * A number of seconds which represents a number of days chosen from an even
     * distribution of days in the specified range.
     *
     * @param days_range
     * @return double seconds.
     */
    public static double randomDays(double days_range)
    {
        double days = random.nextDouble() * days_range;
        return days(days);
    }

    /**
     * A number of seconds which represents a number of weeks chosen from an
     * even distribution of weeks in the specified range.
     *
     * @param weeks_range
     * @return double seconds.
     */
    public static double randomWeeks(double weeks_range)
    {
        double weeks = random.nextDouble() * weeks_range;
        return weeks(weeks);
    }

    /**
     * Convert the specified number of milliseconds to seconds.
     *
     * @param milliseconds
     * @return double seconds.
     */
    public static double milliseconds(double milliseconds)
    {
        double seconds = (milliseconds * SECONDS_PER_MILLISECOND);
        return seconds;
    }

    /**
     * Convert the specified number of minutes to seconds.
     *
     * @param minutes
     * @return double seconds.
     */
    public static double minutes(double minutes)
    {
        double seconds = (minutes * SECONDS_PER_MINUTE);
        return seconds;
    }

    /**
     * Convert the specified number of hours to seconds.
     *
     * @param hours
     * @return double seconds.
     */
    public static double hours(double hours)
    {
        double seconds = (hours * SECONDS_PER_HOUR);
        return seconds;
    }

    /**
     * Convert the specified number of days to seconds.
     *
     * @param days
     * @return double seconds.
     */
    public static double days(double days)
    {
        double seconds = (days * SECONDS_PER_DAY);
        return seconds;
    }

    /**
     * Convert the specified number of weeks to seconds.
     *
     * @param weeks
     * @return double seconds.
     */
    public static double weeks(double weeks)
    {
        double seconds = (weeks * SECONDS_PER_WEEK);
        return seconds;
    }

}
