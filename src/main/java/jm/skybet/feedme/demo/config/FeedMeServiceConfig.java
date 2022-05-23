package jm.skybet.feedme.demo.config;

import jm.skybet.feedme.demo.service.FeedMeServiceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FeedMeServiceProperties.class)
public class FeedMeServiceConfig {
}
