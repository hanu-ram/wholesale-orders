package com.levi.wholesale.lambda.common.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommonUtilsTest {

    @Test
    void testValidateFilePatter_returnTrue(){
        String testPattern = "^salesorder_(.*?).csv";
        String testName = "salesorder_testName.csv";
        boolean actual = CommonUtils.validateFilePattern(testPattern, testName);
        assertTrue(actual);
    }

    @Test
    void testValidateFilePatter_returnTrue_whenNameContainSlash(){
        String testPattern = "^salesorder_(.*?).csv";
        String testName = "test/salesorder_testName.csv";
        boolean actual = CommonUtils.validateFilePattern(testPattern, testName);
        assertTrue(actual);
    }

    @Test
    void testValidateFilePatter_throwException(){
        String testPattern = "^salesorder_(.*?).csv";
        String testName = "salesordertestName.csv";
        assertThrows(RuntimeException.class, () -> CommonUtils.validateFilePattern(testPattern, testName));
    }

    @Test
    void testUtcDateTime(){
        assertNotNull(CommonUtils.getUtcDateTime());
    }
}