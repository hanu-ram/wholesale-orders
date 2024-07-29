package com.levi.common.repository;

import com.levi.common.model.ScheduleLineEntry;
import com.levi.common.model.UnconfirmedQuantityDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface ScheduleLineEntryRepository extends JpaRepository<ScheduleLineEntry, String> {

    @Query(value = "select sku.lineEntry.lineEntryId as lineEntryId, "
            + " sum(sku.virUnconfirmedQuantity) as virUnconfirmedQuantity, "
            + " sum(sku.pacUnconfirmedQuantity) as pacUnconfirmedQuantity, "
            + " sum(sku.virUnconfirmedQuantity) + sum(sku.pacUnconfirmedQuantity) "
            + " as unconfirmedQuantity,  min(sku.cancelDate) as cancelDate, min(sku.requestedDeliveryDate) as requestedDeliveryDate "
            + " from  ScheduleLineEntry sku "
            + " where sku.modifiedTimestamp > :modifiedTimestamp "
            + " group by sku.lineEntry.lineEntryId "
            + " having sum(sku.virUnconfirmedQuantity) > 0 ")
    Page<UnconfirmedQuantityDetails> getVirUnconfirmedSchLineEntry(@Param("modifiedTimestamp") Timestamp date, Pageable pageable);

    @Query(value = "select sku.lineEntry.lineEntryId as lineEntryId, "
            + " sum(sku.virUnconfirmedQuantity) as virUnconfirmedQuantity, "
            + " sum(sku.pacUnconfirmedQuantity) as pacUnconfirmedQuantity, "
            + " sum(sku.virUnconfirmedQuantity) + sum(sku.pacUnconfirmedQuantity) "
            + " as unconfirmedQuantity,  min(sku.cancelDate) as cancelDate, min(sku.requestedDeliveryDate) as requestedDeliveryDate "
            + " from  ScheduleLineEntry sku "
            + " where sku.modifiedTimestamp > :modifiedTimestamp "
            + " group by sku.lineEntry.lineEntryId "
            + " having sum(sku.pacUnconfirmedQuantity) > 0 ")
    Page<UnconfirmedQuantityDetails> getPacUnconfirmedSchLineEntry(@Param("modifiedTimestamp") Timestamp date, Pageable pageable);


}
