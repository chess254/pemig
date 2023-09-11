package com.pemig.api.account.repository;

import com.google.common.collect.Lists;
import com.pemig.api.account.Account;
import com.pemig.api.account.AccountStatus;
import com.pemig.api.user.service.AuthCheck;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.SortingOrder;
import com.pemig.api.util.exceptions.WrongDateFormatException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.pemig.api.util.Const.*;

@Repository
@RequiredArgsConstructor
public class AccountQueryParamsRepositoryImpl implements AccountQueryParamsRepository {

  private final AuthCheck authCheck;
  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Account> findAccountsByProvidedFilters(
          QueryParams params, User loggedInUser) throws WrongDateFormatException {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Account> criteriaQuery = criteriaBuilder.createQuery(Account.class);
    Root<Account> cardRoot = criteriaQuery.from(Account.class);
//predicates
    List<Predicate> predicates;
    try {
      predicates =
          extractPredicatesFromFilterParams(
              params.getFilterParams(), criteriaBuilder, cardRoot, loggedInUser);
    } catch (ParseException pe) {
      throw new WrongDateFormatException(pe.getMessage());
    }
    criteriaQuery.where(predicates.toArray(new Predicate[0]));

    criteriaQuery.orderBy(
        params.getSortingOrder() == SortingOrder.ASC
            ? criteriaBuilder.asc(cardRoot.get(params.getSortByField()))
            : criteriaBuilder.desc(
                cardRoot.get(
                    params
                        .getSortByField())));

    return entityManager
        .createQuery(criteriaQuery)
        .setMaxResults(params.getPageSize())
        .setFirstResult(params.getPage() * params.getPageSize())
        .getResultList();
  }
  private List<Predicate> extractPredicatesFromFilterParams(
          Map<String, String> params, CriteriaBuilder cb, Root<Account> root, User user)
      throws ParseException {
    if(params == null) {
      return Collections.emptyList();
    }
    List<Predicate> query = Lists.newArrayList();
    if (params.containsKey(NAME_FILTER_STRING)) {
      query.add(cb.equal(root.get("name"), params.get(NAME_FILTER_STRING)));
    }
    if (params.containsKey(COLOR_FILTER_STRING)) {
      query.add(cb.equal(root.get("color"), params.get(COLOR_FILTER_STRING)));
    }
    if (params.containsKey(STATUS_FILTER_STRING)) {
      query.add(
          cb.equal(root.get("status"), AccountStatus.valueOf(params.get(STATUS_FILTER_STRING))));
    }
    if (params.containsKey(BEGIN_CREATION_DATE_FILTER_STRING)) {
      query.add(
          cb.greaterThanOrEqualTo(
              root.get("createdDateTime"),
              LocalDateTime.parse(
                  params.get(BEGIN_CREATION_DATE_FILTER_STRING), DATE_TIME_FORMATTER)));
    }
    if (params.containsKey(END_CREATION_DATE_FILTER_STRING)) {
      query.add(
          cb.lessThanOrEqualTo(
              root.get("createdDateTime"),
              LocalDateTime.parse(
                  params.get(END_CREATION_DATE_FILTER_STRING), DATE_TIME_FORMATTER)));
    }
    if (params.containsKey(CREATING_USER_FILTER_STRING)) {
      query.add(cb.equal(root.get("createdBy"), params.get(CREATING_USER_FILTER_STRING)));
    }

    if (!authCheck.userIsAdmin(user)) {
      query.add(cb.equal(root.get("createdBy"), user.getUsername()));
    }
    return query;
  }
}
