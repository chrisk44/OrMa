import java.util.ArrayList;
import java.util.HashMap;

class PrepArea{
	private static ArrayList<PrepArea> allPrepAreas = new ArrayList<>();
	protected long id;
	protected LatLng lat_lng;
	protected HashMap<Product, Integer> product_stock;
	protected int floor;
	protected ArrayList<Order> orders = new ArrayList<>();
	protected ArrayList<Order> assigned_orders = new ArrayList<>();
	protected PrepAreaEmployee employee;

	PrepArea(long id, LatLng lat_lng, HashMap<Product, Integer> stock, int floor){
		this.id = id;
		this.lat_lng = lat_lng;
		this.product_stock = stock;
		this.floor = floor;

		allPrepAreas.add(this);
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
		
		// Send the new order to (PrepAreaDevice) (employee.getDevice())
	}
	static PrepArea getPrepAreaById(long id){
		// THIS IS A VERY BAD IMPLEMENTATION but I don't care

		for(PrepArea pa : allPrepAreas){
			if(pa.id == id)
				return pa;
		}

		return null;
	}
}
