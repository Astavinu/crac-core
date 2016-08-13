package crac.sync.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.ManyToOne;

@Embeddable
public class UserMap {
	
	@Column(name="db_ext_user")
	private long idExt;

	@ManyToOne
	private DbExt dbExt;

	/**
	 * constructors
	 */
	
	public UserMap(long idExt, DbExt dbExt){
		this.idExt = idExt;
		this.dbExt = dbExt;
	}
	
	public UserMap(){
	}
	
	/**
	 * getters and setters
	 */

	public long getIdExt() {
		return idExt;
	}

	public void setIdExt(long idExt) {
		this.idExt = idExt;
	}

	public DbExt getDbExt() {
		return dbExt;
	}

	public void setDbExt(DbExt dbExt) {
		this.dbExt = dbExt;
	}
}
