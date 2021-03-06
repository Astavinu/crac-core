package crac.utility;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import crac.models.Competence;
import crac.models.CracUser;
import crac.models.Task;

public class UpdateEntitiesHelper {

	public static void checkAndUpdateUser(CracUser old, CracUser updated){
		
		BCryptPasswordEncoder bcryptEncoder = new BCryptPasswordEncoder();

		if (updated.getPassword() != null) {
			old.setPassword(bcryptEncoder.encode(updated.getPassword()));
		}
		
		if (updated.getName() != null) {
			old.setName(updated.getName());
		}
		if (updated.getFirstName() != null) {
			old.setFirstName(updated.getFirstName());
		}
		if (updated.getLastName() != null) {
			old.setLastName(updated.getLastName());
		}
		if (updated.getBirthDate() != null) {
			old.setBirthDate(updated.getBirthDate());
		}
		if (updated.getEmail() != null) {
			old.setEmail(updated.getEmail());
		}
		if (updated.getAddress() != null) {
			old.setAddress(updated.getAddress());
		}
		if (updated.getPhone() != null) {
			old.setPhone(updated.getPhone());
		}
		if (updated.getRole() != null) {
			old.setRole(updated.getRole());
		}
		if (updated.getStatus() != null) {
			old.setStatus(updated.getStatus());
		}

	}
	
	public static void checkAndUpdateTask(Task old, Task updated){
		if(updated.getName() != null){
			old.setName(updated.getName());
		}
		
		if(updated.getDescription() != null){
			old.setDescription(updated.getDescription());
		}

		if(updated.getLocation() != null){
			old.setLocation(updated.getLocation());
		}

		if(updated.getStartTime() != null){
			old.setStartTime(updated.getStartTime());
		}
		
		if(updated.getEndTime() != null){
			old.setEndTime(updated.getEndTime());
		}

		if(updated.getUrgency() > 0){
			old.setUrgency(updated.getUrgency());
		}
		
		if(updated.getAmountOfVolunteers() > 0){
			old.setAmountOfVolunteers(updated.getAmountOfVolunteers());
		}
		
		if(updated.getFeedback() != null){
			old.setFeedback(updated.getFeedback());
		}
		
		if(updated.getTaskState() != null){
			old.setTaskState(updated.getTaskState());
		}
		
		if(updated.getTaskType() != null){
			old.setTaskType(updated.getTaskType());
		}
	}
	
	public static void checkAndUpdateCompetence(Competence old, Competence updated){
		if (updated.getName() != null) {
			old.setName(updated.getName());
		}
		if (updated.getDescription() != null) {
			old.setDescription(updated.getDescription());
		}
		if (updated.getPermissionType() != null) {
			old.setPermissionType(updated.getPermissionType());
		}

	}

	
}
