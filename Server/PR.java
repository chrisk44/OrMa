import java.util.ArrayList;

public class PR extends Employee {
    static ArrayList<PR> allPRs = new ArrayList<>();            // TODO: Add to CD
    private MobileDevice device;

    public PR(boolean is_new){
        super(is_new);

        allPRs.add(this);
    }

    public void setDevice(MobileDevice dev){                    // TODO: Add to CD
        this.device = dev;
    }
}
