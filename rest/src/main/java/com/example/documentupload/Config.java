package com.example.documentupload;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Config {

    @Value("${blob.endpoint}")
    private String blobEndpoint;


    @Value("${cosmos.endpoint}")
    private String cosmosEndpoint;

    @Bean
    public BlobServiceClient getBlobClient() {
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(blobEndpoint)
                .credential(new DefaultAzureCredentialBuilder().build())
                .buildClient();
        return blobServiceClient;
    }

    @Bean
    public CosmosClient getCosmosClient() {
        CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(cosmosEndpoint)
                .key(System.getenv("COSMOS_PRIMARY_KEY"))
                .buildClient();
        return cosmosClient;
    }
}
