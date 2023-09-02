package com.smart.contactmanager.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.contactmanager.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	@Query("Select u from User u where u.email=:email")
	public User getUserByUserName(@Param("email") String email);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = ?1")
	boolean existsByEmail(String email);
}
