/* Open Log Viewer
 *
 * Copyright 2011
 *
 * This file is part of the OpenLogViewer project.
 *
 * OpenLogViewer software is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenLogViewer software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with any OpenLogViewer software.  If not, see http://www.gnu.org/licenses/
 *
 * I ask that if you make any changes to this file you fork the code on github.com!
 *
 */
package org.diyefi.openlogviewer.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * MathUtils is used to provide math functions specific to the project.
 *
 * @author Ben Fenner
 */
public final class MathUtils {
   private static final char DS = DecimalFormatSymbols.getInstance().getDecimalSeparator();
   private static final DecimalFormat CUSTOM = (DecimalFormat) NumberFormat.getNumberInstance();
   private static final DecimalFormat NORMAL = (DecimalFormat) NumberFormat.getNumberInstance();

   static {
      CUSTOM.setGroupingUsed(false);
      NORMAL.setGroupingUsed(false);
   }

   private MathUtils() {
   }

   /**
    * @param input         - The double you'd like to round the decimal places for
    * @param decimalPlaces - The number of decimal places you'd like
    * @return the formatted number
    */
   public static String roundDecimalPlaces(final double input, final int decimalPlaces) {
      // Deal with zero or negative decimal places requested
      if (decimalPlaces <= 0) {
         return NORMAL.format(Math.round(input));
      }

      final String format;
      final StringBuilder negativeZero = new StringBuilder("-0" + DS);

      format = IntStream
            .range(0, decimalPlaces)
            .mapToObj(i -> "0" + '0')
            .collect(Collectors.joining("", "###0" + DS, ""));

      CUSTOM.applyLocalizedPattern(format);
      final StringBuilder output = new StringBuilder(CUSTOM.format(input));

      // Deal with negative zero
      if (output.toString().equals(negativeZero.toString())) {
         output.deleteCharAt(0);
      }

      return output.toString();
   }
}
