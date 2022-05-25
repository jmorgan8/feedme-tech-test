package jm.skybet.feedme.demo.mapping;

import jm.skybet.feedme.demo.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FixtureMapper {

    public Header mapHeader(final String[] values) {
        return Header.builder()
                .msgId(Long.valueOf(values[0]))
                .operation(Operation.valueOf(values[1]))
                .type(Type.valueOf(values[2]))
                .timestamp(Long.valueOf(values[3]))
                .build();
    }

    public Fixture mapEvent(String[] values) {
        return Fixture.builder()
                .eventId(values[4])
                .category(values[5])
                .subCategory(values[6])
                .eventName(addPipes(values[7]))
                .startTime(Long.valueOf(values[8]))
                .eventDisplayed(Integer.parseInt(values[9]) == 1)
                .eventSuspended(Integer.parseInt(values[10]) == 1)
                .build();
    }

    public void mapEvent(Fixture fixture, String[] values) {
        fixture.setCategory(values[5]);
        fixture.setSubCategory(values[6]);
        fixture.setEventName(addPipes(values[7]));
        fixture.setStartTime(Long.valueOf(values[8]));
        fixture.setEventDisplayed(Integer.parseInt(values[9]) == 1);
        fixture.setEventSuspended(Integer.parseInt(values[10]) == 1);
    }

    public void mapMarket(Fixture fixture, String[] values) {
        fixture.setMarketId(addPipes(values[5]));
        fixture.setMarketName(addPipes(values[6]));
        fixture.setMarketDisplayed(Integer.parseInt(values[7]) == 1);
        fixture.setMarketSuspended(Integer.parseInt(values[8]) == 1);
    }

    public void mapOutcome(Fixture fixture, String[] values) {
        fixture.setOutcomeId(values[5]);
        fixture.setOutcomeName(addPipes(values[6]));
        fixture.setPrice(values[7]);
        fixture.setOutcomeDisplayed(Integer.parseInt(values[8]) == 1);
        fixture.setOutcomeSuspended(Integer.parseInt(values[9]) == 1);
    }

    public String addPipes(String line) {
        return line.replace("<pipe>", "|");
    }
}
