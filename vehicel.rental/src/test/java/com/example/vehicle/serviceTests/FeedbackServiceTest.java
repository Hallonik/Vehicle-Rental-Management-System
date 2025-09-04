package com.example.vehicle.serviceTests;

import com.example.vehicle.rental.model.Feedback;
import com.example.vehicle.rental.repository.FeedbackRepository;
import com.example.vehicle.rental.service.FeedbackService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeedbackServiceTest {

    @Mock
    private FeedbackRepository feedbackRepository;

    @InjectMocks
    private FeedbackService feedbackService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveFeedback() {
        // Arrange
        Feedback feedback = new Feedback();
        feedback.setId(1L);
        feedback.setMessage("Great service!");

        when(feedbackRepository.save(feedback)).thenReturn(feedback);

        // Act
        Feedback savedFeedback = feedbackService.saveFeedback(feedback);

        // Assert
        assertNotNull(savedFeedback);
        assertEquals(1L, savedFeedback.getId());
        assertEquals("Great service!", savedFeedback.getMessage());

        verify(feedbackRepository, times(1)).save(feedback);
    }

    @Test
    void testGetAllFeedback() {
        // Arrange
        Feedback f1 = new Feedback();
        f1.setId(1L);
        f1.setMessage("Excellent!");

        Feedback f2 = new Feedback();
        f2.setId(2L);
        f2.setMessage("Needs improvement");

        List<Feedback> feedbackList = Arrays.asList(f1, f2);

        when(feedbackRepository.findAll()).thenReturn(feedbackList);

        // Act
        List<Feedback> result = feedbackService.getAllFeedback();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Excellent!", result.get(0).getMessage());
        assertEquals("Needs improvement", result.get(1).getMessage());

        verify(feedbackRepository, times(1)).findAll();
    }
}
