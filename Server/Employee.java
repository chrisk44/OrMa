abstract class Employee{
	protected String username;
	protected String password;
	protected double hours_worked;
	protected Device device;
	protected boolean is_new;			// TODO: Change public->protected in CD

	Employee(String username, String password, double hours, boolean is_new){
		this.username = username;
		this.password = password;
		this.hours_worked = hours;
		this.is_new = is_new;
	}

	Device getDevice(){
		return device;
	}
	
	boolean isNew(){
		return this.is_new;
	}
	
}

	
