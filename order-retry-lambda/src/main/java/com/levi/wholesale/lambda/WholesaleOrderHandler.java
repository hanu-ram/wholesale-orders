package com.levi.wholesale.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KafkaEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.wholesale.consumer.OrderConsumer;
import com.levi.wholesale.lambda.common.config.ConnectionManager;
import com.levi.wholesale.lambda.common.domain.dao.ErrorDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.LineEntryDao;
import com.levi.wholesale.lambda.common.domain.dao.OrderDetailsDao;
import com.levi.wholesale.lambda.common.domain.dao.ScheduleLineEntryDao;
import com.levi.wholesale.lambda.common.exception.DatabaseConnectionException;
import com.levi.wholesale.lambda.common.exception.OrderProcessingException;
import com.levi.wholesale.service.ErrorService;
import com.levi.wholesale.service.OrderDetailsRetryService;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.util.Base64;

@Slf4j
public class WholesaleOrderHandler implements RequestHandler<KafkaEvent, String> {

    private final OrderConsumer orderConsumer;
    private final OrderDetailsRetryService orderDetailsRetryService;

    private final OrderDetailsDao orderDetailsDao = new OrderDetailsDao();
    private final LineEntryDao lineEntryDao = new LineEntryDao();
    private final ScheduleLineEntryDao scheduleLineEntryDao = new ScheduleLineEntryDao();
    private final ErrorDetailsDao  errorDetailsDao = new ErrorDetailsDao();
    private final ErrorService  errorService = new ErrorService();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public WholesaleOrderHandler() {
        this.orderConsumer = new OrderConsumer();
        this.orderDetailsRetryService = new OrderDetailsRetryService(orderDetailsDao, lineEntryDao,
                scheduleLineEntryDao, errorDetailsDao, objectMapper, errorService);
    }

    @Override
    public String handleRequest(KafkaEvent event, Context context) {
        log.info("Kafka event received.");

        try (Connection connection = ConnectionManager.getConnection()) {
            ObjectMapper mapper = new ObjectMapper();
            saveOrderData(event, connection, mapper);
        } catch (DatabaseConnectionException ex) {
            log.error("Error when processing order data with requestId {} and exception ", context.getAwsRequestId(), ex);
            return "Event Failed : " + context.getAwsRequestId();
        } catch (Exception ex) {
            log.error("Error when saving order data with requestId {} and exception ", context.getAwsRequestId(), ex);
            return "Event Failed : " + context.getAwsRequestId();
        }
        return "Event Success: " + context.getAwsRequestId();
    }

    private void saveOrderData(KafkaEvent event, Connection connection, ObjectMapper mapper) {

        event.getRecords().forEach((key, values) -> values.forEach(rec -> {
            String decodedValue = getDecodedValue(rec);
            try {
                orderDetailsRetryService.saveOrderDetails(decodedValue, connection, 0);
                log.info("Order data inserted successfully for the key : {} ", key);
            } catch (OrderProcessingException e) {
                log.error("Error when processing the order data : {} ", decodedValue);
                throw new OrderProcessingException("Error when processing the order data : ", e);
            }
        }));
    }

    private static String getDecodedValue(KafkaEvent.KafkaEventRecord rec) {
        byte[] decodedValueBytes = Base64.getDecoder().decode(rec.getValue());
        return new String(decodedValueBytes);
    }
}
