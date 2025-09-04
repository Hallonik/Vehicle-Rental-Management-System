package com.example.vehicle.rental.service;


import com.example.vehicle.rental.dto.PaymentRequestDto;
import com.example.vehicle.rental.dto.RefundRequestDto;
import com.example.vehicle.rental.dto.ResultDTO;
import com.example.vehicle.rental.dto.TransactionDTO;
import com.example.vehicle.rental.model.*;
import com.example.vehicle.rental.repository.PaymentRequestRepository;
import com.example.vehicle.rental.repository.TransactionRepository;
import com.example.vehicle.rental.repository.UserRepository;
import com.example.vehicle.rental.repository.VehicleRepository;
import com.example.vehicle.rental.util.Contants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.model.Refund;
import com.stripe.net.RequestOptions;
import com.stripe.param.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.vehicle.rental.util.Contants.SECRET_KEY;

@Service
public class PaymentProcessService {

    public PaymentRequestRepository paymentRequestRepository;
    public UserRepository userRepository;
    public TransactionRepository transactionRepository;
    public VehicleRepository vehicleRepository;


    public PaymentProcessService(PaymentRequestRepository paymentRequestRepository, UserRepository userRepository, TransactionRepository transactionRepository,VehicleRepository vehicleRepository) {
        this.paymentRequestRepository = paymentRequestRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.vehicleRepository = vehicleRepository;
    }


    public RequestOptions auth() {
        return RequestOptions.builder()
                .setApiKey(SECRET_KEY)
                .build();
    }

    public ResponseEntity<?> paymentProcess(PaymentRequestDto paymentData) throws StripeException, JsonProcessingException {

        var user = userRepository.findById(paymentData.getUserId());

        if (user.isEmpty()) {
            return ResponseEntity.status(401).body("access denied");
        } else if (Objects.equals(user.get().getRole(), Contants.RoleCheck.ADMIN.getRole())) {
            return ResponseEntity.status(401).body("not valid role");
        }

        var vehicle = vehicleRepository.findById(paymentData.getVehicleId());
        if(vehicle.isEmpty()){
            return ResponseEntity.status(201).body("access denied");

        }
        PaymentRequest paymentRequest = new PaymentRequest();

        paymentRequest.setPaymentFor(paymentData.getPaymentFor());
        paymentRequest.setPaymentMode(paymentData.getPaymentMode());
        paymentRequest.setAmount(paymentData.getAmount());
        paymentRequest.setCurrency(paymentData.getCurrency());
        paymentRequest.setEmail(paymentData.getEmail());
        paymentRequest.setName(paymentData.getName());
        paymentRequest.setVehicleId(paymentData.getVehicleId());
        paymentRequest.setUserId(paymentData.getUserId());
        paymentRequest.setBookingId(paymentData.getBookingId());

        String currency = paymentData.getCurrency();

        BigDecimal amount = paymentData.getAmount();

        if (amount == null) {
            throw new IllegalArgumentException("Amount is null");
        }

        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        if (amountInCents < 1) {
            throw new IllegalArgumentException("Amount in cents must be >= 1 but was " + amountInCents);
        }

        System.out.println("Creating payment intent for amount (cents): " + amountInCents);

        var requestOptions = auth();

        PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency(currency)
                .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true)
                                .setAllowRedirects(
                                        PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER
                                )
                                .build()
                )
                .build();

        Stripe.apiKey = SECRET_KEY;

        PaymentIntent paymentIntent = PaymentIntent.create(createParams, requestOptions);

        paymentRequest.setPaymentIntentId(paymentIntent.getClientSecret().split("_secret_")[0]);
        paymentRequest.setStatus("initiated");
        paymentRequest.setRefundAmount(paymentData.getRefundAmount());
        paymentRequestRepository.save(paymentRequest);

        VehicleInfo vehicleIn = vehicle.get();
        vehicleIn.setVehicle_status("UNAVAILABLE");
        vehicleRepository.save(vehicleIn);

        System.out.println("secrest"+ paymentIntent.getClientSecret());
        Map<String, String> response = new HashMap<>();
        response.put("clientSecret", paymentIntent.getClientSecret());
        return ResponseEntity.ok(response);

    }

    public ResponseEntity<ResultDTO<List<TransactionDTO>>> transactionList(Long id)
            throws StripeException, JsonProcessingException {

        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            ResultDTO<List<TransactionDTO>> resultDTO = new ResultDTO<>();
            resultDTO.setStatusCode(404);
            resultDTO.setMessage("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultDTO);
        }

        User user = userOptional.get();

        List<PaymentRequest> userPayments = paymentRequestRepository.findAllByUserId(id);
        List<String> userPaymentIntentIds = userPayments.stream()
                .map(PaymentRequest::getPaymentIntentId)
                .toList();

        var requestOptions = auth();

        PaymentIntentCollection paymentIntents = PaymentIntent.list(
                PaymentIntentListParams.builder().build(),
                requestOptions
        );

        List<TransactionHistory> transactions = getTransactionHistories(paymentIntents);

        transactionRepository.saveAll(transactions);

        if (!user.getRole().equals(Contants.RoleCheck.ADMIN.getRole())) {
            transactions = transactions.stream()
                    .filter(t -> userPaymentIntentIds.contains(t.getPaymentIntentId()))
                    .collect(Collectors.toList());
        }

        List<TransactionDTO> responseList = userPayments.stream().map(v -> {

            var vehicleName = vehicleRepository.findById(v.getVehicleId());

            TransactionDTO vehicleMap = new TransactionDTO();
            vehicleMap.setAmount(v.getAmount());
            vehicleMap.setVehicleName(vehicleName.get().getVehicle_name());
            vehicleMap.setVehicleType(vehicleName.get().getVehicle_type());
            vehicleMap.setStatus(vehicleName.get().getVehicle_status());
            vehicleMap.setUserName(user.getFullName());
            vehicleMap.setId(v.getVehicleId());
            return vehicleMap;
        }).toList();


        ResultDTO<List<TransactionDTO>> resultDTO = new ResultDTO<>();
        resultDTO.setData(responseList);
        resultDTO.setStatusCode(200);
        resultDTO.setMessage("Data fetched successfully");

        return ResponseEntity.ok(resultDTO);
    }

    private static List<TransactionHistory> getTransactionHistories(PaymentIntentCollection paymentIntents) {
        List<TransactionHistory> transactions = new ArrayList<>();

        for (PaymentIntent result : paymentIntents.getData()) {
            TransactionHistory transactionHistory = new TransactionHistory();
            transactionHistory.setAmount(BigDecimal.valueOf(result.getAmount()));
            transactionHistory.setAmountReceived(BigDecimal.valueOf(result.getAmountReceived()));
            transactionHistory.setCanceledAt(result.getCanceledAt());
            transactionHistory.setPaymentIntentId(result.getId());

            transactions.add(transactionHistory);
        }
        return transactions;
    }

    public ResponseEntity<?> allTransactionList()
            throws StripeException, JsonProcessingException {

        // 1. Get all payment requests from DB
        List<PaymentRequest> userPayments = paymentRequestRepository.findAll();
        List<String> userPaymentIntentIds = userPayments.stream()
                .map(PaymentRequest::getPaymentIntentId)
                .toList();

        // 2. Authenticate with Stripe & fetch all payment intents
        var requestOptions = auth();
        PaymentIntentCollection paymentIntents = PaymentIntent.list(
                PaymentIntentListParams.builder().build(),
                requestOptions
        );

        // 3. Map Stripe payment intents to local transactions
        List<TransactionHistory> transactions = getTransactionHistories(paymentIntents);
        transactionRepository.saveAll(transactions);

        // 4. Get users linked to the payment requests
        List<Long> userIds = userPayments.stream()
                .map(PaymentRequest::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());


        List<User> users = userRepository.findByUserIdIn(userIds);

        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResultDTO<>("User not found",404 , null));
        }

        // 5. Role check — only filter if none of them is admin
//        boolean isAdmin = users.stream()
//                .anyMatch(u -> Contants.RoleCheck.ADMIN.getRole().equals(u.getRole()));
//
//        if (!isAdmin) {
//            transactions = transactions.stream()
//                    .filter(t -> userPaymentIntentIds.contains(t.getPaymentIntentId()))
//                    .toList();
//        }

        // 6. Build the response list
        List<TransactionDTO> responseList = userPayments.stream()
                .filter(Objects::nonNull) // skip null payment requests entirely
                .map(v -> {
                    Long vehicleId = Optional.ofNullable(v.getVehicleId()).orElse(0L); // default 0 if null

                    Optional<VehicleInfo> vehicleOpt = vehicleId != 0
                            ? vehicleRepository.findById(vehicleId)
                            : Optional.empty();

                    String userName = users.stream()
                            .filter(u -> Objects.equals(u.getUserId(), v.getUserId()))
                            .map(User::getFullName)
                            .findFirst()
                            .orElse("Unknown User");

                    TransactionDTO dto = new TransactionDTO();
                    dto.setAmount(v.getAmount());
                    dto.setVehicleName(
                            vehicleOpt.map(VehicleInfo::getVehicle_name).orElse("unknown")
                    );
                    dto.setVehicleType(
                            vehicleOpt.map(VehicleInfo::getVehicle_type).orElse("unknown")
                    );
                    dto.setStatus(
                            vehicleOpt.map(VehicleInfo::getVehicle_status).orElse("unknown")
                    );

                    dto.setPaymentStatus(v.getStatus());
                    dto.setUserName(userName);
                    dto.setId(vehicleId); // already safe
                    return dto;
                })
                .toList();



        // 7. Return result
        return ResponseEntity.ok(responseList);
    }


    public ResponseEntity<ResultDTO<PaymentRequest>> refund(RefundRequestDto refundRequestDto)
            throws StripeException, JsonProcessingException {

        var requestOptions = auth();
        var paymentOpt = paymentRequestRepository.findById(refundRequestDto.getPaymentId());

        if (paymentOpt.isEmpty()) {
            ResultDTO<PaymentRequest> resultDto = new ResultDTO<>();
            resultDto.setMessage("Invalid payment intent ID");
            resultDto.setStatusCode(400);
            return ResponseEntity.badRequest().body(resultDto);
        }

        var payment = paymentOpt.get();

        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getPaymentIntentId())
                .setAmount(refundRequestDto.getAmount().longValue())
                .build();

        Refund refund = Refund.create(params, requestOptions);

        BigDecimal refundAmt = BigDecimal.valueOf(refund.getAmount());
        BigDecimal remainingAmt = payment.getAmount().subtract(refundRequestDto.getAmount());

        payment.setRefundAmount(refundAmt);
        payment.setRemainingAmount(remainingAmt);

        if (Objects.equals(payment.getAmount(), refundAmt)){
            payment.setStatus("Cancelled");
            payment.setRemainingAmount(refundAmt.subtract(payment.getAmount()));
        } else if (refundAmt.compareTo(BigDecimal.valueOf(50)) < 0) {
            payment.setRefundStatus("Security Refund");
        }else{
            payment.setRefundStatus("Additional Refund");
        }

        paymentRequestRepository.save(payment);

        ResultDTO<PaymentRequest> resultDto = new ResultDTO<>();
        resultDto.setData(payment);
        resultDto.setStatusCode(200);
        resultDto.setMessage("Refunded successfully");

        return ResponseEntity.ok(resultDto);
    }


    public ResponseEntity<?> findStatus(Long id) throws StripeException {
        var paymentOptional = paymentRequestRepository.findById(id);

        if (paymentOptional.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        var payment = paymentOptional.get();

               
        

        Stripe.apiKey = SECRET_KEY;

        PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getPaymentIntentId());

        String status = paymentIntent.getStatus();
        System.out.println("PaymentIntent status: " + status);
  
        if(payment.getRefundAmount() != null) {
        if (Objects.equals(payment.getAmount(), payment.getRefundAmount())){
            payment.setStatus("Cancelled");
        } else if (payment.getRefundAmount().compareTo(BigDecimal.valueOf(50)) < 0) {
            payment.setRefundStatus("Partial Cleared");
        }else{
            payment.setRefundStatus("Cleared");
        }
 
        }
        paymentRequestRepository.save(payment);

        payment.setStatus(status);

        paymentRequestRepository.save(payment);

        if ("requires_payment_method".equals(status)) {
            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                    .setPaymentMethod("pm_card_visa")
                    .build();
            paymentIntent = paymentIntent.confirm(confirmParams);
            status = paymentIntent.getStatus();
            System.out.println("After confirm, status: " + status);

        }

        if ("requires_capture".equals(status)) {
            PaymentIntentCaptureParams captureParams = PaymentIntentCaptureParams.builder().build();
            paymentIntent = paymentIntent.capture(captureParams);
            status = paymentIntent.getStatus();
            System.out.println("After capture, status: " + status);
        }


        var result = new ResultDTO<>();
        result.setData(status);
        result.setMessage("Status Updated");
        result.setStatusCode(200);
        return ResponseEntity.ok(result);
    }

    public ResponseEntity<?> getPaymentDetail(Long id) throws StripeException {

        var payment = paymentRequestRepository.findById(id);

        if(payment.isEmpty()){
            return ResponseEntity.badRequest().build();
        }

        Stripe.apiKey = SECRET_KEY;

        PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.get().getPaymentIntentId());

       return ResponseEntity.ok(paymentIntent.toJson());

    }
    
    
    
    public ResponseEntity<ResultDTO<List<PaymentRequest>>> getAllPayments() {
        List<PaymentRequest> payments = paymentRequestRepository.findAll();

        ResultDTO<List<PaymentRequest>> resultDTO = new ResultDTO<>();
        resultDTO.setData(payments);
        resultDTO.setStatusCode(200);
        resultDTO.setMessage(payments.isEmpty() ? "No payments found" : "Payments fetched successfully");

        return ResponseEntity.ok(resultDTO);
    }
    
    
    
    public ResponseEntity<ResultDTO<String>> deletePayment(Long paymentId) {
        Optional<PaymentRequest> paymentOpt = paymentRequestRepository.findById(paymentId);

        ResultDTO<String> resultDTO = new ResultDTO<>();

        if (paymentOpt.isPresent()) {
            paymentRequestRepository.deleteById(paymentId);
            resultDTO.setStatusCode(200);
            resultDTO.setMessage("Payment with ID " + paymentId + " deleted successfully");
            return ResponseEntity.ok(resultDTO);
        } else {
            resultDTO.setStatusCode(404);
            resultDTO.setMessage("Payment with ID " + paymentId + " not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultDTO);
        }
    }
    
    
    public ResponseEntity<ResultDTO<Map<String, Long>>> getPaymentCountByMode() {
        List<Object[]> results = paymentRequestRepository.getPaymentCountByMode();

        Map<String, Long> countMap = new HashMap<>();
        for (Object[] row : results) {
            String mode = (String) row[0];
            Long count = (Long) row[1];
            countMap.put(mode, count);
        }

        ResultDTO<Map<String, Long>> resultDTO = new ResultDTO<>();
        resultDTO.setStatusCode(200);
        resultDTO.setMessage("Payment count fetched successfully");
        resultDTO.setData(countMap);

        return ResponseEntity.ok(resultDTO);
    }
    
    public ResponseEntity<ResultDTO<Long>> getPaymentIdByBookingId(Long bookingId) {
        ResultDTO<Long> resultDTO = new ResultDTO<>();
        Optional<PaymentRequest> paymentOpt = paymentRequestRepository.findByBookingId(bookingId);

        if (paymentOpt.isPresent()) {
            resultDTO.setData(paymentOpt.get().getPaymentId());
            resultDTO.setMessage("PaymentId fetched successfully");
            resultDTO.setStatusCode(200);
            return ResponseEntity.ok(resultDTO);
        } else {
            resultDTO.setMessage("No payment found for bookingId: " + bookingId);
            resultDTO.setStatusCode(404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(resultDTO);
        }
    }





}


