package jm.skybet.feedme.demo.service;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class FeedMeService {

    public void processLine(String line) {
        line = line.substring(1);
        String[] values = line.split("\\|");
        System.out.println(Arrays.toString(values));
    }
}
