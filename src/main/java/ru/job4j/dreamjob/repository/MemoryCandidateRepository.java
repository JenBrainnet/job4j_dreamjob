package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
@ThreadSafe
public class MemoryCandidateRepository implements CandidateRepository {

    private final AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    public MemoryCandidateRepository() {
        save(new Candidate(0, "Ivan Ivanov",
                "Junior Java Developer with basic Spring knowledge",
                LocalDateTime.now(), 1));
        save(new Candidate(0, "Petr Petrov",
                "Middle Java Developer experienced in REST APIs",
                LocalDateTime.now(), 2));
        save(new Candidate(0, "Sergey Sergeev",
                "Senior Java Developer, microservices and Docker",
                LocalDateTime.now(), 3));
        save(new Candidate(0, "Bob Brown",
                "Frontend developer with React and JavaScript",
                LocalDateTime.now(), 1));
        save(new Candidate(0, "Rob Wilson",
                "QA engineer with automation testing skills",
                LocalDateTime.now(), 2));
        save(new Candidate(0, "John Miller",
                "DevOps engineer, CI/CD and Kubernetes",
                LocalDateTime.now(), 3));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(candidate.getId(),
                (id, oldCandidate) -> new Candidate(
                        id,
                        candidate.getName(),
                        candidate.getDescription(),
                        oldCandidate.getCreatedAt(),
                        candidate.getCityId()
                )) != null;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return new ArrayList<>(candidates.values());
    }

}
