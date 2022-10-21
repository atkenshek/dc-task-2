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

<br>

**Example of benchmark command:** 
```bash
 hey -n 50 -c 50 -t 0 http://localhost:8080/exec/params
```

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
