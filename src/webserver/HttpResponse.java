/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Wilmer
 */
public class HttpResponse extends Thread {

    private BufferedReader requestContent;
    private DataOutputStream responseContent;
    // El new line y carriage return son importantes (\r\n) en el formato del header
    private final String HEADER_FORMAT = ""
            + "HTTP/1.1 {0}\r\n"
            + "Date: {1}\r\n"
            + "Server: Servidor Java v0.1\r\n"
            + "Content-Type: {2}\r\n"
            + "Connection: close\r\n"
            + "\r\n";
    private final Properties STATUS_CODES;
    private final Properties MIME_TYPES;

    public HttpResponse(BufferedReader requestContent, DataOutputStream responseContent) throws IOException {
        this.requestContent = requestContent;
        this.responseContent = responseContent;
        STATUS_CODES = new Properties();
        MIME_TYPES = new Properties();

        // Crea un HashTable a partir de los archivos .properties (http status codes y mime types)
        InputStream inputStream = getClass().getResourceAsStream("HttpStatusCodes.properties");
        STATUS_CODES.load(inputStream);
        inputStream = getClass().getResourceAsStream("HttpMimeTypes.properties");
        MIME_TYPES.load(inputStream);
    }

    public void buildResponse(BufferedReader requestContent, DataOutputStream responseContent) {
        try {
            // Primera linea del request. Contiene: request verb (get, post,...), path, http protocol
            String startLine = requestContent.readLine();
            String startLineSplit[] = startLine.split(" ");
            String requestVerb = startLineSplit[0];
            String requestPath = startLineSplit[1];
            String httpProtocol = startLineSplit[2];
            String fileExtension = FilenameUtils.getExtension(requestPath);

            FileInputStream file = null;
            try {
                file = new FileInputStream("mi_web" + requestPath);
                // Crear header con un 200 OK, y el content type se define por fileExtension. Luego se escribe en el responseContent
                responseContent.writeBytes(buildHeader("200", fileExtension));
                // Leer archivo y adjuntarlo en el response
                while (true) {
                    int byteData = file.read();
                    if (byteData == -1) {
                        break;
                    }
                    responseContent.write(byteData);
                }
                file.close();
                responseContent.flush();
            } catch (IOException ex) {
                // Archivo no existe, 404 Not Found. Enviar header correspondiente y 404.html
                System.out.println("Archivo no existe");
                System.out.println(ex.getMessage());
            }
            responseContent.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String buildHeader(String status, String fileExtension) {
        return MessageFormat.format(HEADER_FORMAT,
                STATUS_CODES.getProperty(status),
                getDateHeader(),
                MIME_TYPES.getProperty(fileExtension));
    }

    private String getDateHeader() {
        SimpleDateFormat dateFormat;
        String fecha;
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        fecha = dateFormat.format(new Date()) + " GMT";
        return fecha;
    }

    @Override
    public void run() {
        this.buildResponse(requestContent, responseContent);
    }
}
