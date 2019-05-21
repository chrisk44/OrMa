import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Main{
    static final int SERVER_PORT = 3000;

    static ServerSocket serverSocket;

    public static void main(String[] args){
        // Connect to database
        System.out.print("[INIT] Connecting to database...");
        // Let this be a piece of code that connects us to a database...

        // Load dummy data because our puny human linear perception of time along with 3 more projects doesn't allow for this kind of implementations
        // TODO: Because I am simply too lazy...
        System.out.println("OK");

        System.out.print("[INIT] Starting thread for manual input handling...");
        new Thread(() -> {
            BufferedReader sys_in = new BufferedReader(new InputStreamReader(System.in));

            try{
                String sys_req = sys_in.readLine();
                while(sys_req!=null){
                    serveSysRequest(sys_req);
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
        System.out.println();
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

            // Start new thread to handle the new connection
            new Thread(()->{

                try{
                    // Open in and out channels
                    PrintWriter sock_in = new PrintWriter(client.getOutputStream());
                    BufferedReader sock_out = new BufferedReader(new InputStreamReader(client.getInputStream()));

                    String req = sock_out.readLine();
                    while(req!=null && client.isConnected()){
                        if(serveRequest(req, client, sock_in, sock_out)) break;
                        req = sock_out.readLine();
                    }

                    sock_in.close();
                    sock_out.close();
                    client.close();

                }catch(IOException e){
                    e.printStackTrace();
                }

                System.out.println("[-] Connection " + client.getInetAddress().getHostAddress() + ":" + client.getPort() + " closed");

            }).start();
        }
    }

    static boolean serveRequest(String req, Socket client, PrintWriter sock_in, BufferedReader sock_out){
        // Serves the client request 'req'
        // Returns whether the caller should close the connection

        switch(req){
            case "login":
                try{
                    // Read login details: type;username;pass_hash
                    String data[] = sock_out.readLine().split(";");
                    switch(data[0]){
                        case "waiter":{
                            String username = data[1];
                            String pass_hash = data[2];

                            // Assume we verify this with the database
                            Waiter w = new Waiter(username, pass_hash, 0, false, 0, 0, 0);      // It's added to allWaiters by the constructor
                            w.setDevice(new MobileDevice(w, client.getInetAddress()));
                            break;
                        }

                        case "pr":{
                            String username = data[1];
                            String pass_hash = data[2];

                            // Assume we verify this with the database
                            PR pr = new PR(username, pass_hash, 0, false);                      // It's added to allPRs by the constructor
                            pr.setDevice(new MobileDevice(pr, client.getInetAddress()));
                            break;
                        }

                        default:
                            System.out.println("[E] Received unknown login type: " + data[0]);
                            return true;
                    }

                }catch(IOException e){
                    e.printStackTrace();
                    return true;
                }
                break;

            default:
                sock_in.println("STATUS 499 Disappointed: The server has received your request but thinks you can do better.");
                return true;

        }

        return false;
    }
    static void serveSysRequest(String req){
        // Serve commands from CLI

        switch(req){
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

    static void showSuccess(String message){
        System.out.println("showSuccess(): " + message);
    }
    static void showFailure(String message){
        System.out.println("showFailure(): " + message);
    }
}
