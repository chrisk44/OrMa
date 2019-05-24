import java.util.ArrayList;

public class Table {
	static long last_id = 0;								// For debugging, no need to add in CD
	static ArrayList<Table> allTables = new ArrayList<>();
	private long id;
	private LatLng lat_lng;
	private int seats;
	private int floor;
	private Order order;
	public enum Status { FREE, RESERVED, PAID, CALLING, TO_CLEAN, NORMAL, TAKEN }
	private Status status = Status.FREE;

	Table(long id, LatLng lat_lng, int seats, int floor){
		this.id = id;
		this.lat_lng = lat_lng;
		this.seats = seats;
		this.floor = floor;

		allTables.add(this);

		if(id == -1){
			this.id = ++last_id;
		}
	}

	public String toString(){
		return "(Table: id=" + id + ", Location=" + lat_lng.toString() + ", Seats=" + seats + ", order=" + (order==null ? "null" : order.getId()) + ")";
	}
	
	public void onCall(){
		// Called when the table's TableButton is pressed

		new Thread(() -> {

			// Find the most suitable Waiter
			ArrayList<Waiter> rejected = new ArrayList<>();

			while(true){

				Waiter w = Waiter.findBestForTable(this, rejected);
				if(w==null){
					new Exception("onCall: Waiter.findBestForTable returned null").printStackTrace();
					break;
				}

				// If the waiter accepted it, we are done, else try again
				if(w.notify( new TableCallNotification(this, w)) ){
					break;
				}else{
					rejected.add(w);
				}

			}


		}).start();
	}
	public void onOrderPaid(ArrayList<Product> products){
		// Called when a Waiter sets some products as paid

		this.order.setPaid(products);
	}
	public static Table findFreeTable(WaitingGroup wg){
		// Find a free table in which this WaitingGroup can fit
		for(Table t : allTables){
			if(t.isAvailable() && t.seats<=wg.getNumOfPeople()){
				return t;
			}
		}

		return null;
	}


	public void setOrder(Order order){				// TODO: Add to CD
		if(getBalance() > 0.0){
			System.out.println("[E:Table] There is a pending order, can't set a new one");
			return;
		}

		this.order = order;
	}
	public void setTaken(){
		this.status = Status.TAKEN;
	}
	public void setReserved(){
		this.status = Status.RESERVED;
	}
	public boolean isAvailable(){
		return this.status == Status.FREE;
	}


	public static boolean onTopologyEdit(Bundle new_info){
		if(validateData(new_info)){
			for(Waiter w : Waiter.allWaiters){
				TopologyChangeNotification n = new TopologyChangeNotification( w );
				w.notify(n);
			}
			for(PR pr : PR.allPRs){
				TopologyChangeNotification n = new TopologyChangeNotification( pr );
				pr.notify(n);
			}

			// Save new topology in database
			return true;
		}else{
			return false;
		}
	}
	public static boolean validateData(Bundle new_info){
		//elegxei tin egkurotita tis allagis stin topologia

		return true;
	}

	long getClient(){
		// TODO ?
		return -1;
	}
	Order getOrder(){ return this.order; }			// TODO: Add to CD
	long getId(){ return this.id; }					// TODO: Add to CD
	double getBalance(){
		return this.order==null ? 0.0 : order.getBalance();
	}
	LatLng getLocation(){ return this.lat_lng; }	// TODO: Add to CD

	public static Table getTableById(long id){
		// THIS IS A VERY BAD IMPLEMENTATION but I don't care

		for(Table t : allTables){
			if(t.id == id)
				return t;
		}

		return null;
	}

}
