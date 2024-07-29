package com.levi.common.repository;

import com.levi.common.model.JobExecutionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



import java.sql.Timestamp;
import java.util.Date;

@Repository
public interface JobStatusRepository extends JpaRepository<JobExecutionStatus, String> {
    @Query("select lastRun FROM JobExecutionStatus where jobName = :jobName and dateOfRun = :dateOfRun")
    Timestamp getLastRunForCXJob(@Param("jobName") String jobName, @Param("dateOfRun") Date dateOfRun);


    @Modifying
    @Query("update JobExecutionStatus set lastRun = :lastRun, dateOfRun = :dateOfRun where jobName = :jobName")
    void updateLastRunForCXJob(@Param("jobName") String jobName,
                               @Param("lastRun") Timestamp lastRun,
                               @Param("dateOfRun") Date dateOfRun);
}
