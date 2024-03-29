package com.pemig.api.audit;

import java.util.Optional;

import com.pemig.api.config.JpaConfig;
import lombok.NonNull;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

/**
 * The implementation of {@link AuditorAware} that we use in our application.
 * 
 * @author caleb
 * 
 * @see JpaConfig
 */
public class AuditorAwareImpl implements AuditorAware<String> {

  @Override
  public @NonNull Optional<String> getCurrentAuditor() {
    //TODO: fix this to stop returning dummy data <String cannot be cast into UserDetails> when trying to extract user name for auditing
    return Optional.of(
        ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .getUsername());
  }
}