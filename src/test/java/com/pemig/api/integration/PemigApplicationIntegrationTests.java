package com.pemig.api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import com.pemig.api.card.controller.CardController;
import com.pemig.api.auth.controller.JwtAuthController;
import com.pemig.api.card.model.CardDto;
import com.pemig.api.card.model.Status;
import com.pemig.api.auth.JwtReq;
import com.pemig.api.auth.JwtResp;
import com.pemig.api.user.model.UserDto;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.SortingOrder;
import com.pemig.api.util.exceptions.*;
//import com.logicea.cardtask.util.exceptions.*;
import com.pemig.api.util.Const;
import com.pemig.api.util.TestUtils;
import io.micrometer.common.util.StringUtils;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.NoArgsConstructor;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
@NoArgsConstructor
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
@SuppressWarnings("ConstantConditions")
public class PemigApplicationIntegrationTests {

  @Autowired private CardController cardController;
  @Autowired private JwtAuthController authenticationController;
  
  /* ********************* */
  /* Authentication Tests */
  /* ********************* */

  @Test
  public void whenRegisteringAUserSuccessfully_thenTheUserIsReturned() {
    // Test registering an admin.
    ResponseEntity<UserDto> responseEntity = authenticationController.registerUser(TestUtils.ADMIN_DTO);
    registrationAssertions(
        responseEntity.getStatusCode(),
        TestUtils.ADMIN_DTO,
        Objects.requireNonNull(responseEntity.getBody()));

    // Registration.
    responseEntity = authenticationController.registerUser(TestUtils.MEMBER_ONE_DTO);
    registrationAssertions(
        responseEntity.getStatusCode(),
        TestUtils.MEMBER_ONE_DTO,
        Objects.requireNonNull(responseEntity.getBody()));
  }

  private void registrationAssertions(HttpStatusCode statusCode, UserDto userOne, UserDto userTwo) {
    assertEquals(HttpStatus.CREATED, statusCode);
    assertEquals(userOne.getEmail(), userTwo.getEmail());
    assertEquals(userOne.getRole(), userTwo.getRole());
  }

  @Test
  public void whenRegisteringAndThenAuthenticating_thenTokenIsReturned() {
    //admin.
    registerThenAuthenticateThenMakeAssertions(TestUtils.ADMIN_DTO, TestUtils.ADMIN_EMAIL, TestUtils.ADMIN_PASSWORD);
    //ember.
    registerThenAuthenticateThenMakeAssertions(
        TestUtils.MEMBER_ONE_DTO, TestUtils.MEMBER_ONE_EMAIL, TestUtils.MEMBER_ONE_PASSWORD);
  }

  private void registerThenAuthenticateThenMakeAssertions(
      UserDto userDto, String email, String password) {
    authenticationController.registerUser(userDto);
    ResponseEntity<JwtResp> responseEntity =
        authenticationController.authenticate(new JwtReq(email, password));
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertTrue(
        StringUtils.isNotBlank(Objects.requireNonNull(responseEntity.getBody()).getJwtToken()));
  }

  @Test(expected = EmailExistsException.class)
  public void whenRegisteringTheSameEmailTwice_thenEmailAlreadyInDatabaseExceptionIsThrown() {
    authenticationController.registerUser(TestUtils.ADMIN_DTO);
    authenticationController.registerUser(TestUtils.ADMIN_DTO);
  }

  @Test(expected = BadCredentialsException.class)
  public void whenAuthenticatingWithNonExistentUsername_thenBadCredentialsExceptionIsThrown() {
    authenticationController.registerUser(TestUtils.ADMIN_DTO);
    authenticationController.authenticate(new JwtReq("memes@memecompany.com", TestUtils.ADMIN_PASSWORD));
  }

  @Test(expected = BadCredentialsException.class)
  public void whenAuthenticatingWithAWrongPassword_thenBadCredentialsExceptionIsThrown() {
    authenticationController.registerUser(TestUtils.ADMIN_DTO);
    authenticationController.authenticate(new JwtReq(TestUtils.ADMIN_EMAIL, TestUtils.ADMIN_PASSWORD + "memes"));
  }

  /* *********** */
  /* Cards
  /* ********** */


  @Test
  public void whenPostingACard_thenTheCardIsReturned() {
    // Test for admins.
    adminLogin();
    CardDto cardOneDto = CardDto.builder()
            .id(1L)
            .name("TST-1")
            .description("desc")
            .color("#45A891")
            .build();
    ResponseEntity<EntityModel<CardDto>> responseEntity = cardController.postCard(cardOneDto);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    Assertions.assertTrue(
        TestUtils.cardDtosEqual(cardOneDto, responseEntity.getBody().getContent()));

    // Test for members.
    memberOneLogin();
    CardDto cardTwoDto = CardDto.builder()
            .id(2L)
            .name("TST-2")
            .description("desc")
            .color("#111111")
            .build();
    responseEntity = cardController.postCard(cardTwoDto);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    Assertions.assertTrue(TestUtils.cardDtosEqual(cardTwoDto, responseEntity.getBody().getContent()));
  }

  private void adminLogin() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.ADMIN_UPAT);
  }

  private void memberOneLogin() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.MEMBER_ONE_UPAT);
  }

  @Test
  public void whenPostingACard_thenGettingItByItsGeneratedIdRetrievesTheCard() {
    adminLogin();
    CardDto cardDtoOne = CardDto.builder()
            .id(1L)
            .name("TST-1")
            .color("#45C909")
            .description("Such a nice card!")
            .build();
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(cardDtoOne);
    final Long generatedIdOne = Objects.requireNonNull(postResponse.getBody().getContent()).getId();
    ResponseEntity<EntityModel<CardDto>> getOneResponse = cardController.getCard(generatedIdOne);
    assertEquals(HttpStatus.OK, getOneResponse.getStatusCode());
    CardDto retrievedCard = getOneResponse.getBody().getContent();
    Assertions.assertTrue(TestUtils.cardDtosEqual(cardDtoOne, retrievedCard));
    memberOneLogin();
    CardDto carDtoTwo = CardDto.builder()
            .id(2L)
            .name("TST-2")
            .color("#56AA91")
            .description("Another nice card.")
            .build();
    postResponse = cardController.postCard(carDtoTwo);
    final Long generatedIdTwo = Objects.requireNonNull(postResponse.getBody().getContent()).getId();
    ResponseEntity<EntityModel<CardDto>> getTwoResponse = cardController.getCard(generatedIdTwo);
    assertEquals(HttpStatus.OK, getTwoResponse.getStatusCode());
    Assertions.assertTrue(
        TestUtils.cardDtosEqual(carDtoTwo, getTwoResponse.getBody().getContent()));
  }

  @Test
  public void whenPostingACardWithAStatusNotTODO_thenThatStatusIsIgnored() {
    adminLogin();
    CardDto cardDto = CardDto.builder().name("Test LoanDetails").status(Status.IN_PROGRESS).build();
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(cardDto);
    assertEquals(Status.TODO, postResponse.getBody().getContent().getStatus());
  }

  @Test(expected = UnauthorizedException.class)
  public void whenPostingACardAsAdmin_andThenLoggingInAMember_thenAccessToTheCardIsForbidden() {
    adminLogin();
    CardDto cardDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(cardDto);
    final Long postedCardId = postResponse.getBody().getContent().getId();
    memberOneLogin();
    cardController.getCard(postedCardId);
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenPostingACardAsMember_andThenLoggingInAsDifferentMember_thenAccessToTheCardIsForbidden() {
    memberOneLogin();
    CardDto cardDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(cardDto);
    final Long postedCardId = postResponse.getBody().getContent().getId();
    memberTwoLogin();
    cardController.getCard(postedCardId);
  }

  private void memberTwoLogin() {
    SecurityContextHolder.getContext().setAuthentication(TestUtils.MEMBER_TWO_UPAT);
  }

  @Test
  public void whenPostingACardAsMember_andThenLoggingInAsAdmin_thenAccessToTheCardIsAllowed() {
    memberOneLogin();
    CardDto cardDto = CardDto.builder()
            .id(1L)
            .name("TST")
            .description("desc")
            .color("#45FF90")
            .build();
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(cardDto);
    final Long postedCardId = postResponse.getBody().getContent().getId();
    adminLogin();
    ResponseEntity<EntityModel<CardDto>> getResponse = cardController.getCard(postedCardId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    Assertions.assertTrue(TestUtils.cardDtosEqual(cardDto, getResponse.getBody().getContent()));
  }

  @Test(expected = LoanNotFoundException.class)
  public void whenTryingToGetACardThatHasNotBeenPosted_thenResourceCannotBeFound() {
    adminLogin();
    cardController.getCard(1L);
  }

  @Test(expected = LoanNameNotValidException.class)
  public void whenTryingToPostANamelessCard_thenCardNameNotProvidedExceptionIsThrown() {
    adminLogin();
    cardController.postCard(CardDto.builder().description("This card cannot be posted.").build());
  }

  /* POST-ing and PUT-ing */

  @Test
  public void whenPostingACard_andThenPuttingADifferentCardOnTheId_thenNewCardIsReturned() {
    adminLogin();
    CardDto postedDto = CardDto.builder()
            .id(1L)
            .name("TST")
            .description("desc")
            .color("#45EA90")
            .build();
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long postedId = postResponse.getBody().getContent().getId();
    CardDto putDto = CardDto.builder()
            .id(1L)
            .name("TST-PUT")
            .description("desc-put")
            .color("#111aaA")
            .build();;
    ResponseEntity<EntityModel<CardDto>> putResponse = cardController.putCard(postedId, putDto);
    assertEquals(HttpStatus.OK, putResponse.getStatusCode());
    Assertions.assertTrue(TestUtils.cardDtosEqual(putDto, putResponse.getBody().getContent()));
  }

  @Test
  public void
      whenPostingACard_andThenPuttingADifferentCardOnTheId_thenGettingTheIdReturnsTheNewCard() {
    adminLogin();
    CardDto postedDto = CardDto.builder()
            .id(1L)
            .name("TST")
            .description("desc")
            .color("#452B90")
            .build();
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    CardDto putDto = CardDto.builder()
            .id(1L)
            .name("TST-PUT")
            .description("desc-put")
            .color("#111aaA")
            .build();
    cardController.putCard(cardId, putDto);
    ResponseEntity<EntityModel<CardDto>> getResponse = cardController.getCard(cardId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    Assertions.assertTrue(TestUtils.cardDtosEqual(putDto, getResponse.getBody().getContent()));
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenPostingACardAsAdmin_andThenLoggingInAsMember_thenPuttingANewCardOnTheIdIsNotAllowed() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    memberOneLogin();
    CardDto putDto = TestUtils.CARD_DTOS.get(1);
    cardController.putCard(cardId, putDto);
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenPostingACardAsMember_andThenLoggingInAsDifferentMember_thenPuttingANewCardOnTheIdIsNotAllowed() {
    memberOneLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    memberTwoLogin();
    CardDto putDto = TestUtils.CARD_DTOS.get(1);
    cardController.putCard(cardId, putDto);
  }

  @Test
  public void
      whenPostingACardAsMember_andThenLoggingInAsAdmin_thenPuttingANewCardOnTheIdIsAllowed() {
    memberOneLogin();
    CardDto postedDto = CardDto.builder()
            .id(1L)
            .name("TST")
            .description("desc")
            .color("#453C90")
            .build();
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    adminLogin();
    CardDto putDto = CardDto.builder()
            .id(1L)
            .name("TST-PUT")
            .description("desc-put")
            .color("#111aaA")
            .build();
    ResponseEntity<EntityModel<CardDto>> putResponse = cardController.putCard(cardId, putDto);
    assertEquals(HttpStatus.OK, putResponse.getStatusCode());
    Assertions.assertTrue(TestUtils.cardDtosEqual(putDto, putResponse.getBody().getContent()));
  }

  @Test(expected = LoanNotFoundException.class)
  public void whenPuttingACardOnANonExistentId_thenResourceCannotBeFound() {
    adminLogin();
    cardController.putCard(1L, TestUtils.CARD_DTOS.get(0));
  }

  @Test(expected = LoanNameNotValidException.class)
  public void whenPuttingANamelessCardOnAnExistingId_thenCardNameNotProvidedExceptionIsThrown() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    cardController.putCard(cardId, CardDto.builder().description("desc").build());
  }

  /* POST-ing and PATCH-ing */

  @Test
  public void whenPostingACard_andThenApplyingAPatchOnTheId_thenPatchedCardIsReturned() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    CardDto patchOne =
        CardDto.builder().name("New name").color("#DA86DF").status(Status.IN_PROGRESS).build();
    ResponseEntity<EntityModel<CardDto>> patchResponse = cardController.patchCard(cardId, patchOne);
    Assertions.assertTrue(
        TestUtils.cardDtosEqual(
            CardDto.builder()
                .id(cardId)
                .name(patchOne.getName())
                .color(patchOne.getColor())
                .description(postedDto.getDescription())
                .status(patchOne.getStatus())
                .build(),
            patchResponse.getBody().getContent()));

    // Let's demonstrate that we can clear color and description fields.
    CardDto patchTwo =
        CardDto.builder().description("").color("").status(Status.IN_PROGRESS).build();
    patchResponse = cardController.patchCard(cardId, patchTwo);
    Assertions.assertTrue(
        TestUtils.cardDtosEqual(
            CardDto.builder()
                .id(cardId)
                .name(patchOne.getName())
                .description("")
                .status(patchOne.getStatus())
                .color("")
                .build(),
            patchResponse.getBody().getContent()));
  }

  @Test
  public void
      whenPostingACard_andThenApplyingAPatchOnTheId_thenGettingTheIdReturnsThePatchedCard() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    CardDto patchOne =
        CardDto.builder().name("New name").color("#DA86DF").status(Status.IN_PROGRESS).build();
    cardController.patchCard(cardId, patchOne);
    ResponseEntity<EntityModel<CardDto>> getResponse = cardController.getCard(cardId);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
    Assertions.assertTrue(
        TestUtils.cardDtosEqual(
            CardDto.builder()
                .id(cardId)
                .name(patchOne.getName())
                .color(patchOne.getColor())
                .status(patchOne.getStatus())
                .description(postedDto.getDescription())
                .build(),
            getResponse.getBody().getContent()));
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenPostingACardAsAdmin_andThenLoggingInAsMember_thenApplyingAPatchOnTheIdIsNotAllowed() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    memberOneLogin();
    CardDto patchOne =
        CardDto.builder().name("New name").color("#DD09AE").status(Status.IN_PROGRESS).build();
    cardController.patchCard(cardId, patchOne);
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenPostingACardAsMember_andThenLoggingInAsDifferentMember_thenApplyingAPatchOnTheIdIsNotAllowed() {
    memberOneLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    memberTwoLogin();
    CardDto patchOne =
        CardDto.builder().name("New name").color("#90FB42").status(Status.IN_PROGRESS).build();
    cardController.patchCard(cardId, patchOne);
  }

  @Test
  public void
      whenPostingACardAsMember_andThenLoggingInAsAdmin_thenApplyingAPatchOnTheIdIsAllowed() {
    memberOneLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    adminLogin();
    CardDto patchOne =
        CardDto.builder().name("New name").color("#DA86DF").status(Status.IN_PROGRESS).build();
    ResponseEntity<EntityModel<CardDto>> patchResponse = cardController.patchCard(cardId, patchOne);
    Assertions.assertTrue(
        TestUtils.cardDtosEqual(
            CardDto.builder()
                .id(cardId)
                .name(patchOne.getName())
                .color(patchOne.getColor())
                .description(postedDto.getDescription())
                .status(patchOne.getStatus())
                .build(),
            patchResponse.getBody().getContent()));
  }

  @Test(expected = LoanNotFoundException.class)
  public void whenApplyingAPatchOnANonExistentId_thenResourceCannotBeFound() {
    adminLogin();
    cardController.patchCard(1L, CardDto.builder().color("#45AA91").build());
  }

  @Test(expected = CardNameBlankException.class)
  public void whenApplyingAPatchWithABlankName_thenCardNameCannotBeBlankExceptionIsThrown() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    CardDto patchOne =
        CardDto.builder().name("").color("#90DB42").status(Status.IN_PROGRESS).build();
    cardController.patchCard(cardId, patchOne);
  }

  /* POST-ing and DELETE-ing */

  @Test
  public void whenPostingACard_andThenDeletingIt_thenNoContentResponseIsReturned() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    assertEquals(ResponseEntity.noContent().build(), cardController.deleteCard(cardId));
  }

  @Test(expected = UnauthorizedException.class)
  public void whenPostingACardAsAdmin_andThenLoggingInAsMember_thenDeletingTheCardIsForbidden() {
    adminLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    memberOneLogin();
    cardController.deleteCard(cardId);
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenPostingACardAsMember_andThenLoggingInAsDifferentMember_thenDeletingTheCardIsForbidden() {
    memberOneLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    memberTwoLogin();
    cardController.deleteCard(cardId);
  }

  @Test
  public void whenPostingACardAsMember_andThenLoggingInAsAdmin_thenDeletingTheCardIsAllowed() {
    memberOneLogin();
    CardDto postedDto = TestUtils.CARD_DTOS.get(0);
    ResponseEntity<EntityModel<CardDto>> postResponse = cardController.postCard(postedDto);
    final Long cardId = postResponse.getBody().getContent().getId();
    adminLogin();
    assertEquals(ResponseEntity.noContent().build(), cardController.deleteCard(cardId));
  }

  @Test(expected = LoanNotFoundException.class)
  public void whenDeletingANonExistentCard_thenResourceCannotBeFound() {
    adminLogin();
    cardController.deleteCard(1L);
  }

  /* Aggregate GET with pagination / sorting */

  @Test
  public void whenRequestingTheEntireDataset_thenWeGetTheEntireDataset() {
    adminLogin();
    postAllCards();
    int totalNumRecords = TestUtils.CARD_DTOS.size();
    
    // Sort entire dataset by ID in ASC order
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(totalNumRecords)
            .sortByField("id")
            .sortingOrder(SortingOrder.ASC)
            .build(), totalNumRecords);
    
    // Now in DESC order
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(totalNumRecords)
            .sortByField("id")
            .sortingOrder(SortingOrder.DESC)
            .build(), totalNumRecords);
    
    // Now by name in ASC order
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(totalNumRecords)
            .sortByField("name")
            .sortingOrder(SortingOrder.ASC)
            .build(), totalNumRecords);

    // And by name in DESC order
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(totalNumRecords)
            .sortByField("name")
            .sortingOrder(SortingOrder.DESC)
            .build(), totalNumRecords);
    
    // Date of creation ASC
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(totalNumRecords)
            .sortByField("createdDateTime")
            .sortingOrder(SortingOrder.ASC)
            .build(), totalNumRecords);
    
    // Date of creation DESC.
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(totalNumRecords)
            .sortByField("createdDateTime")
            .sortingOrder(SortingOrder.DESC)
            .build(), totalNumRecords);
  }

  private void postAllCards() {
    TestUtils.CARD_DTOS.forEach(cardController::postCard);
  }


  private void sortingAndPaginationAssertions(QueryParams params, int expectedCardNumber){
    ResponseEntity<CollectionModel<EntityModel<CardDto>>> response = cardController.aggregateGetCards(
            params.getFilterParams(), params.getPage(),
            params.getPageSize(), params.getSortByField(), params.getSortingOrder());
    Collection<CardDto> cardsReturned = response.getBody().getContent().stream().map(EntityModel::getContent).toList();
    assertEquals(expectedCardNumber, cardsReturned.size());
    Assertions.assertTrue(
            TestUtils.collectionIsSortedByFieldInGivenDirection(
                    cardsReturned, params.getSortByField(), params.getSortingOrder()));
  }

  @Test
  public void whenWeSortByAField_andRequestASpecificPage_thenWeGetThatPage() {
    adminLogin();
    postAllCards();
    
    // There are 20 cards total.
    
    // Sort by id ASC and get the first page.
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(5)
            .sortByField("id")
            .sortingOrder(SortingOrder.ASC)
            .build(), 5);    
    
    // Sort by id DESC and get the first page.
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(7)
            .sortByField("id")
            .sortingOrder(SortingOrder.DESC)
            .build(), 7);
    
    // Sort by name ASC and get the second page.
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(1)
            .pageSize(5)
            .sortByField("id")
            .sortingOrder(SortingOrder.ASC)
            .build(), 5);
    
    // Sort by creation date ASC and get the third page.
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(2)
            .pageSize(6)
            .sortByField("createdDateTime")
            .sortingOrder(SortingOrder.ASC)
            .build(), 6);
    
    // Sort by status DESC and get the last page.
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(2)
            .pageSize(8)
            .sortByField("status")
            .sortingOrder(SortingOrder.DESC)
            .build(), 4);
  }

  @Test
  public void
      whenWeSortByAField_andFilterByAnAttribute_andRequestASpecificPage_thenWeGetThatPage() {
    adminLogin();
    postAllCards();
    
    // Τhere are 5 cards that share a name. We will request the second page of those 5 cards.
    sortingAndPaginationAssertions(QueryParams
            .builder()
            .page(1)
            .pageSize(2)
            .sortByField("id")
            .sortingOrder(SortingOrder.ASC)
            .filterParams(Map.of(Const.NAME_FILTER_STRING, TestUtils.NAME))
            .build(), 2);
    
    // 5 cards share the same color. We will split them in 2 pages and request the last one.
    sortingAndPaginationAssertions(QueryParams
            .builder()
            .page(1)
            .pageSize(4)
            .sortByField("name")
            .sortingOrder(SortingOrder.DESC)
            .filterParams(Map.of(Const.COLOR_FILTER_STRING, TestUtils.COLOR))
            .build(), 1);
  }

  @Test
  public void
      whenWeSortByAField_andFilterByTwoAttributes_andRequestASpecificPage_thenWeGetThatPage() {
    adminLogin();
    postAllCards();
    
    // There are 4 cards with name "MEME_CARD" and color "#45F780". 
    sortingAndPaginationAssertions(QueryParams
            .builder()
            .page(0)
            .pageSize(4)
            .sortByField("id")
            .sortingOrder(SortingOrder.ASC)
            .filterParams(Map.of(Const.NAME_FILTER_STRING, TestUtils.NAME,
                    Const.COLOR_FILTER_STRING, TestUtils.COLOR))
            .build(), 4);
  }

  @Test
  public void whenNoCardsHaveBeenPosted_thenWeGetAnEmptyList() {
    adminLogin();
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(5)
            .sortByField("id")
            .sortingOrder(SortingOrder.ASC)
            .build(), 0);
  }

  @Test(expected = UnauthorizedException.class)
  public void
      whenAMemberFiltersByTheNameOfAnotherMember_thenInsufficientPrivilegesExceptionIsThrown() {
    memberOneLogin();
    cardController.postCard(TestUtils.CARD_DTOS.get(0));
    cardController.postCard(TestUtils.CARD_DTOS.get(1));
    memberTwoLogin();
    cardController.postCard(TestUtils.CARD_DTOS.get(2));
    cardController.postCard(TestUtils.CARD_DTOS.get(3));
    sortingAndPaginationAssertions(QueryParams.builder()
            .page(0)
            .pageSize(5)
            .sortByField("id")
            .sortingOrder(SortingOrder.ASC)
            .filterParams(Map.of(Const.CREATING_USER_FILTER_STRING, TestUtils.MEMBER_ONE_EMAIL))
            .build(), 2);
  }
}
