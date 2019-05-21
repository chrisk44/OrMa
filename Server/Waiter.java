import java.util.ArrayList;

public class Waiter extends Employee {
    static ArrayList<Waiter> allWaiters = new ArrayList<>();
	private double cash;
	private double pos_charges;
	private double billing_account_charges;
	public MobileDevice device;

	public Waiter(String username, String password, double hours, boolean is_new, double c, double pos_c, double bill_account, MobileDevice dev){
		super(username, password, hours, is_new);

		this.cash = c;
		this.pos_charges = pos_c;
		this.billing_account_charges = bill_account;
		this.device = dev;

		allWaiters.add(this);
	}

	public static Waiter findBestForTable(Table t){
 		// TODO

	    return null;
	}
	
	public boolean notify(Notification n){
		return n.show();
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
