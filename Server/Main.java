import java.io.*;
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
            new Session(client).start();
        }
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
