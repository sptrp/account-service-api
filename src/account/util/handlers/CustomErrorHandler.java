package account.util.handlers;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.time.DateTimeException;

@ControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(TransactionSystemException.class)
    public void handleConstraintViolationException(TransactionSystemException exception,
                                                   ServletWebRequest webRequest) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public void handleSQLIntegrityConstraintViolationException(DataIntegrityViolationException exception,
                                                   ServletWebRequest webRequest) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }

    @ExceptionHandler(DateTimeException.class)
    public void handleDateTimeException(DateTimeException exception,
                                                   ServletWebRequest webRequest) throws IOException {
        webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
    }
}