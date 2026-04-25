package ru.job4j.dreamjob.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String getRegistrationPage(Model model, HttpSession session) {
        addUserToModel(model, session);
        return "users/register";
    }

    @PostMapping("/register")
    public String register(Model model, @ModelAttribute User user, HttpSession session) {
        var savedUser = userService.save(user);
        if (savedUser.isEmpty()) {
            addUserToModel(model, session);
            model.addAttribute("message", "A user with this email already exists");
            return "errors/404";
        }
        return "redirect:/vacancies";
    }

    @GetMapping("/login")
    public String getLoginPage(Model model, HttpSession session) {
        addUserToModel(model, session);
        return "users/login";
    }

    @PostMapping("/login")
    public String loginUser(@ModelAttribute User user, Model model, HttpServletRequest request) {
        var userOptional = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        var session = request.getSession();
        if (userOptional.isEmpty()) {
            addUserToModel(model, session);
            model.addAttribute("error", "Invalid email or password");
            return "users/login";
        }
        session.setAttribute("user", userOptional.get());
        return "redirect:/vacancies";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }

    private void addUserToModel(Model model, HttpSession session) {
        var user = (User) session.getAttribute("user");
        if (user == null) {
            user = new User();
            user.setName("Guest");
        }
        model.addAttribute("user", user);
    }

}
