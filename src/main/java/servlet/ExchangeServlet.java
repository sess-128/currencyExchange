package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

import static filters.Validator.isValidCurrencyCode;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            BigDecimal amount;


            if (from == null || to == null || from.isBlank() || to.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(errorsHandler.getMessage(resp));
                return;
            }

            try {
                amount = BigDecimal.valueOf(Float.parseFloat(req.getParameter("amount")));
            } catch (NumberFormatException e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(errorsHandler.getMessage(resp));
                return;
            }

            if (!isValidCurrencyCode(from) || !isValidCurrencyCode(to)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(errorsHandler.getMessage(4217));
                return;
            }

            ExchangeDto converted = exchangeRateService.convert(from, to, amount);

            String jsonResponse = new ObjectMapper().writeValueAsString(converted);

            resp.setStatus(HttpServletResponse.SC_OK);
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(jsonResponse);
            }
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
        }
    }

}
