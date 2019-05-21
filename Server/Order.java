import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class Order{
	static ArrayList<Order> allOrders = new ArrayList<>();          // TODO: Add to CD

	private long id;
	private double balance;
	private Date create_time;
	private Date delivery_time;
	private Date pay_time;
	private Table table;
	ArrayList<Product> products = new ArrayList<>();				// TODO: Change private->public in CD
	private ArrayList<Boolean> products_paid = new ArrayList<>();
	private ArrayList<Waiter> waiters = new ArrayList<>();
	private PrepArea assigned_prepArea;

	Order(long id, double balance, Date create, Date delivery, Date pay, Table table, PrepArea prepArea){
		this.id = id;
		this.balance = balance;
		this.create_time = create;
		this.delivery_time = delivery;
		this.pay_time = pay;
		this.table = table;
		this.assigned_prepArea = prepArea;

		allOrders.add(this);
	}

	void onEdit(List<Product> p, List<Integer> action){
		
		boolean flag = true;
		if(isAssigned()){
			PrepAreaNotification n = new PrepAreaNotification(this.assigned_prepArea, this);
			if(!n.show()){
				flag = false;
			}
		}
		
		if (flag){
			for(int i=0; i<p.size(); i++){
				if (action.get(i) == 1){            // Add a product
					this.products.add(p.get(i));
				}else{                              // Remove a product
					this.products.remove(p.get(i));
				}
			}
			send();
			Main.showSuccess("Successful update");
		}else{
			Main.showFailure("Update failed");
		}
	}

	void send(){
		PrepArea prepArea = PrepArea.findBestForOrder(this);
		prepArea.showNewOrder(this);
	}

	boolean isAssigned(){
		return this.assigned_prepArea != null;
	}

	void assignOrder(PrepArea prepArea, Order o){
		assigned_prepArea = prepArea;
		prepArea.assigned_orders.add(o);
	}

	void setReady(){
		Waiter w;
		OrderReadyNotification n;
		do{
			w = Waiter.findBestForTable(this.table);	// TODO: Estw oti ka8e fora epistrefei allon Waiter
			n = new OrderReadyNotification(this, this.assigned_prepArea, w);
		}while(w.notify(n));
	}
	
	void setPaid(ArrayList<Product> products){
		for(int i=0; i<products.size(); i++){
			products_paid.set(i, true);
			this.balance -= products.get(i).price;
		}
	}

	void onRejected(){
		send();
	}

	static List<Order> findBestCombination(List<Order> orders){
		ArrayList<Order> result = new ArrayList<>();

		return result;
	}

	boolean hasKnownDevice(){
		// TODO ?
		return false;
	}

	void addProduct(Product p){
		products.add(p);
		products_paid.add(false);
		balance += p.price;
	}
		
	double getBalance(){
		return balance;
	}
}
