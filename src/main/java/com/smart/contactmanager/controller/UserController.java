package com.smart.contactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.contactmanager.dao.ContactRepository;
import com.smart.contactmanager.dao.UserRepository;
import com.smart.contactmanager.helper.Message;
import com.smart.contactmanager.model.Contact;
import com.smart.contactmanager.model.User;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contactRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@ModelAttribute
	public void addCommonDta(Model model, Principal principal) {
		String userName = principal.getName();
		System.out.println("User name : " + userName);
		User user = userRepository.getUserByUserName(userName);
		System.out.println("User : " + user);
		model.addAttribute("user", user);
	}

//	User Dashboard
	@RequestMapping("/dashboard")
	public String user_Dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard :Smart Contact Manager");
		return "normal/user_dashboard";
	}

//	Open Add Contact Form Handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) {
		model.addAttribute("title", "Add Contact :Smart Contact Manager");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}

	@PostMapping("/process-contact")
	public String processContact(@Valid @ModelAttribute Contact contact, BindingResult result,
			@RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
		try {

			String email = principal.getName();
			User user = userRepository.getUserByUserName(email);

			// processing and uploading image
			if (file.isEmpty()) {
				System.out.println("File is Empty");
				contact.setImage("contact.png");
//				throw new Exception("File is Empty!!");
			} else {
				contact.setImage(file.getOriginalFilename());
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image is uploaded");
			}
			if (result.hasErrors()) {
				System.out.println(result);
				model.addAttribute("contact", contact);
				return "normal/add_contact_form";
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			userRepository.save(user);
			System.out.println("Data " + contact);
			System.out.println("Added data");
			session.setAttribute("message", new Message("Your Contact is Added Successfully !! ", "success"));

		} catch (Exception e) {
			System.out.println("Error " + e.getMessage());
			e.printStackTrace();
			model.addAttribute("contact", contact);
			session.setAttribute("message",
					new Message("Something went wrong!! Try again......" + e.getMessage(), "danger"));
			return "normal/add_contact_form";
		}
		return "normal/add_contact_form";
	}
//show contact handler
	// per page =5(n)
	// current page=0[page]

	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		model.addAttribute("title", "Show-contact : Smart Contact Manager");
		// contact list
		String email = principal.getName();
		User user = userRepository.getUserByUserName(email);
		Pageable pageable = PageRequest.of(page, 8);
		Page<Contact> contacts = contactRepository.findAllContactsByUser(user.getId(), pageable);
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		return "normal/show_contacts";
	}

	// showing particular conatct details
	@RequestMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("cId" + cId);
		model.addAttribute("title", "Contact :Smart Contact Manager");
		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = userRepository.getUserByUserName(userName);
		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
		}

		return "normal/contact_details";
	}

	@GetMapping("/delete/{cid}")
	@Transactional
	public String deleteContact(@PathVariable("cid") Integer cId, Model model, HttpSession session,
			Principal principal) {
		System.out.println("cId" + cId);
		Contact contact = contactRepository.findById(cId).get();
		User user = userRepository.getUserByUserName(principal.getName());
		user.getContacts().remove(contact);
		userRepository.save(user);
		System.out.println("Deleted");
		session.setAttribute("message", new Message("Contact Deleted Successfully !!", "success"));
		return "redirect:/user/show-contacts/0";
	}

//open update contact handler
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") Integer cid, Model model) {
		Contact contact = contactRepository.findById(cid).get();
		model.addAttribute("contact", contact);
		model.addAttribute("title", "Update Contact : Smart Contact Manager");

		return "normal/update_contact_form";
	}

	// process update contact
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact, Model model,
			@RequestParam("profileImage") MultipartFile file, HttpSession session, Principal principal) {
		try {
			Contact oldContactDetails = contactRepository.findById(contact.getcId()).get();
			// image
			if (!file.isEmpty()) {
				// file work
				// rewrite

				// delete old photo
				File deleteFile = new ClassPathResource("static/images").getFile();
				File file1 = new File(deleteFile, oldContactDetails.getImage());
				file1.delete();
				// update new photo
				File saveFile = new ClassPathResource("static/images").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				contact.setImage(file.getOriginalFilename());
			} else {
				contact.setImage(oldContactDetails.getImage());
			}
			User user = userRepository.getUserByUserName(principal.getName());
			contact.setUser(user);
			contactRepository.save(contact);
			session.setAttribute("message", new Message("Your Contact is Updated Successfuly..", "success"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "redirect:/user/" + contact.getcId() + "/contact";
	}

	// profile handler
	@GetMapping("/profile")
	public String viewProfile(Model model) {
		model.addAttribute("title", "Profile page : Smart Contact Manager");
		return "normal/view_profile";
	}

	// delete user
//	@PostMapping("/deleteUser/{id}")
//	public String deleteUser(@PathVariable("id") int id,Principal principal,HttpSession session) {
//		User user = userRepository.getUserByUserName(principal.getName());
//		int id1 = user.getId();
//		userRepository.deleteById(id1);
//		session.setAttribute("message", new Message("User Deleted Successfully !!", "success"));
//		return "redirect:/signup";
//	}
//	@GetMapping("/deleteUser/{id}")
//	public String deleteUser(@PathVariable("id") int id, HttpSession session) {
//		System.out.println(id);
//	    userRepository.deleteById(id);
//	    session.setAttribute("message", new Message("User Deleted Successfully !!", "success"));
//	    return "normal/view_profile";
//	}

	// Open setting handler
	@GetMapping("/settings")
	public String openSetings(Model model) {
		model.addAttribute("title", "Settings : Smart Contact Manager");
		return "normal/settings";
	}

	// change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session,Model model) {
		String userName = principal.getName();
		model.addAttribute("title", "Change Password :Smart Contact Manager");
		User currentUser = userRepository.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		if (bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) {
			// change password
			currentUser.setPassword(bCryptPasswordEncoder.encode(newPassword));
			userRepository.save(currentUser);
			session.setAttribute("message",
					new Message("Your Password Updated Successfully, add your contact !!", "success"));

		} else {
			// error

			session.setAttribute("message", new Message("Please enter correct old password", "danger"));
			return "redirect:/user/settings";
		}

		return "redirect:/user/dashboard";
	}

}
