package com.levi.order.service;

import com.levi.common.dao.ErrorDetailsDao;
import com.levi.common.mapper.ErrorDetailsMapper;
import com.levi.common.model.ErrorDetails;
import com.levi.common.repository.ErrorRepository;
import com.levi.wholesale.common.dto.OrderData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorDetailsServiceTest {

    private static final String SALES_DOCUMENT_NUMBER = "123456";
    @InjectMocks
    private ErrorDetailsService errorDetailsService;

    @Mock
    private ErrorDetailsMapper errorDetailsMapper;

    @Mock
    private ErrorRepository errorRepository;

    @Mock
    private ErrorDetailsDao errorDetailsDao;

    @Test
    void testSaveErrorDetails() {
        OrderData orderData = new OrderData();
        String id = "test_sales_document_number-Test_line_item-Test_Sch_line_item-test_material_code";
        String message = "Invalid fields : testField";

        when(errorDetailsDao.saveWithQuery(orderData, message, false)).thenReturn(id);
        errorDetailsService.saveErrorDetails(orderData, "testField");
        Mockito.verify(errorDetailsDao).saveWithQuery(orderData, message, false);
    }

    @Test
    void testUpdateErrorDetails() {
        ErrorDetails errorDetails = new ErrorDetails();
        OrderData orderData = new OrderData();
        String id = "test_sales_document_number-Test_line_item-Test_Sch_line_item-test_material_code";
        String message = "Invalid fields : testField";

        when(errorDetailsMapper.mapToModel(orderData, errorDetails)).thenReturn(errorDetails);
        when(errorDetailsDao.saveWithQuery(orderData, message, true)).thenReturn(id);

        errorDetails.setErrorMessage(message);
        errorDetails.setIsProcessed(true);
        errorDetailsService.updateErrorDetails(orderData, errorDetails);

        Mockito.verify(errorDetailsDao).saveWithQuery(orderData, message, true);
    }

    @Test
    void testGetErrorDetails_ShouldReturnErrorDetailsBySchLineEntry() {
        OrderData orderData = getOrderData();
        String errorId = orderData.getSalesDocumentNumber()
                + "-" + orderData.getLineItem()
                + "-" + orderData.getSchLineItem()
                + "-" + orderData.getMaterialCode();
        errorDetailsService.getErrorDetails(errorId);
        Mockito.verify(errorRepository).findById(errorId);
    }

    @Test
    void testGetErrorDetails_ShouldReturnErrorDetailsBySchLineEntryAndIsProcessed() {
        List<ErrorDetails> errorDetails = List.of(new ErrorDetails());
        when(errorRepository.findErrorDetailsBySalesDocument(SALES_DOCUMENT_NUMBER, true)).thenReturn(errorDetails);
        List<ErrorDetails> actual = errorDetailsService.getErrorDetails(SALES_DOCUMENT_NUMBER, true);
        Assertions.assertSame(errorDetails, actual);
    }

    private static OrderData getOrderData() {
        OrderData orderData = new OrderData();
        orderData.setSalesDocumentNumber(SALES_DOCUMENT_NUMBER);
        orderData.setLineItem("LineItem1");
        orderData.setSchLineItem("SchLineItem1");
        orderData.setMaterialCode("011-00012");
        return orderData;
    }
}