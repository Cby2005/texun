package com.agriculture.system.service;

import com.agriculture.common.security.LoginUser;
import java.util.List;

public interface LoginService {
    LoginUser authenticate(String username, String password);
    List<String> getUserRoles(Long userId);
}
