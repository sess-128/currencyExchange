package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDto;
import exception.CurrencyNotFoundException;
import exception.ExchangeRateAlreadyExistException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import static filters.Validator.isValidCurrencyCode;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRateDto> exchangeRateDto = exchangeRateService.findAll();

        String jsonResponse = new ObjectMapper().writeValueAsString(exchangeRateDto);

        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write(jsonResponse);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        Float rate = Float.parseFloat((req.getParameter("rate")));

        String reqParameter = req.getParameter("rate");
        BigDecimal rate2 = new BigDecimal(reqParameter);

        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        if (!isValidCurrencyCode(baseCurrencyCode) || !isValidCurrencyCode(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(4217));
            return;
        }

        try {
            try {
                ExchangeRateDto save = exchangeRateService.save(baseCurrencyCode, targetCurrencyCode, rate);
                String jsonResponse = new ObjectMapper().writeValueAsString(save);

                try (var printWriter = resp.getWriter()) {
                    printWriter.write(jsonResponse);
                }
            } catch (ExchangeRateAlreadyExistException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write(errorsHandler.getMessage(resp));
            }
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
        }

    }
}
