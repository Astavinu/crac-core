package crac.sync.model;

import java.net.URL;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="external_db")
public class DbExt {

	@Id
	@Column(name = "db_id")
	private long id;
	
	@NotNull
	private String name;
	
	@NotNull
	private URL web;
	
	private URL support;
	
	/**
	 * constructors
	 */

	public DbExt(long id, String name, URL web) {
		super();
		this.id = id;
		this.name = name;
		this.web = web;
	}
	
	public DbExt() {
	}
	
	/**
	 * getters and setters
	 */

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public URL getWeb() {
		return web;
	}

	public void setWeb(URL web) {
		this.web = web;
	}

	public URL getSupport() {
		return support;
	}

	public void setSupport(URL support) {
		this.support = support;
	}
}
