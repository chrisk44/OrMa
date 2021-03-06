import java.util.ArrayList;

class Product{
	static ArrayList<Product> allProducts = new ArrayList<>();
	enum ProductType{ PRODUCT_DRINK, PRODUCT_FOOD, PRODUCT_OTHER }
	private double price;
	private long id;
	private String description;
	private ProductType type;
	private int stock;
	private boolean has_alcohol;
	private double time_to_prepare;
	
	Product(long id, String description, double price, ProductType type, int stock, boolean has_alcohol, double time_to_prepare){
		this.id = id;
		this.description = description;
		this.price = price;
		this.type = type;
		this.stock = stock;
		this.has_alcohol = has_alcohol;
		this.time_to_prepare = time_to_prepare;

		allProducts.add(this);
	}

	long getId(){ return this.id; }
	double getPrice(){ return this.price; }
	boolean isCurrentlyInOrder(){
		for(Order order : Order.allOrders){
			if(order.products.contains(this) && !order.products_paid.contains(this)) return true;
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
	
	boolean onEdit(Bundle new_info){
		if(validateData(new_info)){
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

			// Save to database

			// Notify waiters
			for(Waiter w : Waiter.allWaiters){
				new ProductPriceNotification(this, w).show();
			}

			return true;
		}else{
			return false;
		}
	}

	public static Product getProductById(long id){
		// THIS IS A VERY BAD IMPLEMENTATION but I don't care

		for(Product p : allProducts){
			if(p.id == id)
				return p;
		}

		return null;
	}
	public static Product getOfferForClient(long client_id){
		// Dummy (random product, normally would be a special discounted product)
		return allProducts.get(Main.r.nextInt(allProducts.size()));
	}
}
