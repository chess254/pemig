package com.pemig.api.transaction;


import com.pemig.api.account.Account;
import com.pemig.api.user.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    private BigDecimal amount;

    private String initiatedBy;
    private String status;


    @OneToOne
    @JoinColumn(name = "from_id")
    private User from;
    @OneToOne
    @JoinColumn(name = "to_id")
    private User to;
    private String transactionReference;
    private TransactionType transactionType;
}
