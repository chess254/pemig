package com.logicea.cardtask.card.repository;

import static com.logicea.cardtask.util.Const.*;

import com.google.common.collect.Lists;
import com.logicea.cardtask.card.model.Card;
import com.logicea.cardtask.card.model.Status;
import com.logicea.cardtask.user.service.AuthCheck;
import com.logicea.cardtask.util.QueryParams;
import com.logicea.cardtask.util.SortingOrder;
import com.logicea.cardtask.util.exceptions.WrongDateFormatException;
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
    if (params.containsKey(NAME_FILTER_STRING)) {
      query.add(cb.equal(root.get("name"), params.get(NAME_FILTER_STRING)));
    }
    if (params.containsKey(COLOR_FILTER_STRING)) {
      query.add(cb.equal(root.get("color"), params.get(COLOR_FILTER_STRING)));
    }
    if (params.containsKey(STATUS_FILTER_STRING)) {
      query.add(
          cb.equal(root.get("status"), Status.valueOf(params.get(STATUS_FILTER_STRING))));
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
