package com.levi.common.mapper;

import com.levi.common.model.UnconfirmedQuantityDetails;
import com.levi.wholesale.common.dto.PacVirUnconfirmedErrorDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UnconfirmedQtyErrorMapperTest {

    @InjectMocks
    UnconfirmedQtyErrorMapper unconfirmedQtyErrorMapper;


    @Test
    void mapToDto_Test() {
        UnconfirmedQuantityDetails details = getUnconfirmedQuantityDetails();

        PacVirUnconfirmedErrorDto errorDto = unconfirmedQtyErrorMapper.mapToDto(details, details);

        assertNull(errorDto.getOrderValue());
        assertEquals(details.getSalesDocumentNumber(), errorDto.getSalesDocumentNumber());
        assertEquals(details.getSoldTo(), errorDto.getSoldTo());
        assertEquals("20230131", errorDto.getSalesDocumentDate());
        assertEquals("20230131", errorDto.getRequestedDeliveryDate());
        assertEquals("20230131", errorDto.getCancelDate());
        assertEquals(details.getMaterialCode(), errorDto.getMaterialCode());
        assertEquals(details.getCurrency(), errorDto.getCurrency());
        assertEquals(details.getPurchaseOrderNumber(), errorDto.getPurchaseOrderNumber());
        assertEquals(details.getBrand(), errorDto.getBrand());
        assertEquals(details.getConsumerGroup(), errorDto.getConsumerGroup());
        assertEquals(details.getItemCategoryDescription(), errorDto.getItemCategoryDescription());
        assertEquals(details.getItemDescription(), errorDto.getItemDescription());
        assertEquals(details.getPlant(), errorDto.getPlant());
        assertEquals(details.getPacUnconfirmedQuantity(), errorDto.getPacUnconfirmedQuantity());
        assertEquals(details.getVirUnconfirmedQuantity(), errorDto.getVirUnconfirmedQuantity());
        assertEquals(6, errorDto.getUnconfirmedQuantity());
        assertEquals(details.getStockType(), errorDto.getStockType());
        assertEquals(details.getLineItem(), errorDto.getLineItem());
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
                return getPacUnconfirmedQuantity() + getVirUnconfirmedQuantity();
            }

            @Override
            public Timestamp getRequestedDeliveryDate() {
                return Timestamp.valueOf(LocalDateTime.of(2023, 1, 31, 12, 30));
            }

            @Override
            public Timestamp getCancelDate() {
                return Timestamp.valueOf(LocalDateTime.of(2023, 1, 31, 12, 30));
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
                return Timestamp.valueOf(LocalDateTime.of(2023, 1, 31, 12, 30));
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
                return "test-iten-category";
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