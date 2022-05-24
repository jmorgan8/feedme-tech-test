package jm.skybet.feedme.demo.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Data
@Builder
@Jacksonized
@Document(collection = "fixture")
public class Event{
    @Id
    String eventId;
    String category;
    String subCategory;
    String name;
    Long startTime;
    Boolean displayed;
    Boolean suspended;
    List<Market> markets;
}
