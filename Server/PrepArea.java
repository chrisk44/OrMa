import java.util.ArrayList;
import java.util.HashMap;

class PrepArea{

	protected LatLng lat_lng;
	protected HashMap<Product, Integer> product_stock;					// Change type in CD
	protected int floor;
	protected ArrayList<Order> orders = new ArrayList<>();
	protected ArrayList<Order> assigned_orders = new ArrayList<>();
	protected ArrayList<Product> product_list = new ArrayList<>();
	protected PrepAreaEmployee employee;

	PrepArea(LatLng lat_lng, HashMap<Product, Integer> stock, int floor){
		this.lat_lng = lat_lng;
		this.product_stock = stock;
		this.floor = floor;
	}

	static PrepArea findBestForOrder(Order o){
		// TODO

		return null;
	}

	PrepAreaEmployee getEmployee(){
		return employee;
	}
	
	void showNewOrder(Order o){
		orders.add(o);
		
		//Show new order to employee
	}
}
