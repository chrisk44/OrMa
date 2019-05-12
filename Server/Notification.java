abstract class Notification{
	private static last_id = 0;
	protected long id;
	protected Date timestamp;
	protected boolean needs_response = true;
	
	Notification(){
		this.id = getId();
		this.timestamp = new Date();
	}
	synchronized private static getNewId(){
		// Returns a new id
		return last_id++;
	}
	boolean show(){
		//Send notification to device
		boolean response = device.dispatch(this);
		
		if(needs_response){
			if(response){
				accept();
				return true;
			}else{
				reject();
				return false;
			}
		}
	}
	
	void accept(){}
	void reject(){}
	Device getDevice();
}

class OrderProblemNotification extends Notification{
	private Order order;
	private PrepArea sender;
	private Waiter receiver;
	private String text;
	
	OrderProblemNotification(Order order, PrepArea sender, Waiter receiver, String text){
		super();
		
		this.order = order;
		this.sender = sender;
		this.device = receiver.getDevice();
		this.receiver = receiver;
		this.text = text;
		this.needs_response = false;
	}
	
	Device getDevice(){ return this.receiver.getDevice(); }
}

class OrderReadyNotification extends Notification{
	private Order order;
	private PrepArea sender;
	private Waiter receiver;
	
	OrderReadyNotification(Order order, PrepArea sender, Waiter receiver){
		super();
		
		this.order = order;
		this.sender = sender;
		this.receiver = receiver;
	}
	
	Device getDevice(){ return this.receiver.getDevice(); }
}

class TableCallNotification extends Notification{
	private Table table;
	private Waiter receiver;
	
	TableCallNotification(Table table, Waiter waiter){
		super();
		
		this.table = table;
		this.receiver = receiver;
		this.device = receiver.getDevice();
	}
	
	Device getDevice(){ return this.receiver.getDevice(); }
}

class TableFreeNotification extends Notification{
	private Table table;
	private PR receiver;
	private WaitingGroup wg;
	
	TableFreeNotification(Table table, PR receiver, WaitingGroup wg){
		super();
		
		this.table = table;
		this.receiver = receiver;
		this.wg = wg;
		this.device = receiver.getDevice();
	}
	
	void accept(){
		table.setTaken();
		wg.remove();
	}
	void reject(){
		wg.notifyWhenAvailable();
	}
	
	Device getDevice(){ return this.receiver.getDevice(); }
}

class PrepAreaNotification extends Notification{
	private PrepArea prepArea;
	private Order order;
	
	PrepAreaNotification(PrepArea prepArea, Order order){
		super();
		
		this.prepArea = prepArea;
		this.order = order;
		this.device = prepArea.getEmployee().getDevice();
	}
	
	Device getDevice(){ return this.PrepArea.employee.getDevice(); }
}

class TopologyChangeNotification extends Notification{
	private Employee employee;
	
	TopologyChangeNotification(Employee employee){
		super();
		
		this.employee = employee;
		this.device = employee.getDevice();
		this.needs_response = false;
	}
	
	Device getDevice(){ return this.employee.getDevice(); }
}

class ProductPriceNotification extends Notification{
	Product product;
	Waiter receiver;
	
	ProductPriceNotification(Product product, Waiter receiver){
		super();
		
		this.product = product;
		this.receiver = receiver;
		this.needs_response = false;
	}
	
	Device getDevice(){ return this.receiver.getDevice(); }
}


