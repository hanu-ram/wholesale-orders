package com.levi.wholesale.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.levi.common.constant.Constants;
import com.levi.common.constant.CxFeedJobs;
import com.levi.common.mapper.UnconfirmedQtyErrorMapper;
import com.levi.common.model.UnconfirmedQuantityDetails;
import com.levi.common.repository.JobStatusRepository;
import com.levi.common.repository.LineEntryRepository;
import com.levi.common.repository.ScheduleLineEntryRepository;
import com.levi.common.utils.CommonUtils;
import com.levi.wholesale.common.dto.PacVirUnconfirmedErrorDto;
import com.levi.wholesale.producer.CxKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import static com.levi.common.constant.Constants.WHOLESALE_VIR_PROCESSING_FAILED;

@Service
@Slf4j
public class VirProcessingService {

    @Autowired
    private ScheduleLineEntryRepository scheduleLineEntryRepository;

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Autowired
    private LineEntryRepository lineEntryRepository;

    @Autowired
    private UnconfirmedQtyErrorMapper unconfirmedQtyErrorMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${kafka.vir.error.topic}")
    private String virUnconfirmedErrorTopic;

    @Autowired
    private CxKafkaProducer cxKafkaProducer;

    @Value("${cx.batch.page.offset}")
    private int offset;

    @Value("${cx.batch.page.limit}")
    private int limit;

    public void processVirUnconfirmedQuantity() {
        Long startTime = System.currentTimeMillis();
        log.info("Processing VIR Unconfirmed Quantities ...");
        try {
            // Getting Last_run timestamp for VIR_JOB
            Timestamp lastRunTimeStamp = jobStatusRepository.getLastRunForCXJob(CxFeedJobs.VIR_JOB,
                    CommonUtils.getCurrentDate());
            // Getting current timestamp and getting the previous day time stamp by subtracting 24H
            LocalDateTime localDateTime = CommonUtils.getUtcDateTime();
            Timestamp previousDayTimeStamp = Timestamp.valueOf(localDateTime.minusHours(Constants.HOURS));

            Timestamp requiredTimestamp = lastRunTimeStamp != null ? lastRunTimeStamp : previousDayTimeStamp;
            Pageable page = PageRequest.of(offset, limit);
            Page<UnconfirmedQuantityDetails> virUnconfirmedPage;
            while (true) {
                virUnconfirmedPage = scheduleLineEntryRepository.getVirUnconfirmedSchLineEntry(requiredTimestamp, page);
                if (virUnconfirmedPage.isEmpty() && virUnconfirmedPage.isFirst()) {
                    log.error(WHOLESALE_CX_NO_DATA_FOUND + " : Data to be sent to outbound topic not found");
                    break;
                }
                processVirData(virUnconfirmedPage);
                if (!virUnconfirmedPage.hasNext()) {
                    break;
                }
                page = virUnconfirmedPage.nextPageable();
            }
            log.info("Updating lastRun timestamp for VIR JOB : {}", Timestamp.valueOf(localDateTime));
            jobStatusRepository.updateLastRunForCXJob(CxFeedJobs.VIR_JOB,
                    Timestamp.valueOf(localDateTime), CommonUtils.getCurrentDate());
        } catch (Exception e) {
            log.error(WHOLESALE_VIR_PROCESSING_FAILED + " : Exception while processing vir error data", e);
            throw new RuntimeException(e);
        }
        Long endTime = System.currentTimeMillis();
        log.info("Time taken to send records to kafka vir unconfirmed topic : {}", endTime - startTime);
    }

    private void processVirData(Page<UnconfirmedQuantityDetails> virUnconfirmedPage) {
        if (virUnconfirmedPage.hasContent()) {
            List<UnconfirmedQuantityDetails> virUnconfirmedDetails = virUnconfirmedPage.getContent();
            log.info("Sending records to {} topic with page size : {}", virUnconfirmedErrorTopic, virUnconfirmedDetails.size());
            if (!CollectionUtils.isEmpty(virUnconfirmedDetails)) {
                virUnconfirmedDetails.forEach(sku -> {
                    try {
                        String key = sku.getLineEntryId() + "-VIR";
                        PacVirUnconfirmedErrorDto virUnconfirmedErrorDto = getVirUnconfirmedErrorDto(sku);
                        if (virUnconfirmedErrorDto != null) {
                            String json = getJson(virUnconfirmedErrorDto);
                            log.info("Sending message to vir unconfirmed topic with key : {}", key);
                            cxKafkaProducer.publishOrderDetailsToKafka(json, key, virUnconfirmedErrorTopic);
                        }
                    } catch (JsonProcessingException e) {
                        log.error(WHOLESALE_VIR_PROCESSING_FAILED + " : Unable to create json for the line with id : {}", sku.getLineEntryId());
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        log.error(WHOLESALE_VIR_PROCESSING_FAILED + " : IOException in processVirUnconfirmedQuantity for : {}", sku.getLineEntryId());
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public PacVirUnconfirmedErrorDto getVirUnconfirmedErrorDto(UnconfirmedQuantityDetails skuDetails) {
        UnconfirmedQuantityDetails lineEntry = lineEntryRepository.getVirPacUnconfirmedLineEntry(skuDetails.getLineEntryId());
        if (lineEntry != null) {
            log.debug("lineEntryId : {}", skuDetails.getLineEntryId());
            PacVirUnconfirmedErrorDto virUnconfirmedErrorDto = unconfirmedQtyErrorMapper.mapToDto(skuDetails, lineEntry);
            virUnconfirmedErrorDto.setPacUnconfirmedQuantity(null);
            virUnconfirmedErrorDto.setOrderValue(lineEntry.getNetPrice() * skuDetails.getVirUnconfirmedQuantity());
            return virUnconfirmedErrorDto;
        }
        return null;
    }

    public String getJson(PacVirUnconfirmedErrorDto errorDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(errorDto);
    }
}
