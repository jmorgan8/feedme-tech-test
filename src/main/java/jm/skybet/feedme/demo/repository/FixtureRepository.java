package jm.skybet.feedme.demo.repository;

import jm.skybet.feedme.demo.model.Event;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

public interface FixtureRepository extends MongoRepository<Event, String> {
    @Query(value = "{ 'eventId' : ?0, 'markets.marketId' : ?1 }")
    Event findByMarketId(String eventId, String marketId);

    @Query(value = "{ 'markets.marketId' : ?0 }")
    Event findByMarketId(String marketId);

    @Query(value = "{ 'markets.outcomes.outcomeId' : ?0 }")
    Event findByOutcomeId(String outcomeId);
}
