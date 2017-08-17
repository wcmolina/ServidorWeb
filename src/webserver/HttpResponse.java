/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author Wilmer
 */
public class HttpResponse extends Thread {

    private final BufferedReader REQUEST_INPUT;
    private final DataOutputStream RESPONSE_OUTPUT;
    private final HashMap<String, Object> HEADERS;
    private int status;
    public final static Properties STATUS_CODES;
    public final static Properties MIME_TYPES;

    static {
        STATUS_CODES = new Properties();
        MIME_TYPES = new Properties();
        try {
            InputStream inputStream = HttpResponse.class.getResourceAsStream("HttpStatusCodes.properties");
            // Crea un HashTable a partir de los archivos .properties (http status codes y mime types)
            STATUS_CODES.load(inputStream);
            inputStream = HttpResponse.class.getResourceAsStream("HttpMimeTypes.properties");
            MIME_TYPES.load(inputStream);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public HttpResponse(BufferedReader requestInput, DataOutputStream responseOutput) {
        REQUEST_INPUT = requestInput;
        RESPONSE_OUTPUT = responseOutput;
        HEADERS = new HashMap();
        // Agregar headers que van por defecto
        HEADERS.put("Server", "JavaServer");
        HEADERS.put("Connection", "close");
    }

    public void buildResponse() {
        try {
            // Primera linea del request. Contiene: request verb (get, post,...), path, http protocol
            String startLine = REQUEST_INPUT.readLine();
            String startLineSplit[] = startLine.split(" ");
            String requestVerb = startLineSplit[0];
            String requestPath = startLineSplit[1];
            //String httpProtocol = startLineSplit[2];

            switch (requestVerb) {
                case "GET": {
                    sendGetResponse(requestPath);
                    break;
                }
                case "POST": {
                    break;
                }
                case "PUT": {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendGetResponse(String requestPath) throws IOException {
        File file;
        String fileExtension = FilenameUtils.getExtension(requestPath);
        try {
            if (requestPath.isEmpty() || requestPath.equals("/")) {
                // 302, redireccionamiento
                status = 302;
                addHeader("Location", "/index.html");
                addHeader("Content-Type", MIME_TYPES.getProperty("html"));
                RESPONSE_OUTPUT.writeBytes(getHeaders());
            } else {
                file = new File("mi_web" + requestPath);
                if (file.exists()) {
                    status = 200;
                    addHeader("Content-Type", MIME_TYPES.getProperty(fileExtension));
                } else {
                    file = new File("mi_web/404.html");
                    status = 404;
                    addHeader("Content-Type", MIME_TYPES.getProperty("html"));
                }
                RESPONSE_OUTPUT.writeBytes(getHeaders());
                RESPONSE_OUTPUT.write(FileUtils.readFileToByteArray(file));
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
        RESPONSE_OUTPUT.close();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void addHeader(String headerName, String headerValue) {
        HEADERS.put(headerName, headerValue);
    }

    public String getHeaders() {
        StringBuilder builder = new StringBuilder();
        // Date header
        addHeader("Date", getDateHeader());
        // Protocolo y status code
        builder.append(String.format("%s %s\r\n", "HTTP/1.1", STATUS_CODES.getProperty(String.valueOf(status))));
        // Los demas headers
        HEADERS.entrySet().forEach((entry) -> {
            builder.append(String.format("%s: %s\r\n", entry.getKey(), entry.getValue()));
        });
        //Separar headers del body del response
        builder.append("\r\n");
        System.out.println(builder.toString());
        return builder.toString();
    }

    private String getDateHeader() {
        SimpleDateFormat dateFormat;
        String date;
        dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        date = dateFormat.format(new Date()) + " GMT";
        return date;
    }

    @Override
    public void run() {
        buildResponse();
    }
}
