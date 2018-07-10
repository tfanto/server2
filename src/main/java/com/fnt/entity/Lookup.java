package com.fnt.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity
@Table(name = "lookup", indexes= {@Index(name="lookup00",  columnList="constant,code", unique=true) })
@NamedQueries({
        @NamedQuery(name = Lookup.SYSVAL_GETALLFOR, query = "SELECT m FROM Lookup m where  m.primaryKey.constant=:constant"),
        @NamedQuery(name = Lookup.SYSVAL_GETALLCONSTANTS, query = "SELECT distinct m.primaryKey.constant FROM Lookup m "),
})
public class Lookup {

    public static final String SYSVAL_GETALLFOR = "SYSVAL_GETALLFOR";
    public static final String SYSVAL_GETALLCONSTANTS = "SYSVAL_GETALLCONSTANTS";

    public Lookup(){
        // for json
    }

    LookupPK primaryKey;

    @Column(name = "description")
    private String description;
    
    private String  datastr;
    private Long datalong;
    private Double datadouble;
    private LocalDateTime datadate;

    @EmbeddedId
    public LookupPK getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(LookupPK primaryKey){
        this.primaryKey = primaryKey;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

	public String getDatastr() {
		return datastr;
	}

	public void setDatastr(String datastr) {
		this.datastr = datastr;
	}

	public Long getDatalong() {
		return datalong;
	}

	public void setDatalong(Long datalong) {
		this.datalong = datalong;
	}

	public Double getDatadouble() {
		return datadouble;
	}

	public void setDatadouble(Double datadouble) {
		this.datadouble = datadouble;
	}

	public LocalDateTime getDatadate() {
		return datadate;
	}

	public void setDatadate(LocalDateTime datadate) {
		this.datadate = datadate;
	}
    
    /*
     * 
     private Map<String,String> namevalue = new HashMap<>();

    @ElementCollection // this is a collection of primitives
    @MapKeyColumn(name="key") // column name for map "key"
    @Column(name="value") // column name for map "value"
    public Map<String,String> getNameValue() {
        return namevalue;
    }

    public void  setNameValue(Map<String,String> namevalue) {
        this.namevalue=namevalue;
    }
    */

}
