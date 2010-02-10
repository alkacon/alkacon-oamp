/*
 * File   : $Source: /alkacon/cvs/alkacon/com.alkacon.opencms.survey/src/com/alkacon/opencms/survey/CmsAverageUtil.java,v $
 * Date   : $Date: 2010/02/10 09:25:30 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (C) 2002 - 2009 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.alkacon.opencms.survey;

import java.util.ArrayList;
import java.util.List;

/**
 * A class providing utility functions for the "average bar" functionality of the survey
 * report.<p>
 * 
 *  @author Georg Westenberger
 *
 */
public class CmsAverageUtil {

    /**
     * Tries to parse a string as a "double" number and return it.<p>
     * 
     * @param s the string that should be parsed
     * @return a Double object or null if the parsing failed 
     */
    public static Double parseDouble(String s) {

        try {
            return new Double(Double.parseDouble(s));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Tries to parse a string as a "Long" number and return it.<p>
     *  
     * @param s the string that should be parsed 
     * @return a Long object or null if the parsing failed
     */
    public static Long parseLong(String s) {

        try {
            return new Long(Long.parseLong(s));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Given a list of Double objects, this method creates a new list of Double objects
     * by negating all the values from the input list.<p>
     * 
     * For example, if the input list is [1, 2, -3], the result will be [-1, -2, 3].
     * The input list is not modified.
     * 
     * @param numbers the input list
     * @return the negated list of numbers
     */
    public static List<Double> negateAll(List<Double> numbers) {

        List<Double> result = new ArrayList<Double>();
        for (double number : numbers) {
            result.add(-number);
        }
        return result;
    }

    /**
     * From a "FieldDefault" specification used in form generator forms for e.g. 
     * radio buttons, this method extracts all the numeric keys.<p>
     * 
     *  For example, if the input is "1:foo|2:bar|3|bar:qqq|xyzzy", then the list
     *  [1, 2, 3] will be returned. 
     *  
     * @param fieldDefault The FieldDefault entry
     * @return the list of extracted numbers.
     */

    public static List<Double> getNumbersFromDefault(String fieldDefault) {

        List<Double> result = new ArrayList<Double>();
        String[] entries = fieldDefault.split("\\|");
        for (String entry : entries) {
            int colonIndex = entry.indexOf(":");
            if (colonIndex != -1) {
                entry = entry.substring(0, colonIndex);
            }
            Double d = parseDouble(entry);
            if (d != null) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Finds the maximum from a list of Doubles.<p>
     * 
     * If the list is empty, 0 is returned. 
     * 
     * @param numbers the list of Doubles
     * @return the maximum
     */
    public static double getMax(List<Double> numbers) {

        if (numbers.size() == 0) {
            return 0.0;
        }
        double max = numbers.get(0);
        for (Double number : numbers) {
            max = Math.max(max, number);
        }
        return max;
    }

    /**
     * Finds the minimum from a list of Doubles.<p>
     * 
     * If the list is empty, 0 is returned. 
     * @param numbers the list of Doubles
     * @return the minimum
     */

    public static double getMin(List<Double> numbers) {

        if (numbers.size() == 0) {
            return 0.0;
        }
        double min = numbers.get(0);
        for (Double number : numbers) {
            min = Math.min(min, number);
        }
        return min;
    }

    /**
     * Returns the difference between the maximum and the minimum of a list of Doubles.<p> 
     * 
     * @param numbers the input list
     * @return the range of the input list
     */

    public static double getRange(List<Double> numbers) {

        return getMax(numbers) - getMin(numbers);
    }

    /**
     * Formats a ratio as a percentage.<p>
     * 
     *  For example, 0.362 will be formatted as "36.2%".
     * @param ratio the value that should be formatted as a percentage.
     * @return the input value formatted as a percentage.
     */
    public static String asPercentage(double ratio) {

        return "" + (ratio * 100) + "%";
    }

    /**
     * Returns a corrected bar split ratio by reserving a certain portion of the bar
     * as initial part that will always be displayed even if the split ratio is 0 (i.e.
     * the average equals the minimum value.<p>
     * 
     * For example, if the split ratio is 0.5 and initialPart is 1/7, then the corrected 
     * split ratio is 4/7. 
     * 
     * 
     * @param initialPart the portion of the bar that should be reserved as the initial part
     * @param ratio the original split ratio
     * @return the corrected split ratio
     */
    public static double getBarSplitRatio(double initialPart, double ratio) {

        return (1 - initialPart) * ratio + initialPart;
    }

    /**
     * This method does the same as getBarHtml, but reverses the orientation,
     * so that an average value equal to the minimum value will be displayed as 
     * a full bar.<p>
     * 
     * @see CmsAverageUtil#getBarHtml(String, String, String, String)
     *    
     */

    public static String getBarHtmlReverse(String fieldDefault, String averageStr, String displayStr, String color) {

        Double average = parseDouble(averageStr);
        if (average == null) {
            return "<!-- average is null -->";
        }
        Double negAverage = new Double(-average.doubleValue());
        List<Double> numbers = getNumbersFromDefault(fieldDefault);
        List<Double> negNumbers = negateAll(numbers);
        double range = getRange(negNumbers);
        if (range == 0) {
            return "<!-- range is 0 -->";
        }
        double ratio = (negAverage - getMin(negNumbers)) / range;

        double correctedRatio = getBarSplitRatio(0.03, ratio);
        return makeBar(correctedRatio, displayStr, color);
    }

    /**
     * Returns the HTML for a bar showing the average value.<p> 
     *  
     * @param fieldDefault the FieldDefault entry 
     * @param averageStr a string containing the average value
     * @param displayStr the string that should be displayed inside the bar.
     * @param color the color of the bar. 
     * @return the HTML for the average bar.
     */
    public static String getBarHtml(String fieldDefault, String averageStr, String displayStr, String color) {

        Double average = parseDouble(averageStr);
        if (average == null) {
            return "<!-- average is null -->";
        }
        List<Double> numbers = getNumbersFromDefault(fieldDefault);
        double range = getRange(numbers);
        if (range == 0) {
            return "<!-- range is 0 -->";
        }
        double ratio = (average - getMin(numbers)) / range;

        double correctedRatio = getBarSplitRatio(0.03, ratio);
        return makeBar(correctedRatio, displayStr, color);
    }

    /**
     * Helper method that actually creates the HTML for a bar.<p>
     * 
     * @param ratio  the width of the bar as a value between 0 and 1
     * @param text the text to be displayed inside a bar 
     * @param color the bar color
     * @return the HTML code for the bar. 
     */

    public static String makeBar(double ratio, String text, String color) {

        StringBuffer buffer = new StringBuffer();
        buffer.append("<div class=\"reportitem\">");
        buffer.append("<span class=\"processbar\">");

        buffer.append("<span class=\"bar\" style=\"width:"
            + asPercentage(ratio)
            + "; background-color:"
            + color
            + "; color:#FFF;\" >"
            + text
            + "</span>");

        buffer.append("</span>");
        buffer.append("</div>");
        return buffer.toString();
    }

}
