package jm.skybet.feedme.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.skybet.feedme.demo.mapping.FixtureMapper;
import jm.skybet.feedme.demo.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedMeService {
    private final FeedMeServiceProperties feedMeServiceProperties;
    private final FixtureMapper fixtureMapper;
    public void processFeed() {
        try (Socket socket = new Socket(feedMeServiceProperties.getHost(), feedMeServiceProperties.getPort())) {
            InputStream input = socket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            readFixtures(reader);
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
        }
    }

    public String[] cleanupFixture(String line) {
        // Remove first pipe
        line = line.substring(1);
        // Replace escaped pipes with text that can replaced later (see addPipes)
        line = line.replace("\\|", "<pipe>");
        // Split fields in the line
        return line.split("\\|");
    }

    public void readFixtures(BufferedReader reader) throws IOException {
        Event event = null;
        Market market = null;
        Outcome outcome;
        List<Market> markets = new ArrayList<>();
        List<Outcome> outcomes = new ArrayList<>();
        String currentEventId = null;
        String currentMarketId = null;
        String fixture;    // reads a line of text
        // int count = 0; Use for local testing

        ObjectMapper mapper = new ObjectMapper();
        String json;

        while (((fixture = reader.readLine()) != null)) {
            String[] values = cleanupFixture(fixture);
            // count++;

            Header header = fixtureMapper.mapHeader(values);

            if (header.getOperation().equals(Operation.create)) {
                if (header.getType().equals(Type.event)) {
                    if (currentEventId != null) {
                        // If we're here, it's a new event, refresh necessary variables
                        json = mapper.writeValueAsString(event);
                        System.out.println(json);

                        // Initialise lists for new market and outcomes
                        markets = new ArrayList<>();
                        outcomes = new ArrayList<>();
                    }
                    currentEventId = values[4];
                    event = fixtureMapper.mapEvent(values);
                }

                if (header.getType().equals(Type.market)) {
                    // If not null, we have a new market so add the previous to list
                    if (currentMarketId != null) {
                        // Initialise outcomes for new market
                        outcomes = new ArrayList<>();
                    }
                    currentMarketId = values[5];
                    market = fixtureMapper.mapMarket(values);

                    if (event != null) {
                        markets.add(market);
                        event.setMarkets(markets);
                    }
                }

                if (header.getType().equals(Type.outcome)) {
                    outcome = fixtureMapper.mapOutcome(values);

                    if (market != null) {
                        outcomes.add(outcome);
                        market.setOutcomes(outcomes);
                    }
                }
            }
//                  else if (header.getOperation().equals(OPERATION_UPDATE)) {
//                    // do nothing for now until we get records from DB
//                    continue;
//                }
        }
    }
}
