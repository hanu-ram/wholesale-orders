package com.levi.wholesale.lambda.common.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorEvent {

    private static final String NULL = "<null>";

    @JsonProperty("event_id")
    private String eventId;
    @JsonProperty("event_name")
    private String eventName;
    @JsonProperty("error_message")
    private String errorMessage;
    @JsonProperty("event_module")
    private String eventModule;
    @JsonProperty("payload")
    private String payload;

    public ErrorEvent withEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public ErrorEvent withEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public ErrorEvent withErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public ErrorEvent withEventModule(String eventModule) {
        this.eventModule = eventModule;
        return this;
    }

    public ErrorEvent withPayload(String payload) {
        this.payload = payload;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ErrorEvent.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("eventId");
        sb.append('=');
        sb.append(((this.eventId == null) ? NULL : this.eventId));
        sb.append(',');
        sb.append("eventName");
        sb.append('=');
        sb.append(((this.eventName == null) ? NULL : this.eventName));
        sb.append(',');
        sb.append("errorMessage");
        sb.append('=');
        sb.append(((this.errorMessage == null) ? NULL : this.errorMessage));
        sb.append(',');
        sb.append("eventModule");
        sb.append('=');
        sb.append(((this.eventModule == null) ? NULL : this.eventModule));
        sb.append(',');
        sb.append("payload");
        sb.append('=');
        sb.append(((this.payload == null) ? NULL : this.payload));
        sb.append(',');
        if (sb.charAt((sb.length() - 1)) == ',') {
            sb.setCharAt((sb.length() - 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }
}
