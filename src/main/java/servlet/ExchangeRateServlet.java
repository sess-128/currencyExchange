package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDto;
import exception.ExchangeRateNotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@WebServlet("/exchange-rate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();
    private static final int RATE_LENGTH_W_SLASH = 7;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() < RATE_LENGTH_W_SLASH) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        var code = pathInfo.substring(1);

        Optional<ExchangeRateDto> optionalExchangeRateDto = exchangeRateService.findByPair(code);

        if (optionalExchangeRateDto.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
        }

        try (var printWriter = resp.getWriter()) {
            String jsonResponse = new ObjectMapper().writeValueAsString(optionalExchangeRateDto.get());
            printWriter.write(jsonResponse);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        var pathInfo = req.getPathInfo();

        String baseCurrencyCode = pathInfo.substring(1, 4);
        String targetCurrencyCode = pathInfo.substring(4);
        String stringRate = req.getReader().readLine().substring(5);

        if (baseCurrencyCode.isBlank() || targetCurrencyCode.isBlank() || stringRate.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        float rates = Float.parseFloat((stringRate));

        try {
            ExchangeRateDto updated = exchangeRateService.update(baseCurrencyCode, targetCurrencyCode, rates);

            String jsonResponse = new ObjectMapper().writeValueAsString(updated);

            try (var printWriter = resp.getWriter()) {
                printWriter.write(jsonResponse);
            }
        }catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            errorsHandler.getMessage(resp);
        }

    }
}
