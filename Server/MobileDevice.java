import java.net.InetAddress;
import java.util.ArrayList;

public class MobileDevice extends Device {
    private ArrayList<Notification> notification_list = new ArrayList<>();

    MobileDevice(Employee empl, InetAddress ip_addr){
        super(empl, ip_addr);
    }
}
