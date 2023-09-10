package com.pemig.api.card.repository;

import com.pemig.api.card.model.Card;
import com.pemig.api.util.logger.Logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Logs
public interface CardRepository
    extends JpaRepository<Card, Long>, CardQueryParamsRepository {}
