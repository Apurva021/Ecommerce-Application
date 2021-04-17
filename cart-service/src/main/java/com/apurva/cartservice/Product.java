package com.apurva.cartservice;

public class Product {
	
	private String productId;
	private String title;
	private Double price;
	private Integer inStock;
	private String imageUri;
	private String shortDescription;
	private Integer quantityBought;
	private String sellerId;
	
	
	
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public Integer getQuantityBought() {
		return quantityBought;
	}
	public void setQuantityBought(Integer quantityBought) {
		this.quantityBought = quantityBought;
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
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Integer getInStock() {
		return inStock;
	}
	public void setInStock(Integer inStock) {
		this.inStock = inStock;
	}
	public String getImageUri() {
		return imageUri;
	}
	public void setImageUri(String imageUri) {
		imageUri = imageUri;
	}
	public String getShortDescription() {
		return shortDescription;
	}
	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}
	
	
	
	
	
}
