package com.example.portfolio.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SendFlexQueryRequest {

    @Value("${ibkr_flex_token}")
    private String flexToken;

    @Value("${ibkr_flex_query_id}")
    private String flexQueryId;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final Logger log = LoggerFactory.getLogger(SendFlexQueryRequest.class);

    public String requestFlexQueryReferenceCode(){

        log.info("Sending request for Reference Code to IBKR");

        String ibkrSendRequestUrl = "https://ndcdyn.interactivebrokers.com/AccountManagement/FlexWebService/SendRequest";

        log.info("ibkrSendRequestUrl:{}", ibkrSendRequestUrl);
        log.info("flexToken:{}",flexToken);
        log.info("flexQueryId:{}",flexQueryId);

        String request = String.format("%s?t=%s&q=%s&v=3", ibkrSendRequestUrl, flexToken, flexQueryId);


        String response = restTemplate.getForObject(request, String.class);

        String referenceCode = response.replaceAll("(?s).*<ReferenceCode>(\\d+)</ReferenceCode>.*", "$1");

        log.info("Obtained Reference Code response from IBKR:{}",referenceCode);

        return referenceCode;

        }
    }
