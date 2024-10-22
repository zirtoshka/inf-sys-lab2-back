package org.zir.dragonieze.dragon.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zir.dragonieze.dragon.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
}
