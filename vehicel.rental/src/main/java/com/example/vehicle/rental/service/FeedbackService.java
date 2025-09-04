package com.example.vehicle.rental.service;

import com.example.vehicle.rental.model.Feedback;
import com.example.vehicle.rental.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedbackService {
    private final FeedbackRepository repo;

    public FeedbackService(FeedbackRepository repo) {
        this.repo = repo;
    }

    public Feedback saveFeedback(Feedback f) {
        return repo.save(f);
    }

    public List<Feedback> getAllFeedback() {
        return repo.findAll();
    }
}