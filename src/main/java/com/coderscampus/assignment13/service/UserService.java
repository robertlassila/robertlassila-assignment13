package com.coderscampus.assignment13.service;

import java.util.Optional;
import java.util.Set;

import com.coderscampus.assignment13.repository.AccountRepository;
import org.springframework.stereotype.Service;
import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.repository.UserRepository;

@Service
public class UserService {

	private final UserRepository userRepo;
	private final AccountRepository accountRepository;

	public UserService(UserRepository userRepo, AccountRepository accountRepository) {
		this.userRepo = userRepo;
		this.accountRepository = accountRepository;
	}

	public Set<User> findAll () {
		return userRepo.findAllUsersWithAccountsAndAddresses();
	}

	public User findById(Long userId) {
		Optional<User> userOpt = userRepo.findById(userId);
		return userOpt.orElse(null);
	}

	public User save(User user) {
		if (user.getUserId() == null) {
			Account checking = new Account();
			checking.setAccountName("Checking Account");
			checking.getUsers().add(user);
			Account savings = new Account();
			savings.setAccountName("Savings Account");
			savings.getUsers().add(user);

			user.getAccounts().add(checking);
			user.getAccounts().add(savings);
			accountRepository.save(checking);
			accountRepository.save(savings);
		}
		return userRepo.save(user);
	}

	public void delete(Long userId) {
		userRepo.deleteById(userId);
	}

	public void update(User user, User existingUser) {
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
		save(existingUser);
	}
}