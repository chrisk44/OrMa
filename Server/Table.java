import java.util.ArrayList;

public class Table {
	static ArrayList<Table> allTables = new ArrayList<>();		// TODO: Add to CD
	private String id;
	private LatLng lat_lng;
	private int seats;
	private int floor;
	private Order order;
	private double balance;
	public enum Status { FREE, RESERVED, PAID, CALLING, TO_CLEAN, NORMAL, TAKEN }
	private Status status = Status.FREE;

	Table(String id, LatLng lat_lng, int seats, int floor, double balance){
		this.id = id;
		this.lat_lng = lat_lng;
		this.seats = seats;
		this.floor = floor;
		this.balance = balance;

		allTables.add(this);
	}
	
	
	public void onCall(){
		// Called when the table's TableButton is pressed

		// TODO: This is a loop
		// Find the most suitable Waiter
		Waiter w = Waiter.findBestForTable(this);
		w.notify(new TableCallNotification(this, w));
	}

	public boolean onOrderPaid(ArrayList<Product> products){		// TODO: Change parameter in CD
		// Called when a Waiter sets some products as paid

		this.order.setPaid(products);
		return true;
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


	public boolean onTopologyEdit(Bundle new_info){
		if(validateData(new_info)){
			for(Waiter w : Waiter.allWaiters){
				TopologyChangeNotification n = new TopologyChangeNotification( w );
				w.notify(n);
			}
			Main.showSuccess("success");
			return true;
		}else{
			Main.showFailure("failure");
			return false;
		}
	}


	public boolean validateData(Bundle new_info){

		//elegxei tin egkurotita tis allagis stin topologia

		return true;
	}

}
