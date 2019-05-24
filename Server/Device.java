import java.net.InetAddress;

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
         System.out.println("[Notification] Sending notification to device " + n.getDevice().ip_addr.getHostAddress() + "... (waiting 5 seconds to return " + resp + ")");
         try{
             Thread.sleep(5000);
         }catch(InterruptedException ignored){}

         // TODO

         return resp;
     }

     public Employee getEmployee(){
         return employee;
     }

}
