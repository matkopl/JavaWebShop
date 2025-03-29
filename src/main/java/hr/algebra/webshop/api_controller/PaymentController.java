package hr.algebra.webshop.api_controller;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import hr.algebra.webshop.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@RequestParam double total, @RequestParam String currency) {
        try {
            Payment payment = paymentService.createPayment(total, currency, "paypal", "sale", "Order description");

            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return ResponseEntity.ok(link.getHref());
                }
            }
            return ResponseEntity.badRequest().body("No approval URL found in PayPal response.");
        } catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body("Error creating payment: " + e.getMessage());
        }
    }

    @GetMapping("/success")
    public ResponseEntity<String> executePayment(@RequestParam String paymentId, @RequestParam String payerId) {
        try {
            Payment payment = paymentService.executePayment(paymentId, payerId);

            return ResponseEntity.ok("Payment successfully executed. Transaction ID: " + payment.getId());
        } catch (PayPalRESTException e) {
            return ResponseEntity.badRequest().body("Error executing payment: " + e.getMessage());
        }
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancelPayment() {
        return ResponseEntity.ok("Payment cancelled by user.");
    }
}
