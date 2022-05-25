package jm.skybet.feedme.demo.repository;

import jm.skybet.feedme.demo.model.Fixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@DataMongoTest
public class FixtureRepositoryTest {

    @Autowired private FixtureRepository fixtureRepository;

    private final static String EVENT_ID = "fed81d3c-c99d-418a-ac7b-e5d7d12852d8";

//    An example test
//    @Test
//    void shouldFindByEventId(){
//        final Fixture fixture = Fixture.builder().eventId(EVENT_ID).build();
//        mongodbTestEntityManager.persistAndFlush(fixture);
//
//        List<Fixture> result = fixtureRepository.findByEventId(EVENT_ID);
//
//        assertThat(result.size(), is(1));
//    }

}
