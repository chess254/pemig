package com.logicea.cardtask.user.service;

import static com.logicea.cardtask.util.Const.ADMIN_AUTHORITY;
import static com.logicea.cardtask.util.Const.MEMBER_AUTHORITY;

import com.logicea.cardtask.card.model.Card;
import com.logicea.cardtask.user.model.Role;
import com.logicea.cardtask.util.logger.Logs;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


@Service
@Logs
public class AuthCheck {

  /**
   * Checks to see if a {@link User} has ONLY the authorities of a {@link Role#MEMBER}.
   * @param user A {@link User} instance.
   * @return {@literal true} iff the {@link User} has ONLY the authorities of a {@link Role#MEMBER}.
   */
  public boolean userIsMember(User user) {
    return user.getAuthorities().contains(MEMBER_AUTHORITY) && user.getAuthorities().size() == 1;
  }

  /**
   * Checks to see if a {@link User} has ONLY the authorities of a {@link Role#ADMIN}.
   * @param user A {@link User} instance.
   * @return {@literal true} iff the {@link User} has ONLY the authorities of a {@link Role#ADMIN}.
   */
  public boolean userIsAdmin(User user) {
    return user.getAuthorities().contains(ADMIN_AUTHORITY) && user.getAuthorities().size() == 1;
  }

  /**
   * Checks to see if a {@link User} has access to a specific card.
   * @param user A {@link User} instance.
   * @param card A {@link Card} instance pulled from our database.
   * @return {@literal true} iff the {@link User} has access to the card.
   */
  public boolean userHasAccessToCard(User user, Card card) {
    return userIsAdmin(user) || user.getUsername().equals(card.getCreatedBy());
  }
}
