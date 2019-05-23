import java.net.InetAddress;

public class TableButton extends Device {
      private Table table;
      
      public TableButton(InetAddress ip_addr, Table t){
          super(null, ip_addr);
          this.table = t; 
      }
}
