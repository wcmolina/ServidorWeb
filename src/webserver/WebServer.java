/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Wilmer
 */
public class WebServer extends Thread {

    private final int PORT = 80;
    private ServerSocket socket;

    public WebServer() throws IOException {
        socket = new ServerSocket(PORT);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // No pasa del accept() hasta que entre una solicitud
                Socket request = socket.accept();
                // Contiene el contenido del request
                BufferedReader requestContent = new BufferedReader(new InputStreamReader(request.getInputStream()));
                // Contiene el contenido del response
                DataOutputStream responseContent = new DataOutputStream(request.getOutputStream());
                HttpResponse response = new HttpResponse(requestContent, responseContent);
                response.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
