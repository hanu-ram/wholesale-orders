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
import static com.levi.common.constant.Constants.WHOLESALE_PAC_PROCESSING_FAILED;

@Service
@Slf4j
public class PacProcessingService {

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

    @Autowired
    private CxKafkaProducer cxKafkaProducer;

    @Value("${kafka.pac.error.topic}")
    private String pacUnconfirmedErrorTopic;

    @Value("${cx.batch.page.offset}")
    private int offset;

    @Value("${cx.batch.page.limit}")
    private int limit;

    public void processPacUnconfirmedQuantity() {
        Long startTime = System.currentTimeMillis();
        log.debug("Processing PAC Unconfirmed Quantities ...");
        try {
            // Getting Last_run timestamp for PAC_JOB
            Timestamp lastRunTimeStamp = jobStatusRepository.getLastRunForCXJob(CxFeedJobs.PAC_JOB,
                    CommonUtils.getCurrentDate());
            // Getting current timestamp and getting the previous day time stamp by subtracting 24H
            LocalDateTime localDateTime = CommonUtils.getUtcDateTime();
            Timestamp previousDayTimeStamp = Timestamp.valueOf(localDateTime.minusHours(Constants.HOURS));

            Timestamp requiredTimestamp = lastRunTimeStamp != null ? lastRunTimeStamp : previousDayTimeStamp;
            Pageable page = PageRequest.of(offset, limit);
            Page<UnconfirmedQuantityDetails> pacUnconfirmedPage;
            while (true) {
                pacUnconfirmedPage = scheduleLineEntryRepository.getPacUnconfirmedSchLineEntry(requiredTimestamp, page);
                if (pacUnconfirmedPage.isEmpty() && pacUnconfirmedPage.isFirst()) {
                    log.error(WHOLESALE_CX_NO_DATA_FOUND + " : Data to be sent to outbound topic not found");
                    break;
                }
                processPacData(pacUnconfirmedPage);
                if (!pacUnconfirmedPage.hasNext()) {
                    break;
                }
                page = pacUnconfirmedPage.nextPageable();
            }
            log.info("Updating lastRun timestamp for PAC JOB : {}", Timestamp.valueOf(localDateTime));
            jobStatusRepository.updateLastRunForCXJob(CxFeedJobs.PAC_JOB,
                    Timestamp.valueOf(localDateTime), CommonUtils.getCurrentDate());
        } catch (Exception e) {
            log.error(WHOLESALE_PAC_PROCESSING_FAILED + " :Exception while processing pac error data", e);
            throw new RuntimeException(e);
        }
        Long endTime = System.currentTimeMillis();
        log.info("Time taken to send records to kafka pac unconfirmed topic : {}", endTime - startTime);
    }

    private void processPacData(Page<UnconfirmedQuantityDetails> pacUnconfirmedPage) {
        if (pacUnconfirmedPage.hasContent()) {
            List<UnconfirmedQuantityDetails> pacUnconfirmedDetails = pacUnconfirmedPage.getContent();
            log.info("Sending records to {} topic with page size : {}", pacUnconfirmedErrorTopic, pacUnconfirmedDetails.size());
            if (!CollectionUtils.isEmpty(pacUnconfirmedDetails)) {
                pacUnconfirmedDetails.forEach(sku -> {
                    try {
                        String key = sku.getLineEntryId() + "-PAC";
                        PacVirUnconfirmedErrorDto pacUnconfirmedErrorDto = getPacUnconfirmedErrorDto(sku);
                        if (pacUnconfirmedErrorDto != null) {
                            String json = getJson(pacUnconfirmedErrorDto);
                            cxKafkaProducer.publishOrderDetailsToKafka(json, key, pacUnconfirmedErrorTopic);
                            log.info("Sent message to pac unconfirmed error topic with key : {}", key);
                        }
                    } catch (JsonProcessingException e) {
                        log.error(WHOLESALE_PAC_PROCESSING_FAILED
                                + " : Unable to create json for the line with id : {}", sku.getLineEntryId());
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        log.error(WHOLESALE_PAC_PROCESSING_FAILED
                                + " : IOException in processPacUnconfirmedQuantity for  : {}", sku.getLineEntryId());
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public PacVirUnconfirmedErrorDto getPacUnconfirmedErrorDto(UnconfirmedQuantityDetails skuDetails) {
        UnconfirmedQuantityDetails lineEntry = lineEntryRepository.getVirPacUnconfirmedLineEntry(skuDetails.getLineEntryId());
        if (lineEntry != null) {
            log.debug("lineEntryId : {}", skuDetails.getLineEntryId());
            PacVirUnconfirmedErrorDto pacUnconfirmedErrorDto = unconfirmedQtyErrorMapper.mapToDto(skuDetails, lineEntry);
            pacUnconfirmedErrorDto.setVirUnconfirmedQuantity(null);
            pacUnconfirmedErrorDto.setOrderValue(lineEntry.getNetPrice() * skuDetails.getPacUnconfirmedQuantity());
            return pacUnconfirmedErrorDto;
        }
        return null;
    }

    public String getJson(PacVirUnconfirmedErrorDto errorDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(errorDto);
    }
}
