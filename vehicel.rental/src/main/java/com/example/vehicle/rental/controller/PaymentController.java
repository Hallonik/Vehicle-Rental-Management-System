package com.example.vehicle.rental.controller;


import com.example.vehicle.rental.dto.PaymentRequestDto;
import com.example.vehicle.rental.dto.RefundRequestDto;
import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.dto.TransactionDTO;
import com.example.vehicle.rental.model.PaymentRequest;
import com.example.vehicle.rental.model.TransactionHistory;
import com.example.vehicle.rental.service.PaymentProcessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/paymentprocess")
@Tag(name = "PaymentController", description = "Endpoints for Payments and related operations")
public class PaymentController {



    private static final Logger log = LogManager.getLogger(PaymentController.class);
    public PaymentProcessService paymentProcessService;


    public PaymentController(PaymentProcessService paymentProcessService) {
        this.paymentProcessService = paymentProcessService;
    }


    @PostMapping(
            path = "/PaymentProcess",
    consumes =  "application/json",
    produces =  "application/json")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequestDto paymentData) {
        try {

            return paymentProcessService.paymentProcess(paymentData);

        } catch (StripeException e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    //TODO: Send payment table Id
    @GetMapping(path = "/Transactions/{id}",
    produces =  "application/json")
    public ResponseEntity<ResultDTO<List<TransactionDTO>>> getAllPaymentIntents(@PathVariable("id") Long id) {
        try {

            return paymentProcessService.transactionList(id);

        } catch (StripeException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(path = "/AllTransactions",
            produces =  "application/json")
    public ResponseEntity<?> getAllTransaction() {
        try {

            return paymentProcessService.allTransactionList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: Send payment table Id
    @PostMapping(path = "/RefundByTransactionId",
            consumes =  "application/json",
            produces =  "application/json")
    public ResponseEntity<ResultDTO<PaymentRequest>> refundCharge(@RequestBody RefundRequestDto refundRequestDto) {
        try {
            return paymentProcessService.refund(refundRequestDto);

        } catch (StripeException e) {
            log.error("Error processing refund: {}", e.getMessage(), e);

            ResultDTO<PaymentRequest> resultDTO = new ResultDTO<>();
            resultDTO.setMessage("Refund failed: " + e.getMessage());
            resultDTO.setStatusCode(400);
            return ResponseEntity.badRequest().body(resultDTO);

        } catch (JsonProcessingException e) {
            ResultDTO<PaymentRequest> resultDTO = new ResultDTO<>();
            resultDTO.setMessage("JSON processing error: " + e.getMessage());
            resultDTO.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultDTO);
        }
    }

    //TODO: Send payment table Id
    @GetMapping(path = "/GetPaymentStatus/{id}",
            produces =  "application/json")
    public ResponseEntity<?> getStatus(@PathVariable("id") Long id) {
       try{
           return paymentProcessService.findStatus(id);
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Stripe error: " + e.getMessage() + " | code: " + e.getCode());
        }
    }

    //TODO: Send payment table Id
    @GetMapping(path = "/GetPaymentInvoice/{id}",
            produces =  "application/json")
    public ResponseEntity<?> getPaymentInvoice(@PathVariable("id") Long id) {

        try {
           return paymentProcessService.getPaymentDetail(id);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }
    
    
    @GetMapping(path = "/AllPayments", produces = "application/json")
    public ResponseEntity<ResultDTO<List<PaymentRequest>>> getAllPayments() {
        try {
            return paymentProcessService.getAllPayments();
        } catch (Exception e) {
            ResultDTO<List<PaymentRequest>> resultDTO = new ResultDTO<>();
            resultDTO.setMessage("Error fetching payments: " + e.getMessage());
            resultDTO.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultDTO);
        }
    }
    
    @DeleteMapping("/delete/{paymentId}")
    public ResponseEntity<ResultDTO<String>> deletePayment(@PathVariable Long paymentId) {
        try {
            return paymentProcessService.deletePayment(paymentId);
        } catch (Exception e) {
            ResultDTO<String> resultDTO = new ResultDTO<>();
            resultDTO.setMessage("Error deleting payment: " + e.getMessage());
            resultDTO.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultDTO);
        }
    }
    
    
    
    @GetMapping("/countByMode")
    public ResponseEntity<ResultDTO<Map<String, Long>>> getPaymentCountByMode() {
        return paymentProcessService.getPaymentCountByMode();
    }
    
    
    @GetMapping("/getPaymentIdByBookingId/{bookingId}")
    public ResponseEntity<ResultDTO<Long>> getPaymentIdByBookingId(@PathVariable Long bookingId) {
        try {
            return paymentProcessService.getPaymentIdByBookingId(bookingId);
        } catch (Exception e) {
            ResultDTO<Long> resultDTO = new ResultDTO<>();
            resultDTO.setMessage("Error fetching paymentId: " + e.getMessage());
            resultDTO.setStatusCode(500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resultDTO);
        }
    }





}
