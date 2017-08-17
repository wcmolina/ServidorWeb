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
    private final ServerSocket SOCKET;
    private final ThreadPool THREAD_POOL;

    public WebServer(ThreadPool threadPool) throws IOException {
        SOCKET = new ServerSocket(PORT);
        THREAD_POOL = threadPool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // No pasa del accept() hasta que entre una solicitud
                Socket request = SOCKET.accept();
                // Contiene el contenido del request
                BufferedReader requestContent = new BufferedReader(new InputStreamReader(request.getInputStream()));
                // Contiene el contenido del response
                DataOutputStream responseContent = new DataOutputStream(request.getOutputStream());
                HttpResponse response = new HttpResponse(requestContent, responseContent);
                // Mandarlo al thread pool para que se ejecute el response ahi
                THREAD_POOL.execute(response);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
