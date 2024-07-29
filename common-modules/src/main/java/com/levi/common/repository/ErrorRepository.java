package com.levi.common.repository;

import com.levi.common.model.ErrorDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ErrorRepository extends JpaRepository<ErrorDetails, String> {

    @Query("select details from ErrorDetails details where salesDocumentNumber = :salesDocumentNumber and isProcessed = :isProcessed")
    List<ErrorDetails> findErrorDetailsBySalesDocument(@Param("salesDocumentNumber") String salesDocumentNumber,
                                  @Param("isProcessed") boolean isProcessed);

}
