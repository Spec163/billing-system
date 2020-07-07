package com.billing.system.springsecurityjwt.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class AccountInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balance;

    private String phoneNumber;

    private String title;
    private Long price;
    private Long call;
    private Long sms;
    private Long internet;

}
