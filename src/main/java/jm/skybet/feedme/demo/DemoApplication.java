package jm.skybet.feedme.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import jm.skybet.feedme.demo.model.Event;
import jm.skybet.feedme.demo.model.Header;
import jm.skybet.feedme.demo.model.Market;
import jm.skybet.feedme.demo.model.Outcome;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

@SpringBootApplication
public class DemoApplication {
	private static final String OPERATION_CREATE = "create";
	private static final String OPERATION_UPDATE = "update";

	private static final String TYPE_EVENT = "event";
	private static final String TYPE_MARKET = "market";
	private static final String TYPE_OUTCOME = "outcome";
	//FeedMeService service;


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

		// TODO: Tidy this up into the correct folders

		try (Socket socket = new Socket("localhost", 8282)) {
			InputStream input = socket.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			String fixture;    // reads a line of text
			int count = 0;

			ObjectMapper mapper = new ObjectMapper();


			while (((fixture = reader.readLine()) != null) && count < 10) {
				// Remove first pipe
				fixture = fixture.substring(1);
				// Replace escaped pipes with text before
				fixture = fixture.replace("\\|","<pipe>");
				// Split fields in the line
				String[] values = fixture.split("\\|");
				String json;
				// Show values extracted ok
				System.out.println(Arrays.toString(values));
				count++;

				Header header = Header.builder()
						.msgId(Long.valueOf(values[0]))
						.type(values[1])
						.operation(values[2])
						.timestamp(Long.valueOf(values[3]))
						.build();

				System.out.println(header);

				if (header.getOperation().equals(TYPE_EVENT)) {
					Event event = Event.builder()
							.eventId(values[4])
							.category(values[5])
							.subCategory(values[6])
							.name(values[7].replace("<pipe>","|"))
							.startTime(Long.valueOf(values[8]))
							.displayed(Integer.parseInt(values[9]) == 1)
							.suspended(Integer.parseInt(values[10]) == 1)
							.build();
					json = mapper.writeValueAsString(event);
					System.out.println(json);
				}

				if (header.getOperation().equals(TYPE_MARKET)) {
					Market market = Market.builder()
							.eventId(values[4])
							.marketId(values[5])
							.name(values[6].replace("<pipe>","|"))
							.displayed(Integer.parseInt(values[7]) == 1)
							.suspended(Integer.parseInt(values[8]) == 1)
							.build();
					json = mapper.writeValueAsString(market);
					System.out.println(json);
				}

				if (header.getOperation().equals(TYPE_OUTCOME)) {
					Outcome outcome = Outcome.builder()
							.marketId(values[4])
							.outcomeId(values[5])
							.name(values[6].replace("<pipe>","|"))
							.price(values[7])
							.displayed(Integer.parseInt(values[8]) == 1)
							.suspended(Integer.parseInt(values[9]) == 1)
							.build();
					json = mapper.writeValueAsString(outcome);
					System.out.println(json);
				}

				//if (header.getOperation().equals(OPERATION_UPDATE)) {
				//	break;
				//}
				//else if (header.getOperation().equals(OPERATION_CREATE)) {
				//	break;
				//}


			}

		} catch (UnknownHostException ex) {

			System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

			System.out.println("I/O error: " + ex.getMessage());
		}
	}

}
