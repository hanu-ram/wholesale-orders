package com.levi.order.service;

import com.levi.common.dao.ErrorDetailsDao;
import com.levi.common.mapper.ErrorDetailsMapper;
import com.levi.common.model.ErrorDetails;
import com.levi.common.repository.ErrorRepository;
import com.levi.wholesale.common.dto.OrderData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ErrorDetailsService {

    private final ErrorDetailsMapper errorDetailsMapper;

    private final ErrorRepository errorRepository;

    private final ErrorDetailsDao errorDetailsDao;

    public ErrorDetailsService(ErrorDetailsMapper errorDetailsMapper, ErrorRepository errorRepository, ErrorDetailsDao errorDetailsDao) {
        this.errorDetailsMapper = errorDetailsMapper;
        this.errorRepository = errorRepository;
        this.errorDetailsDao = errorDetailsDao;
    }

    @Transactional
    public void saveErrorDetails(OrderData orderData, String invalidFields) {
        String message = "Invalid fields : " + invalidFields;
        String id = errorDetailsDao.saveWithQuery(orderData, message, false);
        log.debug("Error details saved with id : {}", id);
    }

    @Transactional
    public void updateErrorDetails(OrderData orderData, ErrorDetails errorDetails) {
        ErrorDetails errorDetailsFromDb = errorDetailsMapper.mapToModel(orderData, errorDetails);
        String id = errorDetailsDao.saveWithQuery(orderData, errorDetailsFromDb.getErrorMessage(), errorDetailsFromDb.getIsProcessed());
        log.info("Error details updated having id : {} ", id);
    }

    public ErrorDetails getErrorDetails(String errorId) {
        Optional<ErrorDetails> errorDetailsOptional = errorRepository.findById(errorId);
        return errorDetailsOptional.orElse(null);
    }

    public List<ErrorDetails> getErrorDetails(String salesDocumentNumber, boolean isProcessed) {
        return errorRepository.findErrorDetailsBySalesDocument(salesDocumentNumber, isProcessed);
    }
}
