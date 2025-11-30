package com.example.portfolio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class SendFlexQueryRequest {

    @Value("${ibkr.flex.token}")
    private String flexToken;

    @Value("${ibkr.flex.query.id}")
    private String flexQueryId;

    @Value("${ibkr.send.request.url}")
    private String ibkrSendRequestUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String requestFlexQueryReferenceCode(){

        LocalDate today = LocalDate.now();
        LocalDate fromDate = today.minusDays(14);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String fromThisDate = fromDate.format(formatter);
        String toToday = today.format(formatter);

        String request = String.format("%s?t=%s&q=%s&v=3&fd=%s&td=%s",ibkrSendRequestUrl, flexToken, flexQueryId, fromThisDate, toToday);

        String response = restTemplate.getForObject(request, String.class);

        String referenceCode = response.replaceAll("(?s).*<ReferenceCode>(\\d+)</ReferenceCode>.*", "$1");

        return referenceCode;

    }


}
