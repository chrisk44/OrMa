import java.net.InetAddress;

public class TableButton extends Device {
      private Table table;
      
      public TableButton(Employee empl, InetAddress ip_addr, Table t){
          super(empl, ip_addr);
          this.table = t; 
      }

      public void onPressed(){          // TODO: Add to CD, change SD_1
          table.onCall();
      }
}
