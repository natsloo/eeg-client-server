package org.example.server;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private String username;
    private Server server;
    private Socket socket;

    private Scanner input;
    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        input = new Scanner(socket.getInputStream());
    }

    @Override
    public void run() {
        username = input.nextLine();
        String line;
        int i=0;
        while ((line=input.nextLine())!="Bye"){
            System.out.println(line);
            server.parseLine(line,username,i);
            i++;
        }
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
