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

    public Market mapMarket(String[] values) {
        return Market.builder()
                .eventId(values[4])
                .marketId(values[5])
                .name(addPipes(values[6]))
                .displayed(Integer.parseInt(values[7]) == 1)
                .suspended(Integer.parseInt(values[8]) == 1)
                .build();
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

    public String addPipes(String line) {
        return line.replace("<pipe>", "|");
    }
}
