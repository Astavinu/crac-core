package crac.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import crac.daos.GroupDAO;
import crac.daos.CracUserDAO;
import crac.models.Group;
import crac.models.CracUser;

/**
 * REST controller for managing groups.
 */
@RestController
@RequestMapping("/group")
public class GroupController {
	@Autowired
	private GroupDAO groupDAO;

	@Autowired
	private CracUserDAO userDAO;

	/**
	 * GET / or blank -> get all groups.
	 */
	@RequestMapping(value = { "/", "" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> index() throws JsonProcessingException {
		Iterable<Group> groupList = groupDAO.findAll();
		ObjectMapper mapper = new ObjectMapper();
		return ResponseEntity.ok().body(mapper.writeValueAsString(groupList));
	}

	/**
	 * GET /{group_id} -> get the group with given ID.
	 */
	@RequestMapping(value = "/{group_id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> show(@PathVariable(value = "group_id") Long id) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Group myGroup = groupDAO.findOne(id);
		return ResponseEntity.ok().body(mapper.writeValueAsString(myGroup));
	}

	/**
	 * POST / or blank -> create a new group, creator is the logged-in user.
	 */
	@RequestMapping(value = { "/", "" }, method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> create(@RequestBody String json) throws JsonMappingException, IOException {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		CracUser myUser = userDAO.findByName(userDetails.getUsername());		
		ObjectMapper mapper = new ObjectMapper();
		Group myGroup = mapper.readValue(json, Group.class);
		myGroup.setCreator(myUser);
		groupDAO.save(myGroup);

		return ResponseEntity.ok().body("{\"created\":\"true\"}");

	}

	/**
	 * DELETE /{group_id} -> delete the group with given ID.
	 */
	@RequestMapping(value = "/{group_id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> destroy(@PathVariable(value = "group_id") Long id) {
		Group deleteGroup = groupDAO.findOne(id);
		groupDAO.delete(deleteGroup);
		return ResponseEntity.ok().body("{\"deleted\":\"true\"}");

	}

	/**
	 * PUT /{group_id} -> update the group with given ID.
	 */
	@RequestMapping(value = "/{group_id}", method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
	@ResponseBody
	public ResponseEntity<String> update(@RequestBody String json, @PathVariable(value = "group_id") Long id)
			throws JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		Group updatedGroup = mapper.readValue(json, Group.class);
		Group oldGroup = groupDAO.findOne(id);
		oldGroup = updatedGroup;

		groupDAO.save(oldGroup);

		return ResponseEntity.ok().body("{\"updated\":\"true\"}");

	}

}
