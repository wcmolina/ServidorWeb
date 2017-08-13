/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.IOException;

/**
 *
 * @author Wilmer
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            // TODO code application logic here
            WebServer servidor = new WebServer();
            servidor.start();
            // Ir a localhost en un browser para enviar un request
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
