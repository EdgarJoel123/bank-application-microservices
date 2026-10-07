package com.devsu.hackerearth.backend.account.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction extends Base {

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String type;
    private double amount;
    private double balance;

    @Column(name = "account_id")
    private Long accountId;
}