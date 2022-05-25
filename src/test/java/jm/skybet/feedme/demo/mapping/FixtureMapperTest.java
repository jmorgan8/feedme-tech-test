package jm.skybet.feedme.demo.mapping;

import jm.skybet.feedme.demo.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;


class FixtureMapperTest {
    private FixtureMapper mapper;

    @BeforeEach
    void beforeEach() { mapper = new FixtureMapper(); }

    @ParameterizedTest
    @MethodSource("fixtureArguments")
    void shouldMapHeader(final String[] fixture, final Header expected) {
        Header header = mapper.mapHeader(fixture);

        assertThat(header, samePropertyValuesAs(expected));
    }

    @Test
    void shouldMapEvent() {
        final String[] values = buildEventValues();
        Fixture event = mapper.mapEvent(values);

        assertThat(event, samePropertyValuesAs(buildFixtureEventInfoOnly()));
    }

    @Test
    void shouldMapMarket() {
        Fixture fixture = buildFixtureEventInfoOnly();
        final String[] values = buildMarketValues();
        mapper.mapMarket(fixture, values);

        assertThat(fixture, samePropertyValuesAs(buildFixtureEventAndMarketInfo()));
    }

    @Test
    void shouldMapOutcome() {
        Fixture fixture = buildFixtureEventAndMarketInfo();
        final String[] values = buildOutcomeValues();
        mapper.mapOutcome(fixture, values);

        assertThat(fixture, samePropertyValuesAs(buildFixtureAllInfo()));
    }

    private static Stream<Arguments> fixtureArguments(){
        return Stream.of(
                Arguments.of(buildEventValues(), expectedHeader(1L, Type.event)),
                Arguments.of(buildMarketValues(), expectedHeader(2L, Type.market)),
                Arguments.of(buildOutcomeValues(), expectedHeader(3L, Type.outcome))
        );
    }

    private static String[] buildEventValues() {
        return new String[]{"1", "create", "event", "1653341739821", "df268eab-a4f0-4749-bf78-1fa9446d403e", "Football", "Sky Bet League One", "<pipe>MK Dons<pipe> vs <pipe>Oxford<pipe>", "1653341165688", "0", "1"};
    }

    private static String[] buildMarketValues() {
        return new String[]{ "2", "create", "market", "1653341739821", "df268eab-a4f0-4749-bf78-1fa9446d403e", "03b3e2c4-5470-4792-a1b8-1d35f19e8017", "Full Time Result", "0", "1"};
    }

    private static String[] buildOutcomeValues() {
        return new String[]{ "3", "create", "outcome", "1653341739821", "03b3e2c4-5470-4792-a1b8-1d35f19e8017", "f2a092e1-46b5-474b-8907-6e9b4998750a", "<pipe>MK Dons<pipe>", "1/6", "0", "1"};
    }

    private static Header expectedHeader(Long msgId, Type type) {
        return Header.builder()
                .msgId(msgId)
                .operation(Operation.create)
                .type(type)
                .timestamp(1653341739821L)
                .build();
    }

    private Fixture buildFixtureEventInfoOnly() {
        return Fixture.builder()
                .eventId("df268eab-a4f0-4749-bf78-1fa9446d403e")
                .category("Football")
                .subCategory("Sky Bet League One")
                .eventName("|MK Dons| vs |Oxford|")
                .startTime(1653341165688L)
                .eventDisplayed(false)
                .eventSuspended(true)
                .build();
    }

    private Fixture buildFixtureEventAndMarketInfo() {
        return Fixture.builder()
                .eventId("df268eab-a4f0-4749-bf78-1fa9446d403e")
                .category("Football")
                .subCategory("Sky Bet League One")
                .eventName("|MK Dons| vs |Oxford|")
                .startTime(1653341165688L)
                .eventDisplayed(false)
                .eventSuspended(true)
                .marketId("03b3e2c4-5470-4792-a1b8-1d35f19e8017")
                .marketName("Full Time Result")
                .marketDisplayed(false)
                .marketSuspended(true)
                .build();
    }

    private Fixture buildFixtureAllInfo() {
        return Fixture.builder()
                .eventId("df268eab-a4f0-4749-bf78-1fa9446d403e")
                .category("Football")
                .subCategory("Sky Bet League One")
                .eventName("|MK Dons| vs |Oxford|")
                .startTime(1653341165688L)
                .eventDisplayed(false)
                .eventSuspended(true)
                .marketId("03b3e2c4-5470-4792-a1b8-1d35f19e8017")
                .marketName("Full Time Result")
                .marketDisplayed(false)
                .marketSuspended(true)
                .outcomeId("f2a092e1-46b5-474b-8907-6e9b4998750a")
                .outcomeName("|MK Dons|")
                .price("1/6")
                .outcomeDisplayed(false)
                .outcomeSuspended(true)
                .build();
    }

}
