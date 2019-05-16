public class Table {

	private String id;
	private LatLng lat_lng;
	private int seats;
	private int floor;
	private Order order;
	private double balance;
	public enum Status { FREE, RESERVED, PAID, CALLING, TO_CLEAN, NORMAL, TAKEN };
	private Status status = FREE;

	Table(String id, LatLng lat_lng, int seats, int floor, double balance)
	{
		this.id = id;
		this.lat_lng = lat_lng;
		this.seats = seats;
		this.floor = floor;
		this.balance = balance;
	}
	
	
	public void onCall()
	{
		//kaleitai otan enas pelatis pataei to TableButton
		
		Waiter w; //dilwnw waiter
		w = Waiter.findBestForTable(this); //bazw sto w auto p epistrefei to findBestForTable
		TableCallNotification n = new TableCallNotification( this, w ); // dimiourgw ena table notification n 
		// to opoio pairnei san orisma to table kai ton waiter
		w.notify( n ); // notify ton waiter me to notification p dimiourgi8ike	
	}

	public boolean onOrderPaid()
	{
		//kaleitai otan o servitoros epile3ei ena trapezi gia plirwmi apo tin o8oni tou
		this.order.setPaid(); 
		return true;
	}


	public static Table findFreeTable(WaitingGroup wg)
	{
		//kaleitai apo tin WaitingGroup kai prepei na ekteleite mexri na bre8ei trapezi gia ena waitinggroup
		
		//den 8a tin ulopoieisoume
	}


	public void setTaken()
	{
		//kanei ena table taken an to epile3ei o PR
		
		this.status = TAKEN; // allazei to status tou table pou exei bre8ei

	}


	public void setReserved()
	{
		//kanei ena table reserved an to epile3ei o Admin
		this.status = RESERVED;
	}


	public boolean isAvailable()
	{
		if ( this.status == FREE )
		{
			return true;
		}
		else
		{
			return false;
		}
	}


	public boolean onTopologyEdit(Bundle new_info)
	{
		//kalei tin validateData 
		if ( validateData(new_info) == true )
		{
			//Gia ka8e Waiter w{
				TopologyChangeNotification n = new TopologyChangeNotification( w ); // pairnei san orisma kapoion waiter
				w.notify(n);
			//}
			showSuccess("success");
			return true;
		}
		else
		{
			showFailure("failure");
			return false;
		}
	}


	public boolean validateData(Bundle new_info)
	{

		//elegxei tin egkurotita tis allagis stin topologia

		//de 8a ulopoii8ei
	}

}
