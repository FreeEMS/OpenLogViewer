package org.diyefi.openlogviewer;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FileExtensionsTest {

   private static Stream<Arguments> fileExtensionProvider() {
      return Stream.of(
            Arguments.of(FileExtensions.STAR_DOT, "*."),
            Arguments.of(FileExtensions.LA, "la"),
            Arguments.of(FileExtensions.BIN, "bin"),
            Arguments.of(FileExtensions.BIN, "bin"),
            Arguments.of(FileExtensions.CSV, "csv"),
            Arguments.of(FileExtensions.LOG, "log"),
            Arguments.of(FileExtensions.XLS, "xls"),
            Arguments.of(FileExtensions.MSL, "msl"));
   }

   @ParameterizedTest(name = "run #{index} with [{arguments}]")
   @MethodSource("fileExtensionProvider")
   void shouldReturnCorrectExtensionStringForGivenEnum(FileExtensions given, String expected) {
      // ARRANGE

      // ACT
      String result = given.getExtension();

      // ASSERT
      assertThat(result).isEqualTo(expected);
   }

   @ParameterizedTest(name = "run #{index} with [{arguments}]")
   @MethodSource("fileExtensionProvider")
   void shouldReturnCorrectEnumforGivenExtensionString(FileExtensions expected, String given) {
      // ARRANGE

      // ACT
      FileExtensions result = FileExtensions.getByExtension(given);

      // ASSERT
      assertThat(result).isEqualTo(expected);
   }
}