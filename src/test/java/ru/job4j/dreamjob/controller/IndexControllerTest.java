package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IndexControllerTest {

    private IndexController indexController;

    @BeforeEach
    void initServices() {
        indexController = new IndexController();
    }

    @Test
    void whenRequestIndexPageThenGetIndexPage() {
        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }

}
