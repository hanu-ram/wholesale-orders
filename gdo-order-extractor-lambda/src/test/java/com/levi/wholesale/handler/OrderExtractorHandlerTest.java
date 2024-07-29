package com.levi.wholesale.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.Storage;
import com.levi.wholesale.config.GCPConfiguration;
import com.levi.wholesale.lambda.common.config.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class OrderExtractorHandlerTest {

    private static final String THE_BUCKET = "theBucket";
    private static final String THE_DESTINATION_BUCKET = "theDestinationBucket";
    private static final String KEY_NAME = "keyName";
    private static final String FILE_NAME = "input.csv";

    private static MockedStatic<GCPConfiguration> gcpConfigurationMockedStatic;
    private static MockedStatic<Configuration> configurationMockedStatic;

    @BeforeAll
    static void setUp() {
        gcpConfigurationMockedStatic = mockStatic(GCPConfiguration.class);
        configurationMockedStatic = mockStatic(Configuration.class);
        Mockito.when(Configuration.getGDOBucketName()).thenReturn(THE_BUCKET);
        Mockito.when(Configuration.getGDOBucketPrefixName()).thenReturn(KEY_NAME);
        Mockito.when(Configuration.getBootStrapHost()).thenReturn("localhost:9092");
        Mockito.when(Configuration.getProducerAck()).thenReturn("all");
        Mockito.when(Configuration.getProducerRetries()).thenReturn("1");
        Mockito.when(Configuration.shouldUseSASL()).thenReturn("FALSE");
    }

    @AfterAll
    static void close() {
        gcpConfigurationMockedStatic.close();
        configurationMockedStatic.close();
    }

    @Test
    void handleRequestTest() throws IOException {
        OrderExtractorHandler extractorHandler = new OrderExtractorHandler();
        ScheduledEvent input = mock(ScheduledEvent.class);
        Context context = mock(Context.class);

        List<Blob> blobList = new ArrayList<>();
        Storage googleStorageClient = mock(Storage.class);
        Page blobPage = mock(Page.class);

        Blob blob1 = mock(Blob.class);
        Blob blob2 = mock(Blob.class);
        blobList.add(blob1);
        blobList.add(blob2);

        when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

        when(googleStorageClient.list(anyString(), ArgumentMatchers.<Storage.BlobListOption>any())).thenReturn(blobPage);
        when(blobPage.iterateAll()).thenReturn(blobList);
        when(blob1.getName()).thenReturn("sales/order/blob1.csv");
        when(blob2.getName()).thenReturn("sales/order/blob2.csv");
        when(blob1.getUpdateTimeOffsetDateTime()).thenReturn(Instant.now().atOffset(ZoneOffset.UTC));
        when(blob2.getUpdateTimeOffsetDateTime()).thenReturn(Instant.now().minus(1, ChronoUnit.DAYS).atOffset(ZoneOffset.UTC));
        when(googleStorageClient.get(ArgumentMatchers.<BlobId>any())).thenReturn(blob1);
        when(blob1.exists()).thenReturn(Boolean.TRUE);
        ReadChannel readChannel = mock(ReadChannel.class);
        when(googleStorageClient.reader(ArgumentMatchers.any())).thenReturn(readChannel);
        CopyWriter copyWriter = mock(CopyWriter.class);
        when(googleStorageClient.copy(any())).thenReturn(copyWriter);
        when(copyWriter.getResult()).thenReturn(blob1);
        String actualResult = extractorHandler.handleRequest(input, context);
        Assertions.assertNotNull(actualResult);
        Assertions.assertEquals("failure", actualResult);
    }
}
