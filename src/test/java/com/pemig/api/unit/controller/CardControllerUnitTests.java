package com.pemig.api.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pemig.api.card.controller.CardController;
import com.pemig.api.card.model.CardDto;
import com.pemig.api.card.model.CardAssembler;
import com.pemig.api.card.service.CardService;
import com.pemig.api.util.QueryParams;
import com.pemig.api.util.SortingOrder;
import com.pemig.api.util.exceptions.CardNameBlankException;
import com.pemig.api.util.exceptions.LoanNameNotValidException;
import com.pemig.api.util.exceptions.WrongSortFieldException;
import java.util.Collections;

import com.pemig.api.util.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RunWith(MockitoJUnitRunner.class)
public class CardControllerUnitTests {

    @Mock
    private CardService cardService;

    @Mock
    private CardAssembler cardAssembler;

    @InjectMocks
    private CardController cardController;

    private static final CardDto STANDARD_DTO = TestUtils.CARD_DTOS.get(0);
    
    @Before
    public void setupAssembler(){
        // Pseudo-mocking the assembler.
        lenient().when(cardAssembler.toModel(any(CardDto.class))).thenCallRealMethod();
        lenient().when(cardAssembler.toCollectionModel(any())).thenCallRealMethod();
    }

    /* Get by ID tests */

    @Test
    public void whenServiceSuccessfullyReturnsDto_thenResponseEntityContainsFormattedDto(){
        when(cardService.getCard(STANDARD_DTO.getId())).thenReturn(STANDARD_DTO);
        assertEquals(ResponseEntity.ok(cardAssembler.toModel(STANDARD_DTO)),
                cardController.getCard(STANDARD_DTO.getId()));
    }

    /* POST tests */
    
    @Test
    public void whenServiceSuccessfullyStoresAndReturnsDto_thenResponseEntityContainsFormattedDto(){
        when(cardService.storeCard(STANDARD_DTO)).thenReturn(STANDARD_DTO);
        assertEquals(new ResponseEntity<>(cardAssembler.toModel(STANDARD_DTO), HttpStatus.CREATED),
                cardController.postCard(STANDARD_DTO));
    }
    
    @Test(expected = LoanNameNotValidException.class)
    public void whenUserDoesNotProvideANameForCard_thenCardNameNotProvidedExceptionisThrown(){
        cardController.postCard(CardDto.builder().color("#9aA042").build());
    }

    /* GET ALL tests */

    @Test
    public void whenServiceReturnsACollectionOfDtos_thenResponseEntityContainsFormattedCollection(){
        when(cardService.getAllCardsByFilter(any(QueryParams.class))).thenReturn(TestUtils.CARD_DTOS);
        assertEquals(ResponseEntity.ok(cardAssembler.toCollectionModel(TestUtils.CARD_DTOS)),
                cardController.aggregateGetCards(Collections.emptyMap(), 0, 1, "id", SortingOrder.ASC));
    }
    @Test(expected = WrongSortFieldException.class)
    public void whenAnInvalidSortByFieldIsUsed_thenSInvalidSortByFieldExceptionIsThrown(){
        cardController.aggregateGetCards(Collections.emptyMap(), 0, 1, 
                "MEME_FIELD", SortingOrder.ASC);
    }
    
    /* PUT tests */

    @Test
    public void whenServiceSuccessfullyReplacesCardWithDto_thenResponseEntityContainsFormattedDto(){
        when(cardService.replaceCard(STANDARD_DTO.getId(), STANDARD_DTO)).thenReturn(STANDARD_DTO);
        assertEquals(ResponseEntity.ok(cardAssembler.toModel(STANDARD_DTO)),
                cardController.putCard(STANDARD_DTO.getId(), STANDARD_DTO));
    }
    
    @Test(expected = LoanNameNotValidException.class)
    public void whenAttemptingToReplaceACardWithANamelessCard_thenCardNameNotProvidedExceptionIsThrown(){
        cardController.putCard(1L, CardDto.builder().description("desc").color("#45EFAB").build());
    }
    
    /* PATCH tests */
    @Test
    public void whenServiceSuccessfullyUpdatesCardWithDto_thenResponseEntityContainsFormattedDto(){
        when(cardService.updateCard(STANDARD_DTO.getId(), STANDARD_DTO)).thenReturn(STANDARD_DTO);
        assertEquals(ResponseEntity.ok(cardAssembler.toModel(STANDARD_DTO)),
                cardController.patchCard(STANDARD_DTO.getId(), STANDARD_DTO));
    }
    
    @Test(expected = CardNameBlankException.class)
    public void whenAttemptingToClearTheNameOfACard_thenCardNameCannotBeBlankExceptionIsThrown(){
        cardController.patchCard(1L, CardDto.builder().name("     ").description("desc").build());
    }
    
    /* DELETE tests */
    
    @Test
    public void whenServiceSuccessfullyDeletesACard_thenOk(){
        doNothing().when(cardService).deleteCard(STANDARD_DTO.getId());
        assertEquals(ResponseEntity.noContent().build(), cardController.deleteCard(STANDARD_DTO.getId()));
    }

}
