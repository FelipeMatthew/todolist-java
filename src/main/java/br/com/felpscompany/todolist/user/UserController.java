package br.com.felpscompany.todolist.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private InterfaceUserRepository userRepository;

  // CREATE
  @PostMapping("/")
  public ResponseEntity createUser(@RequestBody UserModel userModel) {
    var user = this.userRepository.findByUsername(userModel.getUsername());
    var userPassword = userModel.getPassword();

    // User already created?
    if(user != null) {
      ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exist");
    }

    // Hashed password
    var hashedPassword = BCrypt.withDefaults().
    hashToString(12, userPassword.toCharArray());

    userModel.setPassword(hashedPassword);

    var userCreated = this.userRepository.save(userModel);
    return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
  }
}
