class Product{
	enum ProductType{ PRODUCT_DRINK, PRODUCT_FOOD, PRODUCT_OTHER }
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
			if(this.isCurrentlyInOrder()) return false;
		}
		
		//Other checks that return false on failure...
		
		return true;
	}
	
	void onEdit(Bundle new_info){
		if(validateData(new_info)){
			Main.showSuccess("Successful update");
			for(Waiter w : Waiter.allWaiters){
				new ProductPriceNotification(this, w).show();
			}
			
			// Update the data info
			this.description = new_info.getString("description");
			this.price = new_info.getDouble("price");
			this.stock = new_info.getInt("stock");
			this.has_alcohol = new_info.getBoolean("has_alcohol");
			this.time_to_prepare = new_info.getDouble("time_to_prepare");
			switch(new_info.getInt("type")){
				case 0:
					this.type = ProductType.PRODUCT_DRINK;
					break;
				case 1:
					this.type = ProductType.PRODUCT_FOOD;
					break;
				case 2:
					this.type = ProductType.PRODUCT_OTHER;
					break;
			}
			
			// Somehow save to database
			// ...
		}else{
			Main.showFailure("Update failed");
		}
	}
}
