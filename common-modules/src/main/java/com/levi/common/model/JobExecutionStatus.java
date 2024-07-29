package com.levi.common.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
@Table(name = "job_execution_status")
public class JobExecutionStatus {

    @Id
    @Column(name = "job_name", nullable = false)
    private String jobName;

    @Column(name = "last_run")
    private Date lastRun;

    @Column(name = "date_of_run")
    private Date dateOfRun;
}
