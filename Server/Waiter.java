import java.util.ArrayList;

public class Waiter extends Employee {
    static ArrayList<Waiter> allWaiters = new ArrayList<>();
	protected double cash = 0.0;
	protected double pos_charges = 0.0;
	protected double billing_account_charges = 0.0;

	public Waiter(long id, boolean is_new){
		super(id, is_new);

		allWaiters.add(this);
	}

	public static Waiter findBestForTable(Table t){
 		// TODO

	    return null;
	}

	public void setDevice(MobileDevice dev){
	    this.device = dev;
    }
	public void addCash(double d){
	    cash += d;
    }
    public void addPos(double d){
        pos_charges += d;
    }
    public void addBa(double d){
        billing_account_charges += d;
    }
    public void reset(){
        cash = 0.0;
        pos_charges = 0.0;
        billing_account_charges = 0.0;
    }
    public LatLng getLocation(){
	    // Contact the device and get its location
        // TODO

        return new LatLng(0, 0);
    }
}
