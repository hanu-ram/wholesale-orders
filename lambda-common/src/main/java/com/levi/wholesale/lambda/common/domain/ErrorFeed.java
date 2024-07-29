package com.levi.wholesale.lambda.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true, setterPrefix = "with")
public class ErrorFeed implements Serializable {

    private static final long serialVersionUID = 1L;

    private String invSupplyFeedErrorKey;

    private String errorType;

    private String skuId;

    private String nodeId;

    private String rowData;

    private String errorDescription;

    private LocalDateTime createTs;

    private String createUser;

    private LocalDateTime modifyTs;

    private String modifyUser;

}
