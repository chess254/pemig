package com.pemig.api.account;

import com.pemig.api.audit.Auditable;
import com.pemig.api.transaction.Transaction;
import com.pemig.api.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.*;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name="ACCOUNT")
public class Account extends Auditable<String> {


    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @NotBlank
    @Column(name = "ACC_NUMBER")
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long accountNumber;

    @NotBlank
    @NotNull
    private String accountName;

    @OneToOne(targetEntity = User.class)
    private User customer;

    private boolean active;

    @OneToMany(targetEntity = Transaction.class, cascade = ALL, mappedBy = "account")
    private Set<Transaction> transactions =  new HashSet<>();

//    @OneToMany(targetEntity= Transaction.class, cascade=ALL,
//            mappedBy="account_id")
//    public Set getTransactions() { return transactions; }
}
