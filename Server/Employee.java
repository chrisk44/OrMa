abstract class Employee{
	// TODO: Remove vars from CD
	protected Device device;
	protected boolean is_new;			// TODO: Change public->protected in CD

	Employee(boolean is_new){
		this.is_new = is_new;
	}

	Device getDevice(){
		return device;
	}
	
	boolean isNew(){
		return this.is_new;
	}
	
}

	
