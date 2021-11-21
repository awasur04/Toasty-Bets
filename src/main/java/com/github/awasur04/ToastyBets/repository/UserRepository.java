package com.github.awasur04.ToastyBets.repository;

import com.github.awasur04.ToastyBets.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {

    @Query(value="SELECT * FROM users WHERE NOT permission_level = 'INACTIVE'", nativeQuery = true)
    List<User> getActiveUsers();
}
