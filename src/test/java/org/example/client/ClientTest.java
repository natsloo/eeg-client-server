package org.example.client;

import org.example.databasecreator.Creator;
import org.example.server.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.IOException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTest {

    private static Server server;
    private static Creator creator;
    private static String url = "jdbc:sqlite:C:\\Users\\natal\\Desktop\\studia\\I ROK\\II semestr\\programowanie obiektowe\\samodzielne\\eeg\\usereeg.db";

    @BeforeAll
    public static void start(){
        creator=new Creator();
        creator.create(url);
        try {
            server=new Server();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        new Thread(()-> {
            try {
                server.listen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();

    }

    @AfterAll
    public static void stop(){
        server.stop();
    }
    @ParameterizedTest
    @CsvFileSource(resources = "/test.csv",numLinesToSkip = 1)
    public void clientTest(String username,String filepath, int electrode, String image) {
        Client client = new Client();
        client.sendData(username,filepath);
        String imageFromDB = getImage(username,electrode);
        assertEquals(image,imageFromDB);

    }

    public String getImage(String username, int electrode_number)
    {
        String image = null;
        String sql = "SELECT image FROM user_eeg WHERE username = ? AND electrode_number = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setInt(2, electrode_number);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    image = rs.getString("image");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return image;
    }

}
