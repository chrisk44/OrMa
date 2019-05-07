class Product{
	static final enum ProductType{ PRODUCT_DRINK, PRODUCT_FOOD, PRODUCT_OTHER };
	double price;
	String id;
	String description;
	ProductType type;
	int stock;
	boolean has_alcohol;
	double time_to_prepare;
	
	boolean isCurrentlyInOrder(){
		for(Order order : Order.allOrders){
			if(order.products.contains(this)) return true;
		}
		return false;
	}
	boolean validateData(Bundle new_info){
		if(new_info.getDouble("price") != this.price){
			if(this.isCurrentlyInOrder) return false;
		}
		
		//Other checks that return false on failure...
		
		return true;
	}
	
	void onEdit(Bundle new_info){
		if(validateData(new_info)){
			showSuccess("Successful update");
			new ProductPriceNotification(this).show();
		}else{
			showFailure("Update failed");
		}
	}
}
