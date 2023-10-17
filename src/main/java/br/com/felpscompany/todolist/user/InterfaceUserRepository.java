package br.com.felpscompany.todolist.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

// First parm - class, second - type of the id
public interface InterfaceUserRepository extends JpaRepository<UserModel, UUID> {
  UserModel findByUsername(String username);
}
