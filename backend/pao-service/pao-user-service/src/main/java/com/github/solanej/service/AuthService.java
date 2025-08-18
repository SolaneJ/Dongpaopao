package com.github.solanej.service;

import com.github.solanej.ResponseData;

public interface AuthService {

    ResponseData login(String code);
}
