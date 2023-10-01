package com.pemig.api.user.model;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pemig.api.util.Const;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO class for Users.
 * 
 * @see User
 * 
 * @author caleb
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class UserDto {

  @Schema(example = "user@pemig.com")
  @NonNull
  @NotBlank
  @Email
  @Size(min = 5, max = 100)
  private String email;

  @Schema(example = "password")
  @JsonProperty(access = WRITE_ONLY)
  @NonNull
  @ToString.Exclude
  @Size(min = 8, max = 20)
  private String password;

  @Schema(example = "ADMIN")
  @NonNull
  private Role role;

  @Schema(example = "John")
  @NonNull
  private String first_name;

  @Schema(example = "Doe")
  @NonNull
  private String last_name;

  @Schema(example = "Blow")
  @NonNull
  private String middle_name;

  @Schema(example = "12345678")
  @NonNull
  private String idNo;

  @Schema(example = "2023-08-28 09:43:43.000000")
  @DateTimeFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
  @JsonFormat(pattern = Const.GLOBAL_DATE_TIME_PATTERN)
//  @NonNull
  private LocalDateTime birth_date;

}
