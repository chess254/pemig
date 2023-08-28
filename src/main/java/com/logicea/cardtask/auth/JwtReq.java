package com.logicea.cardtask.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class JwtReq implements Serializable {

  private static final long serialVersionId = 97867564534231L;

  @Schema(example = "user@cards.com")
  @NonNull
  @NotBlank
  @Email
  private String email;

  @Schema(example = "password")
  @Size(min = 8, max = 30)
  @NonNull
  @ToString.Exclude
  private String password;
}
