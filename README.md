# Distributed Computing course
### dc-task-2

2nd task prepared by Temirzhanov Meiram Sopy, IT-2001

Developed multi-threaded web server.

The app defines following commands:

| Method | URL | Description | 
| ------ | --- | ----------- |  
| GET    | /create/itemid | Prints sample HTML webpage | 
| GET    | /delete/itemid | Prints sample HTML webpage | 
| GET    | /exec/params   | Calculates 1M fibonacci sequence |

<br>

**Example of sample http requests:**
```bash
 curl http://localhost:8080/exec/params
```


**Example of benchmark command:** 
```bash
 hey -n 50 -c 50 -t 0 http://localhost:8080/exec/params
```
**Adding multi-threading to web app, so it creates new thread at each request:**
```java
Thread t = new Thread(new Runnable() {
    @Override
    public void run() {
        Processor proc = new Processor(socket, request);
        try {
            Thread.sleep(100);
            proc.process();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
});

t.start();
```
**In the next step, updated my multi-threaded web server to use a thread pool:**

Incoming requests I added in a shared queue, from which worker threads taking these requests dynamically for processing.
Also, employed necessary synchronization, in order to make my threads work with the shared queue correctly.

```java
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
```
**Generic ThreadSafeQueue class:**
```java
public class ThreadSafeQueue<T> {
    private final Queue<T> queue;

    public ThreadSafeQueue() {
        this.queue = new LinkedList<>();
    }

    // Put element in the queue.
    public synchronized void add(T elem) {
        queue.add(elem);
        notify();
    }

    // Wait for new element in the queue and return it.
    public synchronized T pop() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        return this.queue.poll();
    }

    public synchronized int size() {
        return queue.size();
    }
}
```
**Consumer class:**
```java
public class Consumer extends Thread {
    private final int id;
    private final ThreadSafeQueue<Processor> queue;

    public Consumer(int id, ThreadSafeQueue<Processor> queue) {
        this.id = id;
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Wait for new element.
                Processor elem = queue.pop();

                // Stop consuming if null is received.
                if (elem == null) {
                    return;
                }
                // Process element.
                try {
                    elem.process();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
```
**Processor class:**
```java
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

```