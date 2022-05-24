package jm.skybet.feedme.demo.service;

import lombok.Value;
import lombok.experimental.NonFinal;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;

@Value
@NonFinal
@ConstructorBinding
@ConfigurationProperties(prefix = "jm.skybet.feedme.demo")
@Validated
public class FeedMeServiceProperties {
    String host;
    int port;
}
