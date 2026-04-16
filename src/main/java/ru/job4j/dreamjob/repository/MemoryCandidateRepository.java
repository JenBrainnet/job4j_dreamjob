package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan Ivanov",
                "Junior Java Developer with basic Spring knowledge",
                LocalDateTime.now()));
        save(new Candidate(0, "Petr Petrov",
                "Middle Java Developer experienced in REST APIs",
                LocalDateTime.now()));
        save(new Candidate(0, "Sergey Sergeev",
                "Senior Java Developer, microservices and Docker",
                LocalDateTime.now()));
        save(new Candidate(0, "Bob Brown",
                "Frontend developer with React and JavaScript",
                LocalDateTime.now()));
        save(new Candidate(0, "Rob Wilson",
                "QA engineer with automation testing skills",
                LocalDateTime.now()));
        save(new Candidate(0, "John Miller",
                "DevOps engineer, CI/CD and Kubernetes",
                LocalDateTime.now()));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public void deleteById(int id) {
        candidates.remove(id);
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(
                        id,
                        candidate.getName(),
                        candidate.getDescription(),
                        oldCandidate.getCreatedAt()
                )) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }

}
