package com.pemig.api.loan.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.pemig.api.util.Const.GLOBAL_DATE_TIME_PATTERN;

/**
 * DTO class for loans.
 *
 * @author caleb
 * @see Loan
 */
@Data
@Builder
@EqualsAndHashCode
public class LoanDto {
  @Schema(example = "1", hidden = true)
  private Long id;

  @Schema(example = "John Doe")
  private String customerName;

  @Schema(example = "Loan Name")
  @Size(max = 50)
  private String name;

  @Schema(example = "10")
  private String customerId;

  @Schema(example = "5000")
  @Min(2000)
  @Max(20000)
  private BigDecimal principal;

  @Schema(example = "13.0")
  private float rate;

  @Schema(example = "30")
  private int time;

  @Schema(example = "1")
  private long loanDetailsId;

  @Schema(example = "Business Reg Url")
  private String businessRegDocUrl;

  @Schema(example = "id/passport url")
  private String idOrPassportUrl;

  @Schema(example = "Payslip url")
  private String payslipUrl;

  @Schema(example = "Brief Loan description")
  @Size(max = 100)
  private String description;

//  @Schema(hidden = true)
  @Schema(example = "APPLIED")
  private LoanStatus status;

  @DateTimeFormat(pattern = GLOBAL_DATE_TIME_PATTERN)
  @JsonFormat(pattern = GLOBAL_DATE_TIME_PATTERN)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime created;

  @Size(min = 5, max = 100)
  @Email
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String createdBy;

  @DateTimeFormat(pattern = GLOBAL_DATE_TIME_PATTERN)
  @JsonFormat(pattern = GLOBAL_DATE_TIME_PATTERN)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private LocalDateTime modified;

  @Size(min = 5, max = 100)
  @Email
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private String modifiedBy;
}
