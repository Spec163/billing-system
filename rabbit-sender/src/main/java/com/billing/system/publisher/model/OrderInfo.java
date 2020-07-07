package com.billing.system.publisher.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class OrderInfo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String phoneNumber;
	private Long serviceId;
	private Long expenses;

}
