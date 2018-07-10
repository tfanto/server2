package com.fnt.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "item")
public class ItemView1 {

	@Id
	@Column(name = "id")
	private String id;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "in_stock")
	private Integer inStock;
	
	@Column(name = "price")
	private Double price;

	public ItemView1() {

	}

	public ItemView1(String id, String description, Integer inStock, Double price) {
		this.id = id;
		this.description = description;
		this.inStock = inStock;
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getInStock() {
		return inStock;
	}

	public void setInStock(Integer inStock) {
		this.inStock = inStock;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
