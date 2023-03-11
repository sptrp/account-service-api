package account.business.model.responses;

public class PaymentDataResponse {

    private String name;
    private String lastname;
    private String period;
    private String salary;

    public PaymentDataResponse(String name, String lastname, String period, String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = period;
        this.salary = getSalaryFormatted(salary);
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPeriod() {
        return period;
    }

    public String getSalary() {
        return salary;
    }

    private String getSalaryFormatted(String salary) {
        String cent  = salary.substring(salary.length() - 2);

        if (salary.length() > 2) {
            String dol  = salary.substring(0, salary.length() - 2);

            return String.format("%s dollar(s) %s cent(s)", dol, cent);
        }
        return String.format("0 dollar(s) %s cent(s)", cent);
    }
}
