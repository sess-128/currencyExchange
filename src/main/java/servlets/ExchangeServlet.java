package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;

import static utils.errorHandle.ErrorHandler.getMessage;
import static utils.errorHandle.Validation.isValidCurrencyCode;
import static utils.errorHandle.Validation.isValidRateAndAmount;

@WebServlet(name = "ExchangeServlet", urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {
    private static final int NOT_ISO_FORMAT = 4217;
    private static final int SAME_CODES_ERROR = 1000;
    private static final int NUMBER_INCORRECT_INPUT_ERROR = 888;
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String sAmount = req.getParameter("amount");
        BigDecimal amount = new BigDecimal(sAmount);


        if (baseCurrencyCode == null || targetCurrencyCode == null || baseCurrencyCode.isBlank() || targetCurrencyCode.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(resp));
            return;
        }

        if (!isValidCurrencyCode(baseCurrencyCode) || !isValidCurrencyCode(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(NOT_ISO_FORMAT));
            return;
        }

        if (baseCurrencyCode.equals(targetCurrencyCode)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write((getMessage(SAME_CODES_ERROR)));
            return;
        }

        if (!isValidRateAndAmount(amount)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(NUMBER_INCORRECT_INPUT_ERROR));
            return;
        }

        try {

            ExchangeDto converted = exchangeRateService.convert(baseCurrencyCode, targetCurrencyCode, amount);
            resp.getWriter().write(objectMapper.writeValueAsString(converted));
        } catch (RuntimeException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(getMessage(resp));
        }
    }
}
