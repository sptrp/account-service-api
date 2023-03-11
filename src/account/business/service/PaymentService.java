package account.business.service;

import account.business.model.entities.Payment;
import account.business.model.entities.User;
import account.business.model.responses.PaymentDataResponse;
import account.persistence.PaymentRepository;
import account.persistence.UserRepository;
import account.util.misc.DateUtil;
import account.util.handlers.ResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.*;

@Service
public class PaymentService {

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    UserRepository userRepository;

    @Transactional
    public void addPayments(List<Payment> payments) {

        paymentRepository.saveAll(payments);
    }

    public List<PaymentDataResponse> getPayments(String employee) {
        User user = userRepository.findUserByEmail(employee).orElse(null);
        List<Payment> paymentList = paymentRepository.findAllByEmployeeOrderByPeriodDesc(employee);
        List<PaymentDataResponse> response = new ArrayList<>();

        for (Payment payment: paymentList) {
            String periodStr = DateUtil.yearMonthToString(payment.getPeriod());

            response.add(new PaymentDataResponse(user.getName(), user.getLastname(), periodStr, payment.getSalary().toString()));
        }
        return response;
    }

    public PaymentDataResponse getPayment(YearMonth period, String employee) {
        User user = userRepository.findUserByEmail(employee).orElse(null);
        Payment payment = paymentRepository.findByPeriodAndEmployee(period, employee);
        String periodStr = DateUtil.yearMonthToString(payment.getPeriod());

        return new PaymentDataResponse(user.getName(), user.getLastname(), periodStr, payment.getSalary().toString());
    }

    public ResponseEntity<Object> updatePayment(Payment payment) {
        if (payment.getSalary() <= 0) {
            return ResponseHandler.generateBadRequestResponse("/api/acct/payments");
        }

        Payment paymentToUpd = paymentRepository.findByPeriodAndEmployee(payment.getPeriod(), payment.getEmployee());

        paymentToUpd.setSalary(payment.getSalary());
        paymentRepository.save(paymentToUpd);

        Map<String, Object> map = new HashMap<>();
        map.put("status", "Updated successfully!");

        return new ResponseEntity<>(map, HttpStatus.OK);
    }
}
