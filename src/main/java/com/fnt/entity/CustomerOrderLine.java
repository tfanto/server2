package com.fnt.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "customer_order_line")
public class CustomerOrderLine {

	private CustomerOrderLinePK primaryKey;

	@Column(name = "date")
	@NotNull(message = "Customer orderline date cannot be null")
	private LocalDateTime date;

	@Column(name = "item_id")
	@NotNull(message = "Customer orderline itemId cannot be null")
	private String itemId;

	@Column(name = "number_of_items")
	@NotNull(message = "Customer orderline number of items cannot be null")
	@Min(value=1, message="At least one item must be ordered for a customerorderline")
	private Integer numberOfItems;

	@Column(name = "price_per_item")
	@NotNull(message = "Customer orderline price cannot be null")
	@Min(value=1, message="Customer order line price cannot be below 1")
	private Double pricePerItem;

	@EmbeddedId
	public CustomerOrderLinePK getPrimaryKey() {
		return primaryKey;
	}

	public void setPrimaryKey(CustomerOrderLinePK primaryKey) {
		this.primaryKey = primaryKey;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public Integer getNumberOfItems() {
		return numberOfItems;
	}

	public void setNumberOfItems(Integer numberOfItems) {
		this.numberOfItems = numberOfItems;
	}

	public Double getPricePerItem() {
		return pricePerItem;
	}

	public void setPricePerItem(Double pricePerItem) {
		this.pricePerItem = pricePerItem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((itemId == null) ? 0 : itemId.hashCode());
		result = prime * result + ((numberOfItems == null) ? 0 : numberOfItems.hashCode());
		result = prime * result + ((pricePerItem == null) ? 0 : pricePerItem.hashCode());
		result = prime * result + ((primaryKey == null) ? 0 : primaryKey.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CustomerOrderLine other = (CustomerOrderLine) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (itemId == null) {
			if (other.itemId != null)
				return false;
		} else if (!itemId.equals(other.itemId))
			return false;
		if (numberOfItems == null) {
			if (other.numberOfItems != null)
				return false;
		} else if (!numberOfItems.equals(other.numberOfItems))
			return false;
		if (pricePerItem == null) {
			if (other.pricePerItem != null)
				return false;
		} else if (!pricePerItem.equals(other.pricePerItem))
			return false;
		if (primaryKey == null) {
			if (other.primaryKey != null)
				return false;
		} else if (!primaryKey.equals(other.primaryKey))
			return false;
		return true;
	}

}
