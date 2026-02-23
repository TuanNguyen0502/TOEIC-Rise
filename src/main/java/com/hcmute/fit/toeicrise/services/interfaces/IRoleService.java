package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.models.entities.Role;
import com.hcmute.fit.toeicrise.models.enums.ERole;

public interface IRoleService {
    Role findByName(ERole roleName);
}
