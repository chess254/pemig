package com.logicea.cardtask.card.repository;

import com.logicea.cardtask.card.model.Card;
import com.logicea.cardtask.card.service.CardService;
import com.logicea.cardtask.util.logger.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Logs
public interface CardRepository
    extends JpaRepository<Card, Long>, CardQueryParamsRepository {}
