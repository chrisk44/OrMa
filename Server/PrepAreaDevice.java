import java.net.InetAddress;
import java.util.ArrayList;

public class PrepAreaDevice extends Device {
      private PrepArea prepArea;
      private ArrayList<Order> order_list = new ArrayList<>();
      private ArrayList<Notification> notification_list = new ArrayList<>();

      PrepAreaDevice(Employee empl, PrepArea prepArea, InetAddress ip_addr){
            super(empl, ip_addr);
            this.prepArea = prepArea;
      }

      PrepArea getPrepArea(){ return this.prepArea; }
}
