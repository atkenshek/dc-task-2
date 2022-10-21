import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Simple web server.
 */
public class WebServer {
    public static void main(String[] args) {
        int numOfThreads = (args.length > 1 ? Integer.parseInt(args[1]) : 4);
        // Port number for http request
        int port = args.length > 1 ? Integer.parseInt(args[1]) : 8080;
        // The maximum queue length for incoming connection
        int queueLength = args.length > 2 ? Integer.parseInt(args[2]) : 50;;


        try (ServerSocket serverSocket = new ServerSocket(port, queueLength)) {
            System.out.println("Web Server is starting up, listening at port " + port + ".");
            System.out.println("You can access http://localhost:" + port + " now.");

            ThreadSafeQueue<Processor> queue = new ThreadSafeQueue<>();

            // Starting consumer threads.
            for (int i = 0; i < numOfThreads; i++) {
                Consumer cons = new Consumer(i, queue);
                cons.start();
            }

            while (true) {
                // Make the server socket wait for the next client request
                Socket socket = serverSocket.accept();
                System.out.println("Got connection!");

                // To read input from the client
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));

                // Get request
                HttpRequest request = HttpRequest.parse(input);

                // Adding items in the queue for consumers.
                queue.add(new Processor(socket, request));

//                Process request
//                Thread t = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Processor proc = new Processor(socket, request);
//                        try {
//                            Thread.sleep(100);
//                            proc.process();
//                        } catch (IOException | InterruptedException e) {
//                            e.printStackTrace();
//
//                        }
//                    }
//                });
//
//                t.start();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        finally {
            System.out.println("Server has been shutdown!");
        }
    }
}
