package jm.skybet.feedme.demo.model;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Data
@Builder
public class Market{
    String marketId;
    String eventId;
    String name;
    Boolean displayed;
    Boolean suspended;
    List<Outcome> outcomes;
}
