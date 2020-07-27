package com.sysco.rps.util;

import com.sysco.rps.dto.pp.masterdata.BaseGlobalDTO;
import com.sysco.rps.dto.pp.masterdata.BaseOpCoDTO;
import com.sysco.rps.service.exception.ErrorCode;
import com.sysco.rps.service.exception.ValidationException;
import com.sysco.rps.validators.annotations.ValidDateFormat;
import io.swagger.annotations.ApiModelProperty;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Validator;
import javax.validation.constraints.NotNull;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CustomValidationUtilTest {

  @Autowired
  @Qualifier("CustomValidator")
  Validator validator;

  // this is a custom class which is used to check the constraint validations
  static class TestCustomDTO extends BaseGlobalDTO {
    // string with not null
    @NotNull(message = "opCo number can not be blank")
    @ApiModelProperty(example = "US0001")
    private String opCoNumber;

    // string with pattern
    @ValidDateFormat(pattern = "MM-dd-yyyy", message = "Date should be valid in the format MM-dd-yyyy")
    @ApiModelProperty(example = "12-23-2020")
    private String startDate;

    // attribute without any constraints
    private int total;

    public String getOpCoNumber() {
      return opCoNumber;
    }

    public void setOpCoNumber(String opCoNumber) {
      this.opCoNumber = opCoNumber;
    }

    public String getStartDate() {
      return startDate;
    }

    public void setStartDate(String startDate) {
      this.startDate = startDate;
    }

    public int getTotal() {
      return total;
    }

    public void setTotal(int total) {
      this.total = total;
    }
  }
  String validationMessage = "TestCustomDTO invalid";

  @Test
  @Tag("validateDTOConstraints")
  @DisplayName("Verify empty/null dto throw exceptions when validating")
  void verifyViolationsWithEmptyDTO() {
    // test null dto
    ValidationException nullTestCustomDTOException = assertThrows(ValidationException.class,
            () -> CustomValidationUtil.validateDTOConstraints(null, validationMessage, validator)
    );
    assertEquals(validationMessage, nullTestCustomDTOException.getMessage());
    assertEquals(ErrorCode.VALIDATION_FAILURE.getCode(), nullTestCustomDTOException.getErrorDTO().getCode());

    // test empty dto
    TestCustomDTO emptyTestCustomDTO = new TestCustomDTO();

    ValidationException emptyTestCustomDTOException = assertThrows(ValidationException.class,
            () -> CustomValidationUtil.validateDTOConstraints(emptyTestCustomDTO, validationMessage, validator)
    );
    assertEquals(validationMessage, emptyTestCustomDTOException.getMessage());
    assertEquals(ErrorCode.VALIDATION_FAILURE.getCode(), emptyTestCustomDTOException.getErrorDTO().getCode());
  }

  @Test
  @Tag("validateDTOConstraints")
  @DisplayName("Verify constraint violations are captured when validating")
  void validateDTOConstraintViolations() {
    TestCustomDTO violatingTestCustomDTO = new TestCustomDTO();
    violatingTestCustomDTO.setOpCoNumber(null); // invalid as cannot be null
    violatingTestCustomDTO.setStartDate("2020-12-13"); // invalid pattern

    ValidationException violatingTestCustomDTOException = assertThrows(ValidationException.class,
            () -> CustomValidationUtil.validateDTOConstraints(violatingTestCustomDTO, validationMessage, validator)
    );

    assertEquals(ErrorCode.VALIDATION_FAILURE.getCode(), violatingTestCustomDTOException.getErrorDTO().getCode());

    Map<String, String> errorData = (Map<String, String>) violatingTestCustomDTOException.getErrorDTO().getErrorData();
    assertEquals(2, errorData.size());
    assertEquals("opCo number can not be blank", errorData.get("opCoNumber"));
    assertEquals("Date should be valid in the format MM-dd-yyyy", errorData.get("startDate"));
  }

  @Test
  @Tag("validateDTOConstraints")
  @DisplayName("Verify valid dto with no constraint violations doesn't throw any errors")
  void validateDTONonViolations() {
    TestCustomDTO validTestCustomDTO = new TestCustomDTO();
    validTestCustomDTO.setOpCoNumber("US0001");
    validTestCustomDTO.setStartDate("12-13-2020");

    assertDoesNotThrow(() -> CustomValidationUtil.validateDTOConstraints(validTestCustomDTO, validationMessage, validator));
  }

  @Test
  @Tag("validateOpCoNumberMatchesWithDTO")
  @DisplayName("Verify validation exception is thrown when given opCo number mismatches with dto")
  void whenOpCoNumberMismatch_thenValidationException() {
    String opCoNumber = "US0001";
    BaseOpCoDTO baseOpCoDTO = new BaseOpCoDTO();
    baseOpCoDTO.setOpCoNumber("US0002");

    assertThrows(ValidationException.class, () -> CustomValidationUtil.validateOpCoNumberMatchesWithDTO(opCoNumber, baseOpCoDTO));
  }

  @Test
  @Tag("validateOpCoNumberMatchesWithDTO")
  @DisplayName("Verify validation exception is thrown when opCo number is not defined in the dto")
  void whenOpCoNumberNotDefinedInDTO_thenValidationException() {
    String opCoNumber = "US0001";
    BaseOpCoDTO baseOpCoDTO = new BaseOpCoDTO();
    assertThrows(ValidationException.class, () -> CustomValidationUtil.validateOpCoNumberMatchesWithDTO(opCoNumber, baseOpCoDTO));
  }

  @Test
  @Tag("validateOpCoNumberMatchesWithDTO")
  @DisplayName("Verify no exceptions are thrown when opCo number defined in the dto matches with the given opCo number")
  void whenOpCoNumberSameInDTO_thenNoException() {
    String opCoNumber = "US0001";
    BaseOpCoDTO baseOpCoDTO = new BaseOpCoDTO();
    baseOpCoDTO.setOpCoNumber("US0001");
    assertDoesNotThrow(() -> CustomValidationUtil.validateOpCoNumberMatchesWithDTO(opCoNumber, baseOpCoDTO));
  }
}
