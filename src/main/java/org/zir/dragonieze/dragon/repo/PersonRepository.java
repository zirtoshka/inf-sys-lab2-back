package org.zir.dragonieze.dragon.repo;

import org.springframework.data.repository.CrudRepository;
import org.zir.dragonieze.dragon.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {
}
