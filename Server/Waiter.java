public class Waiter extends Employee {
	private double cash;
	private double pos_charges;
	private double billing_account_charges;
	public MobileDevice device;

	public Waiter(double c, double pos_c, double bill_account, MobileDevice dev)
	{
		this.cash = c;
		this.pos_charges = pos_c;
		this.billing_account_charges = bill_account;
		this.device = dev;
		
		
	}

	public static Waiter findBestForTable(Table t)
	{
 		
	}
	
	public boolean notify(Notification n)
	{
		boolean res = n.show();
		return res;
	}

	public boolean isNew()
	{
		if (this.is_new == true)
		{
			return true;
			
		}
		else
		{
			return false;
		}
	}

	public boolean editData(Bundle new_info)
	{
		if (validateData(new_info))
		{
			showSuccess();
			return true;
		}
		else
		{
			showFailure();
			return false;
		}

	}

	public boolean validateData(Bundle new_info)
	{
		if (new_info getBoolean("is_new") == true)
		{
			if (pos_charges == 0 && billing_account_charges == 0) return true;
			else return false;
		}
	}

}
