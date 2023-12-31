package com.smart.contactmanager.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CONTACT")
public class Contact {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int cId;
	@NotBlank(message = "Name can't be null")
	@Size(min = 2, max = 20, message = "minimum 3 and maximum 20 caharacter is alowed")
	private String name;
	@NotBlank(message = " can't be null")
	private String secondName;
	@NotBlank(message = " can't be null")
	private String work;
	@Email(regexp = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}", message = "Invalid mail")
	private String email;
	@NotBlank(message = "can not be blank")
	@Size(min = 10, max = 13, message = "phone no should have 10 digits")
	private String phone;
	private String image;
	@Column(length = 1000)
	@NotBlank(message = "should contain some value")
	@Size(max = 50000)
	private String description;
	@ManyToOne
	@JsonIgnore
	private User user;

	public Contact() {
		super();
	}

	public Contact(int cId, String name, String secondName, String work, String email, String phone, String image,
			String description, User user) {
		super();
		this.cId = cId;
		this.name = name;
		this.secondName = secondName;
		this.work = work;
		this.email = email;
		this.phone = phone;
		this.image = image;
		this.description = description;
		this.user = user;
	}

	public int getcId() {
		return cId;
	}

	public void setcId(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSecondName() {
		return secondName;
	}

	public void setSecondName(String secondName) {
		this.secondName = secondName;
	}

	public String getWork() {
		return work;
	}

	public void setWork(String work) {
		this.work = work;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public boolean equals(Object obj) {
		return this.cId == ((Contact) obj).getcId();
	}

}
