package com.apurva.bookmarkservice;

public class Product {
	
	private String _id;
	private String productId;
	private String title;
	private String longDescription;
	private String shortDescription;
	private String color;
	private double price;
	private String material;
	private float avgRating;
	private String size;
	private String type;
	private String brand;
	private String occasion;
	private String imageUri;
	private String thumbnail;
	private int inStock;
	private String sellerName;
	private String sellerId;
	
	
	public Product() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Product(String _id, String productId, String title, String longDescription, String shortDescription,
			String color, double price, String material, float avgRating, String size, String type, String brand,
			String occasion, String imageUri, String thumbnail, int inStock, String sellerName, String sellerId) {
		super();
		this._id = _id;
		this.productId = productId;
		this.title = title;
		this.longDescription = longDescription;
		this.shortDescription = shortDescription;
		this.color = color;
		this.price = price;
		this.material = material;
		this.avgRating = avgRating;
		this.size = size;
		this.type = type;
		this.brand = brand;
		this.occasion = occasion;
		this.imageUri = imageUri;
		this.thumbnail = thumbnail;
		this.inStock = inStock;
		this.sellerName = sellerName;
		this.sellerId = sellerId;
	}
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public float getAvgRating() {
		return avgRating;
	}
	public void setAvgRating(float avgRating) {
		this.avgRating = avgRating;
	}
	public String getSize() {
		return size;
	}
	public void setSize(String size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getOccasion() {
		return occasion;
	}
	public void setOccasion(String occasion) {
		this.occasion = occasion;
	}
	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		this.imageUri = imageUri;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public int getInStock() {
		return inStock;
	}
	public void setInStock(int inStock) {
		this.inStock = inStock;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	
	
	
}
