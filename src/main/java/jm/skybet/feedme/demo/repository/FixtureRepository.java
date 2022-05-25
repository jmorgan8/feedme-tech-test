package jm.skybet.feedme.demo.repository;

import jm.skybet.feedme.demo.model.Fixture;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FixtureRepository extends MongoRepository<Fixture, String> {
    @Query(value = "{ 'eventId' : ?0 }")
    List<Fixture> findByEventId(String eventId);

    @Query(value = "{ 'marketId' : ?0 }")
    List<Fixture> findByMarketId(String marketId);

    @Query(value = "{ 'outcomeId' : ?0 }")
    List<Fixture> findByOutcomeId(String outcomeId);
}
