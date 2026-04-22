package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.File;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class Sql2oCandidateRepositoryTest {

    private static Sql2oCandidateRepository sql2oCandidateRepository;

    private static Sql2oFileRepository sql2oFileRepository;

    private static File file;

    @BeforeAll
    static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader()
                .getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oCandidateRepository = new Sql2oCandidateRepository(sql2o);
        sql2oFileRepository = new Sql2oFileRepository(sql2o);

        file = new File("test", "test");
        sql2oFileRepository.save(file);
    }

    @AfterAll
    static void deleteFile() {
        sql2oFileRepository.deleteById(file.getId());
    }

    @AfterEach
    void clearCandidates() {
        var candidates = sql2oCandidateRepository.findAll();
        for (var candidate : candidates) {
            sql2oCandidateRepository.deleteById(candidate.getId());
        }
    }

    @Test
    void whenSaveThenGetSame() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(
                0, "title", "description", creationDate, 1, file.getId())
        );
        var savedCandidate = sql2oCandidateRepository.findById(candidate.getId()).get();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(candidate);
    }

    @Test
    void whenSaveSeveralThenGetAll() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate1 = sql2oCandidateRepository.save(new Candidate(
                0, "title1", "description1", creationDate, 1, file.getId())
        );
        var candidate2 = sql2oCandidateRepository.save(new Candidate(
                0, "title2", "description2", creationDate, 1, file.getId())
        );
        var candidate3 = sql2oCandidateRepository.save(new Candidate(
                0, "title3", "description3", creationDate, 1, file.getId())
        );
        var result = sql2oCandidateRepository.findAll();
        assertThat(result).isEqualTo(List.of(candidate1, candidate2, candidate3));
    }

    @Test
    void whenDontSaveThenNothingFound() {
        assertThat(sql2oCandidateRepository.findAll()).isEqualTo(Collections.emptyList());
        assertThat(sql2oCandidateRepository.findById(0)).isEqualTo(Optional.empty());
    }

    @Test
    void whenDeleteThenGetEmptyOptional() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(
                0, "title", "description", creationDate, 1, file.getId())
        );
        var isDeleted = sql2oCandidateRepository.deleteById(candidate.getId());
        var savedCandidate = sql2oCandidateRepository.findById(candidate.getId());
        assertThat(isDeleted).isTrue();
        assertThat(savedCandidate).isEqualTo(Optional.empty());
    }

    @Test
    void whenDeletedByInvalidIdThenGetFalse() {
        assertThat(sql2oCandidateRepository.deleteById(0)).isFalse();
    }

    @Test
    void whenUpdateThenGetUpdated() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = sql2oCandidateRepository.save(new Candidate(
                0, "title", "description", creationDate, 1, file.getId())
        );
        var updatedCandidate = new Candidate(
                candidate.getId(), "new title", "new description",
                creationDate, 1, file.getId()
        );
        var isUpdated = sql2oCandidateRepository.update(updatedCandidate);
        var savedCandidate = sql2oCandidateRepository.findById(updatedCandidate.getId()).get();
        assertThat(isUpdated).isTrue();
        assertThat(savedCandidate).usingRecursiveComparison().isEqualTo(updatedCandidate);
    }

    @Test
    void whenUpdateUnexistingCandidateThenGetFalse() {
        var creationDate = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var candidate = new Candidate(
                0, "title", "description", creationDate, 1, file.getId()
        );
        var isUpdated = sql2oCandidateRepository.update(candidate);
        assertThat(isUpdated).isFalse();
    }

}