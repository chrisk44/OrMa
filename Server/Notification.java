import java.util.ArrayList;
import java.util.Date;

abstract class Notification{
	private static long last_id = 0;
	protected long id;
	protected Date timestamp;
	protected boolean needs_response = true;
	
	Notification(){
		this.id = getNewId();
		this.timestamp = new Date();
	}
	synchronized private static long getNewId(){
		// Returns a new id
		return last_id++;
	}
	boolean show(){
		//Send notification to device
		boolean response = getDevice().dispatch(this);
		
		if(needs_response){
			if(response){
				accept();
				return true;
			}else{
				reject();
				return false;
			}
		}

		return true;
	}
	
	void accept(){}
	void reject(){}
	abstract Device getDevice();
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
		this.receiver = receiver;
		this.text = text;
		this.needs_response = false;
	}
	
	Device getDevice(){ return receiver.getDevice(); }
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
	
	Device getDevice(){ return receiver.getDevice(); }
}

class TableCallNotification extends Notification{
	private Table table;
	private Waiter receiver;
	
	TableCallNotification(Table table, Waiter waiter){
		super();
		
		this.table = table;
		this.receiver = waiter;
	}
	
	Device getDevice(){ return receiver.getDevice(); }
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
	}
	
	void accept(){
		table.setTaken();
		wg.remove();
	}
	void reject(){
		wg.notifyWhenAvailable();
	}
	
	Device getDevice(){ return receiver.getDevice(); }
}

class PrepAreaNotification extends Notification{
	private PrepArea prepArea;
	private Order order;
	private ArrayList<Product> products;
	private ArrayList<Integer> actions;

	PrepAreaNotification(PrepArea prepArea, Order order, ArrayList<Product> products, ArrayList<Integer> actions){
		super();
		
		this.prepArea = prepArea;
		this.order = order;
		this.products = products;
		this.actions = actions;
	}
	
	Device getDevice(){ return prepArea.employee.getDevice(); }
}

class TopologyChangeNotification extends Notification{
	private Employee employee;
	
	TopologyChangeNotification(Employee employee){
		super();
		
		this.employee = employee;
		this.needs_response = false;
	}
	
	Device getDevice(){ return employee.getDevice(); }
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
	
	Device getDevice(){ return receiver.getDevice(); }
}


