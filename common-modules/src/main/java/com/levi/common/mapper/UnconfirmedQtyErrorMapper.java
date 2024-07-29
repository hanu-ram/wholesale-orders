package com.levi.common.mapper;

import com.levi.common.model.UnconfirmedQuantityDetails;
import com.levi.wholesale.common.dto.PacVirUnconfirmedErrorDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.levi.common.utils.CommonUtils.getDateString;

@Component
@Slf4j
public class UnconfirmedQtyErrorMapper {

    public PacVirUnconfirmedErrorDto mapToDto(UnconfirmedQuantityDetails skuDetails, UnconfirmedQuantityDetails lineEntry) {
       log.info("Mapping UnconfirmedQuantityDetails to PacVirUnconfirmedErrorDto.");
        PacVirUnconfirmedErrorDto errorDto = new PacVirUnconfirmedErrorDto();
        errorDto.setUnconfirmedQuantity(skuDetails.getUnconfirmedQuantity());
        errorDto.setVirUnconfirmedQuantity(skuDetails.getVirUnconfirmedQuantity());
        errorDto.setPacUnconfirmedQuantity(skuDetails.getPacUnconfirmedQuantity());

        errorDto.setRequestedDeliveryDate(getDateString(skuDetails.getRequestedDeliveryDate()));
        errorDto.setCancelDate(getDateString(skuDetails.getCancelDate()));

        errorDto.setMaterialCode(lineEntry.getMaterialCode());
        errorDto.setBrand(lineEntry.getBrand());
        errorDto.setConsumerGroup(lineEntry.getConsumerGroup());
        errorDto.setItemCategoryDescription(lineEntry.getItemCategoryDescription());
        errorDto.setItemDescription(lineEntry.getItemDescription());
        errorDto.setPlant(lineEntry.getPlant());
        errorDto.setCurrency(lineEntry.getCurrency());

        errorDto.setSoldTo(lineEntry.getSoldTo());
        errorDto.setSalesDocumentNumber(lineEntry.getSalesDocumentNumber());

        errorDto.setSalesDocumentDate(getDateString(lineEntry.getSalesDocumentDate()));
        errorDto.setPurchaseOrderNumber(lineEntry.getPurchaseOrderNumber());
        errorDto.setStockType(lineEntry.getStockType());
        errorDto.setLineItem(lineEntry.getLineItem());
        return errorDto;
    }
}
