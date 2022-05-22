package jm.skybet.feedme.demo.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection {

    public ServerConnection() throws IOException {

        try (Socket socket = new Socket("localhost", 8282)) {
            InputStream input = socket.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String fixture;    // reads a line of text

            while (((fixture = reader.readLine()) != null)) {
                System.out.println(fixture);
            }

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}
