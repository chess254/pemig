package com.logicea.cardtask.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Defines the default API response for unauthenticated calls to any endpoint. The response is
 * {@link HttpServletResponse#SC_UNAUTHORIZED}} and the message is &quot;Unauthorized&quot;
 *
 * @author caleb
 * @see AuthenticationEntryPoint
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

  @Serial private static final long serialVersionUID = -1324335464758L;

  @Override
  public void commence(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException authException)
      throws IOException {

    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
  }
}
