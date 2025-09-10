package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Account;
import com.hcmute.fit.toeicrise.models.entities.User;

public interface IUserService {
    User register(Account account, String fullName);

    User findByAccountId(Long accountId);
}
