package com.example.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SendFlexQueryRequest.class);

    public String requestFlexQueryReferenceCode(){

        log.info("Sending request for Reference Code to IBKR");
        log.info("ibkrSendRequestUrl:{}",ibkrSendRequestUrl);
        log.info("flexToken:{}",flexToken);
        log.info("flexQueryId:{}",flexQueryId);

        String request = String.format("%s?t=%s&q=%s&v=3",ibkrSendRequestUrl, flexToken, flexQueryId);


        String response = restTemplate.getForObject(request, String.class);

        String referenceCode = response.replaceAll("(?s).*<ReferenceCode>(\\d+)</ReferenceCode>.*", "$1");

        log.info("Obtained Reference Code response from IBKR:{}",referenceCode);

        return referenceCode;

        }
    }
