package org.example.client;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    private PrintWriter writer;
    public static void main(String[] args) {
        Client client = new Client();
        Scanner input = new Scanner(System.in);
        String name = input.nextLine();
        String filepath = input.nextLine();
        client.sendData(name,filepath);
    }
        public void sendData(String name, String filepath) {
            try {
                Socket socket = new Socket("localhost", 2137);
                writer = new PrintWriter(socket.getOutputStream());
                send(name);
                BufferedReader reader = new BufferedReader(new FileReader(filepath));
                List<String> lines = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                    System.out.println(line);
                }

                for (String l : lines) {
                    send(l);
                    Thread.sleep(2000);
                }
                String bye = "Bye";
                send(bye);
                writer.close();
                socket.close();
            } catch (IOException | InterruptedException e){
                throw new RuntimeException(e);

            }
        }

    private void send(String message){

        writer.println(message);
        writer.flush();


    }

}
