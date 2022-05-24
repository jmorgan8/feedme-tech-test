package jm.skybet.feedme.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
public class Outcome{
    String outcomeId;
    String marketId;
    String name;
    String price;
    Boolean displayed;
    Boolean suspended;
}
