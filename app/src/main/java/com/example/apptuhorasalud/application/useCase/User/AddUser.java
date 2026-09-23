package com.example.apptuhorasalud.application.useCase.User;

import com.example.apptuhorasalud.domain.models.User;
import com.example.apptuhorasalud.domain.interfaces.IUserRepository;

public class AddUser {

    private final IUserRepository repo;

    public AddUser(IUserRepository repo){
        this.repo= repo;
    }

    public void execute(User user){
        repo.addUser(user);
    }

}
