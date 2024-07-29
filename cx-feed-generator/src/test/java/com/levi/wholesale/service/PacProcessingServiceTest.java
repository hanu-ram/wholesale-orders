package com.levi.wholesale.service;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.common.mapper.UnconfirmedQtyErrorMapper;
import com.levi.common.model.UnconfirmedQuantityDetails;
import com.levi.common.repository.JobStatusRepository;
import com.levi.common.repository.LineEntryRepository;
import com.levi.common.repository.ScheduleLineEntryRepository;
import com.levi.wholesale.common.dto.PacVirUnconfirmedErrorDto;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;

import static com.levi.common.constant.Constants.WHOLESALE_CX_NO_DATA_FOUND;
import static com.levi.wholesale.util.TestUtility.getListAppenderForClass;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacProcessingServiceTest {
    @InjectMocks
    private PacProcessingService pacProcessingService;
    @Mock
    private ScheduleLineEntryRepository scheduleLineEntryRepository;
    @Mock
    private LineEntryRepository lineEntryRepository;
    @Mock
    private JobStatusRepository jobStatusRepository;
    @Mock
    private UnconfirmedQtyErrorMapper unconfirmedQtyErrorMapper;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CxKafkaProducer cxKafkaProducer;

    private static ListAppender<ILoggingEvent> listAppenderForClass;

    @BeforeAll
    static void beforeClass() {
        listAppenderForClass = getListAppenderForClass(PacProcessingService.class);

    }

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(pacProcessingService, "pacUnconfirmedErrorTopic", "TestTopic");
        ReflectionTestUtils.setField(pacProcessingService, "offset", 0);
        ReflectionTestUtils.setField(pacProcessingService, "limit", 5);
    }


    @Test
    void processPacUnconfirmedQuantity_test() throws IOException {
        List<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsList = new ArrayList<>();
        UnconfirmedQuantityDetails quantityDetails = getUnconfirmedQuantityDetails();
        unconfirmedQuantityDetailsList.add(quantityDetails);
        Page<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsPage = new PageImpl<>(unconfirmedQuantityDetailsList);

        PacVirUnconfirmedErrorDto errorDto = new PacVirUnconfirmedErrorDto();

        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(scheduleLineEntryRepository.getPacUnconfirmedSchLineEntry(any(Timestamp.class), any(Pageable.class))).thenReturn(unconfirmedQuantityDetailsPage);
        when(lineEntryRepository.getVirPacUnconfirmedLineEntry("test-line-entry")).thenReturn(quantityDetails);
        when(unconfirmedQtyErrorMapper.mapToDto(quantityDetails, quantityDetails))
                .thenReturn(errorDto);

        String expectedJson = new ObjectMapper().writeValueAsString(errorDto);
        when(objectMapper.writeValueAsString(errorDto)).thenReturn(expectedJson);


        pacProcessingService.processPacUnconfirmedQuantity();

        verify(cxKafkaProducer).publishOrderDetailsToKafka(expectedJson, "test-line-entry-PAC", "TestTopic");
    }

    @Test
    void testProcessPacUnconfirmedQuantity_processMultiPageData() throws IOException {
        List<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsList = new ArrayList<>();
        UnconfirmedQuantityDetails quantityDetails = getUnconfirmedQuantityDetails();
        unconfirmedQuantityDetailsList.add(quantityDetails);
        unconfirmedQuantityDetailsList.add(quantityDetails);
        unconfirmedQuantityDetailsList.add(quantityDetails);
        Pageable pageable = PageRequest.of(0, 2);
        Page<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsPage = new PageImpl<>(unconfirmedQuantityDetailsList, pageable, 3);
        Page<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsPage1 = new PageImpl<>(unconfirmedQuantityDetailsList);
        PacVirUnconfirmedErrorDto errorDto = new PacVirUnconfirmedErrorDto();

        ReflectionTestUtils.setField(pacProcessingService, "limit", 2);
        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(scheduleLineEntryRepository.getPacUnconfirmedSchLineEntry(any(Timestamp.class), any(Pageable.class)))
                .thenReturn(unconfirmedQuantityDetailsPage)
                .thenReturn(unconfirmedQuantityDetailsPage1);
        when(lineEntryRepository.getVirPacUnconfirmedLineEntry("test-line-entry")).thenReturn(quantityDetails);
        when(unconfirmedQtyErrorMapper.mapToDto(quantityDetails, quantityDetails))
                .thenReturn(errorDto);

        String expectedJson = new ObjectMapper().writeValueAsString(errorDto);
        when(objectMapper.writeValueAsString(errorDto)).thenReturn(expectedJson);

        pacProcessingService.processPacUnconfirmedQuantity();

        verify(cxKafkaProducer, times(6)).publishOrderDetailsToKafka(expectedJson, "test-line-entry-PAC", "TestTopic");
    }

    @Test
    void processPacUnconfirmedQuantity_throwsRuntimeException_onJsonProcessingException() throws JsonProcessingException {
        List<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsList = new ArrayList<>();
        UnconfirmedQuantityDetails quantityDetails = getUnconfirmedQuantityDetails();
        unconfirmedQuantityDetailsList.add(quantityDetails);
        Page<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsPage = new PageImpl<>(unconfirmedQuantityDetailsList);
        PacVirUnconfirmedErrorDto errorDto = new PacVirUnconfirmedErrorDto();

        when(scheduleLineEntryRepository.getPacUnconfirmedSchLineEntry(any(Timestamp.class), any(Pageable.class))).thenReturn(unconfirmedQuantityDetailsPage);
        when(lineEntryRepository.getVirPacUnconfirmedLineEntry("test-line-entry")).thenReturn(quantityDetails);
        when(unconfirmedQtyErrorMapper.mapToDto(quantityDetails, quantityDetails))
                .thenReturn(errorDto);
        when(objectMapper.writeValueAsString(errorDto)).thenThrow(new JsonProcessingException("Test Exception") {
        });

        Assertions.assertThrows(RuntimeException.class,
                () -> pacProcessingService.processPacUnconfirmedQuantity(), "Test Exception");
        verifyNoInteractions(cxKafkaProducer);
    }

    @Test
    void processPacUnconfirmedQuantity_throwsRuntimeException_onIOException() throws IOException {
        List<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsList = new ArrayList<>();
        UnconfirmedQuantityDetails quantityDetails = getUnconfirmedQuantityDetails();
        unconfirmedQuantityDetailsList.add(quantityDetails);
        Page<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsPage = new PageImpl<>(unconfirmedQuantityDetailsList);
        PacVirUnconfirmedErrorDto errorDto = new PacVirUnconfirmedErrorDto();

        when(scheduleLineEntryRepository.getPacUnconfirmedSchLineEntry(any(Timestamp.class), any(Pageable.class))).thenReturn(unconfirmedQuantityDetailsPage);
        when(lineEntryRepository.getVirPacUnconfirmedLineEntry("test-line-entry")).thenReturn(quantityDetails);
        when(unconfirmedQtyErrorMapper.mapToDto(quantityDetails, quantityDetails))
                .thenReturn(errorDto);
        when(objectMapper.writeValueAsString(errorDto)).thenReturn("{}");
        doThrow(IOException.class).when(cxKafkaProducer).publishOrderDetailsToKafka("{}", "test-line-entry-PAC", "TestTopic");

        Assertions.assertThrows(RuntimeException.class,
                () -> pacProcessingService.processPacUnconfirmedQuantity(), "Test Exception");
    }

    @Test
    void processPacUnconfirmedQuantity_ShouldNotSendInvalidDataToKafkaTopic() {
        List<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsList = new ArrayList<>();
        UnconfirmedQuantityDetails quantityDetails = getUnconfirmedQuantityDetails();
        unconfirmedQuantityDetailsList.add(quantityDetails);
        Page<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsPage = new PageImpl<>(unconfirmedQuantityDetailsList);

        when(scheduleLineEntryRepository.getPacUnconfirmedSchLineEntry(any(Timestamp.class), any(Pageable.class))).thenReturn(unconfirmedQuantityDetailsPage);
        when(lineEntryRepository.getVirPacUnconfirmedLineEntry("test-line-entry")).thenReturn(null);

        pacProcessingService.processPacUnconfirmedQuantity();

        verifyNoInteractions(objectMapper);
        verifyNoInteractions(cxKafkaProducer);
    }

    @Test
    void processPacUnconfirmedQuantity_ShouldNotProcessNullData() throws IOException {
        List<UnconfirmedQuantityDetails> emptyList = new ArrayList<>();
        Page<UnconfirmedQuantityDetails> unconfirmedQuantityDetailsPage = new PageImpl<>(emptyList);


        when(jobStatusRepository.getLastRunForCXJob(anyString(), any(Date.class))).thenReturn(null);
        when(scheduleLineEntryRepository.getPacUnconfirmedSchLineEntry(any(Timestamp.class), any(Pageable.class))).thenReturn(unconfirmedQuantityDetailsPage);

        pacProcessingService.processPacUnconfirmedQuantity();

        org.assertj.core.api.Assertions.assertThat(listAppenderForClass.list)
                .extracting(ILoggingEvent::getFormattedMessage)
                .anyMatch(s -> s.startsWith(WHOLESALE_CX_NO_DATA_FOUND + " : Data to be sent to outbound topic not found"));

        verifyNoInteractions(lineEntryRepository, unconfirmedQtyErrorMapper);
        verify(cxKafkaProducer, never()).publishOrderDetailsToKafka(anyString(), anyString(), anyString());
    }


    public UnconfirmedQuantityDetails getUnconfirmedQuantityDetails() {
        return new UnconfirmedQuantityDetails() {
            @Override
            public String getLineEntryId() {
                return "test-line-entry";
            }

            @Override
            public Double getVirUnconfirmedQuantity() {
                return 4.0;
            }

            @Override
            public Double getPacUnconfirmedQuantity() {
                return 2.0;
            }

            @Override
            public Double getUnconfirmedQuantity() {
                return 6.0;
            }

            @Override
            public Timestamp getRequestedDeliveryDate() {
                return Timestamp.valueOf(LocalDateTime.of(2023, 3, 14, 0, 0));
            }

            @Override
            public Timestamp getCancelDate() {
                return Timestamp.valueOf(LocalDateTime.of(2023, 3, 14, 0, 0));
            }

            @Override
            public String getSalesDocumentNumber() {
                return "test-sales-document-number";
            }

            @Override
            public String getSoldTo() {
                return "test-sold-to";
            }

            @Override
            public Timestamp getSalesDocumentDate() {
                return Timestamp.valueOf(LocalDateTime.of(2023, 3, 14, 0, 0));
            }

            @Override
            public String getPurchaseOrderNumber() {
                return "test-purchase-order-number";
            }

            @Override
            public String getBrand() {
                return "test-brand";
            }

            @Override
            public String getConsumerGroup() {
                return "test-consumer-group";
            }

            @Override
            public String getItemCategoryDescription() {
                return "test-item-category";
            }

            @Override
            public String getItemDescription() {
                return "test-description";
            }

            @Override
            public Double getNetPrice() {
                return 100d;
            }

            @Override
            public String getStockType() {
                return "test-stock-type";
            }

            @Override
            public Currency getCurrency() {
                return Currency.getInstance("USD");
            }

            @Override
            public String getPlant() {
                return "test-plant";
            }

            @Override
            public String getMaterialCode() {
                return "test-material-code";
            }

            @Override
            public String getLineItem() {
                return "test-line-item";
            }
        };
    }
}

