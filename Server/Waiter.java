import java.util.ArrayList;

public class Waiter extends Employee {
    static ArrayList<Waiter> allWaiters = new ArrayList<>();
	private double cash = 0.0;
	private double pos_charges = 0.0;
	private double billing_account_charges = 0.0;
	public MobileDevice device;

	public Waiter(boolean is_new){
		super(is_new);

		allWaiters.add(this);
	}

	public static Waiter findBestForTable(Table t){
 		// TODO

	    return null;
	}

	public void setDevice(MobileDevice dev){                    // TODO: Add to CD
	    this.device = dev;
    }
	public boolean notify(Notification n){
		return n.show();
	}
	public void addCash(double d){                              // TODO: Add to CD
	    cash += d;
    }
    public void addPos(double d){                               // TODO: Add to CD
        pos_charges += d;
    }
    public void addBa(double d){                                // TODO: Add to CD
        billing_account_charges += d;
    }
    public void reset(){                                        // TODO: Add to CD
        cash = 0.0;
        pos_charges = 0.0;
        billing_account_charges = 0.0;
    }

	public boolean editData(Bundle new_info){
		if (validateData(new_info)){
			Main.showSuccess("Success");
			return true;
		}else{
            Main.showFailure("Failure");
			return false;
		}
	}

	public boolean validateData(Bundle new_info){
		if (new_info.getBoolean("is_new")){
			return pos_charges == 0 && billing_account_charges == 0;
		}

		// Other checks that return false on failure...

		return true;
	}

}
