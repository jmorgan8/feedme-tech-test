package jm.skybet.feedme.demo.service;

import jm.skybet.feedme.demo.mapping.FixtureMapper;
import jm.skybet.feedme.demo.model.Header;
import jm.skybet.feedme.demo.model.Operation;
import jm.skybet.feedme.demo.model.Type;
import jm.skybet.feedme.demo.repository.FixtureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedMeServiceTest {
    private final static String EVENT_LINE = "|1|create|event|1653343941179|de4a2dec-61cf-4b4f-964f-78167a225b3e|Football|Sky Bet League One|\\|Doncaster\\| vs \\|Bristol Rovers\\||1653343842527|0|1|";
    private final static String MARKET_LINE = "|2|create|market|1653343941179|de4a2dec-61cf-4b4f-964f-78167a225b3e|1744eb2f-02ad-42b8-b93e-a12bafa8a173|Full Time Result|0|1|";
    private final static String OUTCOME_LINE1 = "|3|create|outcome|1653343941179|1744eb2f-02ad-42b8-b93e-a12bafa8a173|f03c59a0-c49f-40fe-a660-f34b0d0e2e81|\\|Doncaster\\||6/4|0|1|";
    private final static String OUTCOME_LINE2 = "|4|create|outcome|1653343941179|1744eb2f-02ad-42b8-b93e-a12bafa8a173|583f9788-cc63-4ad3-b07a-9f604b1668ca|Draw|9/1|0|1|";
    private final static String OUTCOME_LINE3 = "|5|create|outcome|1653343941179|1744eb2f-02ad-42b8-b93e-a12bafa8a173|df908b80-1d95-4be2-a443-afa6257b34d1|\\|Bristol Rovers\\||16/1|0|1|";

    private FeedMeService feedMeService;

    @Mock
    private BufferedReader bufferedReader;

    @Mock
    private FeedMeServiceProperties feedMeServiceProperties;

    @Mock
    private FixtureMapper fixtureMapper;

    @Mock
    private FixtureRepository fixtureRepository;

    @BeforeEach
    void setUp() {
        feedMeService = new FeedMeService(feedMeServiceProperties, fixtureMapper, fixtureRepository);
    }

    @Test
    public void shouldMapFixturesForDifferentTypes() throws Exception {
        when(bufferedReader.readLine()).thenReturn(EVENT_LINE, MARKET_LINE, OUTCOME_LINE1, OUTCOME_LINE2, OUTCOME_LINE3, null);

        when(fixtureMapper.mapHeader(buildEventValues())).thenReturn(expectedHeader(1L, Type.event));
        when(fixtureMapper.mapHeader(buildMarketValues())).thenReturn(expectedHeader(2L, Type.market));
        when(fixtureMapper.mapHeader(buildOutcomeValues("3","f03c59a0-c49f-40fe-a660-f34b0d0e2e81", "<pipe>Doncaster<pipe>","6/4"))).thenReturn(expectedHeader(3L, Type.outcome));
        when(fixtureMapper.mapHeader(buildOutcomeValues("4","583f9788-cc63-4ad3-b07a-9f604b1668ca", "Draw","9/1"))).thenReturn(expectedHeader(4L, Type.outcome));
        when(fixtureMapper.mapHeader(buildOutcomeValues("5","df908b80-1d95-4be2-a443-afa6257b34d1", "<pipe>Bristol Rovers<pipe>","16/1"))).thenReturn(expectedHeader(5L, Type.outcome));


        feedMeService.readFixtures(bufferedReader);
        verify(fixtureMapper, times(5)).mapHeader(any());
        verify(fixtureMapper, times(1)).mapEvent(any());
        verify(fixtureMapper, times(1)).mapMarket(any(), any());
        verify(fixtureMapper, times(3)).mapOutcome(any(), any());
    }

    @Test
    public void shouldThrowUnknownHostExceptionWhenProcessFeed() {
        when(feedMeServiceProperties.getHost()).thenReturn("NOT A HOST");

        final UnknownHostException exception = assertThrows(UnknownHostException.class,
                () -> feedMeService.processFeed());

        assertThat(exception.getMessage(), is("Server not found: NOT A HOST"));

        verifyNoInteractions(fixtureMapper);
    }

    @Test
    public void shouldThrowIOExceptionWhenReadFixtures() throws IOException {
        doThrow(new IOException("I/O error: No lines to read"))
                .when(bufferedReader)
                .readLine();

        final IOException exception = assertThrows(IOException.class,
                () -> feedMeService.readFixtures(bufferedReader));

        assertThat(exception.getMessage(), is("I/O error: No lines to read"));

        verifyNoInteractions(fixtureMapper);
    }

    private static String[] buildEventValues() {
        return new String[]{"1", "create", "event", "1653343941179", "de4a2dec-61cf-4b4f-964f-78167a225b3e", "Football", "Sky Bet League One", "<pipe>Doncaster<pipe> vs <pipe>Bristol Rovers<pipe>", "1653343842527", "0", "1"};
    }

    private static String[] buildMarketValues() {
        return new String[]{ "2", "create", "market", "1653343941179", "de4a2dec-61cf-4b4f-964f-78167a225b3e", "1744eb2f-02ad-42b8-b93e-a12bafa8a173", "Full Time Result", "0", "1"};
    }

    private static String[] buildOutcomeValues(String msgId, String outcomeId, String name, String price) {
        return new String[]{ msgId, "create", "outcome", "1653343941179", "1744eb2f-02ad-42b8-b93e-a12bafa8a173", outcomeId, name, price, "0", "1"};
    }

    private static Header expectedHeader(Long msgId, Type type) {
        return Header.builder()
                .msgId(msgId)
                .operation(Operation.create)
                .type(type)
                .timestamp(1653341739821L)
                .build();
    }
}
