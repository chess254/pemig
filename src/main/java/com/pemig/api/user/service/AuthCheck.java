package com.pemig.api.user.service;

import com.pemig.api.card.model.Card;
import com.pemig.api.loan.model.Loan;
import com.pemig.api.user.model.Role;
import com.pemig.api.util.Const;
import com.pemig.api.util.logger.Logs;
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
    return user.getAuthorities().contains(Const.MEMBER_AUTHORITY) && user.getAuthorities().size() == 1;
  }

  /**
   * Checks to see if a {@link  User} has ONLY the authorities of a {@link Role#CLIENT}
   * @param user A {@link User} instance.
   * @return {@literal true} iff the {@link User} has ONLY the authorities of a {@link Role#CLIENT}.
   */
  public boolean userIsClient(User user) {
    return user.getAuthorities().contains(Const.CLIENT_AUTHORITY) && user.getAuthorities().size() == 1;
  }

  /**
   * Checks to see if a {@link User} has ONLY the authorities of a {@link Role#ADMIN}.
   * @param user A {@link User} instance.
   * @return {@literal true} iff the {@link User} has ONLY the authorities of a {@link Role#ADMIN}.
   */
  public boolean userIsAdmin(User user) {
    return user.getAuthorities().contains(Const.ADMIN_AUTHORITY) && user.getAuthorities().size() == 1;
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

  public boolean userHasAccessToLoan(User user, Loan loan) {
    return userIsAdmin(user) || user.getUsername().equals(loan.getCreatedBy());
  }
}
