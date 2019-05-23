import java.util.ArrayList;

public class PR extends Employee {
    static ArrayList<PR> allPRs = new ArrayList<>();

    public PR(long id, boolean is_new){
        super(id, is_new);

        allPRs.add(this);
    }

    public void setDevice(MobileDevice dev){
        this.device = dev;
    }
}
