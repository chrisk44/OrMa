import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

	ArrayList<Order> findBestCombination(){		// TODO: Add to CD
		/*
		 * This will involve some kind of dark magic that I'm currently too busy to come up with
		 * Just returns the first 2 orders that have been sent to this prepArea
		 */

		ArrayList<Order> result = new ArrayList<>();

		System.out.println("[ :( ] Not yet implemented, returning the first 2 orders of the PrepArea");
		for(int i = 0;i<Math.min(orders.size(), 3);i++){
			result.add(orders.get(i));
		}

		return result;
	}

	ArrayList<Order> getOrders(){ return this.orders; }						// TODO: Add to CD
	ArrayList<Order> getAssignedOrders(){ return this.assigned_orders; }    // TODO: Add to CD
	void clearAssignedOrders(){
		orders.removeAll(assigned_orders);
		assigned_orders.clear();
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
	static PrepArea findBestForOrder(Order o){
		/*
		 * This will involve some kind of dark magic that I'm currently too busy to come up with
		 * Just returns the closest prepArea
		 */

		LatLng tLocation = o.getTable().getLocation();

		PrepArea best = allPrepAreas.get(0);
		double min_dist = tLocation.dist(best.lat_lng);

		double tmp;
		for(PrepArea pa : allPrepAreas){
			tmp = tLocation.dist(pa.lat_lng);
			if(tmp < min_dist){
				min_dist = tmp;
				best = pa;
			}
		}

		return best;
	}
}
