package com.coderscampus.assignment13.service;

import com.coderscampus.assignment13.domain.Account;
import com.coderscampus.assignment13.domain.User;
import com.coderscampus.assignment13.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    public AccountService(AccountRepository accountRepository, UserService userService) {
        this.accountRepository = accountRepository;
        this.userService = userService;
    }

    public Account findAccountByUser(User user, Long accountId) {
		Account account = user.getAccounts().stream()
        .filter(acc -> acc.getAccountId().equals(accountId))
        .findFirst()
        .orElse(null);
		return account;
	}

    public void save(User user, Long accountId, Account account) {
        for (Account acc : user.getAccounts()) {
        if (acc.getAccountId().equals(accountId)) {
            acc.setAccountName(account.getAccountName());
            account = acc;
            break;
        }
    }
        accountRepository.save(account);
    	userService.save(user);
    }

}
