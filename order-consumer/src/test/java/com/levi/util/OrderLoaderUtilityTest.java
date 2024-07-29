package com.levi.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.levi.wholesale.common.dto.OrderData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;

import static com.levi.common.constant.Constants.OC_INVALID_DATE_FORMAT;
import static com.levi.common.constant.Constants.OC_PARSING_FAILURE_ALERT;
import static com.levi.order.test.util.TestUtility.getListAppenderForClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderLoaderUtilityTest {
    @InjectMocks
    private OrderLoaderUtility orderLoaderUtility;

    @Mock
    private ObjectMapper objectMapper;
    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    public static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(OrderLoaderUtility.class);
    }

    @Test
    void testGetOrderDataFromJsonString_returnsOrderData() throws JsonProcessingException {
        OrderData validOrderData = getValidOrderData();
        String json = "{\"sales_document_number\":\"test_sales_doc_number\"}";
        when(objectMapper.readValue(json, OrderData.class)).thenReturn(validOrderData);

        OrderData orderDataFromJsonString = orderLoaderUtility.getOrderDataFromJsonString(json);

        assertEquals(validOrderData, orderDataFromJsonString);
    }

    @Test
    void testGetOrderDataFromJsonString_returnsNull_onException() throws JsonProcessingException {
        String json = "{\"sales_document_number\":\"test_sales_doc_number\"}";
        when(objectMapper.readValue(json, OrderData.class)).thenThrow(JsonProcessingException.class);

        OrderData orderDataFromJsonString = orderLoaderUtility.getOrderDataFromJsonString(json);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith(OC_PARSING_FAILURE_ALERT
                        + ": Failed when deserializing the JSON into POJO : {\"sales_document_number\":\"test_sales_doc_number\"}"));

        assertNull(orderDataFromJsonString);
    }

    @Test
    void testGetOrderDataFromJsonString_returnsNull_forInvalidDateFormat() throws JsonProcessingException {
        String json = "{\"sold_to\": \"test_sold_to\",\n"
                + "  \"purchase_order_number\": \"1129\",\n"
                + "  \"sales_document_number\": \"10001\",\n"
                + "  \"sales_document_date\": \"2021/10/22\"}";
        when(objectMapper.readValue(json, OrderData.class)).thenThrow(new InvalidFormatException("expected format \"yyyyMMdd\"", "", Date.class));

        OrderData orderDataFromJsonString = orderLoaderUtility.getOrderDataFromJsonString(json);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith(OC_INVALID_DATE_FORMAT
                        + " : Invalid date format for some of the fields"));

        assertNull(orderDataFromJsonString);
    }

    @Test
    void testGetOrderDataFromJsonString_returnsNull_onJsonProcessingException() throws JsonProcessingException {
        String json = "{\"sold_to\": \"test_sold_to\",\n"
                + "  \"purchase_order_number\": \"1129\",\n"
                + "  \"sales_document_number\": \"10001\",\n"
                + "  \"sales_document_date\": \"2021/10/22\"}";
        when(objectMapper.readValue(json, OrderData.class)).thenThrow(new InvalidFormatException("Test message", "", Date.class));

        OrderData orderDataFromJsonString = orderLoaderUtility.getOrderDataFromJsonString(json);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith(OC_PARSING_FAILURE_ALERT
                        + ": Failed when deserializing the JSON into POJO"));

        assertNull(orderDataFromJsonString);
    }

    private static OrderData getValidOrderData() {
        OrderData orderData = new OrderData();
        orderData.setSoldTo("TestSoldTo");
        orderData.setPurchaseOrderNumber("TestPurchaseOrderNumber");
        orderData.setSalesDocumentNumber("TestSalesDocumentNumber");
        orderData.setCurrency("USD");
        orderData.setRegion("TestRegion");
        orderData.setCountryCode("TestCountryCode");
        orderData.setMaterialCode("TestMaterialCode");
        orderData.setLineItem("TestLineItem");
        orderData.setSchLineItem("TestSchLineItem");
        orderData.setMaterialSize("TestMaterialSize");
        orderData.setSalesDocumentDate(Date.valueOf("2023-1-31"));
        return orderData;
    }
}