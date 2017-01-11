/*
* CMPT 371 Project 1
* Create by Yilun Qian
* Login name: yilunq
*/

/*
* 1. Simple request
*   index.html was opened in the web browser, and shows nicely.
*
* 2. Simple request in subdirectory
*   sub.html was requested in the web browser, and shows nicely.
*
* 3. Object not found
*   browser shows "404 page not found" error
*
* 4. Basic MIME type handling
*   chrome browser begin to download the gif/jpg files automatically
*   differently, IE browser shows pictures
*
* 5. Concurrency
*   multiple requests can be done concurrently.
*   there are 10 archive.zip files and 10 wget-log files created automatically in the directory
*    after running ./test5
*
*/

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Created by Allen on 2016-10-02.
 */

public final class WebServer {
    public static void main(String[] args) throws Exception{

        Scanner keyboard = new Scanner(System.in);
        System.out.println("enter an port number (like 5678)");

        // set the port number
        int port = keyboard.nextInt();
        System.out.println("Port number " + port + " selected!");
        System.out.println("WebServer is running...");

        ServerSocket welcomeSocket = new ServerSocket(port);

        while (true) {
            // Listen for a TCP connection request
            Socket connectionSocket = welcomeSocket.accept();

            // addressing the request
            addressing(connectionSocket);
        }
    }

    private static void addressing(Socket socket) {
        // Construct an object to process the HTTP request message
        try {
            HttpRequest httpRequest = new HttpRequest(socket);

            // Create a new thread to process the request
            Thread thread = new Thread(httpRequest);

            // Start the thread
            thread.start();

        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

final class HttpRequest implements Runnable {
    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        // socket's input and output streams
        // create input stream, attached to socket
        BufferedReader br = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));

        // create output stream, attached to socket
        DataOutputStream os = new DataOutputStream(
                socket.getOutputStream());

        String requestLine = br.readLine();

        // part 2
        stageTwo(requestLine, os);

        // close streams and socket
        os.close();
        br.close();
        socket.close();
    }

    private void stageTwo(String requestLine, DataOutputStream os){
        StringTokenizer tokens = new StringTokenizer(requestLine);
        tokens.nextToken(); // should be "GET"
        String fileName = tokens.nextToken();
        fileName = "." + fileName;

        // Open the requested file
        FileInputStream fis = null;
        boolean fileExists = true;

        try {
            fis = new FileInputStream(fileName);
        } catch (FileNotFoundException e) {
            fileExists = false;
        }
        // Construct the response message
        responseMessage(fileExists, fileName, os, fis);
    }

    private void responseMessage(boolean fileExists, String fileName, DataOutputStream os,
                                 FileInputStream fis) {
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;

        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-type: " + contentType( fileName ) + CRLF;
        }
        else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-type: " + contentType( ".html" ) + CRLF;
            entityBody = "<HTML>" +
                    "<HEAD><TITLE>404 Not Found</TITLE></HEAD>" +
                    "<BODY>404 Not Found</BODY></HTML>";
        }

        // Write to Client dataOutputStream
        try {
            // Send the status line
            os.writeBytes(statusLine);

            // Send the content type line
            os.writeBytes(contentTypeLine);

            // Send a blank line to indicate the end of the header lines
            os.writeBytes(CRLF);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Send the entity body.
        try {
            if (fileExists) {
                sendBytes(fis, os);
                fis.close();
            } else {
                os.writeBytes(entityBody);
            }
        } catch (IOException e) {
            System.out.println("File failed to close!");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("File failed to sendBytes!");
            e.printStackTrace();
        }
    }

    private String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }

        if (fileName.endsWith(".gif")) {
            return "imge/gif";
        }

        if (fileName.endsWith(".jpeg")) {
            return "image/jpeg";
        }
        // file extension is unknown:
        return "application/octet-stream";
    }

    private static void sendBytes(FileInputStream file, OutputStream os) throws Exception {
        // Construct a 1K buffer to hold bytes on their way to the socket.
        byte[] buffer = new byte[1024]; // intermediate storage space for bytes from file to the output stream
        int bytes = 0;

        // Copy requested file into the socket's output stream.
        while((bytes = file.read(buffer)) != -1 ) {
            os.write(buffer, 0, bytes); // both read() and write() will throw exceptions
        }
    }
}
