package com.fnt.dto;

import java.util.ArrayList;
import java.util.List;

import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;

public class CustomerOrder {

	private CustomerOrderHead head = null;
	private List<CustomerOrderLine> lines = new ArrayList<>();

	public CustomerOrder() {
	}

	public CustomerOrderHead getHead() {
		return head;
	}

	public void setHead(CustomerOrderHead head) {
		this.head = head;
	}

	public List<CustomerOrderLine> getLines() {
		return lines;
	}

	public void setLines(List<CustomerOrderLine> lines) {
		this.lines = lines;
	}

}
