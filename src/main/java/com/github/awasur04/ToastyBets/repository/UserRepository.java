package com.github.awasur04.ToastyBets.repository;

import com.github.awasur04.ToastyBets.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

}
