package org.diyefi.openlogviewer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum FileExtensions {
   STAR_DOT("*."),
   LA("la"),
   BIN("bin"),
   CSV("csv"),
   LOG("log"),
   XLS("xls"),
   MSL("msl");

   private static final Map<String, FileExtensions> lookupByExtension = new HashMap<>();

   static {
      Arrays.stream(FileExtensions.values())
            .forEach(fileExtension -> lookupByExtension.put(fileExtension.getExtension(), fileExtension));
   }

   private final String extension;

   public static FileExtensions getByExtension(String extension) {
      return Optional.ofNullable(lookupByExtension.get(extension))
            .orElseThrow(() -> new IllegalArgumentException("Unsupported file extension '" + extension + "'"));
   }

   FileExtensions(String extension) {
      this.extension = extension;
   }

   public String getExtension() {
      return extension;
   }
}
