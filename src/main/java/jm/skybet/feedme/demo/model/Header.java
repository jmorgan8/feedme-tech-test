package jm.skybet.feedme.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
public class Header {
    private static final String OPERATION_CREATE = "create";
    private static final String OPERATION_UPDATE = "update";

    private static final String TYPE_EVENT = "event";
    private static final String TYPE_MARKET = "market";
    private static final String TYPE_OUTCOME = "outcome";

    Long msgId;
    Operation operation;
    Type type;
    Long timestamp;
}
