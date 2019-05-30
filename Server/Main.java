import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Main{
    static final int MOBILE_PORT = 3001;
    static final int SERVER_PORT = 3000;
    static ServerSocket serverSocket;

    static Random r = new Random();

    public static void main(String[] args){
        // Connect to database
        System.out.print("[INIT] Connecting to database...");
        // Let this be a piece of code that connects us to a database...

        // Load dummy data because our puny human linear perception of time doesn't allow for this kind of implementations
        // TODO: Add dummy data (Tables, PrepAreas, Products) so the server has enough to work with
        for(int i=1; i<=10; i++){
            new Table(i, LatLng.random(), 4, 0);
            new PrepArea(i, LatLng.random(), null, 0);
            new Product(i, "desc", 1.0, Product.ProductType.PRODUCT_DRINK, 10, false, 2.0);
        }

        System.out.println("OK");

        // Start thread for CLI
        System.out.print("[INIT] Starting thread for CLI...");
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

        try{
            switch(req){
                case "print":{
                    System.out.println("1. Tables");
                    System.out.println("2. PrepAreas");
                    System.out.println("3. Orders");
                    System.out.println("4. Waiting List");

                    int choice = Integer.parseInt(sys_in.readLine());
                    switch(choice){
                        case 1:
                            for(Table t : Table.allTables)
                                System.out.println(t.toString());

                            break;

                        case 2:
                            for(PrepArea pa : PrepArea.allPrepAreas)
                                System.out.println(pa.toString());

                            break;

                        case 3:
                            for(Order o : Order.allOrders)
                                System.out.println(o.toString());

                            break;

                        case 4:
                            for(WaitingGroup wg : WaitingGroup.waitingList)
                                System.out.println(wg.toString());
                    }
                    break;
                }

                case "auto":{
                    // Automatically create some random orders and send them to prepAreas, by connecting to the actual server
                    // Connect to serverSocket
                    Socket cl = new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort());
                    BufferedReader cl_out = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                    PrintWriter cl_in = new PrintWriter(cl.getOutputStream());

                    // Log in as a waiter
                    cl_in.print("login\n"+
                            "waiter;l;l\n");
                    cl_in.flush();
                    cl_out.readLine();      // Read the login id

                    int num_of_orders = 10;

                    // Create and edit num_of_orders orders
                    for(int i=0; i<num_of_orders; i++){
                        int num_of_products = 1+r.nextInt(Product.allProducts.size()-1);

                        // Create the new order and start editing it
                        cl_in.print("new_order\n" + (1 + (i%Table.allTables.size())) + "\n");
                        cl_in.flush();

                        int new_id = Integer.parseInt(cl_out.readLine());

                        cl_in.print("edit_order\n"+
                                new_id+"\n"+
                                num_of_products+"\n");

                        // Add num_of_products products, chosen randomly from allProducts
                        for(int j = 0;j<num_of_products;j++){
                            cl_in.print("1;"+Product.allProducts.get(r.nextInt(Product.allProducts.size())).getId()+"\n");
                        }
                        cl_in.flush();
                        cl_out.readLine();      // Read the ok

                        // Send the order
                        cl_in.print("send_order\n"+
                                new_id+"\n");
                        cl_in.flush();
                        cl_out.readLine();      // Read the ok

                        // Maybe send it again
                        while(r.nextBoolean()){
                            cl_in.print("send_order\n"+
                                    new_id+"\n");
                            cl_in.flush();
                            cl_out.readLine();      // Read the ok
                        }
                    }

                    // Close the connection
                    cl_in.println("exit");
                    cl_in.flush();

                    cl_in.close();
                    cl.close();
                    break;
                }

                case "connect":{
                    // Connect to server as a client (for testing)
                    // Connect to serverSocket
                    Socket cl = new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort());
                    BufferedReader cl_out = new BufferedReader(new InputStreamReader(cl.getInputStream()));
                    PrintWriter cl_in = new PrintWriter(cl.getOutputStream());

                    // Create new thread to read data from socket
                    new Thread(()->{
                        try{
                            String str = cl_out.readLine();
                            while(cl.isConnected() && str!=null){
                                System.out.println(str);
                                str = cl_out.readLine();
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

                    break;
                }

                case "exit":{
                    exitServer();
                    break;
                }
            }
        }catch(IOException e){
            e.printStackTrace();
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
