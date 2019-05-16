class Order{

	private long id;
	private double balance;
	private Date create_time;
	private Date delivery_time;
	private Date pay_time;
	private Table table;
	private ArrayList<Product> products = new ArrayList();
	private ArrayList<Boolean> products_paid = new ArrayList();
	private ArrayList<Waiter> waiters = new ArrayList();
	private PrepArea assigned_prepArea;
	public static ArrayList<Order> allorders = new ArrayList();

	Order(long id, double balance, Date create, Date delivery, Date pay, Table table, PrepArea prepArea){
		this.id = id;
		this.balance = balance;
		this.create_time = create;
		this.delivery_time = delivery;
		this.pay_time = pay;
		this.table = table;
		this.assigned_prepArea = prepArea;
	}

	void onEdit(List<Product> p, List<int> action){
		
		boolean flag = true;
		if(isAssigned()){
			PrepAreaNotification n = new PrepAreaNotification(this.assigned_prepArea, this);
			if(n.show() == false){
				flag = false;
			}
		}
		
		if (flag == true){
			for(int i=0; i<p.size(); i++){
				if (action.get(i) == 1){   //add a product
					this.products.add(p.get(i));
				}else{   //remove a product
					this.products.remove(p.get(i));
				}
			}
			send();
			showSuccess("Successful update");
		}else{
			showFailure("Update failed");
		}
	}

	void send(){
		PrepArea prepArea = PrepArea.findBestForOrder(this);
		prepArea.showNewOrder(this);
	}

	boolean isAssigned(){
		if (this.assigned_prepArea == null){
			return false;
		}
		else{
			return true;
		}
	}

	void assignOrder(PrepArea prepArea, Order o){
		assigned_prepArea = prepArea;
		prepArea.assigned_orders.add(o);
	}

	void setReady(){
		Waiter w;
		do{
			w = Waiter.findBestForTable(this.table);	// estw oti ka8e fora epistrefei allon Waiter
			OrderReadyNotification n = new OrderReadyNotification(this, this.assigned_prepArea, w);
		}until(w.notify(n) == true);
	}
	
	void setPaid(List<Product> products){
		for(Product p : products){
			products_paid.add(p);
			this.balance -= p.price;
		}
	}

	void onRejected(){
		send();
	}

	static List<Order> findBestCombination(List<Order> orders){
		return List<Order>;
	}

	boolean hasKnownDevice(){

	}

	void addProduct(Product p){
		products.add(p);
	}
		
	double getBalance(){
		return balance;
	}
}
