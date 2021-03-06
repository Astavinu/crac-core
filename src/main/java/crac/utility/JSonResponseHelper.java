package crac.utility;

import org.springframework.http.ResponseEntity;

import crac.enums.TaskState;
import crac.models.Competence;
import crac.models.CracUser;
import crac.models.Task;

public class JSonResponseHelper {
	
	//Creation Helpers
		
	public static ResponseEntity<String> successFullyCreated(CracUser u){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"create\", \"user\":\"" + u.getId() + "\",\"name\":\"" + u.getName() + "\"}");
	}
	
	public static ResponseEntity<String> successFullyCreated(Competence c){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"create\", \"competence\":\"" + c.getId() + "\",\"name\":\"" + c.getName() + "\"}");
	}
	
	public static ResponseEntity<String> successFullyCreated(Task t){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"create\", \"task\":\"" + t.getId() + "\",\"name\":\"" + t.getName() + "\"}");
	}
	
	//Delete Helpers
	
	public static ResponseEntity<String> successFullyDeleted(CracUser u){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"delete\", \"user\":\"" + u.getId() + "\",\"name\":\"" + u.getName() + "\"}");
	}
	
	public static ResponseEntity<String> successFullyDeleted(Competence c){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"delete\", \"competence\":\"" + c.getId() + "\",\"name\":\"" + c.getName() + "\"}");
	}
	
	public static ResponseEntity<String> successFullyDeleted(Task t){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"delete\", \"task\":\"" + t.getId() + "\",\"name\":\"" + t.getName() + "\"}");
	}
	
	//Update Helpers
	
	public static ResponseEntity<String> successFullyUpdated(CracUser u){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"update\", \"user\":\"" + u.getId() + "\",\"name\":\"" + u.getName() + "\"}");
	}
	
	public static ResponseEntity<String> successFullyUpdated(Competence c){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"update\", \"competence\":\"" + c.getId() + "\",\"name\":\"" + c.getName() + "\"}");
	}
	
	public static ResponseEntity<String> successFullyUpdated(Task t){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"update\", \"task\":\"" + t.getId() + "\",\"name\":\"" + t.getName() + "\"}");
	}

	//Error Helpers

	public static ResponseEntity<String> idNotFound(){
		return ResponseEntity.badRequest().body("{\"success\":\"false\", \"error\":\"bad_request\", \"cause\":\"id does not exist\"}");
	}
	
	public static ResponseEntity<String> alreadyExists(){
		return ResponseEntity.badRequest().body("{\"success\":\"false\", \"error\":\"bad_post_request\", \"cause\":\"dataset already exists\"}");
	}
	
	public static ResponseEntity<String> jsonReadError(){
		return ResponseEntity.badRequest().body("{\"success\":\"false\", \"error\":\"bad_post_put_request\", \"cause\":\"json-file contains error(s)\"}");
	}
	
	public static ResponseEntity<String> jsonMapError(){
		return ResponseEntity.badRequest().body("{\"success\":\"false\", \"error\":\"bad_post_put_request\", \"cause\":\"can not map json to object\"}");
	}
	
	//Task State-Change Helper
	
	public static ResponseEntity<String> successTaskStateChanged(Task t, TaskState ts){
		return ResponseEntity.ok().body("{\"success\":\"true\", \"action\":\"state_change\", \"state\":\""+ts.toString()+"\", \"task\":\"" + t.getId() + "\",\"name\":\"" + t.getName() + "\"}");
	}
	
	public static ResponseEntity<String> stateNotAvailable(String name){
		return ResponseEntity.badRequest().body("{\"success\":\"false\", \"error\":\"bad_request\", \"cause\":\"There is no such state "+name+"\"}");
	}
	
}
