package com.logicea.cardtask.card.service;

import static com.logicea.cardtask.util.Const.CREATING_USER_FILTER_STRING;
import static com.logicea.cardtask.util.Utils.fromCardEntityToCardDto;

import com.logicea.cardtask.card.model.Card;
import com.logicea.cardtask.card.model.CardDto;
import com.logicea.cardtask.card.model.Status;
import com.logicea.cardtask.card.repository.CardRepository;
import com.logicea.cardtask.user.service.AuthCheck;
import com.logicea.cardtask.util.QueryParams;
import com.logicea.cardtask.util.Utils;
import com.logicea.cardtask.util.exceptions.CardNotFoundException;
import com.logicea.cardtask.util.exceptions.UnauthorizedException;
import com.logicea.cardtask.util.logger.Logs;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Logs
public class CardService {

  private final CardRepository cardRepository;
  private final AuthCheck authCheck;
  private final CardDtoToCard cardDtoToCard;

  @Transactional(readOnly = true)
  public CardDto getCard(Long id) throws CardNotFoundException, UnauthorizedException {
    Optional<Card> card = cardRepository.findById(id);
    if (card.isEmpty()) {
      throw new CardNotFoundException(id);
    }
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userHasAccessToCard(loggedInUser, card.get())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    return fromCardEntityToCardDto(card.get());
  }

  @Transactional
  public CardDto replaceCard(Long id, CardDto cardDto)
      throws CardNotFoundException, UnauthorizedException {
    Optional<Card> cardOptional = cardRepository.findById(id);
    if (cardOptional.isEmpty()) {
      throw new CardNotFoundException(id);
    }
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userHasAccessToCard(loggedInUser, cardOptional.get())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    Card oldCard = cardOptional.get();
    LocalDateTime createdDateTime = oldCard.getCreatedDateTime();
    String createdBy = oldCard.getCreatedBy();
    Card newCard =
        cardRepository.save(
            Card.builder()
                .id(oldCard.getId())
                .name(cardDto.getName())
                .color(cardDto.getColor())
                .description(cardDto.getDescription())
                .status(Optional.ofNullable(cardDto.getStatus()).orElse(Status.TODO))
                .build());
    newCard.setCreatedDateTime(createdDateTime);
    newCard.setCreatedBy(createdBy);
    newCard.setLastModifiedDateTime(LocalDateTime.now());
    newCard.setLastModifiedBy(loggedInUser.getUsername());
    return fromCardEntityToCardDto(newCard);
  }

  @Transactional
  public CardDto updateCard(Long id, CardDto cardDto){
    Optional<Card> cardOptional = cardRepository.findById(id);
    if(cardOptional.isEmpty()){
      throw new CardNotFoundException(id);
    }
    User loggedInUser =
            (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userHasAccessToCard(loggedInUser, cardOptional.get())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    Card card = cardOptional.get();
    LocalDateTime createdDateTime = card.getCreatedDateTime();
    String createdBy = card.getCreatedBy();
    cardDtoToCard.updateEntityFromDto(cardDto, card);
    Card patchedCard = cardRepository.save(Card.builder()
            .id(card.getId())
            .name(card.getName())
            .description(card.getDescription())
            .color(card.getColor())
            .status(card.getStatus())
            .build());
    patchedCard.setCreatedDateTime(createdDateTime);
    patchedCard.setCreatedBy(createdBy);
    patchedCard.setLastModifiedDateTime(LocalDateTime.now());
    patchedCard.setLastModifiedBy(loggedInUser.getUsername());
    return fromCardEntityToCardDto(patchedCard);
  }

  @Transactional
  public CardDto storeCard(CardDto cardDto) {
    Card storedCard =
        cardRepository.save(
            Card.builder()
                .name(cardDto.getName())
                .color(cardDto.getColor())
                .description(cardDto.getDescription())
                .build());
    return fromCardEntityToCardDto(storedCard);
  }


  @Transactional(readOnly = true)
  public List<CardDto> getAllCardsByFilter(QueryParams params)
      throws UnauthorizedException {
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (authCheck.userIsMember(loggedInUser)
        && filterParamsIncludeOtherMemberCards(loggedInUser, params.getFilterParams())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    return cardRepository.findCardsByProvidedFilters(params, loggedInUser).stream()
        .map(Utils::fromCardEntityToCardDto)
        .collect(Collectors.toList());
  }

  private boolean filterParamsIncludeOtherMemberCards(User user, Map<String, String> filterParams) {
    if (!filterParams.containsKey(CREATING_USER_FILTER_STRING)) {
      return false;
    }
    return !filterParams.get(CREATING_USER_FILTER_STRING).equals(user.getUsername());
  }

  @Transactional
  public void deleteCard(Long id) throws CardNotFoundException, UnauthorizedException {
    Optional<Card> card = cardRepository.findById(id);
    if (card.isEmpty()) {
      throw new CardNotFoundException(id);
    }
    User loggedInUser =
        (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (!authCheck.userHasAccessToCard(loggedInUser, card.get())) {
      throw new UnauthorizedException(loggedInUser.getUsername());
    }
    cardRepository.deleteById(id);
  }
}
