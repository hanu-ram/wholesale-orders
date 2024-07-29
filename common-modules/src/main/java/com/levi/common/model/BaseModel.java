package com.levi.common.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

@Data
@MappedSuperclass
public class BaseModel {

    @Column(name = "create_ts")
    private Timestamp createTimestamp;

    @Column(name = "create_user")
    private String createdBy;

    @Column(name = "modify_ts")
    private Timestamp modifiedTimestamp;

    @Column(name = "modify_user")
    private String modifiedBy;

}
