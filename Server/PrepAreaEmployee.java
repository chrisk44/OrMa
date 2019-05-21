public class PrepAreaEmployee extends Employee{
    protected PrepAreaDevice device;

    public PrepAreaEmployee(String username, String password, double hours, boolean is_new, PrepAreaDevice dev){
        super(username, password, hours, is_new);
        this.device = dev;
    }

}
