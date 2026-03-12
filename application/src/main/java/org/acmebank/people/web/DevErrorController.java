package org.acmebank.people.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.PrintWriter;
import java.io.StringWriter;

@Controller
public class DevErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exceptionAttr = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object requestUri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        int statusCode = status != null ? Integer.parseInt(status.toString()) : 500;
        HttpStatus httpStatus = HttpStatus.resolve(statusCode);

        model.addAttribute("statusCode", statusCode);
        model.addAttribute("statusReason", httpStatus != null ? httpStatus.getReasonPhrase() : "Unknown");
        model.addAttribute("requestUri", requestUri != null ? requestUri.toString() : "N/A");
        model.addAttribute("errorMessage", message != null ? message.toString() : "No message available");

        if (exceptionAttr instanceof Throwable throwable) {
            model.addAttribute("exceptionClass", throwable.getClass().getName());
            model.addAttribute("stackTrace", toStackTrace(throwable));
        } else {
            model.addAttribute("exceptionClass", "No exception captured");
            model.addAttribute("stackTrace", "No stack trace available");
        }

        return "error";
    }

    private String toStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
