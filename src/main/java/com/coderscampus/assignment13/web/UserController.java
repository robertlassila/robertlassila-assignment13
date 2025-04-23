package com.coderscampus.assignment13.web;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.coderscampus.assignment13.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	private UserService userService;
    @Autowired
    private AccountRepository accountRepository;

	@GetMapping("/register")
	public String getCreateUser(ModelMap model) {
		model.put("user", new User());
		return "register";
	}

	@PostMapping("/register")
	public String postCreateUser(User user) {
		System.out.println(user);
		userService.saveUser(user);
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
		model.put("users", Arrays.asList(user));
		model.put("user", user);
		return "users";
	}

	@PostMapping("/users/{userId}")
	public String postOneUser(@PathVariable Long userId, User user) {
		User existingUser = userService.findById(userId);
    	existingUser.setUsername(user.getUsername());
    	existingUser.setName(user.getName());
    	if (user.getPassword() != null && !user.getPassword().isEmpty()) {
        	existingUser.setPassword(user.getPassword());
   	 	}
    	if (user.getAddress() != null) {
        if (existingUser.getAddress() == null) {
            existingUser.setAddress(user.getAddress());
        } else {
            existingUser.getAddress().setAddressLine1(user.getAddress().getAddressLine1());
            existingUser.getAddress().setAddressLine2(user.getAddress().getAddressLine2());
            existingUser.getAddress().setCity(user.getAddress().getCity());
            existingUser.getAddress().setRegion(user.getAddress().getRegion());
            existingUser.getAddress().setCountry(user.getAddress().getCountry());
            existingUser.getAddress().setZipCode(user.getAddress().getZipCode());
			}
    	}
    	userService.saveUser(existingUser);
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

		Account account = user.getAccounts().stream()
        .filter(acc -> acc.getAccountId().equals(accountId))
        .findFirst()
        .orElse(null);

		model.put("user", user);
		model.put("account", account);
		return "accounts";
	}

	@PostMapping("/users/{userId}/accounts/{accountId}")
	public String postAccount(@PathVariable Long userId, @PathVariable Long accountId, Account account) {
    	User user = userService.findById(userId);
		for (Account acc : user.getAccounts()) {
        if (acc.getAccountId().equals(accountId)) {
            acc.setAccountName(account.getAccountName());
            account = acc;
            break;
        }
    }
		accountRepository.save(account);
    	userService.saveUser(user);
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
    	userService.saveUser(user);
    	return "redirect:/users/" + userId;
}
}