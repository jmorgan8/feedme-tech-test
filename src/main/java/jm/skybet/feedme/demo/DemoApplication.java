package jm.skybet.feedme.demo;

import jm.skybet.feedme.demo.service.FeedMeService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class DemoApplication {
	public static void main(String[] args) throws IOException {
		ApplicationContext applicationContext = SpringApplication.run(DemoApplication.class, args);
		FeedMeService service = applicationContext.getBean(FeedMeService.class);
		service.processFeed();
	}

}
