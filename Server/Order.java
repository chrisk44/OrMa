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
			send();
			return true;
		}else{
			return false;
		}
	}

	void send(){
		PrepArea prepArea = PrepArea.findBestForOrder(this);
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
		Waiter w;
		OrderReadyNotification n;
		do{
			w = Waiter.findBestForTable(this.table);	// TODO: Estw oti ka8e fora epistrefei allon Waiter
			n = new OrderReadyNotification(this, this.assigned_prepArea, w);
		}while(w.notify(n));

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

	double getBalance(){
		double balance = 0.0;

		for(Product p : products){
			if(!products_paid.contains(p)){
				balance += p.price;
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
	static List<Order> findBestCombination(List<Order> orders){
		ArrayList<Order> result = new ArrayList<>();

		return result;
	}
}
