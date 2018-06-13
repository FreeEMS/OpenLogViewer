package org.diyefi.openlogviewer.utils;

import java.awt.*;

public class JavaFXUtils {

   private JavaFXUtils() {
      // NO-OP
   }

   private static double rangeConversionFactor = 255;

   public static Color convertFXColorToAWTColor(javafx.scene.paint.Color fxColor)
   {
      return new Color((float) fxColor.getRed(), (float) fxColor.getGreen(), (float) fxColor.getBlue(), (float) fxColor.getOpacity());
   }

   public static javafx.scene.paint.Color convertAWTColorToFXColor(Color awtColor)
   {
      return new javafx.scene.paint.Color(awtColor.getRed() / rangeConversionFactor, awtColor.getGreen() / rangeConversionFactor, awtColor.getBlue() / rangeConversionFactor, 1);
   }
}
