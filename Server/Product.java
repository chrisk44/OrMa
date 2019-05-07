class Product{
	static final enum ProductType{ PRODUCT_DRINK, PRODUCT_FOOD, PRODUCT_OTHER };
	double price;
	String id;
	String description;
	ProductType type;
	int stock;
	boolean has_alcohol;
	double time_to_prepare;
	
	Product(String id, String description, double price, ProductType type, int stock, boolean has_alcohol, double time_to_prepare){
		this.id = id;
		this.description = description;
		this.price = price;
		this.type = type;
		this.stock = stock;
		this.has_alcohol = has_alcohol;
		this.time_to_prepare = time_to_prepare;
	}
	
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
			
			// Update the data info
			this.description = bundle.getString("description");
			this.price = bundle.getDouble("price");
			this.type = bundle.getInt("type");
			this.stock = bundle.getInt("stock");
			this.has_alcohol = bundle.getBoolean("has_alcohol");
			this.time_to_prepare = bundle.getDouble("time_to_prepare");
			
			// Somehow save to database
			// ...
		}else{
			showFailure("Update failed");
		}
	}
}
