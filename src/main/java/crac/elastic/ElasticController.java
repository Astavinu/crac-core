package crac.elastic;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import crac.daos.CracUserDAO;
import crac.daos.TaskDAO;
import crac.models.CracUser;
import crac.models.SearchTransformer;
import crac.models.Task;

@RestController
@RequestMapping("/elastic")
public class ElasticController {
	
	private ElasticConnector<ElasticTask> ESConnTask = new ElasticConnector<ElasticTask>("localhost", 9300, "crac_core", "elastic_task");
	private ElasticConnector<ElasticPerson> ESConnUser = new ElasticConnector<ElasticPerson>("localhost", 9300, "crac_core", "elastic_user");
	private SearchTransformer ST = new SearchTransformer();
	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private TaskDAO taskDAO;
	
	@Autowired
	private CracUserDAO userDAO;

	@RequestMapping(value = "/addTask/{task_id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> addTask(@PathVariable(value = "task_id") long task_id) throws JsonProcessingException {

		Task originalTask = taskDAO.findOne(task_id);
		
		if(ESConnTask.indexOrUpdate(""+originalTask.getId(), ST.transformTask(originalTask)).isCreated()){
			return ResponseEntity.ok().body("{\"entry\":\"true\"}");
		}else{
			return ResponseEntity.ok().body("{\"updated\":\"true\"}");
		}
		
	}
	
	@RequestMapping(value = "/getTask/{task_id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> getTask(@PathVariable(value = "task_id") String task_id) throws JsonProcessingException {
		
		GetResponse response = ESConnTask.get(task_id);

		return ResponseEntity.ok().body(response.getSourceAsString());
		
	}
	
	@RequestMapping(value = "/deleteTask/{task_id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> deleteTask(@PathVariable(value = "task_id") String task_id) throws JsonProcessingException {
		
		DeleteResponse response = ESConnTask.delete(task_id);

		return ResponseEntity.ok().body("{\"id\":\""+task_id+"\", \"deleted\": \""+response.isFound()+"\"}");
		
	}
	
	@RequestMapping(value = "/searchES/task", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> searchESTask() throws JsonProcessingException {

		return ResponseEntity.ok().body(ESConnTask.query().toString());
		
	}
	
	@RequestMapping(value = "/addUser/{user_id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> addUser(@PathVariable(value = "user_id") long user_id) throws JsonProcessingException {

		CracUser originalUser = userDAO.findOne(user_id);
		
		if(ESConnUser.indexOrUpdate(""+originalUser.getId(), ST.transformUser(originalUser)).isCreated()){
			return ResponseEntity.ok().body("{\"entry\":\"true\"}");
		}else{
			return ResponseEntity.ok().body("{\"updated\":\"true\"}");
		}
		
	}
	
	@RequestMapping(value = "/getUser/{user_id}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> getUser(@PathVariable(value = "user_id") String user_id) throws JsonProcessingException {
		
		GetResponse response = ESConnUser.get(user_id);

		return ResponseEntity.ok().body(response.getSourceAsString());
		
	}
	
	@RequestMapping(value = "/deleteUser/{user_id}", method = RequestMethod.DELETE, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> deleteUser(@PathVariable(value = "user_id") String user_id) throws JsonProcessingException {
		
		DeleteResponse response = ESConnUser.delete(user_id);

		return ResponseEntity.ok().body("{\"id\":\""+user_id+"\", \"deleted\": \""+response.isFound()+"\"}");
		
	}
	
	@RequestMapping(value = "/searchES/user", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> searchESUser() throws JsonProcessingException {

		return ResponseEntity.ok().body(ESConnUser.query().toString());
		
	}


	
}
