package com.levi.wholesale.util;

import com.google.api.gax.paging.Page;
import com.google.cloud.ReadChannel;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;
import com.levi.wholesale.lambda.common.exception.GdoExtractorException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.channels.Channels;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static com.levi.wholesale.config.GCPConfiguration.createGoogleCloudStorageClientFromLocalCredentials;
import static com.levi.wholesale.lambda.common.constant.Constants.OBJECT;
import static com.levi.wholesale.lambda.common.constant.Constants.NOT_FOUND;


@Slf4j
public class FileDataExtractorUtilities {

    /**
     * This method gets the Google Cloud Storage Blob Names by prefixes and groups
     * all the Blobs by generated/modified on the same UTC date.<br>
     * It returns the group of files with the most recent generated/modified
     * date.<br>
     * <p>
     * This is specifically useful for getting multiple files matching a prefix and
     * generated on the same UTC date, when we have lot of old files in the bucket.
     * <p>
     * Note: This would have unpredictable behavior if the intended files are not
     * generated in the same UTC day.
     *
     * @param bucket
     * @param prefix
     * @param daysRange
     * @return {@code Optional<List<String>>} Blob names
     */
    public Optional<List<String>> getGoogleStorageObjectNamesByPrefixAndLatestUTCModifiedDate(String bucket, String prefix,
                                                                                              long daysRange) throws Exception {
        List<String> blobNames;
        List<Blob> blobs;
        List<Blob> blobList;
        Page<Blob> blobPage;
        try (Storage googleStorageClient = createGoogleCloudStorageClientFromLocalCredentials()) {
            blobNames = null;
            blobList = new ArrayList<>();
            log.info("Fetching objects in Google Cloud Storage bucket:{} with prefix:{}", bucket, prefix);
            blobPage = googleStorageClient.list(bucket, BlobListOption.currentDirectory(), BlobListOption.prefix(prefix));
        }
        Iterable<Blob> blobIterable = blobPage.iterateAll();

        int counter = 0;
        for (Blob blob : blobIterable) {
            blobList.add(blob);
            counter++;
        }
        log.info("Found {}# objects", counter);
        if (blobList.isEmpty()) {
            log.warn("No objects in Google Storage bucket: {} with prefix: {}", bucket, prefix);
            return Optional.empty();
        }

        // Filter the Blobs which have lastUpdated date within daysRange
        blobs = blobList.stream().filter(b -> this.isDateInValidRange(b.getUpdateTimeOffsetDateTime(), daysRange))
                .collect(Collectors.toList());

        log.info("Grouping the blobs by UTC Date");
        // Now group the blobs by the UTC date part of their update date and sort the
        // group with latest date first
        TreeMap<LocalDate, List<String>> blobsNamesByUTCDate = new TreeMap<>(Collections.reverseOrder());

        blobs.stream().filter(blob -> blob.getName().endsWith(".csv")).forEach(blob -> {
            log.info("File name ::{}", blob);
            LocalDate utcDate = blob.getUpdateTimeOffsetDateTime().toLocalDate();
            if (!blobsNamesByUTCDate.containsKey(utcDate)) {
                blobsNamesByUTCDate.put(utcDate, new ArrayList<>());
            }
            blobsNamesByUTCDate.get(utcDate).add(blob.getName());
        });

        // Get the first entry as this contains the files with the same latest UTC date
        if (!blobsNamesByUTCDate.isEmpty()) {
            // As we need to process the current date files taking the first entry
            blobNames = blobsNamesByUTCDate.firstEntry().getValue();
            log.info("Found {} # files for UTC Date:{}", blobNames.size(), blobsNamesByUTCDate.firstEntry().getKey());
        } else {
            log.warn("##### No files found for processing the current date #####");
        }
        return Optional.ofNullable(blobNames);
    }

    /**
     * Get the InputStream for a specified Google Cloud Storage Object
     *
     * @param bucket     - This is the GCS bucket name
     * @param objectName - This is the file name
     * @return InputStream - The file content
     */
    public InputStream getGoogleStorageObjectInputStream(final String bucket, final String objectName) throws Exception {
        ReadChannel readChannel;
        try (Storage googleStorageClient = createGoogleCloudStorageClientFromLocalCredentials()) {
            // Frame a BlobId from bucket and blob name
            BlobId blobId = BlobId.of(bucket, objectName);
            // If Blob does not exist throw exception
            Blob blob = googleStorageClient.get(blobId);
            if (blob == null || !blob.exists()) {
                    throw new GdoExtractorException(OBJECT + objectName + NOT_FOUND + bucket);
            }
            // Get ReadChannel from BlobId
            readChannel = googleStorageClient.reader(BlobId.of(bucket, objectName));
        }
        // Frame InputStream from ReadChannel and return the same
        return Channels.newInputStream(readChannel);
    }

    public Blob copyObject(final String bucket, final String destinationBucket, final String objectName) throws Exception {
        Blob blob;
        try (Storage googleStorageClient = createGoogleCloudStorageClientFromLocalCredentials()) {
            int length = objectName.split("/").length;
            Storage.CopyRequest request =
                    Storage.CopyRequest.newBuilder()
                            .setSource(BlobId.of(bucket, objectName))
                            .setTarget(BlobId.of(bucket, destinationBucket + objectName.split("/")[length - 1]))
                            .build();
            // If Blob does not exist throw exception
            blob = googleStorageClient.copy(request).getResult();
        }
        if (blob == null || !blob.exists()) {
            throw new GdoExtractorException(OBJECT + objectName + NOT_FOUND + bucket);
        }
        return blob;
    }

    public void deleteObject(final String bucket, final String objectName) throws Exception {
        try (Storage googleStorageClient = createGoogleCloudStorageClientFromLocalCredentials()) {
            // Frame a BlobId from bucket and blob name
            BlobId blobId = BlobId.of(bucket, objectName);
            // If Blob does not exist throw exception
            Blob blob = googleStorageClient.get(blobId);
            if (blob == null || !blob.exists()) {
                throw new GdoExtractorException(OBJECT + objectName + NOT_FOUND + bucket);
            }
            googleStorageClient.delete(blobId);
        }
        log.info("File {} deleted successfully", objectName);
    }

    public boolean isDateInValidRange(OffsetDateTime offsetDateTime, long range) {
        LocalDate currentDate = LocalDate.now();
        if (offsetDateTime != null) {
            LocalDate inDate = offsetDateTime.toLocalDate();
            return inDate.isAfter(currentDate.minusDays(range));
        }
        return false;
    }
}
