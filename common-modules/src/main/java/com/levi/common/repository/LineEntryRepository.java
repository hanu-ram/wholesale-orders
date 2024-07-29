package com.levi.common.repository;

import com.levi.common.model.LineEntry;
import com.levi.common.model.PricingErrorMessage;
import com.levi.common.model.UnconfirmedQuantityDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface LineEntryRepository extends JpaRepository<LineEntry, String> {

    @Query("select od.soldTo as soldTo, od.salesDocumentNumber as salesDocumentNumber, "
            + " od.salesDocumentDate as salesDocumentDate, od.purchaseOrderNumber as purchaseOrderNumber, od.amount as amount, "
            + " le.materialCode as materialCode, le.brand as brand, le.consumerGroup as consumerGroup, le.itemCategory as itemCategoryDescription, "
            + " le.materialName as itemDescription, le.plant as plant, le.currency as currency, le.stockType as stockType, le.netPrice as netPrice, "
            + " le.lineItem as lineItem "
            + " from LineEntry le "
            + " inner join OrderDetails od on od.salesDocumentNumber = le.orderDetails.salesDocumentNumber "
            + " where le.lineEntryId = :lineEntryId and od.isValid = true")
    UnconfirmedQuantityDetails getVirPacUnconfirmedLineEntry(@Param("lineEntryId") String lineEntryId);

    @Query("SELECT sku.size as size, sku.requestedDeliveryDate as requestedDeliveryDate, sku.cancelDate as cancelDate, "
            + " sku.lineItem as lineItem, sku.schLineItem as scheduleLineItem, "
            + " le.materialName as itemDescription, le.currency as currency, od.soldTo as soldTo,"
            + " od.salesDocumentNumber as salesDocumentNumber, od.salesDocumentDate as salesDocumentDate, "
            + " od.purchaseOrderNumber as purchaseOrderNumber, od.planningGroup as planningGroup, le.brand as brand,"
            + " le.itemCategory as itemCategoryDescription, le.consumerGroup as consumerGroup, le.materialCode as materialCode,"
            + " le.wholesalePrice as wholesalePrice, le.wholesalePriceValidFrom as wholesalePriceValidFrom, "
            + " le.wholesalePriceValidTo as wholesalePriceValidTo, le.discounts as discounts,le.grossValue as grossValue,"
            + " le.expectedPriceEdi as expectedPriceEdi, le.netValue as orderValue, le.leviRetailPrice as leviRetailPrice, "
            + " le.customerExpcMsrp as customerExpcMsrp, le.rpmPrice as rpmPrice, sku.orderedQuantity as quantity "
            + " FROM LineEntry le INNER JOIN ScheduleLineEntry sku ON le.lineEntryId = sku.lineEntry.lineEntryId "
            + " and le.modifiedTimestamp > :modifiedTimestamp INNER JOIN OrderDetails od"
            + " ON le.orderDetails.salesDocumentNumber = od.salesDocumentNumber and od.isValid = true"
            + " where (le.discounts > le.grossValue"
            + " OR le.grossValue != le.expectedPriceEdi"
            + " OR le.wholesalePrice = 0.0"
            + " OR le.netValue = 0.0) and sku.orderedQuantity > 0")
    Page<PricingErrorMessage> getFilteredPricingErrorData(@Param("modifiedTimestamp") Timestamp date, Pageable pageable);
}
