package com.example.portfolio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        String request = String.format("%s?t=%s&q=%s&v=3",ibkrSendRequestUrl, flexToken, flexQueryId);


        String response = restTemplate.getForObject(request, String.class);

        String referenceCode = response.replaceAll("(?s).*<ReferenceCode>(\\d+)</ReferenceCode>.*", "$1");

        return referenceCode;

        }
    }
