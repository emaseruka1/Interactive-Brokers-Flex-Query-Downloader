package com.example.portfolio.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class GetFlexXmlStatement {

    @Value("${ibkr.flex.token}")
    private String flexToken;

    @Value("${ibkr.get.statement.url}")
    private String ibkrGetStatementUrl;

    @Value("${flex.xml.download.directory}")
    private String flexXmlDownloadDirectory;

    @Autowired
    private SendFlexQueryRequest sendFlexQueryRequest;

    private final RestTemplate restTemplate = new RestTemplate();

    public GetFlexXmlStatement(SendFlexQueryRequest sendFlexQueryRequest) {
        this.sendFlexQueryRequest = sendFlexQueryRequest;
    }

    public String getFlexXmlStatement(){

        String referenceCode = sendFlexQueryRequest.requestFlexQueryReferenceCode();

        String request = String.format("%s?t=%s&q=%s&v=3",ibkrGetStatementUrl, flexToken, referenceCode);

        int retry =1;

        while (true) {

            String flexXmlStatementResponse = restTemplate.getForObject(request, String.class);

            if (!flexXmlStatementResponse.contains("<ErrorMessage>Statement generation in progress. Please try again shortly.</ErrorMessage>")){

                return flexXmlStatementResponse;
            }

            System.out.println("Retry: "+retry);
            retry+=1;

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
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
