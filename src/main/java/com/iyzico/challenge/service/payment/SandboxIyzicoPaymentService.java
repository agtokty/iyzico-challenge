package com.iyzico.challenge.service.payment;

import com.iyzico.challenge.entity.Payment;
import com.iyzico.challenge.exception.PaymentException;
import com.iyzico.challenge.repository.payment.PaymentRepository;
import com.iyzipay.Options;
import com.iyzipay.model.*;
import com.iyzipay.request.CreatePaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author agtokty
 */
@Service
@ConditionalOnProperty(value = "sandbox.enabled", havingValue = "true")
@Slf4j
public class SandboxIyzicoPaymentService implements PaymentService {

    @Value("${sandbox.apiKey}")
    public String apiKey;

    @Value("${sandbox.secretKey}")
    public String secretKey;

    @Value("${sandbox.base:https://sandbox-api.iyzipay.com}")
    public String baseUrl;

    @Value("${sandbox.cardNumber:5528790000000008}")
    public String cardNumber;

    @Autowired
    private BankService bankService;

    @Autowired
    private PaymentRepository paymentRepository;

    private Options options;
    private CreatePaymentRequest request;

    @PostConstruct
    public void init() {
        options = new Options();
        options.setApiKey(apiKey);
        options.setSecretKey(secretKey);
        options.setBaseUrl(baseUrl);

        //create dummy payment request for all payments
        request = new CreatePaymentRequest();
        request.setLocale(Locale.TR.getValue());
        request.setConversationId("123456789");
        request.setPrice(new BigDecimal("1"));
        request.setPaidPrice(new BigDecimal("1.2"));
        request.setCurrency(Currency.TRY.name());
        request.setInstallment(1);
        request.setBasketId("B67832");
        request.setPaymentChannel(PaymentChannel.WEB.name());
        request.setPaymentGroup(PaymentGroup.PRODUCT.name());

        PaymentCard paymentCard = new PaymentCard();
        paymentCard.setCardHolderName("Agit Oktay");
        paymentCard.setCardNumber(cardNumber);
        paymentCard.setExpireMonth("12");
        paymentCard.setExpireYear("2030");
        paymentCard.setCvc("123");
        paymentCard.setRegisterCard(0);
        request.setPaymentCard(paymentCard);

        Buyer buyer = new Buyer();
        buyer.setId("BY789");
        buyer.setName("Agit");
        buyer.setSurname("Oktay");
        buyer.setGsmNumber("+905350000000");
        buyer.setEmail("agitoktay@gmail.com");
        buyer.setIdentityNumber("74300864791");
        buyer.setLastLoginDate("2015-10-05 12:43:35");
        buyer.setRegistrationDate("2013-04-21 15:12:09");
        buyer.setRegistrationAddress("Yukarıbahçelievler mah. Çankaya/Ankara");
        buyer.setIp("85.34.78.112");
        buyer.setCity("Ankara");
        buyer.setCountry("Turkey");
        buyer.setZipCode("34732");
        request.setBuyer(buyer);

        Address shippingAddress = new Address();
        shippingAddress.setContactName("Agit Oktay");
        shippingAddress.setCity("Ankara");
        shippingAddress.setCountry("Turkey");
        shippingAddress.setAddress("Yukarıbahçelievler mah. Çankaya/Ankara");
        shippingAddress.setZipCode("06490");
        request.setShippingAddress(shippingAddress);

        Address billingAddress = new Address();
        billingAddress.setContactName("Agit Oktay");
        billingAddress.setCity("Ankara");
        billingAddress.setCountry("Turkey");
        billingAddress.setAddress("Yukarıbahçelievler mah. Çankaya/Ankara");
        billingAddress.setZipCode("06490");
        request.setBillingAddress(billingAddress);

        List<BasketItem> basketItems = new ArrayList<BasketItem>();
        BasketItem firstBasketItem = new BasketItem();
        firstBasketItem.setId("BI101");
        firstBasketItem.setName("Binocular");
        firstBasketItem.setCategory1("Collectibles");
        firstBasketItem.setCategory2("Accessories");
        firstBasketItem.setItemType(BasketItemType.PHYSICAL.name());
        firstBasketItem.setPrice(new BigDecimal("0.3"));
        basketItems.add(firstBasketItem);
        request.setBasketItems(basketItems);
    }

    public void pay(BigDecimal price) throws PaymentException {
        //pay with iyzico sandbox api
        request.setPrice(price);
        request.getBasketItems().get(0).setPrice(price);
        com.iyzipay.model.Payment response = com.iyzipay.model.Payment.create(request, options);

        //insert records
        Payment payment = new Payment();
        payment.setBankResponse(response.getStatus());
        payment.setPrice(price);
        paymentRepository.save(payment);
        log.info("Payment record saved successfully!");

        if (response.getStatus().equals("failure")) {
            log.info("Payment failed [{}] - {}", response.getErrorCode(), response.getErrorMessage());
            throw new PaymentException(response.getErrorCode() + " - " + response.getErrorMessage(), null);
        }
    }
}
