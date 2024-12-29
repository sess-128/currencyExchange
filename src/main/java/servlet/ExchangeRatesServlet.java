package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDto;
import entity.ExchangeRate;
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
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/exchange-rates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());


        List<ExchangeRateDto> exchangeRateDto = exchangeRateService.findAll();

        String jsonResponse = new ObjectMapper().writeValueAsString(exchangeRateDto);

        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write(jsonResponse);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        Float rate = Float.parseFloat((req.getParameter("rate")));

        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(resp));
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
