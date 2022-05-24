package jm.skybet.feedme.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.result.UpdateResult;
import jm.skybet.feedme.demo.mapping.FixtureMapper;
import jm.skybet.feedme.demo.model.*;
import jm.skybet.feedme.demo.repository.FixtureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedMeService {
    private final FeedMeServiceProperties feedMeServiceProperties;
    private final FixtureMapper fixtureMapper;
    private final FixtureRepository fixtureRepository;

    private final MongoTemplate mongoTemplate;

    public void processFeed() throws IOException {
        try (Socket socket = new Socket(feedMeServiceProperties.getHost(), feedMeServiceProperties.getPort())) {
            InputStream input = socket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            readFixtures(reader);
        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
            throw new UnknownHostException("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error: " + ex.getMessage());
            throw new IOException("I/O error: " + ex.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    public void readFixtures(BufferedReader reader) throws Exception {
        Event event = null;
        Market market = null;
        Outcome outcome;
        List<Market> markets = new ArrayList<>();
        List<Outcome> outcomes = new ArrayList<>();
        String currentMarketId = null;
        String fixture;    // reads a line of text
        int count = 0; //Use for local testing
        boolean outcomeUpdatesOnly = true;
        int originalOutcomeCount = 0;

        ObjectMapper mapper = new ObjectMapper();
        String json;

        while (((fixture = reader.readLine()) != null) && count < 300) {
            System.out.println(fixture);
            String[] values = cleanupFixture(fixture);
            count++;

            Update update = new Update();
            update.set("displayed", values[7]);

//            if (count < 3000) {
//                continue;
//            }

            Header header = fixtureMapper.mapHeader(values);
            boolean updateMode = header.getOperation().equals(Operation.update);

            // Basic rules:
            // 1. When create, all event, market and outcomes come sequentially
            // 2. When update:
            // 2.1  If event comes through, all markets and outcomes follow (usually to reverse
            //      the displayed and suspended flags
            // 2.2  Outcomes can be updated independently, so this needs to be handled when updating
            //      an event


                if (header.getType().equals(Type.event)) {
                    outcomeUpdatesOnly = false;
                    currentMarketId = null;
                    outcomes = new ArrayList<>();
                    //if (currentEventId != null) {
                        // If we're here, it's a new event, refresh necessary variables


                        if (event != null) {
                            json = mapper.writeValueAsString(event);
                            //System.out.println(json);
                            System.out.println("Saving event: " + json);
                            fixtureRepository.save(event);

                            // Initialise lists for new market and outcomes
                            markets = new ArrayList<>();
                            outcomes = new ArrayList<>();
                        }
                    //}
                    //currentEventId = values[4];
                    event = populateEvent(header.getOperation(), values);
                } else if (header.getType().equals(Type.market)) {
                    // Initialise outcomes for new market
                    outcomes = new ArrayList<>();
                    currentMarketId = values[5];
                    market = populateMarket(header.getOperation(),values);

                    if (event != null) {
                        markets.add(market);
                        event.setMarkets(markets);
                    }
                } else if (header.getType().equals(Type.outcome)) {
                    // This is when the same outcomes have been updated in succession
                    if (event != null && originalOutcomeCount != 0 && outcomes.size() == originalOutcomeCount) {
                        json = mapper.writeValueAsString(event);
                        System.out.println("Saving event: " + json);
                        fixtureRepository.save(event);
                        // We need to go back into next if statement
                        outcomeUpdatesOnly = false;
                        originalOutcomeCount = 0;
                    }
                    // If outcome updates, but market it different to last line, process outcomes
                    if (updateMode && currentMarketId != null && !currentMarketId.equals(values[4]) && !outcomeUpdatesOnly) {
                        outcomeUpdatesOnly = true;
                        currentMarketId = values[4];
                        outcomes = new ArrayList<>();

                        event = fixtureRepository.findByOutcomeId(values[5]);
                        System.out.println("Found from outcomeiD" + event);

                        //event = fixtureRepository.findByMarketId(values[4]);

                        //currentEventId = event.getEventId();

                        market = event.getMarkets().stream()
                                .filter(mkt -> mkt.getMarketId().equals(values[4]))
                                .findFirst()
                                .orElseThrow();
                        originalOutcomeCount = market.getOutcomes().size();
                        market.setOutcomes(new ArrayList<>());
                    }

                    outcome = fixtureMapper.mapOutcome(values);
                    originalOutcomeCount++;

                    if (market != null) {
                        outcomes.add(outcome);
                        market.setOutcomes(outcomes);
                    }
                }
            }
        }

    public Event populateEvent(Operation operation, String[] values) throws Exception {
        if (operation.equals(Operation.create)) {
            return fixtureMapper.mapEvent(values);
        } else if (operation.equals(Operation.update)) {

            Query query = new Query().addCriteria(Criteria.where("eventId").is(values[4]));
            Update update = new Update();
            update.set("category", values[5]);
            update.set("subCategory", values[6]);
            update.set("name", addPipes(values[7]));
            update.set("startTime", Long.valueOf(values[8]));
            update.set("displayed", Integer.parseInt(values[9]) == 1);
            update.set("suspended", Integer.parseInt(values[10]) == 1);
            FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(false);

            Event event = mongoTemplate.findAndModify(query, update, options, Event.class);
            System.out.println("Updated event: " + event);



            //Event event = fixtureRepository.findById(values[4]).orElseThrow();
            //System.out.println("Original event: " + event);
            //fixtureMapper.mapEvent(event, values);
            return event;
        } else {
            throw new Exception("Invalid Operation passed in");
        }
    }

    public Market populateMarket(Operation operation, String[] values) throws Exception {
        if (operation.equals(Operation.create)) {
            return fixtureMapper.mapMarket(values);
        } else if (operation.equals(Operation.update)) {
            Query query = new Query().addCriteria(Criteria.where("eventId").is(values[4]).and("marketId").is(values[5]));
            Update update = new Update();
            update.set("name", addPipes(values[6]));
            update.set("displayed", Integer.parseInt(values[7]) == 1);
            update.set("suspended", Integer.parseInt(values[8]) == 1);
            FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(false);

            Event event = mongoTemplate.findAndModify(query, update, options, Event.class);
            System.out.println("Updated event from market: " + event);


//            Event event = fixtureRepository.findById(values[4]).orElseThrow();
//            Optional<Market> optMarket = event.getMarkets().stream()
//                    .filter(mkt -> mkt.getMarketId().equals(values[5]))
//                    .findFirst();
//
//            Market market;
//            if (optMarket.isPresent()) {
//                market = optMarket.get();
//                fixtureMapper.mapMarket(market, values);
//            } else {
//                // A new market?
//                market = fixtureMapper.mapMarket(values);
//            }

            return fixtureMapper.mapMarket(values);
        } else {
            throw new Exception("Invalid Operation passed in");
        }
    }

    public Outcome populateOutcome(Operation operation, String[] values, Event event) throws Exception {
        if (operation.equals(Operation.create)) {
            return fixtureMapper.mapOutcome(values);
        } else if (operation.equals(Operation.update)) {
            // Outcomes can be updated independent of event and market updates

            Market market = event.getMarkets().stream()
                    .filter(mkt -> mkt.getMarketId().equals(values[4]))
                    .findFirst()
                    .orElseThrow();

            Outcome outcome = market.getOutcomes().stream()
                    .filter(otcm -> otcm.getOutcomeId().equals(values[5]))
                    .findFirst()
                    .orElseThrow();

//            Optional<Outcome> optOutcome = evnt.getMarkets().get(0).getOutcomes().stream()
//                    .filter(outcome -> outcome.getOutcomeId().equals(values[5]))
//                    .findFirst();
            fixtureMapper.mapOutcome(outcome, values);

            return outcome;
        } else {
            throw new Exception("Invalid Operation passed in");
        }
    }

    public String addPipes(String line) {
        return line.replace("<pipe>", "|");
    }
}
