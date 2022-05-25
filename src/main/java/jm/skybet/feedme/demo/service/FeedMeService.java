package jm.skybet.feedme.demo.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.skybet.feedme.demo.mapping.FixtureMapper;
import jm.skybet.feedme.demo.model.*;
import jm.skybet.feedme.demo.repository.FixtureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedMeService {
    private final FeedMeServiceProperties feedMeServiceProperties;
    private final FixtureMapper fixtureMapper;
    private final FixtureRepository fixtureRepository;

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
        Fixture fixture = null;
        String line;    // reads a line of text
        int count = 0; //Use for local testing

        // ObjectMapper mapper = new ObjectMapper();
        // String json;

        while (((line = reader.readLine()) != null) && count < 300) {
            System.out.println(line);
            String[] values = cleanupFixture(line);
            count++;

            Header header = fixtureMapper.mapHeader(values);

//            if (count < 1000) {
//                continue;
//            }

            // Basic rules:
            // 1. When create:
            //  1.1  All event, market and outcomes come sequentially
            //  1.2  Inserts will start at outcome level as that is the most denormalised form
            // 2. When update:
            //  2.1  If event comes through, all markets and outcomes follow (usually to reverse
            //       the displayed and suspended flags
            //  2.2  Outcomes can be updated independently - usually to change the price

            if (header.getType().equals(Type.event)) {
                    fixture = populateEvent(header.getOperation(), values);
                } else if (header.getType().equals(Type.market)) {
                    fixture = populateMarket(header.getOperation(),values, fixture);
                } else if (header.getType().equals(Type.outcome)) {
                        fixture = populateOutcome(header.getOperation(),values, fixture);
                        // json = mapper.writeValueAsString(fixture);
                        // Fixture is not null when operation is in create mode
                        if (fixture != null) {
                            //System.out.println("Saving event in loop: " + json);
                            fixture = fixtureRepository.save(fixture);
                            // Remove id so it treats the next fixture as new
                            fixture.setId(null);
                        }

                } else {
                    throw new Exception("Invalid type passed in");
                }
            }
        }

    public Fixture populateEvent(Operation operation, String[] values) throws Exception {
        if (operation.equals(Operation.create)) {
            return fixtureMapper.mapEvent(values);
        } else if (operation.equals(Operation.update)) {
            List<Fixture> fixtures = fixtureRepository.findByEventId(values[4]);
            //System.out.println("Found fixture from event: " + fixtures);
            fixtures.forEach(fixture -> fixtureMapper.mapEvent(fixture, values));
            //System.out.println("Updated fixture from event: " + fixtures);
            fixtureRepository.saveAll(fixtures);

            return null;
        } else {
            throw new Exception("Invalid Operation passed in");
        }
    }

    public Fixture populateMarket(Operation operation, String[] values, Fixture fixture) throws Exception {
        if (operation.equals(Operation.create)) {
            fixtureMapper.mapMarket(fixture, values);
            return fixture;
        } else if (operation.equals(Operation.update)) {
            List<Fixture> fixtures = fixtureRepository.findByMarketId(values[5]);
            //System.out.println("Found fixtures from market: " + fixtures);
            fixtures.forEach(fxtr -> fixtureMapper.mapMarket(fxtr, values));
            //System.out.println("Updated fixture from market: " + fixtures);
            fixtureRepository.saveAll(fixtures);

            return null;
        } else {
            throw new Exception("Invalid Operation passed in");
        }
    }

    public Fixture populateOutcome(Operation operation, String[] values, Fixture fixture) throws Exception {
        if (operation.equals(Operation.create)) {
            fixtureMapper.mapOutcome(fixture, values);
            return fixture;
        } else if (operation.equals(Operation.update)) {
            List<Fixture> fixtures = fixtureRepository.findByOutcomeId(values[5]);
            //System.out.println("Found fixtures from outcome: " + fixtures);
            fixtures.forEach(fxtr -> fixtureMapper.mapOutcome(fxtr, values));
            //System.out.println("Updated fixtures from outcome: " + fixtures);
            fixtureRepository.saveAll(fixtures);

            return null;
        } else {
            throw new Exception("Invalid Operation passed in");
        }
    }
}
