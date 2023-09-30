package com.pemig.api.unit.service.cards;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pemig.api.card.model.Card;
import com.pemig.api.card.model.CardDto;
import com.pemig.api.card.model.Status;
import com.pemig.api.card.repository.CardRepository;
import com.pemig.api.card.service.CardDtoToCard;
import com.pemig.api.user.service.AuthCheck;
import com.pemig.api.card.service.CardService;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.PaginationTestRunner;
import com.pemig.api.util.SortingOrder;
import com.pemig.api.util.exceptions.LoanNotFoundException;
import com.pemig.api.util.exceptions.UnauthorizedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

import com.pemig.api.util.Const;
import com.pemig.api.util.TestUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Unit tests for {@link CardService}. Perform extensive use of Mockito and junit4 assertions. 
 * 
 * @author caleb
 * 
 * @see PaginationTestRunner
 */
@RunWith(MockitoJUnitRunner.class)
public class CardServiceUnitTests {

  @Mock private CardRepository cardRepository;

  @Mock private AuthCheck authCheck;

  @Mock
  private CardDtoToCard cardDtoToCard;

  @InjectMocks private CardService cardService;
  
  private static final CardDto CARD_DTO =
      CardDto.builder()
          .id(1L)
          .name("CARD-1")
          .description("A test card with ID 1")
          .status(Status.TODO)
          .color("#B70AC1")
          .build();

  private static final Card CARD_ENTITY =
      Card.builder()
          .id(CARD_DTO.getId())
          .name(CARD_DTO.getName())
          .description(CARD_DTO.getDescription())
          .status(CARD_DTO.getStatus())
          .color(CARD_DTO.getColor())
          .build();

  /* GET by ID tests */

  @Test
  public void whenRepoGetsAnEntityByIdSuccessfully_thenRelevantDtoIsReturned() {
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.of(CARD_ENTITY));
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);
    when(authCheck.userHasAccessToCard(TestUtils.ADMIN_USER, CARD_ENTITY)).thenReturn(true);
    Assertions.assertTrue(TestUtils.cardDtoAndEntityEqual(cardService.getCard(CARD_DTO.getId()), CARD_ENTITY));
  }

  @Test(expected = LoanNotFoundException.class)
  public void whenRepoCannotFindTheCard_thenCardNotFoundExceptionIsThrown() {
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.empty());
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);
    cardService.getCard(CARD_DTO.getId());
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenUserHasInsufficientPrivilegesToGetCard_thenInsufficientPrivilegesExceptionIsThrown() {
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.of(CARD_ENTITY));
    SecurityContextHolder.getContext().setAuthentication(TestUtils.MEMBER_ONE_UPAT);
    when(authCheck.userHasAccessToCard(TestUtils.MEMBER_ONE_USER, CARD_ENTITY)).thenReturn(false);
    cardService.getCard(CARD_DTO.getId());
  }

  /* Aggregate GET tests */

  @Test
  public void whenRepoReturnsASortedPage_andNoFiltersAreUsed_thenThatPageIsReturned() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);

    // There are 20 Cards total.

    // First, test a page with 20 cards.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(1)
        .pageSize(20)
        .pojoType(CardDto.class)
        .expectedPageSizes(Collections.singletonList(20))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Second, test 2 pages of 10 cards each.
    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(2)
        .pageSize(10)
        .pojoType(CardDto.class)
        .expectedPageSizes(List.of(10, 10))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Third, test 4 pages of 5 cards each.
    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(4)
        .pageSize(5)
        .pojoType(CardDto.class)
        .expectedPageSizes(List.of(5, 5, 5, 5))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Fourth and final, test 7 pages with 3, 3, 3, 3, 3, 3 and 2 cards respectively.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(7)
        .pageSize(3)
        .pojoType(CardDto.class)
        .expectedPageSizes(List.of(3, 3, 3, 3, 3, 3, 2))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);
  }

  @SuppressWarnings("unchecked")
  private void testPaginatedAndSortedAggregateGet(
          QueryParams params, int expectedNumEntries) {
    Integer page = params.getPage();
    Integer pageSize = params.getPageSize();
    String sortByField = params.getSortByField();
    SortingOrder sortingOrder = params.getSortingOrder();
    Predicate<Card> filter = Optional.ofNullable((Predicate<Card>) params.getPredicate()).orElse(cardEntity -> true);
    List<Card> sortedList =
        TestUtils.CARD_ENTITIES.stream()
            .filter(filter)
            .sorted((t1, t2) -> TestUtils.compareFieldsInGivenOrder(t1, t2, sortByField, sortingOrder))
            .toList();
    List<Card> slicedList =
        sortedList.subList(
                page * pageSize,
                Math.min(pageSize * page + pageSize, sortedList.size()));
    mockWithExpectedList(params, slicedList);
    makePaginationAndSortingQueryAssertions(
        cardService.getAllCardsByFilter(params), expectedNumEntries, sortByField, sortingOrder);
  }

  private void mockWithExpectedList(QueryParams params, List<Card> expectedList) {
    when(cardRepository.findCardsByProvidedFilters(params, TestUtils.ADMIN_USER)).thenReturn(expectedList);
    // Mocking the accessCheckService just for consistency, we've logged in as ADMINs anyhow for
    // these tests.
    when(authCheck.userIsMember(TestUtils.ADMIN_USER)).thenReturn(false);
  }

  private void makePaginationAndSortingQueryAssertions(
      List<CardDto> returnedDtos, int expectedNumEntries, String sortByField, SortingOrder sortingOrder) {
    assertEquals(expectedNumEntries, returnedDtos.size());
    Assertions.assertTrue(TestUtils.collectionIsSortedByFieldInGivenDirection(returnedDtos, sortByField, sortingOrder));
  }

  @Test
  public void whenRepoReturnsASortedPage_andNameFilterIsUsed_thenThatPageIsReturned() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);

    // There are 9 cards that share the same name.

    // First, test a page with 9 cards.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(1)
        .pageSize(9)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity->cardEntity.getName().equals(TestUtils.NAME))
        .expectedPageSizes(List.of(9))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Second, test 3 pages with 3 cards each.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(3)
        .pageSize(3)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity->cardEntity.getName().equals(TestUtils.NAME))
        .expectedPageSizes(List.of(3, 3, 3))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Third and final, test 3 pages, with 4 + 4 + 1 cards each, respectively.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(3)
        .pageSize(4)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity->cardEntity.getName().equals(TestUtils.NAME))
        .expectedPageSizes(List.of(4, 4, 1))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);
  }

  @Test
  public void whenRepoReturnsASortedPage_andColorFilterIsUsed_thenThatPageIsReturned() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);

    // There are 5 cards that share the same color.

    // First, test a page with 5 cards.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(1)
        .pageSize(5)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> cardEntity.getColor().equals(TestUtils.COLOR))
        .expectedPageSizes(Collections.singletonList(5))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Second, test 2 pages, one with 3 and another one with 2 cards.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(2)
        .pageSize(3)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> cardEntity.getColor().equals(TestUtils.COLOR))
        .expectedPageSizes(List.of(3, 2))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Third and final, test 3 pages, with 2 + 2 + 1 cards each, respectively.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(3)
        .pageSize(2)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> cardEntity.getColor().equals(TestUtils.COLOR))
        .expectedPageSizes(List.of(2, 2, 1))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);
  }

  @Test
  public void whenRepoReturnsASortedPage_andStatusFilterIsUsed_thenThatPageIsReturned() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);

    // There are 8 cards that share the same status.

    // First, test a page with 8 cards.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(1)
        .pageSize(8)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> cardEntity.getStatus().equals(TestUtils.STATUS))
        .expectedPageSizes(Collections.singletonList(8))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Second, test 2 pages, one with 5 and another one with 3 cards.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(2)
        .pageSize(5)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> cardEntity.getStatus().equals(TestUtils.STATUS))
        .expectedPageSizes(List.of(5, 3))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);
  }

  @Test
  public void whenRepoReturnsASortedPage_andDateOfCreationFiltersAreUsed_thenThatPageIsReturned() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);

    // There are 5 cards created in the set {now() - 5min, now() - 4min, ... now() - 1min}.

    // First, test a page with 5 cards.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(1)
        .pageSize(5)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> creationDateBetween(cardEntity.getCreatedDateTime(),
                LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)
                        .minusMinutes(5),  LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)))
        .expectedPageSizes(Collections.singletonList(5))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Second, test 2 pages, one with 4 and another one with 1 card.

    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(2)
        .pageSize(4)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> creationDateBetween(cardEntity.getCreatedDateTime(),
                  LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)
                      .minusMinutes(5),  LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)))
        .expectedPageSizes(List.of(4, 1))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);
  }

  private boolean creationDateBetween(
      LocalDateTime creationDateTime,
      LocalDateTime fromDateTimeIncl,
      LocalDateTime toDateTimeIncl) {
    assert !toDateTimeIncl.isBefore(fromDateTimeIncl);
    return creationDateTime.isEqual(fromDateTimeIncl)
        || creationDateTime.isEqual(toDateTimeIncl)
        || creationDateTime.isAfter(fromDateTimeIncl) && creationDateTime.isBefore(toDateTimeIncl);
  }

  @Test
  public void
      whenRepoReturnsASortedPage_andStatusAndDateOfCreationFiltersAreUsed_thenThatPageIsReturned() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);

    // There are 3 cards created within 5 minutes of NYE 2023 with a status of IN_PROGRESS.

    // First, test a page of all 3 cards.
    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(1)
        .pageSize(3)
        .pojoType(CardDto.class)
        .filteringPredicate(cardEntity -> creationDateBetween(cardEntity.getCreatedDateTime(), 
                LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)
                .minusMinutes(5), LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)) &&
                cardEntity.getStatus().equals(TestUtils.STATUS))
        .expectedPageSizes(Collections.singletonList(3))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);

    // Second, test 2 pages, 1 with 2 and 1 with 1 card.
    PaginationTestRunner.<CardDto, Card>builder()
        .totalPages(2)
        .pageSize(2)
        .pojoType(CardDto.class)
            .filteringPredicate(cardEntity -> creationDateBetween(cardEntity.getCreatedDateTime(),
                    LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)
                            .minusMinutes(5), LocalDateTime.parse("01/01/2023 00:00:00.000", Const.DATE_TIME_FORMATTER)) &&
                    cardEntity.getStatus().equals(TestUtils.STATUS))
        .expectedPageSizes(List.of(2, 1))
        .build()
        .runTest(this::testPaginatedAndSortedAggregateGet);
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenAttemptingToGetADifferentUsersCards_whileNotAdmin_thenInsufficientPrivilegesExceptionIsThrown() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.MEMBER_ONE_UPAT);
    QueryParams paramsWithOtherUsersEmail =
        QueryParams.builder()
            .filterParams(Map.of(Const.CREATING_USER_FILTER_STRING, TestUtils.ADMIN_EMAIL))
            .build();
    // Mocking just for consistency, since this would return true anyhow.
    when(authCheck.userIsMember(TestUtils.MEMBER_ONE_USER)).thenReturn(true);
    cardService.getAllCardsByFilter(paramsWithOtherUsersEmail);
  }

  /* POST tests */

  @Test
  public void whenRepoPersistsACardSuccessfully_thenTheCardIsReturned() {
    when(cardRepository.save(any(Card.class))).thenReturn(CARD_ENTITY);
    Assertions.assertTrue(TestUtils.cardDtosEqual(CARD_DTO, cardService.storeCard(CARD_DTO)));
  }

  /* DELETE tests */

  @Test
  public void whenRepoFindsTheCardById_andUserHasAccessToIt_thenDeleteShouldWork() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.of(CARD_ENTITY));
    when(authCheck.userHasAccessToCard(TestUtils.ADMIN_USER, CARD_ENTITY)).thenReturn(true);
    doNothing().when(cardRepository).deleteById(CARD_DTO.getId());
    cardService.deleteCard(CARD_DTO.getId());
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenRepoFindsTheCardById_andUserDoesNotHaveAccessToIt_thenInsufficientPrivilegesExceptionIsThrown() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.MEMBER_ONE_UPAT);
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.of(CARD_ENTITY));
    when(authCheck.userHasAccessToCard(TestUtils.MEMBER_ONE_USER, CARD_ENTITY)).thenReturn(false);
    cardService.deleteCard(CARD_DTO.getId());
  }

  @Test(expected = LoanNotFoundException.class)
  public void whenRepoCannotFindTheCardWeWantToDelete_thenCardNotFoundExceptionIsThrown() {
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.empty());
    cardService.deleteCard(CARD_DTO.getId());
  }

  /* PUT tests */

  @Test
  public void whenRepoFindsTheEntityToReplace_andUserHasAccess_thenReplacementIsReturned() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);
    CardDto replacementCard =
        CardDto.builder()
            .id(CARD_DTO.getId())
            .name(CARD_DTO.getName() + " - REPLACEMENT")
            .status(Status.IN_PROGRESS)
            .build();
    when(cardRepository.findById(replacementCard.getId())).thenReturn(Optional.of(CARD_ENTITY));
    when(authCheck.userHasAccessToCard(TestUtils.ADMIN_USER, CARD_ENTITY)).thenReturn(true);
    when(cardRepository.save(any(Card.class)))
        .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    Assertions.assertTrue(
        TestUtils.cardDtosEqual(
            replacementCard, cardService.replaceCard(replacementCard.getId(), replacementCard)));
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenRepoFindsTheEntityToReplace_butUserDoesNotHaveAccess_thenInsufficientPrivilegesExceptionIsThrown() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.MEMBER_ONE_UPAT);
    CardDto replacementCard =
        CardDto.builder()
            .id(CARD_DTO.getId())
            .name(CARD_DTO.getName() + " - REPLACEMENT")
            .status(Status.IN_PROGRESS)
            .build();
    when(cardRepository.findById(replacementCard.getId())).thenReturn(Optional.of(CARD_ENTITY));
    when(authCheck.userHasAccessToCard(TestUtils.MEMBER_ONE_USER, CARD_ENTITY)).thenReturn(false);
    cardService.replaceCard(replacementCard.getId(), replacementCard);
  }

  @Test(expected = LoanNotFoundException.class)
  public void whenRepoCannotFindTheEntityToReplace_thenCardNotFoundExceptionIsThrown() {
    CardDto replacementCard =
        CardDto.builder()
            .id(CARD_DTO.getId())
            .name(CARD_DTO.getName() + " - REPLACEMENT")
            .status(Status.IN_PROGRESS)
            .build();
    when(cardRepository.findById(replacementCard.getId())).thenReturn(Optional.empty());
    cardService.replaceCard(replacementCard.getId(), replacementCard);
  }

  /* PATCH tests */
  
  @Test
  public void whenRepoFindsTheEntityToUpdate_andUserHasAccess_thenOnlySpecifiedFieldsAreUpdated(){
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);
    CardDto patch = CardDto.builder()
            .id(CARD_DTO.getId())
            .name("PATCH")
            .description("A patch to card with ID: " + CARD_DTO.getId())
            .build();
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.of(CARD_ENTITY));
    when(authCheck.userHasAccessToCard(TestUtils.ADMIN_USER, CARD_ENTITY)).thenReturn(true);
    doAnswer(invocationOnMock -> {
      CardDto cardDto = invocationOnMock.getArgument(0);
      Card card = invocationOnMock.getArgument(1);
      card.setName(cardDto.getName());
      card.setDescription(cardDto.getDescription());
      return null;
    }).when(cardDtoToCard).updateEntityFromDto(patch, CARD_ENTITY);
    when(cardRepository.save(any(Card.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));
    Assertions.assertTrue(TestUtils.cardDtosEqual(
            CardDto.builder()
                    .id(CARD_DTO.getId())
                    .name(patch.getName())
                    .description(patch.getDescription())
                    .status(CARD_ENTITY.getStatus())
                    .color(CARD_ENTITY.getColor())
                    .build(), cardService.updateCard(patch.getId(), patch)));
  }
  
  @Test(expected = UnauthorizedException.class)
  public void whenRepoFindsTheEntityToUpdate_butUserDoesNotHaveAccess_thenInsufficientPrivilegesExceptionIsThrown(){
    SecurityContextHolder.getContext().setAuthentication(TestUtils.MEMBER_ONE_UPAT);
    CardDto patch = CardDto.builder()
            .id(CARD_DTO.getId())
            .name("PATCH")
            .description("A patch to card with ID: " + CARD_DTO.getId())
            .build();
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.of(CARD_ENTITY));
    when(authCheck.userHasAccessToCard(TestUtils.MEMBER_ONE_USER, CARD_ENTITY)).thenReturn(false);
    cardService.updateCard(CARD_DTO.getId(), patch);
  }
  
  @Test(expected = LoanNotFoundException.class)
  public void whenRepoCannotFindTheEntityToUpdate_thenCardNotFoundExceptionIsThrown(){
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);
    CardDto patch = CardDto.builder()
            .id(CARD_DTO.getId())
            .name("PATCH")
            .description("A patch to card with ID: " + CARD_DTO.getId())
            .build();
    when(cardRepository.findById(CARD_DTO.getId())).thenReturn(Optional.empty());
    cardService.updateCard(CARD_DTO.getId(), patch);
  }
}
