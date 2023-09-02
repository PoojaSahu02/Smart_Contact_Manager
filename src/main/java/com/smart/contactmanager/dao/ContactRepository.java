package com.smart.contactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.contactmanager.model.Contact;
import com.smart.contactmanager.model.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
//	pagination
	@Query("from Contact as c where c.user.id =:userId")
	// current page
	// contact per page-8
	public Page<Contact> findAllContactsByUser(@Param("userId") int userId, Pageable pageable);

//	search
	public List<Contact> findByNameContainingAndUser(String name, User user);
}
