package account.util.handlers;

import account.business.model.entities.User;
import account.business.service.UserService;
import account.business.service.LoggerService;
import account.util.enums.EventAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    UserService userService;

    @Autowired
    LoggerService loggerService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String authHeader = request.getHeader("authorization");

        if (authHeader != null) {
            String email = extractAndDecodeEmailFromHeader(request.getHeader("authorization"));
            Optional<User> userOptional = userService.findUser(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();

                if (user.isAccountNonLocked()) {
                    if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS) {
                        userService.increaseFailedAttempts(user, request.getRequestURI());

                    } else {
                        loggerService.logEvent(EventAction.LOGIN_FAILED, email, request.getRequestURI(), request.getRequestURI());
                        loggerService.logEvent(EventAction.BRUTE_FORCE, email, request.getRequestURI(), request.getRequestURI());

                        userService.lock(user, email.toLowerCase(), request.getRequestURI());
                    }
                    sendUnauthorisedResponse(request, response, "Wrong password!");

                } else {
                    sendUnauthorisedResponse(request, response, "User account is locked");
                }
            } else {
                loggerService.logEvent(EventAction.LOGIN_FAILED, email, request.getRequestURI(), request.getRequestURI());
                sendUnauthorisedResponse(request, response, "Wrong password!");
            }
        }
        else {
            sendUnauthorisedResponse(request, response, "Wrong password!");
        }
    }

    private void sendUnauthorisedResponse(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        response.setStatus(401);
        response.getWriter().write(ResponseHandler.generateDefaultUnauthorizedJson(request.getRequestURI(), message));
    }

    private String extractAndDecodeEmailFromHeader(String authHeader) {
        String base64String = authHeader.substring(6);
        String decoded = new String(Base64.getDecoder().decode(base64String));

        return decoded.substring(0, decoded.indexOf(':'));
    }
}
