package jm.skybet.feedme.demo.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.util.List;


@Data
@Builder
@Jacksonized
public class Event{
    String eventId;
    String category;
    String subCategory;
    String name;
    Long startTime;
    Boolean displayed;
    Boolean suspended;
    List<Market> markets;
}
