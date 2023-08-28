package com.logicea.cardtask.user.model;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

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
public class UserDto {

  @Schema(example = "user@cards.com")
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
}
