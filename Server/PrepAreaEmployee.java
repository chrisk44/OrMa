public class PrepAreaEmployee extends Employee{
    protected PrepAreaDevice device;

    public PrepAreaEmployee(boolean is_new){
        super(is_new);
    }

    public void setDevice(PrepAreaDevice dev){                    // TODO: Add to CD
        this.device = dev;
    }
}
