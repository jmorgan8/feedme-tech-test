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

    public Event mapEvent(String[] values) {
        return Event.builder()
                .eventId(values[4])
                .category(values[5])
                .subCategory(values[6])
                .name(addPipes(values[7]))
                .startTime(Long.valueOf(values[8]))
                .displayed(Integer.parseInt(values[9]) == 1)
                .suspended(Integer.parseInt(values[10]) == 1)
                .build();
    }

    public void mapEvent(Event event, String[] values) {
        event.setCategory(values[5]);
        event.setSubCategory(values[6]);
        event.setName(addPipes(values[7]));
        event.setStartTime(Long.valueOf(values[8]));
        event.setDisplayed(Integer.parseInt(values[9]) == 1);
        event.setSuspended(Integer.parseInt(values[10]) == 1);
    }

    public Market mapMarket(String[] values) {
        return Market.builder()
                .eventId(values[4])
                .marketId(values[5])
                .name(addPipes(values[6]))
                .displayed(Integer.parseInt(values[7]) == 1)
                .suspended(Integer.parseInt(values[8]) == 1)
                .build();
    }

    public void mapMarket(Market market, String[] values) {
        market.setName(addPipes(values[6]));
        market.setDisplayed(Integer.parseInt(values[7]) == 1);
        market.setSuspended(Integer.parseInt(values[8]) == 1);
    }

    public Outcome mapOutcome(String[] values) {
        return Outcome.builder()
                .marketId(values[4])
                .outcomeId(values[5])
                .name(addPipes(values[6]))
                .price(values[7])
                .displayed(Integer.parseInt(values[8]) == 1)
                .suspended(Integer.parseInt(values[9]) == 1)
                .build();
    }

    public void mapOutcome(Outcome outcome, String[] values) {
        outcome.setName(addPipes(values[6]));
        outcome.setPrice(values[7]);
        outcome.setDisplayed(Integer.parseInt(values[8]) == 1);
        outcome.setSuspended(Integer.parseInt(values[9]) == 1);
    }

    public String addPipes(String line) {
        return line.replace("<pipe>", "|");
    }
}
