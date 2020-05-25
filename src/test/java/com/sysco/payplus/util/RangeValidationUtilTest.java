package com.sysco.payplus.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.Range;
import com.sysco.payplus.service.exception.ValidationException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RangeValidationUtilTest {

  @Test
  @Tag("verifyRangeForCorrectBounds")
  @DisplayName("Verify Range conversion for correct ranges")
  public void whenCorrectBounds_thenOpenCloseRange() throws ValidationException {
    Range<Integer> openClosedRange = RangeValidationUtil.convertToOpenClosedRange(0, 80);
    assertFalse(openClosedRange.contains(0));
    assertTrue(openClosedRange.contains(1));
    assertTrue(openClosedRange.contains(80));
    assertFalse(openClosedRange.contains(81));
  }

  @Test
  @Tag("verifyRangeForIncorrectBounds")
  @DisplayName("Verify exception for incorrect ranges with lower bound greater than/ equal to upper bound")
  public void whenInCorrectBounds_thenThrowValidationException() {
    ValidationException lowerBoundGreaterThanUpperBound = assertThrows(ValidationException.class,
        () -> RangeValidationUtil.convertToOpenClosedRange(80, 0)
    );
    assertEquals(MessageFormat.format(RangeValidationUtil.ERROR_MESSAGE_LOWER_BOUND_GREATER_THAN_UPPER_BOUND, 80, 0),
        lowerBoundGreaterThanUpperBound.getErrorDTO().getErrorData());

    ValidationException lowerBoundEqualsUpperBound = assertThrows(ValidationException.class,
        () -> RangeValidationUtil.convertToOpenClosedRange(0, 0)
    );
    assertEquals(MessageFormat.format(RangeValidationUtil.ERROR_MESSAGE_LOWER_BOUND_AND_UPPER_BOUND_EQUALS, 0, 0),
        lowerBoundEqualsUpperBound.getErrorDTO().getErrorData());
  }

  @Test
  @Tag("verifyDuplicateRanges")
  @DisplayName("Verify exception for duplicate ranges")
  public void whenDuplicateRanges_thenThrowValidationException() {
    Range<Integer> range1 = Range.openClosed(0, 80);
    List<Range<Integer>> rangeList = new ArrayList<>();
    rangeList.add(range1);
    rangeList.add(range1);
    ValidationException validationException = assertThrows(ValidationException.class, () -> RangeValidationUtil.checkDuplicateRanges(rangeList));
    assertEquals(RangeValidationUtil.ERROR_MESSAGE_DUPLICATE_RANGES, validationException.getErrorDTO().getErrorData());
  }

  @Test
  @Tag("verifyNonDuplicateRanges")
  @DisplayName("Verify no exception for non-duplicate ranges")
  public void whenNonDuplicateRanges_thenDoesNotThrowValidationException() {
    Range<Integer> range1 = Range.openClosed(0, 80);
    Range<Integer> range2 = Range.openClosed(80, 100);
    List<Range<Integer>> rangeList = new ArrayList<>();
    rangeList.add(range1);
    rangeList.add(range2);
    assertDoesNotThrow(() -> RangeValidationUtil.checkDuplicateRanges(rangeList));
  }


  @Test
  @Tag("verifyNonIntersectingAndContinuousRanges")
  @DisplayName("Verify no exception for correct non-intersecting and continuous ranges")
  public void whenNonIntersectingAndContinuousRanges_thenThrowValidationException() {
    Range<Integer> range1 = Range.openClosed(80, 100);
    Range<Integer> range2 = Range.openClosed(60, 80);
    Range<Integer> range3 = Range.openClosed(0, 60);
    Range<Integer> range4 = Range.openClosed(100, 110);
    List<Range<Integer>> rangeList = new ArrayList<>();
    rangeList.add(range1);
    rangeList.add(range2);
    rangeList.add(range3);
    rangeList.add(range4);

    assertDoesNotThrow(() -> RangeValidationUtil.checkNonIntersectingContinuousRanges(rangeList));
  }

  @Test
  @Tag("verifyIntersectingRanges")
  @DisplayName("Verify exception for intersecting ranges")
  public void whenIntersectingRanges_thenThrowValidationException() {
    Range<Integer> range1 = Range.openClosed(0, 80);
    Range<Integer> range2 = Range.openClosed(60, 100);
    List<Range<Integer>> rangeList = new ArrayList<>();
    rangeList.add(range1);
    rangeList.add(range2);

    ValidationException validationException = assertThrows(ValidationException.class,
        () -> RangeValidationUtil.checkNonIntersectingContinuousRanges(rangeList)
    );
    assertEquals(RangeValidationUtil.ERROR_MESSAGE_INTERSECTING_RANGES, validationException.getErrorDTO().getErrorData());

  }

  @Test
  @Tag("verifyNonContinuousRanges")
  @DisplayName("Verify exception for non-continuous ranges")
  public void whenNonContinuousRanges_thenThrowValidationException() {
    Range<Integer> range1 = Range.openClosed(61, 100);
    Range<Integer> range2 = Range.openClosed(50, 60);
    Range<Integer> range3 = Range.openClosed(0, 50);
    List<Range<Integer>> rangeList = new ArrayList<>();
    rangeList.add(range1);
    rangeList.add(range2);
    rangeList.add(range3);

    ValidationException validationException = assertThrows(ValidationException.class,
        () -> RangeValidationUtil.checkNonIntersectingContinuousRanges(rangeList)
    );
    assertEquals(RangeValidationUtil.ERROR_MESSAGE_NON_CONTINUOUS_RANGES, validationException.getErrorDTO().getErrorData());
  }

  @Test
  @Tag("verifyNonContinuousAndIntersectingRanges")
  @DisplayName("Verify exception for continuous and intersecting ranges")
  public void whenNonContinuousAndIntersectingRanges_thenThrowValidationException() {
    Range<Integer> range1 = Range.openClosed(55, 100);
    Range<Integer> range2 = Range.openClosed(51, 60);
    Range<Integer> range3 = Range.openClosed(0, 50);
    List<Range<Integer>> rangeList = new ArrayList<>();
    rangeList.add(range1);
    rangeList.add(range2);
    rangeList.add(range3);

    ValidationException validationException = assertThrows(ValidationException.class,
        () -> RangeValidationUtil.checkNonIntersectingContinuousRanges(rangeList)
    );
    assertEquals(RangeValidationUtil.ERROR_MESSAGE_NON_CONTINUOUS_RANGES, validationException.getErrorDTO().getErrorData());
  }

  @Test
  @Tag("verifyEmptyRanges")
  @DisplayName("Verify no exception for empty range list")
  public void whenEmptyRanges_thenDoesNotThrowValidationException() {
    assertDoesNotThrow(() -> RangeValidationUtil.checkNonIntersectingContinuousRanges(new ArrayList<>()));
  }
}
