package com.example.portfolio.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocalGetFlexXmlStatement implements FlexXmlActions{

    @Value("${ibkr.flex.token}")
    private String flexToken;

    @Value("${flex.xml.download.directory}")
    private String flexXmlDownloadDirectory;

    @Autowired
    private SendFlexQueryRequest sendFlexQueryRequest;

    private final RestTemplate restTemplate = new RestTemplate();

    public LocalGetFlexXmlStatement(SendFlexQueryRequest sendFlexQueryRequest) {
        this.sendFlexQueryRequest = sendFlexQueryRequest;
    }

    public String getFlexXmlStatement(){

        String ibkrGetStatementUrl = "https://gdcdyn.interactivebrokers.com/AccountManagement/FlexWebService/GetStatement";

        String referenceCode = sendFlexQueryRequest.requestFlexQueryReferenceCode();

        String request = String.format("%s?t=%s&q=%s&v=3",ibkrGetStatementUrl, flexToken, referenceCode);

        String flexXmlStatementResponse = restTemplate.getForObject(request, String.class);

        return flexXmlStatementResponse;
    }

    @PostConstruct
    public void downloadFlexXmlStatement(){

        String flexXmlStatementResponse = getFlexXmlStatement();

        File downloadDirectory = new File(flexXmlDownloadDirectory);

        File xmlFile = new File(downloadDirectory, "transactions.xml");

        try (FileWriter writer = new FileWriter(xmlFile)) {

            writer.write(flexXmlStatementResponse);

        } catch (IOException e) {

            throw new RuntimeException(e);
        }

    }
}
