package com.fnt.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "customer_order_head")
@NamedQueries({
		@NamedQuery(name = CustomerOrderHead.CUSTOMER_ORDERHEAD_GET_ALL, query = "SELECT oh FROM CustomerOrderHead oh"), })
public class CustomerOrderHead {

	public static final String CUSTOMER_ORDERHEAD_GET_ALL = "customerorderhead.getall";

	@Column(name = "internalordernumber")
	@NotNull
	private String internalordernumber;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "co_generator")
	@SequenceGenerator(name = "co_generator", sequenceName = "co_seq")
	@Column(name = "ordernumber", updatable = false, nullable = false)
	private Long orderNumber;

	@Column(name = "customerid")
	@NotNull
	private String customerId;

	@Column(name = "date")
	@NotNull
	private LocalDateTime date;

	@Column(name = "status")
	@NotNull
	private Integer status;

	public String getInternalordernumber() {
		return internalordernumber;
	}

	public void setInternalordernumber(String internalordernumber) {
		this.internalordernumber = internalordernumber;
	}

	public Long getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Long orderNumber) {
		this.orderNumber = orderNumber;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customerId == null) ? 0 : customerId.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((internalordernumber == null) ? 0 : internalordernumber.hashCode());
		result = prime * result + ((orderNumber == null) ? 0 : orderNumber.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		CustomerOrderHead other = (CustomerOrderHead) obj;
		if (customerId == null) {
			if (other.customerId != null)
				return false;
		} else if (!customerId.equals(other.customerId))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (internalordernumber == null) {
			if (other.internalordernumber != null)
				return false;
		} else if (!internalordernumber.equals(other.internalordernumber))
			return false;
		if (orderNumber == null) {
			if (other.orderNumber != null)
				return false;
		} else if (!orderNumber.equals(other.orderNumber))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		return true;
	}

}
