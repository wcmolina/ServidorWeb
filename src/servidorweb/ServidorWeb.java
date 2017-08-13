/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidorweb;

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
public class ServidorWeb extends Thread {

    private final int PUERTO = 80;
    private ServerSocket socket;

    public ServidorWeb() throws IOException {
        socket = new ServerSocket(PUERTO);
    }

    @Override
    public void run() {
        while (true) {
            try {
                //No pasa del accept() hasta que entre una solicitud
                Socket solicitud = socket.accept();
                System.out.println("Nueva solicitud");
                try (
                        //Contiene la respuesta
                        DataOutputStream out = new DataOutputStream(solicitud.getOutputStream());
                        //Contiene el contenido de la solicitud
                        BufferedReader in = new BufferedReader(new InputStreamReader(solicitud.getInputStream()))) {
                    String input;
                    while ((input = in.readLine()) != null)
                        System.out.println(input);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
