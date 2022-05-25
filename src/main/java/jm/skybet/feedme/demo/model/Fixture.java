package jm.skybet.feedme.demo.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Builder
@Jacksonized
@Document(collection = "fixture")
public class Fixture {
    @Id
    String id;
    String eventId;
    String category;
    String subCategory;
    String eventName;
    Long startTime;
    Boolean eventDisplayed;
    Boolean eventSuspended;
    String marketId;
    String marketName;
    Boolean marketDisplayed;
    Boolean marketSuspended;
    String outcomeId;
    String outcomeName;
    String price;
    Boolean outcomeDisplayed;
    Boolean outcomeSuspended;
}
