package com.levi.wholesale.lambda.common.utils;

import com.amazonaws.util.IOUtils;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@Slf4j
public final class FeedUtil {

    private static final Integer BOM_BYTES_SZ = 3;
    private static final String BOM_STR = "efbbbf";

    private FeedUtil() {
    }

    public static <T> List<T> loadCSV(Class<T> classs, InputStream inputStream) throws IOException {
        try (InputStreamReader inpStreamReader = new InputStreamReader(inputStream)) {
            CSVReader csvReader = new CSVReader(inpStreamReader);
            return new CsvToBeanBuilder<T>(csvReader).withType(classs).build().parse();
        } catch (RuntimeException | IOException ex) {
            log.error("Failed to process CSV" + ex);
            throw ex;
        }
    }

    /*
     * This method is to remove the BOM mark if present in the Input Stream. It is
     * of size 3 bytes that is coming within file.
     */
    public static InputStream processBOM(InputStream is) throws IOException {

        byte[] allBytes = IOUtils.toByteArray(is);

        byte[] bomBytes = Arrays.copyOf(allBytes, BOM_BYTES_SZ);
        // BOM encoded as ef bb bf
        String content = new String(Hex.encodeHex(bomBytes));
        InputStream targetStream = new ByteArrayInputStream(allBytes);
        if (BOM_STR.equalsIgnoreCase(content)) {
            log.info("BOM is present");

            byte[] remainingBytes = Arrays.copyOfRange(allBytes, BOM_BYTES_SZ, allBytes.length);
            targetStream = new ByteArrayInputStream(remainingBytes);
        }

        return targetStream;
    }
}
