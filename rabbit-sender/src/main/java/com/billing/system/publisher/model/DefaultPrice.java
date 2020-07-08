package com.billing.system.publisher.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
/*
* Данный класс предназначен для вычисления
* стоимость расходуемых услуг
* (если пакет услуг в тариффе был исчерпан)
*/

@Data
@Entity
public class DefaultPrice {
    @Id
    private Long id;

    // цена одной минуты
    private Long callCost;
    // цена одной СМС
    private Long smsCost;
    // цена 100Кб интернета
    private Long internetCost;
}
