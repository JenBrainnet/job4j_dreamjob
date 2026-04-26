package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;

    private UserController userController;

    @BeforeEach
    void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    void whenRequestRegistrationPageThenGetPageWithGuestUser() {
        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = userController.getRegistrationPage(model, session);
        var actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("users/register");
        assertThat(actualUser.getName()).isEqualTo("Guest");
    }

    @Test
    void whenRequestRegistrationPageWithUserInSessionThenGetPageWithSessionUser() {
        var expectedUser = new User(1, "user@mail.com", "User", "password");
        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        session.setAttribute("user", expectedUser);

        var view = userController.getRegistrationPage(model, session);

        assertThat(view).isEqualTo("users/register");
        assertThat(model.getAttribute("user")).isSameAs(expectedUser);
    }

    @Test
    void whenRegisterUserThenRedirectToVacanciesPage() {
        var user = new User(1, "user@mail.com", "User", "password");
        var savedUser = new User(2, "user@mail.com", "User", "password");
        when(userService.save(user)).thenReturn(Optional.of(savedUser));

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = userController.register(model, user, session);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    void whenRegisterUserWithExistingEmailThenGetErrorPageWithMessage() {
        var user = new User(1, "user@mail.com", "User", "password");
        when(userService.save(user)).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = userController.register(model, user, session);
        var actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("errors/404");
        assertThat(model.getAttribute("message")).isEqualTo("A user with this email already exists");
        assertThat(actualUser.getName()).isEqualTo("Guest");
    }

    @Test
    void whenRequestLoginPageThenGetPageWithGuestUser() {
        var model = new ConcurrentModel();
        var session = new MockHttpSession();
        var view = userController.getLoginPage(model, session);
        var actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualUser.getName()).isEqualTo("Guest");
    }

    @Test
    void whenLoginUserWithValidCredentialsThenAddUserToSessionAndRedirectToVacanciesPage() {
        var user = new User(1, "user@mail.com", null, "password");
        var expectedUser = new User(2, "user@mail.com", "User", "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.of(expectedUser));

        var model = new ConcurrentModel();
        var request = new MockHttpServletRequest();
        var view = userController.loginUser(user, model, request);
        var actualUser = request.getSession().getAttribute("user");

        assertThat(view).isEqualTo("redirect:/vacancies");
        assertThat(actualUser).isSameAs(expectedUser);
    }

    @Test
    void whenLoginUserWithInvalidCredentialsThenGetLoginPageWithError() {
        var user = new User(1, "user@mail.com", null, "password");
        when(userService.findByEmailAndPassword(user.getEmail(), user.getPassword()))
                .thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var request = new MockHttpServletRequest();
        var view = userController.loginUser(user, model, request);
        var actualUser = (User) model.getAttribute("user");

        assertThat(view).isEqualTo("users/login");
        assertThat(model.getAttribute("error")).isEqualTo("Invalid email or password");
        assertThat(actualUser.getName()).isEqualTo("Guest");
    }

    @Test
    void whenLogoutThenInvalidateSessionAndRedirectToLoginPage() {
        var session = new MockHttpSession();
        var view = userController.logout(session);

        assertThat(view).isEqualTo("redirect:/users/login");
        assertThat(session.isInvalid()).isTrue();
    }

}
