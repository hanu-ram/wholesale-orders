
package com.levi.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.levi.common.model.ErrorDetails;
import com.levi.util.OrderLoaderUtility;
import com.levi.wholesale.common.dto.OrderData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderProcessingServiceTest {
    @InjectMocks
    private OrderProcessingService orderProcessingService;

    @Mock
    private OrderDetailsService orderDetailsService;
    @Mock
    private ErrorDetailsService errorDetailsService;
    @Mock
    private RetryService retryService;
    @Mock
    private OrderLoaderUtility orderLoaderUtility;

    @Test
    void testValidateAndPopulateOrderList_shouldAddValidOrderDataToList() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData orderData = getValidOrderData();
        List<ErrorDetails> errorDetailsList = new ArrayList<>();

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(orderData);
        when(errorDetailsService.getErrorDetails(orderData.getSalesDocumentNumber(), false)).thenReturn(errorDetailsList);

        boolean isProcessedActual = orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        assertEquals(1, orderDataList.size());
        OrderData actual = orderDataList.get(0);
        assertTrue(actual.getIsValid());
        assertTrue(isProcessedActual);
    }

    @Test
    void testValidateAndPopulateOrderList_shouldMarkOrderDataValid_andAddToList() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData orderData = getValidOrderData();
        List<ErrorDetails> errorDetailsList = List.of(getErrorDetails(orderData));

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(orderData);
        when(errorDetailsService.getErrorDetails(orderData.getSalesDocumentNumber(), false)).thenReturn(errorDetailsList);
        doNothing().when(errorDetailsService).updateErrorDetails(any(OrderData.class), any(ErrorDetails.class));

        orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        assertEquals(1, orderDataList.size());
        OrderData actual = orderDataList.get(0);
        assertTrue(actual.getIsValid());
        verify(errorDetailsService).updateErrorDetails(orderData, errorDetailsList.get(0));
    }

    @Test
    void testValidateAndPopulateOrderList_shouldMarkOrderInvalid_andAddToList() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData orderData = getValidOrderData();
        ErrorDetails errorDetails = getErrorDetails(orderData);
        errorDetails.setId("different_error_details_id");
        List<ErrorDetails> errorDetailsList = Arrays.asList(errorDetails);

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(orderData);
        when(errorDetailsService.getErrorDetails(orderData.getSalesDocumentNumber(), false)).thenReturn(errorDetailsList);

        orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        assertEquals(1, orderDataList.size());
        OrderData actual = orderDataList.get(0);
        assertEquals(false, actual.getIsValid());

    }

    @Test
    void testValidateAndPopulateOrderList_updateInvalidOrderToErrorTable() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData invalidOrderData = getInvalidOrderData();
        ErrorDetails errorDetails = getErrorDetails(invalidOrderData);

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(invalidOrderData);
        when(errorDetailsService.getErrorDetails(getErrorId(invalidOrderData))).thenReturn(errorDetails);

        orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);


        verify(errorDetailsService).updateErrorDetails(invalidOrderData, errorDetails);
        assertEquals(1, orderDataList.size());
        OrderData actual = orderDataList.get(0);
        assertEquals(false, actual.getIsValid());
        assertEquals(invalidOrderData, actual);
        assertEquals(false, errorDetails.getIsProcessed());
    }

    @Test
    void testValidateAndPopulateOrderList_saveInvalidOrderToErrorTable() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData invalidOrderData = getInvalidOrderData();
        String errorId = getErrorId(invalidOrderData);

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(invalidOrderData);
        when(errorDetailsService.getErrorDetails(errorId)).thenReturn(null);

        orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        String invalidFields = "region,countryCode,materialSize";

        verify(errorDetailsService).saveErrorDetails(invalidOrderData, invalidFields);
        assertEquals(1, orderDataList.size());
        OrderData actual = orderDataList.get(0);
        assertEquals(false, actual.getIsValid());
        assertEquals(invalidOrderData, actual);

    }

    @Test
    void testValidateAndPopulateOrderList_saveInvalidOrderNegativeQtyToErrorTable() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData invalidOrderData = getValidOrderData();
        invalidOrderData.setQuantity(-2d);
        invalidOrderData.setFixedQty(-3d);
        String errorId = getErrorId(invalidOrderData);

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(invalidOrderData);
        when(errorDetailsService.getErrorDetails(errorId)).thenReturn(null);

        orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        String invalidFields = "quantity,fixedQty";

        verify(errorDetailsService).saveErrorDetails(invalidOrderData, invalidFields);
        assertEquals(1, orderDataList.size());
        OrderData actual = orderDataList.get(0);
        assertEquals(false, actual.getIsValid());
        assertEquals(invalidOrderData, actual);

    }

    @Test
    void testValidateAndPopulateOrderList_saveInvalidOrderNegativeQtyAndMissingFieldsToErrorTable() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData invalidOrderData = getInvalidOrderData();
        invalidOrderData.setQuantity(-2d);
        invalidOrderData.setFixedQty(-3d);
        String errorId = getErrorId(invalidOrderData);

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(invalidOrderData);
        when(errorDetailsService.getErrorDetails(errorId)).thenReturn(null);

        orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        String invalidFields = "region,countryCode,materialSize,quantity,fixedQty";

        verify(errorDetailsService).saveErrorDetails(invalidOrderData, invalidFields);
        assertEquals(1, orderDataList.size());
        OrderData actual = orderDataList.get(0);
        assertEquals(false, actual.getIsValid());
        assertEquals(invalidOrderData, actual);

    }

    @Test
    void testValidateAndPopulateOrderList_doNotAddDataToList_notSaveError() {
        List<OrderData> orderDataList = new ArrayList<>();
        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(null);

        orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        verifyNoInteractions(orderDetailsService);
        verifyNoInteractions(errorDetailsService);
        assertEquals(0, orderDataList.size());
    }

    @Test
    void testValidateAndPopulateOrderList_doNotAddDataToList_notSaveError_onException() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData validOrderData = getValidOrderData();

        when(orderLoaderUtility.getOrderDataFromJsonString("{}")).thenReturn(validOrderData);
        when(errorDetailsService.getErrorDetails(validOrderData.getSalesDocumentNumber(), false)).thenThrow(RuntimeException.class);

        boolean isProcessedActual = orderProcessingService.validateAndPopulateOrderList("{}", orderDataList);

        verifyNoInteractions(orderDetailsService);
        assertEquals(0, orderDataList.size());
        assertFalse(isProcessedActual);
    }

    @Test
    void testSaveOrderDataList_savesOrder() throws JsonProcessingException {
        List<OrderData> orderDataList = new ArrayList<>();
        orderDataList.add(getValidOrderData());

        orderProcessingService.saveOrderDataList(orderDataList);

        verify(orderDetailsService).saveOrderData(orderDataList);
    }

    @Test
    void testSaveOrderDataList_doNotSaveEmptyOrderList() throws JsonProcessingException {
        List<OrderData> orderDataList = new ArrayList<>();

        orderProcessingService.saveOrderDataList(orderDataList);

        verifyNoInteractions(orderDetailsService);
    }

    @Test
    void testSaveOrderDataList_sendDataToRetryTopic_onException() throws JsonProcessingException {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData orderData = getValidOrderData();
        orderDataList.add(orderData);
        doThrow(RuntimeException.class).when(orderDetailsService).saveOrderData(orderDataList);

        orderProcessingService.saveOrderDataList(orderDataList);

        verify(retryService).resendDataToKafkaTopic(orderDataList);
    }

    @Test
    void testValidateOrderData_returnsEmptyStringForValidData() {
        String invalidFields = orderProcessingService.validateOrderData(getValidOrderData());
        assertEquals("", invalidFields);
    }

    @Test
    void testValidateOrderData_returnsInvalidFields() {
        String expectedInvalidFields = "region,countryCode,materialSize";
        String invalidFields = orderProcessingService.validateOrderData(getInvalidOrderData());
        assertEquals(expectedInvalidFields, invalidFields);
    }

    private static ErrorDetails getErrorDetails(OrderData orderData) {
        ErrorDetails error = new ErrorDetails();
        error.setId(getErrorId(orderData));
        error.setSalesDocumentNumber(orderData.getSalesDocumentNumber());
        error.setLineItem(orderData.getLineItem());
        error.setSchLineItem(orderData.getSchLineItem());
        error.setMaterialCode(orderData.getMaterialCode());
        return error;
    }

    private static String getErrorId(OrderData orderData) {
        return orderData.getSalesDocumentNumber()
                + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem()
                + "-" + orderData.getMaterialCode();
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
        return orderData;
    }

    private static OrderData getInvalidOrderData() {
        OrderData orderData = new OrderData();
        orderData.setSoldTo("TestSoldTo");
        orderData.setPurchaseOrderNumber("TestPurchaseOrderNumber");
        orderData.setSalesDocumentNumber("TestSalesDocumentNUmber");
        orderData.setCurrency("USD");
        orderData.setRegion("");
        orderData.setCountryCode("");
        orderData.setMaterialCode("TestMaterialCode");
        orderData.setLineItem("TestLineItem");
        orderData.setSchLineItem("TestSchLineItem");
        orderData.setMaterialSize("");
        orderData.setSalesDocumentDate(Date.valueOf("2023-1-31"));
        return orderData;
    }
}
