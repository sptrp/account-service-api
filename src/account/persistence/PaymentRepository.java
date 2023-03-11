package account.persistence;

import account.business.model.entities.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Payment findByPeriodAndEmployee(YearMonth period, String employee);

    List<Payment> findAllByEmployeeOrderByPeriodDesc(String employee);
}
