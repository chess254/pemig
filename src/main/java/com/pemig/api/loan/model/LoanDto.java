package com.pemig.api.loan.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

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

  @Schema(example = "Loan Name")
  @Size(max = 50)
  private String name;

  @Schema(example = "Brief Loan description")
  @Size(max = 100)
  private String description;

  @Schema(example = "#111111")
  @Pattern(
      regexp = "^#[a-fA-F0-9]{6}$|^$",
      message = "Hex colour code. Must start with #, followed by exactly six hexadecimal characters(0 to 9, A to F) e.g #059ACF",
      flags = Pattern.Flag.CASE_INSENSITIVE)
  private String color;

  @Schema(hidden = true)
  @Builder.Default private LoanStatus status = LoanStatus.TODO;

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
