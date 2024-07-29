package com.levi.wholesale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.common.constant.Constants;
import com.levi.common.constant.CxFeedJobs;
import com.levi.common.model.PricingErrorMessage;
import com.levi.common.repository.JobStatusRepository;
import com.levi.common.repository.LineEntryRepository;
import com.levi.common.utils.CommonUtils;
import com.levi.wholesale.producer.CxKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static com.levi.common.constant.Constants.WHOLESALE_CX_NO_DATA_FOUND;
import static com.levi.common.constant.Constants.WHOLESALE_PE_PROCESSING_FAILED;

@Service
@Slf4j
public class PricingProcessingService {

    @Autowired
    private LineEntryRepository lineEntryRepository;

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper objectMapper;

    @Autowired
    private CxKafkaProducer cxKafkaProducer;

    @Value("${kafka.pricing.error.topic}")
    private String pricingErrorTopic;

    @Value("${cx.batch.page.offset}")
    private int offset;

    @Value("${cx.batch.page.limit}")
    private int limit;

    public void processPricingErrorData() {
        Long startTime = System.currentTimeMillis();
        log.debug("Processing pricing error data...");
        try {
            // Getting Last_run timestamp for PRICING_JOB
            Timestamp lastRunTimeStamp = jobStatusRepository.getLastRunForCXJob(CxFeedJobs.PRICING_JOB,
                    CommonUtils.getCurrentDate());
            // Getting current timestamp and getting the previous day time stamp by subtracting 24H
            LocalDateTime localDateTime = CommonUtils.getUtcDateTime();
            Timestamp previousDayTimeStamp = Timestamp.valueOf(localDateTime.minusHours(Constants.HOURS));
            Timestamp requiredTimestamp = lastRunTimeStamp != null ? lastRunTimeStamp : previousDayTimeStamp;
            Pageable page = PageRequest.of(offset, limit);
            Page<PricingErrorMessage> pricingErrorsPage;
            while (true) {
                pricingErrorsPage = lineEntryRepository.getFilteredPricingErrorData(requiredTimestamp, page);
                if (pricingErrorsPage.isEmpty() && pricingErrorsPage.isFirst()) {
                    log.error(WHOLESALE_CX_NO_DATA_FOUND + " : Data to be sent to outbound topic not found");
                    break;
                }
                processPricingData(pricingErrorsPage);
                if (!pricingErrorsPage.hasNext()) {
                    break;
                }
                page = pricingErrorsPage.nextPageable();
            }
            log.info("Updating lastRun timestamp for PRICING JOB : {}", Timestamp.valueOf(localDateTime));
            jobStatusRepository.updateLastRunForCXJob(CxFeedJobs.PRICING_JOB,
                    Timestamp.valueOf(localDateTime), CommonUtils.getCurrentDate());
        } catch (Exception e) {
            log.error(WHOLESALE_PE_PROCESSING_FAILED + " :Exception while processing pricing error data", e);
            throw new RuntimeException(e);
        }
        Long endTime = System.currentTimeMillis();
        log.info("Time taken to send records to kafka pricing error topic : {}", endTime - startTime);
    }

    private void processPricingData(Page<PricingErrorMessage> pricingErrorsSlice) {
        if (pricingErrorsSlice.hasContent()) {
            List<PricingErrorMessage> pricingErrors = pricingErrorsSlice.getContent();
            log.info("Sending records to {} topic with page size : {}", pricingErrorTopic, pricingErrors.size());
            if (!CollectionUtils.isEmpty(pricingErrors)) {
                pricingErrors.forEach(priceError -> {
                    try {
                        String key = priceError.getSalesDocumentNumber();
                        String json = objectMapper.writeValueAsString(priceError);
                        log.info("Sending message to pricing error topic with key : {}", key);
                        cxKafkaProducer.publishOrderDetailsToKafka(json, key, pricingErrorTopic);
                    } catch (JsonProcessingException e) {
                        log.error(WHOLESALE_PE_PROCESSING_FAILED
                                + " : Unable to create json for the order with id : {}", priceError.getSalesDocumentNumber());
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        log.error(WHOLESALE_PE_PROCESSING_FAILED
                                + " : IOException in processPricingErrorData for : {}", priceError.getSalesDocumentNumber());
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}
