package account.business.model.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.time.YearMonth;

@Entity
@Table(name = "payments", uniqueConstraints=
        { @UniqueConstraint(columnNames = {"employee", "period"}) })
public class Payment {

    @Id
    @Column
    @GeneratedValue
    private Long id;

    @Column
    private String employee;

    @Column
    @DateTimeFormat(pattern="MM-yyyy", iso = DateTimeFormat.ISO.DATE)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "MM-yyyy")
    private YearMonth period;

    @Column
    @Min(value = 0)
    private Long salary;


    public Long getId() {
        return id;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }

    public YearMonth getPeriod() {
        return period;
    }

    public void setPeriod(YearMonth period) {
        this.period = period;
    }

    public Long getSalary() {
        return salary;
    }

    public void setSalary(Long salary) {
        this.salary = salary;
    }
}
