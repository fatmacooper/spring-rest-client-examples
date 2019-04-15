package com.orionsson.springrestclientexamples.service;

import com.orionsson.api.domain.User;

import java.util.List;

public interface ApiService {
    List<User> getUsers(Integer limit);
}
