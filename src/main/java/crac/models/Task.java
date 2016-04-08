package crac.models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * The task-entity.
 */
@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(name = "tasks")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Autowired
	@Column(name = "task_id")
	private long id;

	/**
	 * defines a many to many relation with the competence-entity
	 */
	@Autowired
	@ManyToMany(cascade = { CascadeType.ALL })
	@JoinTable(name = "task_competences", joinColumns = { @JoinColumn(name = "task_id") }, inverseJoinColumns = {
			@JoinColumn(name = "competence_id") })
	private Set<Competence> neededCompetences;

	/**
	 * defines a many to many relation with the cracUser-entity
	 */
	@ManyToMany(mappedBy = "openTasks")
	private Set<CracUser> users;

	@Autowired
	@NotNull
	private String name;

	@Autowired
	@NotNull
	private String description;

	/**
	 * defines a one to many relation with the cracUser-entity
	 */
	@Autowired
	@ManyToOne
	@JoinColumn(name = "creator_id")
	private CracUser creator;

	/**
	 * constructors
	 */

	public Task() {
		this.name = "";
	}

	public Task(String name, String description) {
		this.name = name;
		this.description = description;
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

	public Set<CracUser> getUsers() {
		return users;
	}

	public void setUsers(Set<CracUser> users) {
		this.users = users;
	}

	public CracUser getCreator() {
		return creator;
	}

	public void setCreator(CracUser creator) {
		this.creator = creator;
	}

	public Set<Competence> getNeededCompetences() {
		return neededCompetences;
	}

	public void setNeededCompetences(Set<Competence> neededCompetences) {
		this.neededCompetences = neededCompetences;
	}

}
