package com.billing.system.publisher.dto;

import lombok.Data;

@Data
public class MaxServiceCost {

    private String phoneNumber;

    private Long maxCall;
    private Long maxSms;
    private Long maxInternet;
}
