package crac.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The group-entity.
 */

@Entity
@Table(name = "groups")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "group_id")
	private long id;

	@NotNull
	private String name;

	@NotNull
	private String description;
	
	@NotNull
	private int maxEnrols;

	/**
	 * defines a many to many relation with the cracUser-entity
	 */
	@ManyToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
	@JoinTable(name = "group_users", joinColumns = { @JoinColumn(name = "group_id") }, inverseJoinColumns = {
			@JoinColumn(name = "user_id") })
	private Set<CracUser> enroledUsers;
	
	/**
	 * defines a one to many relation with the cracUser-entity
	 */
	@ManyToOne
	@JsonIdentityReference(alwaysAsId=true)
	@JoinColumn(name = "creator_id")
	private CracUser creator;
	
	/**
	 * constructors
	 */

	public Group(String name, String description, int maxEnrols) {
		this.name = name;
		this.description = description;
		this.maxEnrols = maxEnrols;
	}
	
	public Group() {
		this.name = "";
		this.description = "";
		this.maxEnrols = 0;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMaxEnrols() {
		return maxEnrols;
	}

	public void setMaxEnrols(int maxEnrols) {
		this.maxEnrols = maxEnrols;
	}

	public Set<CracUser> getEnroledUsers() {
		return enroledUsers;
	}

	public void setEnroledUsers(Set<CracUser> enroledUsers) {
		this.enroledUsers = enroledUsers;
	}

	public CracUser getCreator() {
		return creator;
	}

	public void setCreator(CracUser creator) {
		this.creator = creator;
	}

}
