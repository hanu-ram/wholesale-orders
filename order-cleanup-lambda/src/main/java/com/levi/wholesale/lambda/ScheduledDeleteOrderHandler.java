package com.levi.wholesale.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import com.levi.wholesale.lambda.common.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.dao.ScheduleLineEntryDao;
import com.levi.wholesale.service.ScheduledDeleteOrderService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ScheduledDeleteOrderHandler implements RequestHandler<ScheduledEvent, String> {

    @SneakyThrows
    @Override
    public String handleRequest(ScheduledEvent input, Context context) {
        final ScheduledDeleteOrderService deleteOrderService = new ScheduledDeleteOrderService(
                new OrderDetailsDao(), new LineEntryDao(), new ScheduleLineEntryDao());
        return deleteOrderService.deleteData();
    }
}
