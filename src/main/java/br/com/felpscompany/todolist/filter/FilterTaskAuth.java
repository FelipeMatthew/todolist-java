package br.com.felpscompany.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.felpscompany.todolist.user.InterfaceUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

  @Autowired
  private InterfaceUserRepository interfaceUserRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
        // Catch the path that needs auth
        var serveletPath = request.getServletPath();

        // Only apply for this route
        if(serveletPath.startsWith("/tasks/")) {
          // Get auth - user/pass
          // Return a Base64 code and needs to decodify
          var authorization = request.getHeader("Authorization");

          // Get just the code
          var authEncoded = authorization.substring("Basic".length()).trim();

          // Converter e vai virar um array de bites
          byte[] authDecoded = Base64.getDecoder().decode(authEncoded);

          // conver bytes arrays to string
          var authString = new String(authDecoded);

          // Do the division on user and pass return an array
          String[] credentials = authString.split(":");

          // Do the tratatives about this array
          var username = credentials[0];
          var password = credentials[1];

          // User validator
          var user = this.interfaceUserRepository.findByUsername(username);

          if(user == null) {
            response.sendError(401, "User dont have autorization");
          } else {
            // pass validator
            // get the local pass and compare with userPassword table
            var passwordVerify =
            BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

            if(passwordVerify.verified) {
              // get the iduser automatically
              request.setAttribute("idUser", user.getId());
              filterChain.doFilter(request, response);
            } else {
              response.sendError(401, "Password");
            }
          }
        } else {
            filterChain.doFilter(request, response);
        }





      // Return code
  }



}
