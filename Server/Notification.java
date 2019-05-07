// Na sbhstei to type apo to Notification apo Domain Model
// static List<Order> Order.allOrders
// boolean Product.isCurrentlyInOrder()
// Response Device.dispatch(Notification)

// Imports apo Main
// static final enum Response{ RESP_ACCEPTED, RESP_REJECTED };
// void showSuccess(String)
// void showFailure(String)

abstract class Notification{
	long id;
	MobileDevice device;
	Date timestamp;
	
	Response show(){
		//Send notification to device
		Response response = device.dispatch(this);
		
		if(response == RESP_ACCEPTED){
			accept();
			return RESP_ACCEPTED;
		}else{
			reject();
			return RESP_REJECTED
		}
	}
	
	void accept();
	void reject();
}

class PrepAreaNotification{
	PrepArea prepArea;
	Order order;
}

class TableFreeNotification{
	Table table;
	PR receiver;
	WaitingGroup wg;			///////////////////////////
	
	void accept(){
		table.setTaken();
		wg.remove();
	}
	void reject(){
		wg.notifyWhenAvailable();
	}
}
	
