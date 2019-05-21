import java.util.ArrayList;

public class PR extends Employee {
    static ArrayList<PR> allPRs = new ArrayList<>();
    private MobileDevice device;

    public PR(String username, String password, double hours, boolean is_new){
        super(username, password, hours, is_new);

        allPRs.add(this);
    }

    public void setDevice(MobileDevice dev){                    // TODO: Add to CD
        this.device = dev;
    }
}
