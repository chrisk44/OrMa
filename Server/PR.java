public class PR extends Employee {
      private MobileDevice device;

      public PR(String username, String password, double hours, boolean is_new, MobileDevice dev){
            super(username, password, hours, is_new);
            this.device = dev;
      }
}
