package com.pemig.api.util;

import com.pemig.api.user.model.Role;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.format.DateTimeFormatter;

/**
 * Global named constants useful for our application.
 *
 * @author caleb
 */
public final class Const {

  private Const() {}
  public static final String AUTH_HEADER_BEARER_PREFIX = "Bearer" + " ";
  public static final String ALL_LOANS = "all_loans";
  public static final String ALL_CARDS = "all_cards";
  /** Tune this to affect how long the JWT token lasts. Default is 5 * 60 * 60, for 5 hours. */
  public static final long JWT_VALIDITY = (long)5 * 60 * 60; // 5 hours
  // Some constants for pagination and sorting
  public static final String DEFAULT_PAGE_IDX = "0";
  public static final String DEFAULT_PAGE_SIZE = "5";
  public static final String DEFAULT_SORT_BY_FIELD = "id";
  public static final String DEFAULT_SORT_ORDER = "ASC";

  // The following Strings are going to play the role of keys for our filter map.

  public static final String NAME_FILTER_STRING = "name";
  public static final String COLOR_FILTER_STRING = "color";
  public static final String STATUS_FILTER_STRING = "status";
  public static final String BEGIN_CREATION_DATE_FILTER_STRING = "begin_date_created";
  public static final String END_CREATION_DATE_FILTER_STRING = "end_date_created";
  public static final String CREATING_USER_FILTER_STRING = "created_by";

  // Two instances of SimpleGrantedAuthority corresponding to our user roles of Member or Admin.
  public static final SimpleGrantedAuthority ADMIN_AUTHORITY =
      new SimpleGrantedAuthority(Role.ADMIN.name());

  public static final SimpleGrantedAuthority MEMBER_AUTHORITY =
      new SimpleGrantedAuthority(Role.MEMBER.name());

  public static final SimpleGrantedAuthority CLIENT_AUTHORITY =
          new SimpleGrantedAuthority(Role.CLIENT.name());

  public static final SimpleGrantedAuthority CLIENT_MANAGER_AUTHORITY =
          new SimpleGrantedAuthority(Role.CLIENT_MANAGER.name());

  public static final SimpleGrantedAuthority ACCOUNT_MANAGER_AUTHORITY =
          new SimpleGrantedAuthority(Role.ACCOUNT_MANAGER.name());

  public static final SimpleGrantedAuthority PAYMENT_ADMIN_AUTHORITY =
          new SimpleGrantedAuthority(Role.PAYMENT_ADMIN.name());

  public static final SimpleGrantedAuthority REPORTING_AND_ANALYTICS_ADMIN_AUTHORITY =
          new SimpleGrantedAuthority(Role.REPORTING_AND_ANALYTICS_ADMIN.name());

  public static final SimpleGrantedAuthority LOAN_AGENT_ADMIN_AUTHORITY =
          new SimpleGrantedAuthority(Role.LOAN_AGENT_ADMIN.name());


  // Our global date-time pattern, with accuracy up to seconds.
  public static final String GLOBAL_DATE_TIME_PATTERN = "dd/MM/yyyy HH:mm:ss.SSS";

  public static final DateTimeFormatter DATE_TIME_FORMATTER =
          DateTimeFormatter.ofPattern(GLOBAL_DATE_TIME_PATTERN);
}
