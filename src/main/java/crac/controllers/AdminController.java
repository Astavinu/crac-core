package crac.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import crac.daos.CompetenceDAO;
import crac.daos.CracUserDAO;
import crac.daos.GroupDAO;
import crac.daos.TaskDAO;
import crac.daos.UserCompetenceRelDAO;
import crac.daos.UserTaskRelDAO;
import crac.elastic.ElasticConnector;
import crac.elastic.ElasticTask;
import crac.elastic.ElasticUser;
import crac.models.Competence;
import crac.models.CracUser;
import crac.models.Task;
import crac.utility.JSonResponseHelper;
import crac.utility.SearchTransformer;
import crac.utility.UpdateEntitiesHelper;

/**
 * 
 * @author David This controller contains all REAL rest-endpoints. With
 *         ADMIN-rights, all datasets can be manipulated here.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CracUserDAO userDAO;

	@Autowired
	private CompetenceDAO competenceDAO;

	@Autowired
	private TaskDAO taskDAO;

	@Autowired
	private GroupDAO groupDAO;

	@Autowired
	private UserCompetenceRelDAO userCompetenceRelDAO;

	@Autowired
	private UserTaskRelDAO userTaskRelDAO;

	private ElasticConnector<ElasticUser> ESConnUser = new ElasticConnector<ElasticUser>("localhost", 9300, "crac_core",
			"elastic_user");

	private ElasticConnector<ElasticTask> ESConnTask = new ElasticConnector<ElasticTask>("localhost", 9300, "crac_core",
			"elastic_task");

	@Autowired
	private SearchTransformer ST;

	// USER-SECTION

	/**
	 * POST / or blank -> create a new user.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = { "/user/",
			"/user" }, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> createUser(@RequestBody String json) throws JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		CracUser u = mapper.readValue(json, CracUser.class);

		BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();
		u.setPassword(bcryptEncoder.encode(u.getPassword()));

		if (userDAO.findByName(u.getName()) == null) {
			userDAO.save(u);
			ESConnUser.indexOrUpdate("" + u.getId(), ST.transformUser(u));
			return JSonResponseHelper.successFullyCreated(u);
		} else {
			return JSonResponseHelper.alreadyExists();
		}

	}

	/**
	 * DELETE /{user_id} -> delete the user with given ID.
	 */

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = { "/user/{user_id}",
			"/user/{user_id}/" }, method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> destroyUser(@PathVariable(value = "user_id") Long id) {
		CracUser u = userDAO.findOne(id);

		if (u != null) {
			u.getCompetenceRelationships().clear();
			userDAO.delete(u);
			ESConnUser.delete("" + u.getId());
			return JSonResponseHelper.successFullyDeleted(u);
		} else {
			return JSonResponseHelper.idNotFound();
		}

	}

	/**
	 * PUT /{user_id} -> update the user with given ID.
	 */

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = { "/user/{user_id}",
			"/user/{user_id}/" }, method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> updateUser(@RequestBody String json, @PathVariable(value = "user_id") Long id) {
		ObjectMapper mapper = new ObjectMapper();
		CracUser updatedUser;
		try {
			updatedUser = mapper.readValue(json, CracUser.class);
		} catch (JsonMappingException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonMapError();
		} catch (IOException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonReadError();
		}

		CracUser oldUser = userDAO.findOne(id);

		if (oldUser != null) {
			UpdateEntitiesHelper.checkAndUpdateUser(oldUser, updatedUser);
			userDAO.save(oldUser);
			ESConnUser.indexOrUpdate("" + oldUser.getId(), ST.transformUser(oldUser));
			return JSonResponseHelper.successFullyUpdated(oldUser);
		} else {
			return JSonResponseHelper.idNotFound();
		}

	}

	// TASK-SECTION

	/**
	 * POST / or blank -> create a new task, creator is the logged-in user.
	 */
	@RequestMapping(value = { "/task/",
			"/task" }, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> createTask(@RequestBody String json) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CracUser myUser = userDAO.findByName(userDetails.getUsername());
		ObjectMapper mapper = new ObjectMapper();
		Task myTask;
		try {
			myTask = mapper.readValue(json, Task.class);
		} catch (JsonMappingException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonMapError();
		} catch (IOException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonReadError();
		}
		myTask.setCreator(myUser);
		taskDAO.save(myTask);
		ESConnTask.indexOrUpdate("" + myTask.getId(), ST.transformTask(myTask));

		return JSonResponseHelper.successFullyCreated(myTask);

	}

	/**
	 * DELETE /{task_id} -> delete the task with given ID.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = { "/task/{task_id}",
			"/task/{task_id}/" }, method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> destroyTask(@PathVariable(value = "task_id") Long id) {
		Task deleteTask = taskDAO.findOne(id);

		if (deleteTask != null) {
			deleteTask.getNeededCompetences().clear();
			deleteTask.getUserRelationships().clear();
			taskDAO.delete(deleteTask);
			ESConnTask.delete("" + deleteTask.getId());
			return JSonResponseHelper.successFullyDeleted(deleteTask);
		} else {
			return JSonResponseHelper.idNotFound();
		}

	}

	/**
	 * PUT /{task_id} -> update the task with given ID.
	 */
	@RequestMapping(value = { "/task/{task_id}",
			"/task/{task_id}/" }, method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> updateTask(@RequestBody String json, @PathVariable(value = "task_id") Long id) {
		ObjectMapper mapper = new ObjectMapper();
		Task updatedTask;
		try {
			updatedTask = mapper.readValue(json, Task.class);
		} catch (JsonMappingException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonMapError();
		} catch (IOException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonReadError();
		}
		Task oldTask = taskDAO.findOne(id);

		if (oldTask != null) {
			UpdateEntitiesHelper.checkAndUpdateTask(oldTask, updatedTask);
			taskDAO.save(oldTask);
			ESConnTask.indexOrUpdate("" + oldTask.getId(), ST.transformTask(oldTask));
			return JSonResponseHelper.successFullyUpdated(oldTask);
		} else {
			return JSonResponseHelper.idNotFound();
		}

	}

	// COMPETENCE-SECTION

	/**
	 * POST / or blank -> create a new competence, creator is the logged-in
	 * user.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = { "/competence/",
			"/competence" }, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> createCompetence(@RequestBody String json) {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CracUser myUser = userDAO.findByName(userDetails.getUsername());
		ObjectMapper mapper = new ObjectMapper();
		Competence myCompetence;
		try {
			myCompetence = mapper.readValue(json, Competence.class);
		} catch (JsonMappingException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonMapError();
		} catch (IOException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonReadError();
		}
		myCompetence.setCreator(myUser);
		competenceDAO.save(myCompetence);
		return JSonResponseHelper.successFullyCreated(myCompetence);

	}

	/**
	 * DELETE /{competence_id} -> delete the competence with given ID.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = { "/competence/{competence_id}",
			"/competence/{competence_id}/" }, method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> destroyCompetence(@PathVariable(value = "competence_id") Long id) {
		Competence deleteCompetence = competenceDAO.findOne(id);

		if (deleteCompetence != null) {
			competenceDAO.delete(deleteCompetence);
			return JSonResponseHelper.successFullyDeleted(deleteCompetence);

		} else {
			return JSonResponseHelper.idNotFound();
		}

	}

	/**
	 * PUT /{competence_id} -> update the competence with given ID.
	 */
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = { "/competence/{competence_id}",
			"/competence/{competence_id}/" }, method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> updateCompetence(@RequestBody String json,
			@PathVariable(value = "competence_id") Long id) {
		ObjectMapper mapper = new ObjectMapper();
		Competence updatedCompetence;
		try {
			updatedCompetence = mapper.readValue(json, Competence.class);
		} catch (JsonMappingException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonMapError();
		} catch (IOException e) {
			System.out.println(e.toString());
			return JSonResponseHelper.jsonReadError();
		}

		Competence oldCompetence = competenceDAO.findOne(id);

		if (oldCompetence != null) {
			UpdateEntitiesHelper.checkAndUpdateCompetence(oldCompetence, updatedCompetence);
			competenceDAO.save(oldCompetence);
			return JSonResponseHelper.successFullyUpdated(oldCompetence);
		} else {
			return JSonResponseHelper.idNotFound();
		}

	}

}
