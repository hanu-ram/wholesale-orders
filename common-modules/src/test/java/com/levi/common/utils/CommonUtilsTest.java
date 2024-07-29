package com.levi.common.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class CommonUtilsTest {

    @Test
    void testGetUtcDateTime() {
        assertNotNull(CommonUtils.getUtcDateTime());
    }

    @Test
    void testValidateFields_DoNotAppendField() {
        StringBuilder testResultString = new StringBuilder();
        String expectedResult = "";

        CommonUtils.validateField("test-value", "testField", new StringBuilder());
        assertEquals(expectedResult, testResultString.toString());
    }

    @Test
    void testValidateFields_AppendNullStringField() {
        StringBuilder testResultString = new StringBuilder();
        String testFieldName = "testField";
        String testValue = null;
        String expectedResult = "testField,";

        CommonUtils.validateField(testValue, testFieldName, testResultString);
        assertEquals(expectedResult, testResultString.toString());
    }

    @Test
    void testValidateFields_AppendEmptyStringField() {
        StringBuilder testResultString = new StringBuilder();
        String testFieldName = "testField";
        String testValue = "";
        String expectedResult = "testField,";

        CommonUtils.validateField(testValue, testFieldName, testResultString);
        assertEquals(expectedResult, testResultString.toString());
    }

    @Test
    void testValidateFields_AppendNullDateField() {
        StringBuilder testResultString = new StringBuilder();
        String testFieldName = "testDateField";
        Date testValue = null;
        String expectedResult = "testDateField,";

        CommonUtils.validateField(testValue, testFieldName, testResultString);
        assertEquals(expectedResult, testResultString.toString());
    }

    @Test
    void testValidateQuantity_AppendNegativeField(){
        StringBuilder testResultString = new StringBuilder();
        String testFieldName = "testQuantity";
        Double testValue = -2.0;
        String expectedResult = "testQuantity,";

        CommonUtils.validateQuantity(testValue, testFieldName, testResultString);
        assertEquals(expectedResult, testResultString.toString());
    }

    @Test
    void testValidateQuantity_NotAppendPositiveField(){
        StringBuilder testResultString = new StringBuilder();
        String testFieldName = "testQuantity";
        Double testValue = 2.0;
        String expectedResult = "";

        CommonUtils.validateQuantity(testValue, testFieldName, testResultString);
        assertEquals(expectedResult, testResultString.toString());
    }

    @Test
    void testValidateQuantity_NotAppendNullField(){
        StringBuilder testResultString = new StringBuilder();
        String testFieldName = "testQuantity";
        Double testValue = null;
        String expectedResult = "";

        CommonUtils.validateQuantity(testValue, testFieldName, testResultString);
        assertEquals(expectedResult, testResultString.toString());
    }

    @Test
    void testGetDateString_ReturnsFormattedDate() {
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.of(2023, 1, 14, 12, 55));
        String expectedString = "20230114";

        String actual = CommonUtils.getDateString(timestamp);
        assertEquals(expectedString, actual);
    }

    @Test
    void testGetDateString_ReturnsNull() {
        assertNull(CommonUtils.getDateString(null));
    }

    @Test
    void testGetCurrentDate() throws ParseException {
        assertNotNull(CommonUtils.getCurrentDate());
    }

}