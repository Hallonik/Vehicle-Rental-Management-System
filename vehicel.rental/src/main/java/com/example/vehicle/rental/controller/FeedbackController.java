package com.example.vehicle.rental.controller;

import com.example.vehicle.rental.dto.FeedbackRequest;
import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.model.Feedback;
import com.example.vehicle.rental.repository.FeedbackRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackRepository feedbackRepository;

    public FeedbackController(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    // Customer submits feedback
    @PostMapping("/submit")
    public ResponseEntity<ResultDTO<Feedback>> submitFeedback(@RequestBody FeedbackRequest request) {
        Feedback feedback = new Feedback();
        feedback.setName(request.getName());
        feedback.setEmail(request.getEmail());
        feedback.setRating(request.getRating());
        feedback.setMessage(request.getMessage());
        feedback.setCreatedAt(LocalDateTime.now());

        Feedback saved = feedbackRepository.save(feedback);
        return ResponseEntity.ok(new ResultDTO<>(saved, 200, "Feedback submitted successfully"));
    }

    // Admin views all feedback

    
    
    @GetMapping("/getall")
    public ResponseEntity<ResultDTO<List<Feedback>>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackRepository.findAll();
        return ResponseEntity.ok(new ResultDTO<>(feedbackList,200, "Fetched all feedback successfully"));
    }
    
    
 // Admin deletes feedback by ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResultDTO<Void>> deleteFeedback(@PathVariable Long id) {
        if (!feedbackRepository.existsById(id)) {
            return ResponseEntity.status(404)
                    .body(new ResultDTO<>(null, 404, "Feedback with ID " + id + " not found"));
        }

        feedbackRepository.deleteById(id);
        return ResponseEntity.ok(new ResultDTO<>(null, 200, "Feedback deleted successfully"));
    }
}
