package com.example.portfolio.service;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${ibkr.flex.token}")
    private String flexToken;

    @Value("${ibkr.get.statement.url}")
    private String ibkrGetStatementUrl;

    @Value("${flex.query.bucket}")
    private String flexXmlDownloadDirectory;

    @Value("${flex.query.filename}")
    private String flexXmlFilename;

    private final Storage storage = StorageOptions.getDefaultInstance().getService();

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

    public void downloadFlexXmlStatement(){

        String flexXmlStatementResponse = getFlexXmlStatement();

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
            throw new RuntimeException("Failed to write XML to GCS", e);
        }


    }
}
