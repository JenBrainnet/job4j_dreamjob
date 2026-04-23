package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.sql2o.Sql2o;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.Properties;

import static org.assertj.core.api.Assertions.*;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    private static Sql2o sql2o;

    @BeforeAll
    static void initRepository() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oUserRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        sql2o = configuration.databaseClient(datasource);
        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    void clearUsers() {
        try (var connection = sql2o.open()) {
            connection.createQuery("DELETE FROM users").executeUpdate();
        }
    }

    @Test
    void whenSaveUserThenGetUserByEmailAndPassword() {
        var user = sql2oUserRepository.save(
                new User(0, "test@mail.ru", "John", "1234")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(savedUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void whenFindByWrongEmailThenGetEmptyOptional() {
        var user = sql2oUserRepository.save(
                new User(0, "test@mail.ru", "John", "1234")).get();
        var result = sql2oUserRepository.findByEmailAndPassword("john@mail.ru", user.getPassword());
        assertThat(result).isEmpty();
    }

    @Test
    void whenFindByWrongPasswordThenGetEmptyOptional() {
        var user = sql2oUserRepository.save(
                new User(0, "test@mail.ru", "John", "1234")).get();
        var result = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), "4321");
        assertThat(result).isEmpty();
    }

    @Test
    void whenSaveTwoUsersWithSameEmailThenSecondUserNotSaved() {
        var user1 = sql2oUserRepository.save(
                new User(0, "test@mail.ru", "Ivan", "1234"));
        var user2 = sql2oUserRepository.save(
                new User(0, "test@mail.ru", "John", "4321"));
        assertThat(user1).isPresent();
        assertThat(user2).isEmpty();
    }

}


