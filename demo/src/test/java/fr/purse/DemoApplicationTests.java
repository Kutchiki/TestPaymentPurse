package fr.purse;

import fr.purse.domain.Payment;
import fr.purse.domain.PaymentMode;
import fr.purse.domain.PaymentOrderLine;
import fr.purse.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // I know tests are way longer with multiple tests.
class DemoApplicationTests {

	@Autowired
	WebTestClient webClient;

	@Test
	void should_get_empty_response() {
		webClient.get().uri("/payment/v0").exchange().expectStatus().is2xxSuccessful().expectBodyList(Payment.class).hasSize(0);
	}

	@Test
	void should_get_not_found_response() {
		webClient.get().uri("/payment/v0/1425").exchange().expectStatus().isNotFound();
	}

	@Test
	void should_create_and_update_payment() {
		Payment payment = new Payment();
		payment.setAmount(10);
		payment.setCurrency("EUR");
		payment.setPaymentMode(PaymentMode.BANK_CARD);

		//should create one payment with status code 201
		var result = webClient.post()
				.uri("/payment/v0")
				.body(Mono.just(payment), Payment.class)
				.exchange()
				.expectStatus()
				.isCreated()
				.expectBody(Payment.class)
				.returnResult()
				.getResponseBody();
		assertNotNull(result);
		assertNotNull(result.getId());
		assertEquals(PaymentStatus.IN_PROGRESS, result.getPaymentStatus());
		payment.setPaymentStatus(PaymentStatus.CAPTURED);

		//should get 400 bad request when trying to set payment status to captured on a non AUTHORIZED status
		webClient.patch()
				.uri("/payment/v0/" + result.getId())
				.body(Mono.just(payment), Payment.class)
				.exchange()
				.expectStatus()
				.isBadRequest();

		// update payment status to AUTHORIZED
		payment.setPaymentStatus(PaymentStatus.AUTHORIZED);
		webClient.patch()
				.uri("/payment/v0/" + result.getId())
				.body(Mono.just(payment), Payment.class)
				.exchange()
				.expectStatus()
				.is2xxSuccessful();

		// try to update order lines after creating the payment
		var paymentOrderLine = new PaymentOrderLine();
		paymentOrderLine.setProductName("test");
		payment.setPaymentOrderLines(List.of(paymentOrderLine));
		webClient.patch()
				.uri("/payment/v0/" + result.getId())
				.body(Mono.just(payment), Payment.class)
				.exchange()
				.expectStatus()
				.is2xxSuccessful()
				.expectBody()
				.jsonPath("$.paymentOrderLines").isEmpty();

		//assert that updating an authorized payment into a captured one is ok
		payment.setPaymentStatus(PaymentStatus.CAPTURED);
		webClient.patch()
				.uri("/payment/v0/" + result.getId())
				.body(Mono.just(payment), Payment.class)
				.exchange()
				.expectStatus()
				.is2xxSuccessful();

		//assert that updating a captured payment is not possible
		payment.setPaymentStatus(PaymentStatus.IN_PROGRESS);
		webClient.patch()
				.uri("/payment/v0/" + result.getId())
				.body(Mono.just(payment), Payment.class)
				.exchange()
				.expectStatus()
				.isBadRequest();
	}

}
