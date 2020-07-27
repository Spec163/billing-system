package com.billing.system.publisher.controller;

import com.billing.system.publisher.dto.BalanceChangerDto;
import com.billing.system.publisher.dto.MaxServiceCost;
import com.billing.system.publisher.dto.TariffChangerDto;
import com.billing.system.publisher.exceptions.DefaultPriceNotFoundException;
import com.billing.system.publisher.model.DefaultPrice;
import com.billing.system.publisher.model.OrderInfo;
import com.billing.system.publisher.repository.DefaultPriceRepository;
import com.billing.system.publisher.service.RabbitMQSender;
import com.billing.system.springsecurityjwt.config.jwt.JwtFilter;
import com.billing.system.springsecurityjwt.config.jwt.JwtProvider;
import com.billing.system.springsecurityjwt.entity.AccountInfo;
import com.billing.system.springsecurityjwt.repository.AccountInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@RestController
@RequestMapping(value = "rabbitmq")
@EnableRabbit
@CrossOrigin
public class RabbitMQWebController {
	private Logger logger = LoggerFactory.getLogger(RabbitMQWebController.class);

	@Autowired
	private AccountInfoRepository accountInfoRepository;
	@Autowired
	private DefaultPriceRepository defaultPriceRepository;

	private final RabbitMQSender rabbitMQSender;
	private final JwtFilter jwtFilter;
	private final JwtProvider jwtProvider;

	@Autowired
	public RabbitMQWebController(
			final RabbitMQSender rabbitMQSender,
			final JwtFilter jwtFilter,
			final JwtProvider jwtProvider
	) {
		this.rabbitMQSender = rabbitMQSender;
		this.jwtFilter = jwtFilter;
		this.jwtProvider = jwtProvider;
	}

	@PostMapping(value = "billing")
	public void producer(
			@RequestBody OrderInfo orderInfo,
			HttpServletRequest request
	) throws Exception{
		if (orderInfo.getServiceId() == 0) throw new Exception("BAD REQUEST !!!");

		if (orderInfo.getPhoneNumber() == null) {
			orderInfo.setPhoneNumber(getPhoneNumberFromRequest(request));
		} else if (accountInfoRepository.findByPhoneNumber(orderInfo.getPhoneNumber()) == null) {
			orderInfo.setPhoneNumber(getPhoneNumberFromRequest(request));
		}


		rabbitMQSender.sendOrder(orderInfo);
	}

	@PostMapping(value = "new-tariff")
	public ResponseEntity changeTariff(
			@RequestBody TariffChangerDto tariffChangerDto,
			HttpServletRequest request
	) {
		tariffChangerDto.setPhoneNumber(getPhoneNumberFromRequest(request));

		rabbitMQSender.sendTariff(tariffChangerDto);

		return ResponseEntity.ok("Message sent");
	}

	@PostMapping(value = "replenish")
	public String replenishBalance(
			@RequestBody BalanceChangerDto balanceChangerDto,
			HttpServletRequest request
	) throws IOException {
		String phoneNumber = balanceChangerDto.getPhoneNumber();
		if (balanceChangerDto.getPhoneNumber() == null) {
			phoneNumber = getPhoneNumberFromRequest(request);
		} else if (accountInfoRepository.findByPhoneNumber(balanceChangerDto.getPhoneNumber()) == null) {
			phoneNumber = getPhoneNumberFromRequest(request); // сомнительное действие !!!
		}

		balanceChangerDto.setPhoneNumber(phoneNumber);

		// проверки на валидность номера не нужны, тк он будет браться из токена
		if (balanceChangerDto.getMoney() < 1 || balanceChangerDto.getMoney() == null) {
			logger.error("Incorrect amount: {}", balanceChangerDto.getMoney());
			return null;
		}
		rabbitMQSender.changeBalance(balanceChangerDto);
		return String.format("The payment has been sent, in the amount of %s rubles", balanceChangerDto);
	}


	// Рассчёт максимальновозможного расходы каждой из услуг (остатки тарифа + баланс)
	@PostMapping(value = "expenses")
	public MaxServiceCost calculateMaximumExpenses(
			@RequestBody MaxServiceCost maxServiceCost,
			HttpServletRequest request
	) {
		DefaultPrice defaultPrice = defaultPriceRepository
				.findById(1L).orElseThrow(() -> new DefaultPriceNotFoundException(1L));
		AccountInfo accountInfo = accountInfoRepository.findByPhoneNumber(maxServiceCost.getPhoneNumber());

		if (accountInfo == null) {
			maxServiceCost.setPhoneNumber(getPhoneNumberFromRequest(request));
			accountInfo = accountInfoRepository.findByPhoneNumber(maxServiceCost.getPhoneNumber());
		}

		maxServiceCost.setMaxCall(accountInfo.getCall()
				+ accountInfo.getBalance() / defaultPrice.getCallCost());
		maxServiceCost.setMaxSms(accountInfo.getSms()
				+ accountInfo.getBalance() / defaultPrice.getSmsCost());
		maxServiceCost.setMaxInternet(accountInfo.getInternet()
				+ accountInfo.getBalance() / defaultPrice.getInternetCost() * 100);

		return maxServiceCost;
	}

	private String getPhoneNumberFromRequest(HttpServletRequest request) {
		String phoneNumber = null;
		String token = jwtFilter.getTokenFromRequest((HttpServletRequest) request);
		// вытаскиваем значение поля (phoneNumber) из токена
		phoneNumber = (String) jwtProvider.getClaimsFromToken(token).get("phoneNumber");
		return phoneNumber;
	}

}

