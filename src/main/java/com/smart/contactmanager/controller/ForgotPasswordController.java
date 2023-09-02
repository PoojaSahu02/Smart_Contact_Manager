package com.smart.contactmanager.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.contactmanager.dao.UserRepository;
import com.smart.contactmanager.model.User;
import com.smart.contactmanager.service.EmailService;

@Controller
public class ForgotPasswordController {
	Random random = new Random(100000);
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

// email Id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm(Model model) {
		model.addAttribute("title", "Forgot Password : Smart Contact Manager");
		return "forgot_password_form";
	}

	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, HttpSession session, Model model) {
		System.out.println("Email " + email);
		model.addAttribute("title", "OTP Page : Smart Contact Manager");
		// generation OTP 4 digit
		int otp = random.nextInt(999999);
		System.out.println("OTP " + otp);

		// write code for send otp to email
		String subject = "OTP From Contact Diary";
		String message = "" + "<div style='border:1px solid #e2e2e2; padding:20px'>" + "<h1>" + "OTP is :- " + "<b>"
				+ otp + "</n>" + "</h1>" + "</div>";
		String to = email;
		boolean flag = emailService.sendEmail(subject, message, to);
		if (flag) {
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";

		} else {
			session.setAttribute("message", "check your email Id !!");
			return "forgot_password_form";
		}

	}

	// verify otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp, HttpSession session, Model model) {
		model.addAttribute("title", "Verify OTP : Smart Contact Manager");
		int myotp = (int) session.getAttribute("myotp");
		String email = (String) session.getAttribute("email");
		if (myotp == otp) {
			// password change form
			User user = userRepository.getUserByUserName(email);
			if (user == null) {
				// send error message
				session.setAttribute("message", "User Does not exist with this email!!");
				return "forgot_email_form";
			} else {
				// send change password form
			}
			return "password_change_form";
		} else {
			session.setAttribute("message", "you have entered wrong otp");
			return "verify_otp";
		}
	}

	// change password
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword") String newpassword, HttpSession session, Model model) {
		model.addAttribute("title", "Change Password : Smart Contact Manager");
		String email = (String) session.getAttribute("email");
		User user = userRepository.getUserByUserName(email);
		user.setPassword(bCryptPasswordEncoder.encode(newpassword));
		userRepository.save(user);
		return "redirect:/signin?change=password changed successfully.....";
	}
}
