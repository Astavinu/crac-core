package crac.sync.dao;

import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

import crac.sync.model.DbExt;

@Transactional
public interface DbExtDAO extends CrudRepository<DbExt, Long>{
	 public DbExt findByName(String name);
}
