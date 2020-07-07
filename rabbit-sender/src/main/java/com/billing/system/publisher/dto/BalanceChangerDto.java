package com.billing.system.publisher.dto;

import lombok.Data;

@Data
public class BalanceChangerDto {
    private String phoneNumber;
    private Long money;
}
