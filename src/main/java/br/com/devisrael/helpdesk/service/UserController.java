package br.com.devisrael.helpdesk.service;

import br.com.devisrael.helpdesk.api.entity.Response;
import br.com.devisrael.helpdesk.api.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> create(HttpServletRequest request, @RequestBody User user,
                                                 BindingResult bindingResult) {
        var response = new Response<User>();
        try {
            validateCreateUser(user, bindingResult);
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(objectError -> response.getErros().add(objectError.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            var userPersisted = userService.createOrUpdate(user);
            response.setData(userPersisted);
        } catch (Exception exception) {
            response.getErros().add(exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

    private void validateCreateUser(User user, BindingResult bindingResult) {
        if (user.getEmail() == null) {
            bindingResult.addError(new ObjectError("User", "Email no information"));
        }
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> update(HttpServletRequest request, @RequestBody User user,
                                                 BindingResult bindingResult) {
        var response = new Response<User>();
        try {
            validateUpdateUser(user, bindingResult);
            if (bindingResult.hasErrors()) {
                bindingResult.getAllErrors().forEach(objectError -> response.getErros().add(objectError.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            response.setData(userService.createOrUpdate(user));
        } catch (Exception exception) {
            response.getErros().add(exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateUpdateUser(User user, BindingResult bindingResult) {
        if (user.getId() == null) {
            bindingResult.addError(new ObjectError("User", "Id no information"));
        }
        if (user.getEmail() == null) {
            bindingResult.addError(new ObjectError("User", "Email no information"));
        }
    }

    @GetMapping(value = {"{id}"})
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<User>> findById(@PathVariable("id") String id) {
        var response = new Response<User>();
        try {
            response.setData(userService.findById(id).orElseThrow());
        } catch (Exception exception) {
            response.getErros().add(exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = {"{id}"})
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
        var response = new Response<String>();
        try {
            userService.delete(id);
        } catch (Exception exception) {
            response.getErros().add(exception.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(new Response<String>());
    }

    @GetMapping(value = "{page/count}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Response<Page<User>>> findAll(@PathVariable int page, @PathVariable int count) {
        var pageResponse = new Response<Page<User>>();
        var all = userService.findAll(page, count);
        pageResponse.setData(all);
        return ResponseEntity.ok(pageResponse);
    }
}
