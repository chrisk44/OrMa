import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public abstract class Device {
    private Employee employee;
    private InetAddress ip_addr;

    public Device(Employee empl, InetAddress ip_addr){
        this.employee = empl;
        this.ip_addr = ip_addr;
    }

    public boolean dispatch(Notification n){
        // Send n to the device
        // if (n.needs_response) then get response and return it

        // Dummy Implementation
        boolean resp = Main.r.nextBoolean();
        System.out.println("[D] Sending notification to device " + n.getDevice().ip_addr.getHostAddress() + "... (waiting 10 seconds to return " + resp + ")");
        try{
            Thread.sleep(10000);
        }catch(InterruptedException ignored){}

        return resp;

        // TODO: Uncomment the real implementation
//        try{
//            Socket socket = new Socket(ip_addr, Main.MOBILE_PORT);
//            BufferedReader sock_out = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            PrintWriter sock_in = new PrintWriter(socket.getOutputStream());
//
//            // sock_in.print(some kind of authorization token);
//            sock_in.print("notification");
//            sock_in.print(n.toDeviceString());
//            sock_in.flush();
//
//            String response = sock_out.readLine();
//
//            if(response==null) return false;
//            if(!response.equals("OK")){
//                return false;
//            }
//
//            if(n.needs_response){
//                return Boolean.parseBoolean(sock_out.readLine());
//            }
//        }catch(IOException e){
//            e.printStackTrace();
//            return false;
//        }
//
//        return true;
    }

    public Employee getEmployee(){
        return employee;
    }

}
