package org.example.server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;

public class Server {
    private ServerSocket ss;
    private boolean running = false;

    public Server() throws IOException {
        ss = new ServerSocket(2137);
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.listen();
    }

    public void listen() throws IOException {
        running = true;
        while (running) {
            Socket socket = ss.accept();
            ClientHandler clientHandler = new ClientHandler(this, socket);
            Thread thread = new Thread(clientHandler);
            thread.start();
        }
    }

    public void parseLine(String line, String username, int n) {
        String[] parts;
        parts = line.split(",", -1);
        Double[] values = new Double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            values[i] = Double.parseDouble(parts[i]);
        }
        System.out.println(Arrays.toString(values));
        System.out.println(getBase64(values));
        insertIntoDB(username,n,getBase64(values));
    }

    public String getBase64(Double[] values) {
        BufferedImage bufferedImage = new BufferedImage(200,100,BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(Color.WHITE);
        graphics2D.fillRect(0,0,200,100);
        graphics2D.setColor(Color.red);
        for(int j=0; j<values.length; j++) {
            graphics2D.fillRect(j, (int) (50 - values[j]), 1, 1);
        }
            graphics2D.dispose();
            String imageString;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(bufferedImage,"jpg",byteArrayOutputStream);
                imageString = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                return imageString;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    private void insertIntoDB(String username, int number, String image)
    {
        String insertSQL = "INSERT INTO user_eeg (username, electrode_number, image) VALUES (?, ?, ?)";
        String url = "jdbc:sqlite:C:\\Users\\natal\\Desktop\\studia\\I ROK\\II semestr\\programowanie obiektowe\\samodzielne\\eeg\\usereeg.db";
        try (Connection connection = DriverManager.getConnection(url))
        {
            PreparedStatement addUserStatement = connection.prepareStatement(insertSQL);
            addUserStatement.setString(1, username);
            addUserStatement.setInt(2, number);
            addUserStatement.setString(3, image);
            addUserStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
    }


