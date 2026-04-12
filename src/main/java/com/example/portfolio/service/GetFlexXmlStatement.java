package com.example.portfolio.service;

import com.example.portfolio.controller.RunJob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.io.ByteArrayInputStream;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

@Service
public class GetFlexXmlStatement {

        @Value("${ibkr_flex_token:NOT_SET}")
        private String flexToken;

        @Value("${ibkr.get.statement.url:NOT_SET}")
        private String ibkrGetStatementUrl;

        @Value("${flex.query.bucket:NOT_SET}")
        private String flexXmlDownloadDirectory;

        @Value("${flex.query.filename:NOT_SET}")
        private String flexXmlFilename;

        private Storage storage;

        private SendFlexQueryRequest sendFlexQueryRequest;

        private final RestTemplate restTemplate = new RestTemplate();

        private static final Logger log = LoggerFactory.getLogger(GetFlexXmlStatement.class);

        public GetFlexXmlStatement(SendFlexQueryRequest sendFlexQueryRequest) {
            this.sendFlexQueryRequest = sendFlexQueryRequest;
        }

        public String getFlexXmlStatement(){

            String referenceCode = sendFlexQueryRequest.requestFlexQueryReferenceCode();

            String request = String.format("%s?t=%s&q=%s&v=3",ibkrGetStatementUrl, flexToken, referenceCode);

            int retry =1;

            while (true) {

                log.info("Sending request to IBKR for Flex Statement using Reference Code. Retry {}",retry);

                String flexXmlStatementResponse = restTemplate.getForObject(request, String.class);

                if (!flexXmlStatementResponse.contains("<ErrorMessage>Statement generation in progress. Please try again shortly.</ErrorMessage>")){

                    log.info("Obtained IBKR Flex Statement Response");

                    return flexXmlStatementResponse;
                }

                log.info("Retry:{}",retry);
                retry+=1;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error("Failed to get IBKR XML response", e);
                    throw new RuntimeException(e);
                }
            }
        }

        public void downloadFlexXmlStatement(){

            String flexXmlStatementResponse = getFlexXmlStatement();

            log.info("Send Flex statement to GCS bucket {} with filename {}",flexXmlDownloadDirectory,flexXmlFilename);

            BlobId flexXmlStatementGcsLocation = BlobId.of(flexXmlDownloadDirectory, flexXmlFilename);

            BlobInfo flexXmlStatementMetaData = BlobInfo.newBuilder(flexXmlStatementGcsLocation).build();

            try (WritableByteChannel blobChannelToGoogleCloudStorage = storage.writer(flexXmlStatementMetaData);
                 ByteArrayInputStream googleCloudStorageBlobStream = new ByteArrayInputStream(flexXmlStatementResponse.getBytes(StandardCharsets.UTF_8))) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = googleCloudStorageBlobStream.read(buffer)) != -1) {
                    blobChannelToGoogleCloudStorage.write(java.nio.ByteBuffer.wrap(buffer, 0, bytesRead));
                }

            } catch (IOException e) {
                log.error("Failed to write XML to GCS", e);
                throw new RuntimeException("Failed to write XML to GCS", e);
            }


        }

        @PostConstruct
        public void init() {
            storage = StorageOptions.getDefaultInstance().getService();
            log.info("GCS initialized");
        }
    }
