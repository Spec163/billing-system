package com.billing.system.consumer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Id;
/*
* Данный класс предназначен для вычисления
* стоимость расходуемых услуг
* (если пакет услуг в тариффе был исчерпан)
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultTariff {
    @Id
    private Long id;

    // цена одной минуты
    private Long callCost = 3L;
    // цена одной СМС
    private Long callSms = 5L;
    // цена 100Кб интернета
    private Long callInternet = 10L;
}
