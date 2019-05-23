import java.util.ArrayList;

abstract class Employee{
	private static ArrayList<Employee> allEmployees = new ArrayList<>();
	protected long id;
	protected Device device;
	protected boolean is_new;

	Employee(long id, boolean is_new){
		this.id = id;
		this.is_new = is_new;

		allEmployees.add(this);
	}

	Device getDevice(){
		return device;
	}
	
	boolean isNew(){
		return this.is_new;
	}
	public boolean notify(Notification n){
		return n.show();
	}

	public boolean editData(Bundle new_info){
		if(!validateData(new_info)){
			return false;
		}

		// Save data to database

		return true;
	}

	public boolean validateData(Bundle new_info){
		if(new_info.getBoolean("is_new") && this instanceof Waiter){
			return ((Waiter)this).pos_charges == 0 && ((Waiter)this).billing_account_charges == 0;
		}

		// Other checks that return false on failure...

		return true;
	}


	static Employee getEmployeeById(long id){
		// THIS IS A VERY BAD IMPLEMENTATION but I don't care

		for(Employee e : allEmployees){
			if(e.id == id)
				return e;
		}
		return null;
	}
}

	
