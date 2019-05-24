import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main{
    static final int SERVER_PORT = 3000;
    static ServerSocket serverSocket;

    public static void main(String[] args){
        // Connect to database
        System.out.print("[INIT] Connecting to database...");
        // Let this be a piece of code that connects us to a database...

        // Load dummy data because our puny human linear perception of time doesn't allow for this kind of implementations
        // TODO: Add dummy data (waiters, prs, prepAreas, maybe orders)
        for(int i=0; i<10; i++){
            new Table(-1, LatLng.random(), 4, 0);
            new PrepArea(-1, LatLng.random(), null, 0);
            new Product(-1, "desc", 1.0, Product.ProductType.PRODUCT_DRINK, 10, false, 2.0);
        }

        System.out.println("OK");

        System.out.print("[INIT] Starting thread for manual input handling...");
        new Thread(() -> {
            BufferedReader sys_in = new BufferedReader(new InputStreamReader(System.in));

            try{
                String sys_req = sys_in.readLine();
                while(sys_req!=null){
                    serveSysRequest(sys_req, sys_in);
                    sys_req = sys_in.readLine();
                }
            }catch(IOException e){
                e.printStackTrace();
                System.exit(1);
            }
        }).start();
        System.out.println("OK");

        // Open server socket
        System.out.print("[INIT] Opening server socket at port " + SERVER_PORT + "...");
        try{
            serverSocket = new ServerSocket(SERVER_PORT);
        }catch(IOException e){
            System.out.println("Failed");
            e.printStackTrace();
            return;
        }
        System.out.println("OK");

        // Server is ready to start receiving connections
        System.out.println("[Ready] Listening for connections at " + serverSocket.getLocalPort() + "...");

        while(!serverSocket.isClosed()){
            Socket client;

            try{
                client = serverSocket.accept();
            }catch(IOException e){
                e.printStackTrace();
                continue;
            }

            System.out.println("[+] Connection from " + client.getInetAddress().getHostAddress() + ":" + client.getPort());
            new Session(client).start();
        }
    }

    static void serveSysRequest(String req, BufferedReader sys_in){
        // Serve commands from CLI

        switch(req){
            case "add":
                try{
                    System.out.println("1. Table");
                    System.out.println("2. PrepArea");
                    System.out.println("3. Product");
                    System.out.println("Whatever else to return");

                    int choice;
                    while(true){
                        choice = Integer.parseInt(sys_in.readLine());

                        if(choice == 1){
                            // Create a new Table
                            System.out.println(new Table(-1, LatLng.random(), 4, 0));
                        }else if(choice == 2){
                            // Create a new PrepArea
                            System.out.println(new PrepArea(-1, LatLng.random(), null, 0));
                        }else if(choice == 3){
                            // Create a new Product
                            System.out.println(new Product(-1, "desc", 1.0, Product.ProductType.PRODUCT_DRINK, 10, false, 2.0));
                        }else break;
                    }

                }catch(IOException e){
                    e.printStackTrace();
                }
                break;

            case "print":
                try{
                    System.out.println("1. Tables");
                    System.out.println("2. PrepAreas");
                    System.out.println("3. Orders");

                    int choice = Integer.parseInt(sys_in.readLine());
                    if(choice == 1){
                        for(Table t : Table.allTables)
                            System.out.println(t.toString());

                    }else if(choice == 2){
                        for(PrepArea pa : PrepArea.allPrepAreas)
                            System.out.println(pa.toString());

                    }else if(choice == 3){
                        for(Order o : Order.allOrders)
                            System.out.println(o.toString());
                    }
                }catch(IOException e){
                    e.printStackTrace();
                }
                break;

            case "connect":
                // Connect to server as a client (for testing)
                try{
                    // Connect to serverSocket
                    Socket cl = new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort());
                    BufferedReader cl_out = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                    PrintWriter cl_in = new PrintWriter(cl.getOutputStream());

                    // Create new thread to read data from socket
                    new Thread(() -> {
                        try{
                            while(cl.isConnected()){
                                System.out.println(cl_out.readLine());
                            }
                        }catch(IOException e){
                            //e.printStackTrace();
                        }
                        System.out.println("Manual connection terminated");
                    }).start();

                    // Loop to read input from user and send it through the socket, until user enters an empty string
                    String str = sys_in.readLine();
                    while(str!=null){

                        cl_in.println(str);
                        cl_in.flush();

                        if(str.equals("exit")) break;
                        str = sys_in.readLine();
                    }

                    cl_in.close();
                    cl_out.close();
                    cl.close();

                }catch(IOException e){
                    e.printStackTrace();
                }

                break;

            case "exit":
                exitServer();
                break;
        }
    }

    static void exitServer(){
        try{
            serverSocket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        System.exit(0);
    }
}
