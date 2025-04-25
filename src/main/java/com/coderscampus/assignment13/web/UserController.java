package com.coderscampus.assignment13.web;

import java.util.Arrays;
import java.util.Set;

import com.coderscampus.assignment13.repository.AccountRepository;
import com.coderscampus.assignment13.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.service.UserService;

@Controller
public class UserController {

	private final UserService userService;
    private final AccountRepository accountRepository;
	private final AccountService accountService;

	public UserController(UserService userService, AccountRepository accountRepository, AccountService accountService) {
		this.userService = userService;
		this.accountRepository = accountRepository;
		this.accountService = accountService;
	}

	@GetMapping("/error")
	public String error() {
		return "error";
	}

	@GetMapping("/register")
	public String getCreateUser(ModelMap model) {
		model.put("user", new User());
		return "register";
	}

	@PostMapping("/register")
	public String postCreateUser(User user) {
		userService.save(user);
		return "redirect:/register";
	}
	@GetMapping("")
	public String sendToHome() {
		return "redirect:/users";
	}

	@GetMapping("/users")
	public String getAllUsers(ModelMap model) {
		Set<User> users = userService.findAll();

		model.put("users", users);
		if (users.size() == 1) {
			model.put("user", users.iterator().next());
		}
		return "users";
	}

	@GetMapping("/users/{userId}")
	public String getOneUser(ModelMap model, @PathVariable Long userId) {
		User user = userService.findById(userId);
		if(user == null) {
			return "userError";
		}
		model.put("users", Arrays.asList(user));
		model.put("user", user);
		return "users";
	}

	@PostMapping("/users/{userId}")
	public String postOneUser(@PathVariable Long userId, User user) {
		User existingUser = userService.findById(userId);
		userService.update(user, existingUser);
		return "redirect:/users/" + user.getUserId();
	}

	@PostMapping("/users/{userId}/delete")
	public String deleteOneUser(@PathVariable Long userId) {
		userService.delete(userId);
		return "redirect:/users";
	}

	@GetMapping("/users/{userId}/accounts/{accountId}")
	public String getOneAccount(ModelMap model, @PathVariable Long userId, @PathVariable Long accountId) {
		User user = userService.findById(userId);

		Account account = accountService.findAccountByUser(user, accountId);

		model.put("user", user);
		model.put("account", account);
		return "accounts";
	}


	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String postAccount(@PathVariable Long userId, @PathVariable Long accountId, Account account) {
    	User user = userService.findById(userId);
		accountService.save(user, accountId, account);
    	return "redirect:/users/" + userId;
}

	@GetMapping("/users/{userId}/accounts")
	public String createAccount(ModelMap model, @PathVariable Long userId) {
    	User user = userService.findById(userId);
    	Account account = new Account();
    	model.put("user", user);
    	model.put("account", account);
    	return "accounts";
	}

	@PostMapping("/users/{userId}/accounts")
	public String postAccount(@PathVariable Long userId, Account account) {
    	User user = userService.findById(userId);
		account.getUsers().add(user);
    	user.getAccounts().add(account);
		accountRepository.save(account);
    	userService.save(user);
    	return "redirect:/users/" + userId;
}
}