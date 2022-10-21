import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Processor of HTTP request.
 */
public class Processor extends Thread{
    private final Socket socket;
    private final HttpRequest request;

    public Processor(Socket socket, HttpRequest request) {
        this.socket = socket;
        this.request = request;
    }

    @Override
    public void run() {
        try {
            process();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process() throws IOException {
        System.out.println("Got request:");
        System.out.println(request.toString());
        System.out.flush();

        PrintWriter output = new PrintWriter(socket.getOutputStream());
        System.out.println(request.getRequestLine());


        if(request.getRequestLine().equals("GET /create/itemid HTTP/1.1")){
            // We are returning a simple web page now.
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><p>Hello, ITEM CREATED!</p></body>");
            output.println("</html>");
            output.flush();
        }
        else if(request.getRequestLine().equals("GET /delete/itemid HTTP/1.1")){
            // We are returning a simple web page now.
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><p>Hello, ITEM WAS DELETED!</p></body>");
            output.println("</html>");
            output.flush();
        }

        else if(request.getRequestLine().equals("GET /exec/params HTTP/1.1")){
            // We are returning a simple web page now.
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><h2>Hello, Fibonacci sequence till 1.000.000 are: </h2>");
            int firstNumber = 0;
            int secondNumber = 1;
            for(int i = 0; i < 10000000; i++){
               // output.print(String.valueOf(firstNumber) + ", ");
                int nextElement = firstNumber + secondNumber;
                firstNumber = secondNumber;
                secondNumber = nextElement;
            }
            output.println("</body>");
            output.println("</html>");
            output.flush();
        }

        else {
            // We are returning a simple web page now.
            output.println("HTTP/1.1 200 OK");
            output.println("Content-Type: text/html; charset=utf-8");
            output.println();
            output.println("<html>");
            output.println("<head><title>Hello</title></head>");
            output.println("<body><p>Hello, world!</p></body>");
            output.println("</html>");
            output.flush();

        }
        socket.close();
    }
}
