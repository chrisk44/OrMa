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

	public static Waiter findBestForTable(Table t, ArrayList<Waiter> rejected){
	    // Try not to return anyone included in rejected list

        /*
         * This will involve some kind of dark magic that I'm currently too busy to come up with
         * Just returns the closest waiter that isn't on the rejected list (if everyone is on the list, returns the first waiter)
         */

        LatLng tLocation = t.getLocation();

        Waiter best = allWaiters.get(0);
        double min_dist = tLocation.dist(best.getLocation());

        double tmp;
        for(Waiter w : allWaiters){
            tmp = tLocation.dist(w.getLocation());
            if(tmp < min_dist && !rejected.contains(w)){
                min_dist = tmp;
                best = w;
            }
        }

        return best;
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

        // TODO: Retrieve location from device

        return new LatLng(Main.r.nextDouble(), Main.r.nextDouble());
    }
}
