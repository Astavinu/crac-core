package crac.daos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import crac.models.Attachment;


/**
 * Spring Data CrudRepository for the competence entity.
 */
@Transactional
public interface AttachmentDAO extends CrudRepository<Attachment, Long> {
	public Attachment findByName(String name);
}
