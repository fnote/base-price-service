package com.sysco.rps.util;

import static com.sysco.rps.service.exception.ErrorCode.RANGE_VALIDATION_FAILURE;

import com.google.common.collect.Range;
import com.sysco.rps.dto.ErrorDTO;
import com.sysco.rps.service.exception.ValidationException;
import java.text.MessageFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RangeValidationUtil {

  private static final String VALIDATION_FAILURE_MESSAGE = "Band range validation failure";
  public static final String ERROR_MESSAGE_LOWER_BOUND_AND_UPPER_BOUND_EQUALS = "Lower Bound {0} is Equal to Upper Bound {1}";
  public static final String ERROR_MESSAGE_LOWER_BOUND_GREATER_THAN_UPPER_BOUND = "Lower Bound {0} is Greater than Upper Bound {1}";
  public static final String ERROR_MESSAGE_DUPLICATE_RANGES = "Duplicate ranges";
  public static final String ERROR_MESSAGE_NON_CONTINUOUS_RANGES = "Non-continuous Ranges";
  public static final String ERROR_MESSAGE_INTERSECTING_RANGES = "Intersecting Ranges";


  private RangeValidationUtil() {
  }

  public static Range<Integer> convertToOpenClosedRange(Integer lowerBound, Integer upperBound) throws ValidationException {
    try {
      Range<Integer> range = Range.openClosed(lowerBound, upperBound);
      if (range.isEmpty()) {
        ErrorDTO errorDTO = new ErrorDTO(RANGE_VALIDATION_FAILURE.getCode(), RANGE_VALIDATION_FAILURE.getDescription());
        errorDTO.setErrorData(MessageFormat.format(ERROR_MESSAGE_LOWER_BOUND_AND_UPPER_BOUND_EQUALS, lowerBound, upperBound));
        throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
      }
      return range;
    } catch (IllegalArgumentException e) {
      ErrorDTO errorDTO = new ErrorDTO(RANGE_VALIDATION_FAILURE.getCode(), RANGE_VALIDATION_FAILURE.getDescription());
      errorDTO.setErrorData(MessageFormat.format(ERROR_MESSAGE_LOWER_BOUND_GREATER_THAN_UPPER_BOUND, lowerBound, upperBound));
      throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
    }
  }

  public static void checkDuplicateRanges(List<Range<Integer>> rangeList) throws ValidationException {
    Set<Range<Integer>> rangeSet = new HashSet<>(rangeList);
    if (rangeList.size() != rangeSet.size()) {
      ErrorDTO errorDTO = new ErrorDTO(RANGE_VALIDATION_FAILURE.getCode(), RANGE_VALIDATION_FAILURE.getDescription());
      errorDTO.setErrorData(ERROR_MESSAGE_DUPLICATE_RANGES);
      throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
    }
  }

  public static void checkNonIntersectingContinuousRanges(List<Range<Integer>> rangeList) throws ValidationException {
    rangeList.sort(Comparator.comparing(Range::lowerEndpoint));
    if (rangeList.isEmpty()) return;
    Range<Integer> range = rangeList.get(0);
    for (int i = 1; i < rangeList.size(); i++) {
      try {
        Range<Integer> gap = range.gap(rangeList.get(i));
        range = rangeList.get(i);
        if (!gap.isEmpty()) {
          ErrorDTO errorDTO = new ErrorDTO(RANGE_VALIDATION_FAILURE.getCode(), RANGE_VALIDATION_FAILURE.getDescription());
          errorDTO.setErrorData(ERROR_MESSAGE_NON_CONTINUOUS_RANGES);
          throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
        }
      } catch (IllegalArgumentException e) {
        ErrorDTO errorDTO = new ErrorDTO(RANGE_VALIDATION_FAILURE.getCode(), RANGE_VALIDATION_FAILURE.getDescription());
        errorDTO.setErrorData(ERROR_MESSAGE_INTERSECTING_RANGES);
        throw new ValidationException(VALIDATION_FAILURE_MESSAGE, errorDTO);
      }
    }
  }
}
