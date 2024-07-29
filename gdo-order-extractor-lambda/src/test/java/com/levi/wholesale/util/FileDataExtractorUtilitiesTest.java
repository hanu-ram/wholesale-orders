package com.levi.wholesale.util;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.CopyWriter;
import com.google.cloud.storage.Storage;
import com.levi.wholesale.config.GCPConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FileDataExtractorUtilitiesTest {

    private static final String THE_BUCKET = "theBucket";
    private static final String THE_DESTINATION_BUCKET = "theDestinationBucket";
    private static final String KEY_NAME = "keyName";
    private static final String FILE_NAME = "sales/order/input.csv";
    private static MockedStatic<GCPConfiguration> gcpConfigurationMockedStatic;

    @BeforeAll
    static void setUp() {
        gcpConfigurationMockedStatic = Mockito.mockStatic(GCPConfiguration.class);
    }

    @AfterAll
    static void close() {
        gcpConfigurationMockedStatic.close();
    }

    @Test
    void deleteGoogleStorageObjectTest_Success() {
        try {
            Storage googleStorageClient = mock(Storage.class);
            when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

            when(googleStorageClient.delete(ArgumentMatchers.<BlobId>any())).thenReturn(Boolean.TRUE);
            Blob blob1 = mock(Blob.class);
            when(googleStorageClient.get(ArgumentMatchers.<BlobId>any())).thenReturn(blob1);
            when(blob1.exists()).thenReturn(Boolean.TRUE);
            new FileDataExtractorUtilities().deleteObject(THE_BUCKET, FILE_NAME);

        } catch (Exception e) {
            assertFalse(e != null || e.getMessage() != null);
        }
    }

    @Test
    void deleteGoogleStorageObjectTest_Failure() throws IOException {
        Storage googleStorageClient = mock(Storage.class);
        when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

        when(googleStorageClient.delete(ArgumentMatchers.<BlobId>any())).thenReturn(Boolean.TRUE);
        Blob blob1 = mock(Blob.class);
        when(googleStorageClient.get(ArgumentMatchers.<BlobId>any())).thenReturn(blob1);
        when(blob1.exists()).thenReturn(Boolean.FALSE);

        assertThrows(RuntimeException.class, () ->
                new FileDataExtractorUtilities().deleteObject(THE_BUCKET, FILE_NAME));
    }

    @Test
    void deleteGoogleStorageObjectTest_Exception() {
        try {
            Storage googleStorageClient = mock(Storage.class);
            when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

            when(googleStorageClient.delete(ArgumentMatchers.<BlobId>any()))
                    .thenThrow(new RuntimeException("Dummy Exception deleting objects from Google Storage"));

        } catch (Exception e) {
            assertTrue(e != null && e.getMessage() != null);
        }
    }

    @Test
    void copyGoogleStorageObjectTest_Success() throws Exception {
        Storage googleStorageClient = mock(Storage.class);
        when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);
        Blob blob1 = mock(Blob.class);
        CopyWriter copyWriter = mock(CopyWriter.class);
        when(googleStorageClient.copy(any())).thenReturn(copyWriter);
        when(copyWriter.getResult()).thenReturn(blob1);
        when(googleStorageClient.get(ArgumentMatchers.<BlobId>any())).thenReturn(blob1);
        when(blob1.exists()).thenReturn(Boolean.TRUE);
        Blob actualBlob = new FileDataExtractorUtilities().copyObject(THE_BUCKET, THE_DESTINATION_BUCKET, FILE_NAME);
        assertEquals(actualBlob, blob1);
    }

    @Test
    void copyGoogleStorageObjectTest_Failure() throws IOException {
        Storage googleStorageClient = mock(Storage.class);
        when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);
        Blob blob1 = mock(Blob.class);
        CopyWriter copyWriter = mock(CopyWriter.class);
        when(googleStorageClient.copy(any())).thenReturn(copyWriter);
        when(copyWriter.getResult()).thenReturn(null);
        when(googleStorageClient.get(ArgumentMatchers.<BlobId>any())).thenReturn(blob1);
        when(blob1.exists()).thenReturn(Boolean.TRUE);
        assertThrows(RuntimeException.class, () ->
                new FileDataExtractorUtilities().copyObject(THE_BUCKET, THE_DESTINATION_BUCKET, FILE_NAME));
    }

    @Test
    void getGoogleStorageObjectNamesByPrefixAndLatestUTCModifiedDateTest_NoObjectInStorage() {
        try {

            List<Blob> blobList = new ArrayList<>();
            Storage googleStorageClient = mock(Storage.class);
            Page blobPage = mock(Page.class);

            when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

            when(googleStorageClient.list(anyString(), ArgumentMatchers.<Storage.BlobListOption>any())).thenReturn(blobPage);
            when(blobPage.iterateAll()).thenReturn(blobList);

            Optional<List<String>> optionalKeys = new FileDataExtractorUtilities().getGoogleStorageObjectNamesByPrefixAndLatestUTCModifiedDate(THE_BUCKET, KEY_NAME, 1);

            assertTrue(optionalKeys.isEmpty());

        } catch (Exception e) {
            assertFalse(e != null || e.getMessage() != null);
        }
    }

    @Test
    void getGoogleStorageObjectInputStreamTest_Success() {
        try {
            Storage googleStorageClient = mock(Storage.class);
            Page<Blob> blobPage = mock(Page.class);
            Blob blob1 = mock(Blob.class);
            ReadChannel readChannel = mock(ReadChannel.class);

            when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

            when(googleStorageClient.get(ArgumentMatchers.<BlobId>any())).thenReturn(blob1);
            when(blob1.exists()).thenReturn(Boolean.TRUE);

            when(googleStorageClient.reader(ArgumentMatchers.any())).thenReturn(readChannel);

            InputStream inputStream = new FileDataExtractorUtilities().getGoogleStorageObjectInputStream(THE_BUCKET, KEY_NAME);

            assertNotNull(inputStream);

        } catch (Exception e) {
            assertFalse(e != null || e.getMessage() != null);
        }
    }

    @Test
    void getGoogleStorageObjectInputStreamTest_NoObject() {
        try {
            Storage googleStorageClient = mock(Storage.class);
            Page<Blob> blobPage = mock(Page.class);

            Blob blob1 = mock(Blob.class);
            when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

            when(googleStorageClient.get(ArgumentMatchers.<BlobId>any())).thenReturn(blob1);
            when(blob1.exists()).thenReturn(Boolean.FALSE);

            new FileDataExtractorUtilities().getGoogleStorageObjectInputStream(THE_BUCKET, KEY_NAME);

            verify(googleStorageClient, never()).reader(ArgumentMatchers.<BlobId>any());

        } catch (Exception e) {
            assertTrue(e != null && e.getMessage() != null);
            assertEquals("Object:" + KEY_NAME + " not found in Google Cloud Storage Bucket:" + THE_BUCKET, e.getMessage());
        }
    }

    @Test
    void getGoogleStorageObjectNamesByPrefixAndLatestUTCModifiedDateTest_Success() {
        try {
            Blob blob1 = mock(Blob.class);
            Blob blob2 = mock(Blob.class);
            List<Blob> blobList = new ArrayList<>();
            blobList.add(blob1);
            blobList.add(blob2);
            Storage googleStorageClient = mock(Storage.class);
            Page<Blob> blobPage = mock(Page.class);
            when(GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials()).thenReturn(googleStorageClient);

            when(googleStorageClient.list(anyString(), ArgumentMatchers.<Storage.BlobListOption>any())).thenReturn(blobPage);
            when(blobPage.iterateAll()).thenReturn(blobList);

            when(blob1.getName()).thenReturn("blob1.csv");
            when(blob2.getName()).thenReturn("blob2.csv");

            when(blob1.getUpdateTimeOffsetDateTime()).thenReturn(Instant.now().atOffset(ZoneOffset.UTC));
            when(blob2.getUpdateTimeOffsetDateTime()).thenReturn(Instant.now().minus(1, ChronoUnit.DAYS).atOffset(ZoneOffset.UTC));

            Optional<List<String>> optionalKeys = new FileDataExtractorUtilities().getGoogleStorageObjectNamesByPrefixAndLatestUTCModifiedDate(THE_BUCKET, KEY_NAME, 1);

            assertTrue(optionalKeys.isPresent());
            assertEquals(1, optionalKeys.get().size());
            assertEquals("blob1.csv", optionalKeys.get().get(0));

        } catch (Exception e) {
            assertFalse(e != null || e.getMessage() != null);
        }
    }

}
