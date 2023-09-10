package com.pemig.api.card.repository;

import com.google.common.collect.Lists;
import com.pemig.api.card.model.Card;
import com.pemig.api.card.model.Status;
import com.pemig.api.user.service.AuthCheck;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.SortingOrder;
import com.pemig.api.util.exceptions.WrongDateFormatException;
import com.pemig.api.util.Const;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CardQueryParamsRepositoryImpl implements CardQueryParamsRepository {

  private final AuthCheck authCheck;
  @PersistenceContext private EntityManager entityManager;

  @Override
  public List<Card> findCardsByProvidedFilters(
          QueryParams params, User loggedInUser) throws WrongDateFormatException {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Card> criteriaQuery = criteriaBuilder.createQuery(Card.class);
    Root<Card> cardRoot = criteriaQuery.from(Card.class);
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
          Map<String, String> params, CriteriaBuilder cb, Root<Card> root, User user)
      throws ParseException {
    if(params == null) {
      return Collections.emptyList();
    }
    List<Predicate> query = Lists.newArrayList();
    if (params.containsKey(Const.NAME_FILTER_STRING)) {
      query.add(cb.equal(root.get("name"), params.get(Const.NAME_FILTER_STRING)));
    }
    if (params.containsKey(Const.COLOR_FILTER_STRING)) {
      query.add(cb.equal(root.get("color"), params.get(Const.COLOR_FILTER_STRING)));
    }
    if (params.containsKey(Const.STATUS_FILTER_STRING)) {
      query.add(
          cb.equal(root.get("status"), Status.valueOf(params.get(Const.STATUS_FILTER_STRING))));
    }
    if (params.containsKey(Const.BEGIN_CREATION_DATE_FILTER_STRING)) {
      query.add(
          cb.greaterThanOrEqualTo(
              root.get("createdDateTime"),
              LocalDateTime.parse(
                  params.get(Const.BEGIN_CREATION_DATE_FILTER_STRING), Const.DATE_TIME_FORMATTER)));
    }
    if (params.containsKey(Const.END_CREATION_DATE_FILTER_STRING)) {
      query.add(
          cb.lessThanOrEqualTo(
              root.get("createdDateTime"),
              LocalDateTime.parse(
                  params.get(Const.END_CREATION_DATE_FILTER_STRING), Const.DATE_TIME_FORMATTER)));
    }
    if (params.containsKey(Const.CREATING_USER_FILTER_STRING)) {
      query.add(cb.equal(root.get("createdBy"), params.get(Const.CREATING_USER_FILTER_STRING)));
    }

    if (!authCheck.userIsAdmin(user)) {
      query.add(cb.equal(root.get("createdBy"), user.getUsername()));
    }
    return query;
  }
}
