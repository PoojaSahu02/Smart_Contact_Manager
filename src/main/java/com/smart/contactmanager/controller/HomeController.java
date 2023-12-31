package com.smart.contactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contactmanager.dao.UserRepository;
import com.smart.contactmanager.helper.Message;
import com.smart.contactmanager.model.User;

@Controller
public class HomeController {
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/")
	public String Home(Model model) {
		model.addAttribute("title", "Home : Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String About(Model model) {
		model.addAttribute("title", "About : Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String Signup(Model model) {
		model.addAttribute("title", "SignUp : Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if (!agreement) {

				System.out.println("You have not agreed terms and conditions");
				throw new Exception("You have not agreed terms and conditions");
			}
			if (result1.hasErrors()) {
				System.out.println(result1);
				model.addAttribute("user", user);
				return "signup";
			}
			   // Check if the email is already registered
			if (userRepository.existsByEmail(user.getEmail())) {
			    session.setAttribute("message", new Message("Email address is already registered. Please login or signin with other Email", "alert-danger"));
			    return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			System.out.println("Agreement " + agreement);
			System.out.println("User " + user);

			User result = userRepository.save(user);
			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !! please login with username and password ...... ", "alert-success"));
			return "signup";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong!! " + e.getMessage(), "alert-danger"));
			return "signup";

		}

		
	}

	@RequestMapping("/signin")
	public String Login(Model model) {
		model.addAttribute("title", "Login : Smart Contact Manager");
		return "login";
	}
}
