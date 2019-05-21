import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Session{
    static final String RES_OK = "OK", RES_FAIL = "FAIL";
    static final String REQ_LOGIN = "login";
    private Socket socket;
    private PrintWriter sock_in;
    private BufferedReader sock_out;
    private boolean valid = false;
    private Device device;

    Session(Socket sock){
        this.socket = sock;

        try{
            // Open in and out channels
            sock_in = new PrintWriter(sock.getOutputStream());
            sock_out = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            valid = true;
        }catch(IOException e){
            e.printStackTrace();
            sock_in = null;
            sock_out = null;
        }
    }

    void start(){
        if(!valid){
            System.out.println("[E] Tried to start an invalid session");
            return;
        }

        new Thread(()->{

            try{
                String req = sock_out.readLine();
                while(req!=null && socket.isConnected()){
                    if(serveRequest(req)) break;
                    req = sock_out.readLine();
                }

            }catch(IOException e){
                e.printStackTrace();
            }

            close();

        }).start();
    }
    void send(String str){
        sock_in.println(str);
        sock_in.flush();
    }

    void close(){
        System.out.println("[-] Closing connection " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
        try{
            sock_in.close();
            sock_out.close();
            socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    boolean serveRequest(String req){
        // Serves the client request 'req'
        // Returns whether the caller should close the connection

        switch(req){
            case REQ_LOGIN:
                try{
                    // Read login details: type;username;pass_hash
                    String data[] = sock_out.readLine().split(";");
                    if(data.length != 3){
                        return true;
                    }
                    switch(data[0]){
                        case "waiter":{
                            String username = data[1];
                            String pass_hash = data[2];
                            // Assume we verify this with the database, also get is_new

                            Waiter w = new Waiter(false);      // It's added to allWaiters by the constructor
                            device = new MobileDevice(w, socket.getInetAddress());
                            w.setDevice((MobileDevice)device);
                            send(RES_OK);
                            break;
                        }

                        case "pr":{
                            String username = data[1];
                            String pass_hash = data[2];
                            // Assume we verify this with the database, also get is_new

                            PR pr = new PR(false);                      // It's added to allPRs by the constructor
                            device = new MobileDevice(pr, socket.getInetAddress());
                            pr.setDevice((MobileDevice)device);
                            send(RES_OK);
                            break;
                        }

                        case "prepArea":{
                            String username = data[1];
                            String pass_hash = data[2];
                            // Assume we verify this with the database, also get is_new

                            PrepAreaEmployee pae = new PrepAreaEmployee(false);
                            device = new PrepAreaDevice(pae, socket.getInetAddress());
                            pae.setDevice((PrepAreaDevice)device);
                            send(RES_OK);
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
}
