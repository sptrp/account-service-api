package account.presentation.payment;

import account.business.model.entities.Payment;
import account.business.service.PaymentService;
import account.util.misc.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("api/acct/payments")
    public Map<String, String> addPayments(@RequestBody List<@Valid Payment> payments) {
        paymentService.addPayments(payments);

        return Collections.singletonMap("status", "Added successfully!");
    }

    @PutMapping("api/acct/payments")
    public ResponseEntity<Object> changePayment(@RequestBody Payment payment) {
        return paymentService.updatePayment(payment);
    }

    @SuppressWarnings("unchecked")
    @GetMapping("api/empl/payment")
    public <T> T getPayments(@RequestParam(required = false) String period, Authentication auth) {
        if (period == null) {
            return (T) ResponseEntity.ok(paymentService.getPayments(auth.getName()));
        }
        YearMonth periodToYearMonth = DateUtil.stringToYearMonth(period);

        return (T) paymentService.getPayment(periodToYearMonth, auth.getName());
    }
}
