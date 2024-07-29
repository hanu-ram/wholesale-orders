package com.levi.order.service;

import com.levi.common.dao.LineEntryDao;
import com.levi.common.dao.OrderDetailsDao;
import com.levi.common.dao.ScheduleLineEntryDao;
import com.levi.wholesale.common.dto.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class OrderDetailsService {
    private final OrderDetailsDao orderDetailsDao;
    private final LineEntryDao lineEntryDao;
    private final ScheduleLineEntryDao scheduleLineEntryDao;

    @Autowired
    public OrderDetailsService(OrderDetailsDao orderDetailsDao, LineEntryDao lineEntryDao,
                               ScheduleLineEntryDao scheduleLineEntryDao) {
        this.orderDetailsDao = orderDetailsDao;
        this.lineEntryDao = lineEntryDao;
        this.scheduleLineEntryDao = scheduleLineEntryDao;
    }

    @Transactional
    public void saveOrderData(List<OrderData> orderDataList) {
        log.info("Saving order data in order details table");
        orderDetailsDao.saveWithQuery(orderDataList);
        log.info("Saving order data in line entry table");
        lineEntryDao.saveWithQuery(orderDataList);
        log.info("Saving order data in schedule line entry table");
        scheduleLineEntryDao.saveWithQuery(orderDataList);

    }

}
