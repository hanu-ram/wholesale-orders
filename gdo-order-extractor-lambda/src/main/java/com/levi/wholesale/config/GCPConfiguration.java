package com.levi.wholesale.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.levi.wholesale.lambda.common.config.Configuration;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Slf4j
public final class GCPConfiguration {

    private static Storage storage = null;

    private GCPConfiguration() {
    }

    public static Storage createGoogleCloudStorageClientFromLocalCredentials() throws IOException {
        Credentials credentials;
        if (storage == null) {
            final byte[] serviceCredentialsJson = getSecret();
            credentials = ServiceAccountCredentials
                    .fromStream(new ByteArrayInputStream(serviceCredentialsJson));
            storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
            log.info("Storage created using the serviceCredentialsJson");
        }
        return storage;
    }

    public static byte[] getSecret() throws JsonProcessingException {
        String secretName = Configuration.getSecretName();
        Region region = Region.of(Configuration.getSecretRegion());
        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();
        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception exception) {
            log.error("Exception while fetching secret value", exception);
            throw exception;
        }
        String secretJson = getSecretValueResponse.secretString();
        log.debug("Secret Json {}", secretJson);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(secretJson);
        byte[] serviceCredentialsJson = actualObj.get(Configuration.getSecretKey()).asText().getBytes();
        log.debug("Service Credentials Json {}", serviceCredentialsJson);
        return serviceCredentialsJson;
    }
}
