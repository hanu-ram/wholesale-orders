
package com.levi.wholesale.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.common.model.PricingErrorMessage;
import com.levi.common.repository.JobStatusRepository;
import com.levi.common.repository.LineEntryRepository;
import com.levi.wholesale.producer.CxKafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.levi.common.constant.Constants.WHOLESALE_CX_NO_DATA_FOUND;
import static com.levi.wholesale.util.TestUtility.getListAppenderForClass;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PricingProcessingServiceTest {
    @InjectMocks
    private PricingProcessingService pricingProcessingService;
    @Mock
    private LineEntryRepository lineEntryRepository;
    @Mock
    private JobStatusRepository jobStatusRepository;
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CxKafkaProducer cxKafkaProducer;

    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    public static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(PricingProcessingService.class);
    }

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(pricingProcessingService, "pricingErrorTopic", "TestTopic");
        ReflectionTestUtils.setField(pricingProcessingService, "offset", 0);
        ReflectionTestUtils.setField(pricingProcessingService, "limit", 5);
    }

    @Test
    void testProcessPricingErrorData_sendDataToTopic() throws IOException {
        List<PricingErrorMessage> pricingErrors = new ArrayList<>();
        PricingErrorMessage pricingErrorMessage = getPricingErrorMessage();
        pricingErrors.add(pricingErrorMessage);
        Page<PricingErrorMessage> pricingErrorMessagePage = new PageImpl<>(pricingErrors);

        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(lineEntryRepository.getFilteredPricingErrorData(any(Timestamp.class), any(Pageable.class)))
                .thenReturn(pricingErrorMessagePage);
        when(objectMapper.writeValueAsString(pricingErrorMessage)).thenReturn("{}");

        pricingProcessingService.processPricingErrorData();

        verify(cxKafkaProducer).publishOrderDetailsToKafka("{}", "TestSalesDocumentNumber", "TestTopic");
    }

    @Test
    void testProcessPricingErrorData_processMultiPageData() throws IOException {
        List<PricingErrorMessage> pricingErrors = new ArrayList<>();
        PricingErrorMessage pricingErrorMessage = getPricingErrorMessage();
        pricingErrors.add(pricingErrorMessage);
        pricingErrors.add(pricingErrorMessage);
        pricingErrors.add(pricingErrorMessage);
        Pageable pageable = PageRequest.of(0, 2);
        Page<PricingErrorMessage> pricingErrorMessagePage = new PageImpl<>(pricingErrors, pageable, 3);
        Page<PricingErrorMessage> pricingErrorMessagePage1 = new PageImpl<>(pricingErrors);

        ReflectionTestUtils.setField(pricingProcessingService, "limit", 2);
        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(lineEntryRepository.getFilteredPricingErrorData(any(Timestamp.class), any(Pageable.class)))
                .thenReturn(pricingErrorMessagePage)
                .thenReturn(pricingErrorMessagePage1);
        when(objectMapper.writeValueAsString(pricingErrorMessage)).thenReturn("{}");

        pricingProcessingService.processPricingErrorData();

        verify(cxKafkaProducer, times(6)).publishOrderDetailsToKafka("{}", "TestSalesDocumentNumber", "TestTopic");
    }

    @Test
    void processErrorMessageWithNoData_test() {
        List<PricingErrorMessage> pricingErrors = new ArrayList<>();
        Page<PricingErrorMessage> pricingErrorsPage = new PageImpl<>(pricingErrors);

        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(lineEntryRepository.getFilteredPricingErrorData(any(Timestamp.class), any(Pageable.class)))
                .thenReturn(pricingErrorsPage);
        pricingProcessingService.processPricingErrorData();
        verifyNoInteractions(cxKafkaProducer);

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(m -> m.startsWith(WHOLESALE_CX_NO_DATA_FOUND + " : Data to be sent to outbound topic not found"));
    }

    @Test
    void processPricingErrorData_throwRuntimeException_onJsonProcessingException() throws IOException {
        List<PricingErrorMessage> pricingErrors = new ArrayList<>();
        PricingErrorMessage pricingErrorMessage = getPricingErrorMessage();
        pricingErrors.add(pricingErrorMessage);
        Page<PricingErrorMessage> pricingErrorMessagePage = new PageImpl<>(pricingErrors);

        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(lineEntryRepository.getFilteredPricingErrorData(any(Timestamp.class), any(Pageable.class)))
                .thenReturn(pricingErrorMessagePage);
        when(objectMapper.writeValueAsString(pricingErrorMessage)).thenThrow(JsonProcessingException.class);

        Assertions.assertThrows(RuntimeException.class, () -> pricingProcessingService.processPricingErrorData());
        verifyNoInteractions(cxKafkaProducer);
    }


    @Test
    void processPricingErrorData_throwRuntimeException_onIOException() throws IOException {
        List<PricingErrorMessage> pricingErrors = new ArrayList<>();
        PricingErrorMessage pricingErrorMessage = getPricingErrorMessage();
        pricingErrors.add(pricingErrorMessage);
        Page<PricingErrorMessage> pricingErrorMessagePage = new PageImpl<>(pricingErrors);

        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(lineEntryRepository.getFilteredPricingErrorData(any(Timestamp.class), any(Pageable.class)))
                .thenReturn(pricingErrorMessagePage);
        when(objectMapper.writeValueAsString(pricingErrorMessage)).thenReturn("{}");
        doThrow(IOException.class).when(cxKafkaProducer).publishOrderDetailsToKafka("{}", "TestSalesDocumentNumber", "TestTopic");

        Assertions.assertThrows(RuntimeException.class, () -> pricingProcessingService.processPricingErrorData());
    }

    public PricingErrorMessage getPricingErrorMessage() {
        return new PricingErrorMessage() {
            @Override
            public String getSize() {
                return "30 32";
            }

            @Override
            public Timestamp getRequestedDeliveryDate() {
                return Timestamp.valueOf("2018-09-01 09:01:15");
            }

            @Override
            public Timestamp getCancelDate() {
                return Timestamp.valueOf("2018-09-01 09:01:15");
            }

            @Override
            public String getItemDescription() {
                return "testDescription";
            }

            @Override
            public String getCurrency() {
                return "USD";
            }

            @Override
            public String getSoldTo() {
                return "testSoldTo";
            }

            @Override
            public String getSalesDocumentNumber() {
                return "TestSalesDocumentNumber";
            }

            @Override
            public Timestamp getSalesDocumentDate() {
                return Timestamp.valueOf("2018-09-01 09:01:15");
            }

            @Override
            public String getPurchaseOrderNumber() {
                return "testPurchaseOrderNumber";
            }

            @Override
            public String getPlanningGroup() {
                return "testPlanningGroup";
            }

            @Override
            public String getBrand() {
                return "testBrand";
            }

            @Override
            public String getItemCategoryDescription() {
                return "testItemCategoryDescription";
            }

            @Override
            public String getConsumerGroup() {
                return "testConsumerGroup";
            }

            @Override
            public String getMaterialCode() {
                return "testMaterialCode";
            }

            @Override
            public Double getWholesalePrice() {
                return 100.0;
            }

            @Override
            public Timestamp getWholesalePriceValidFrom() {
                return Timestamp.valueOf("2018-09-01 09:01:15");
            }

            @Override
            public Timestamp getWholesalePriceValidTo() {
                return Timestamp.valueOf("2018-09-01 09:01:15");
            }

            @Override
            public Double getDiscounts() {
                return 5.00;
            }

            @Override
            public Double getGrossValue() {
                return 100.00;
            }

            @Override
            public String getExpectedPriceEdi() {
                return "testExpectedPriceEdi";
            }

            @Override
            public Double getOrderValue() {
                return 100.0;
            }

            @Override
            public String getLeviRetailPrice() {
                return null;
            }

            @Override
            public String getCustomerExpcMsrp() {
                return null;
            }

            @Override
            public String getRpmPrice() {
                return null;
            }

            @Override
            public Double getQuantity() {
                return null;
            }

            @Override
            public String getLineItem() {
                return "test-line-item";
            }

            @Override
            public String getScheduleLineItem() {
                return "test-schedule-line-item";
            }
        };
    }
}


