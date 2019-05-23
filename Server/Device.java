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

         // TODO

         return true;
     }

     public Employee getEmployee(){
         return employee;
     }

}
