
package com.levi.order.service;

import com.levi.common.dao.LineEntryDao;
import com.levi.common.dao.OrderDetailsDao;
import com.levi.common.dao.ScheduleLineEntryDao;
import com.levi.wholesale.common.dto.OrderData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderDetailsServiceTest {

    @Mock
    private OrderDetailsDao orderDetailsDao;
    @Mock
    private LineEntryDao lineEntryDao;
    @Mock
    private ScheduleLineEntryDao scheduleLineEntryDao;

    @InjectMocks
    private OrderDetailsService orderDetailsService;

    @Test
    void testSaveOrderDetails() {
        List<OrderData> orderDataList = new ArrayList<>();
        OrderData orderData = getValidOrderData();
        orderDataList.add(orderData);

        orderDetailsService.saveOrderData(orderDataList);

        verify(orderDetailsDao).saveWithQuery(orderDataList);
        verify(lineEntryDao).saveWithQuery(orderDataList);
        verify(scheduleLineEntryDao).saveWithQuery(orderDataList);
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
}

