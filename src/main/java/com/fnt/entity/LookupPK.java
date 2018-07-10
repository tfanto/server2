package com.fnt.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class LookupPK implements Serializable {

	private static final long serialVersionUID = 1L;

	private String constant;
	private String code;

	public LookupPK() {
		// intentionally required by JPA
	}

	public LookupPK(String constant, String code) {
		this.constant = constant;
		this.code = code;
	}

	public String getConstant() {
		return constant;
	}

	public void setConstant(String constant) {
		this.constant = constant;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((constant == null) ? 0 : constant.hashCode());
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
		LookupPK other = (LookupPK) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (constant == null) {
			if (other.constant != null)
				return false;
		} else if (!constant.equals(other.constant))
			return false;
		return true;
	}
	
	
	
	

}
