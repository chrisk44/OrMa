import java.util.ArrayList;

public class Table {
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
	}
	
	
	public void onCall(){
		// Called when the table's TableButton is pressed

		// TODO: Maybe in a different thread ??
		// TODO: This is a loop
		// Find the most suitable Waiter
		Waiter w = Waiter.findBestForTable(this);
		w.notify(new TableCallNotification(this, w));
	}
	public void onOrderPaid(ArrayList<Product> products){
		// Called when a Waiter sets some products as paid

		this.order.setPaid(products);
	}
	public static Table findFreeTable(WaitingGroup wg){
		// TODO
		return null;
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

	double getBalance(){
		return this.order==null ? 0.0 : order.getBalance();
	}


	public static Table getTableById(long id){
		// THIS IS A VERY BAD IMPLEMENTATION but I don't care

		for(Table t : allTables){
			if(t.id == id)
				return t;
		}

		return null;
	}

}
