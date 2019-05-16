public class Device {
    private Employee employee;
    private InetAddress ip_addr;
    
     public Device(Employee empl, InetAddress ip_addr) 
     {
           this.employee = empl;
           this.ip_addr = ip_addr;
     }
    
     public boolean dispatch(Notification n)
     {
          //apostolh tou notification sth suskeuh kai anakthsh ths apanthshs (an uparxei, dld to n.needs_response einai true)
     }

}
