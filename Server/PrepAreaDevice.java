import java.net.InetAddress;
import java.util.ArrayList;

public class PrepAreaDevice extends Device {

      private ArrayList<Order> order_list = new ArrayList<>();
      private ArrayList<Notification> notification_list = new ArrayList<>();

      PrepAreaDevice(Employee empl, InetAddress ip_addr){
            super(empl, ip_addr);
      }
}
