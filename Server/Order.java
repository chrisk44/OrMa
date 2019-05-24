import java.util.ArrayList;
import java.util.List;

class Order{
	static ArrayList<Order> allOrders = new ArrayList<>();

	private long id;
	private Table table;
	private PrepArea assigned_prepArea;
	private ArrayList<Waiter> waiters = new ArrayList<>();
	ArrayList<Product> products = new ArrayList<>();
	ArrayList<Product> products_paid = new ArrayList<>();

	Order(long id, Table table){
		this.id = id;
		this.table = table;

		allOrders.add(this);
	}

	public String toString(){
		String str = "(Order: id=" + id + ", table=" + table.getId() + ", PA=" + (assigned_prepArea==null ? "null" : assigned_prepArea.getId()) + ", [ ";

		for(Product p : products){
			str += p.getId() + " ";
		}
		str += "], [ ";
		for(Product p : products_paid){
			str += p.getId() + " ";
		}

		return str + "])";
	}

	boolean onEdit(ArrayList<Product> products, ArrayList<Integer> actions){
		
		boolean flag = true;
		if(isAssigned()){
			// Order has been assigned. Ask the PrepArea if the change is allowed
			PrepAreaNotification n = new PrepAreaNotification(this.assigned_prepArea, this, products, actions);
			if(!n.show()){
				flag = false;
			}
		}
		
		if (flag){
			for(int i=0; i<products.size(); i++){
				if (actions.get(i) == 1){            // Add a product
					this.products.add(products.get(i));
				}else{                               // Remove a product
					this.products.remove(products.get(i));
				}
			}
			return true;
		}else{
			return false;
		}
	}

	void send(){
		PrepArea prepArea = PrepArea.findBestForOrder(this);
		if(prepArea==null){
			new Exception("Called send() without any PrepAreas in the system").printStackTrace();
			return;
		}
		assigned_prepArea = prepArea;
		prepArea.showNewOrder(this);
	}

	void assignOrder(PrepArea prepArea){
		assigned_prepArea = prepArea;
		prepArea.assigned_orders.add(this);
	}

	void addProduct(Product p){
		products.add(p);
	}
	void addWaiter(Waiter w){
		if(!waiters.contains(w))
			waiters.add(w);
	}

	void setReady(){

		ArrayList<Waiter> rejected = new ArrayList<>();

		while(true){

			Waiter w = Waiter.findBestForTable(this.table, rejected);
			if(w==null){
				new Exception("called setReady() without any Waiters logged in").printStackTrace();
				break;
			}

			// If the waiter accepted it, we are done, else try again
			if(w.notify( new OrderReadyNotification(this, this.assigned_prepArea, w)) )
				break;
			else
				rejected.add(w);

		}

		this.assigned_prepArea = null;
	}
	void setPaid(ArrayList<Product> products){
		products_paid.addAll(products);
	}
	void setPrepArea(PrepArea prepArea){
		this.assigned_prepArea = prepArea;
	}

	boolean isAssigned(){
		return this.assigned_prepArea != null;
	}
	long getId(){ return this.id; }						// TODO: Add to CD
	Table getTable(){ return this.table; }				// TODO: Add to CD
	double getBalance(){
		double balance = 0.0;

		for(Product p : products){
			if(!products_paid.contains(p)){
				balance += p.getPrice();
			}
		}

		return balance;
	}
	PrepArea getAssignedArea(){ return assigned_prepArea; }

	static Order getOrderById(long id){
		// THIS IS A VERY BAD IMPLEMENTATION but I don't care

		for(Order o : allOrders){
			if(o.id == id)
				return o;
		}

		return null;
	}
//	static List<Order> findBestCombination(List<Order> orders)		TODO: Remove from CD
}
