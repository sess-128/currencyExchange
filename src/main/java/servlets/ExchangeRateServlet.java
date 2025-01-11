package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDto;
import exceptions.ExchangeRateNotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

import static errorHandle.ErrorHandler.getMessage;
import static errorHandle.Validation.isValidCurrencyCode;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var codes = req.getPathInfo().replaceAll("/", "");

        if (codes.length() != 6) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(resp));
            return;
        }

        String baseCode = codes.substring(0, 3);
        String targetCode = codes.substring(3);

        if (!isValidCurrencyCode(baseCode) || !isValidCurrencyCode(targetCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(4217));
            return;
        }

        Optional<ExchangeRateDto> optionalExchangeRateDto = exchangeRateService.findByPair(codes);

        try {
            if (optionalExchangeRateDto.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(getMessage(resp));
                return;
            }
            resp.getWriter().write(objectMapper.writeValueAsString(optionalExchangeRateDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var pathInfo = req.getPathInfo();

        String baseCurrencyCode = pathInfo.substring(1, 4);
        String targetCurrencyCode = pathInfo.substring(4);
        String stringRate = req.getReader().readLine().substring(5);

        if (baseCurrencyCode.isBlank() || targetCurrencyCode.isBlank() || stringRate.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(resp));
            return;
        }

        if (!isValidCurrencyCode(baseCurrencyCode) || !isValidCurrencyCode(targetCurrencyCode)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(4217));
            return;
        }

        BigDecimal rates;
        try {
            rates = new BigDecimal(stringRate);
            if (rates.scale() > 6) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("Запись ставки должна иметь не более 6 знаков после запятой.");
                return;
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("Неверный формат ставки. Пожалуйста, укажите числовое значение.");
            return;
        }

        try {
            ExchangeRateDto updated = exchangeRateService.update(baseCurrencyCode, targetCurrencyCode, rates);
            resp.getWriter().write(objectMapper.writeValueAsString(updated));
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(getMessage(resp));
        }

    }
}
