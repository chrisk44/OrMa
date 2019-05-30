import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class PrepArea{
	static ArrayList<PrepArea> allPrepAreas = new ArrayList<>();
	protected long id;
	protected LatLng lat_lng;
	protected HashMap<Product, Integer> product_stock;
	protected int floor;
	protected ArrayList<Order> orders = new ArrayList<>();
	protected ArrayList<Order> assigned_orders = new ArrayList<>();
	protected ArrayList<Order> rejected_orders = new ArrayList<>();
	protected PrepAreaEmployee employee;

	PrepArea(long id, LatLng lat_lng, HashMap<Product, Integer> stock, int floor){
		this.id = id;
		this.lat_lng = lat_lng;
		this.product_stock = stock;
		this.floor = floor;

		allPrepAreas.add(this);
	}

    public String toString(){
	    String str = "(PrepArea: id=" + id + ", Location=" + lat_lng.toString() + ", [ ";

	    for(Order o : orders){
	        str += o.getId() + " ";
        }
	    str += "], [ ";
	    for(Order o : assigned_orders){
	        str += o.getId() + " ";
        }
	    str += "], [ ";
	    for(Order o : rejected_orders){
	        str += o.getId() + " ";
        }

	    return str + "])";
    }

	ArrayList<Order> findBestCombination(){
		/*
		 * This will involve some kind of dark magic that I'm currently too busy to come up with
		 * Just returns the first 2 orders that have been sent to this prepArea
		 */

		ArrayList<Order> result = new ArrayList<>();

		// If it already has assigned orders, return none (UI won't allow this)
		if(!this.assigned_orders.isEmpty()){
			return result;
		}

		for(int i = 0;i<Math.min(orders.size(), 3);i++){
			result.add(orders.get(i));
		}

		return result;
	}

    long getId(){ return this.id; }
	ArrayList<Order> getOrders(){ return this.orders; }
	ArrayList<Order> getAssignedOrders(){ return this.assigned_orders; }
    PrepAreaEmployee getEmployee(){
        return employee;
    }

	void clearAssignedOrders(){
		orders.removeAll(assigned_orders);
		assigned_orders.clear();
	}
	void onOrderReject(Order o){
	    rejected_orders.add(o);
	    orders.remove(o);
	    assigned_orders.remove(o);
    }
	
	void showNewOrder(Order o){
		orders.add(o);
		System.out.println("[D] Sent order " + o.getId() + " to " + this.toString());
		
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
		 * Just returns the closest prepArea that hasn't rejected this order
		 */

		LatLng tLocation = o.getTable().getLocation();

		PrepArea best = allPrepAreas.get(0);
		double min_dist = tLocation.dist(best.lat_lng);

		double tmp;
		for(PrepArea pa : allPrepAreas){
			tmp = tLocation.dist(pa.lat_lng);
			if(tmp < min_dist && !pa.rejected_orders.contains(o)){
				min_dist = tmp;
				best = pa;
			}
		}

		return best;
	}
}
