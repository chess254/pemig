package com.pemig.api.auth;

import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;


@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class JwtResp implements Serializable {

  @Serial private static final long serialVersionUID = -5647382910L;
  //TODO: more research on these annotations
  @ToString.Exclude private final String jwtToken;
}
