public class PrepAreaEmployee extends Employee{
    public PrepAreaEmployee(long id, boolean is_new){
        super(id, is_new);
    }

    public void setDevice(PrepAreaDevice dev){
        this.device = dev;
    }
}
