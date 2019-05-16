class PrepArea{

	protected LatLng lat_lng;
	protected Map<Product, int> product_stock;
	protected int floor;
	protected ArrayList<Order> orders = new ArrayList();
	protected ArrayList<Order> assigned_orders = new ArrayList();
	protected ArrayList<Product> product_list = new ArrayList();
	protected PrepAreaEmployee employee;

	PrepArea(LatLng lat_lng, Map<Product, int> stock, int floor){
		this.lat_lng = lat_lng;
		this.product_stock = stock;
		this.floor = floor;
		}

	static PrepArea findBestForOrder(Order o){
		PrepArea preparea;


		return preparea;
	}

	PrepAreaEmployee getEmployee(){
		return employee;
	}
	
	void showNewOrder(Order o){
		orders.add(o);
		
		//Show new order to employee
	}
}
