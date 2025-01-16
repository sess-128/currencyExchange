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

import static utils.errorHandle.ErrorHandler.getMessage;
import static utils.errorHandle.Validation.isValidCurrencyCode;
import static utils.errorHandle.Validation.isValidRateAndAmount;

@WebServlet(name = "ExchangeRateServlet", urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final int ISO_FORMAT_ERROR = 4217;
    private static final int SAME_CODES_ERROR = 1000;
    private static final int NUMBER_FORMAT_ERROR = 999;
    private static final int NUMBER_INCORRECT_INPUT_ERROR = 888;
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
            resp.getWriter().write(getMessage(ISO_FORMAT_ERROR));
            return;
        }
        if (baseCode.equals(targetCode)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write((getMessage(SAME_CODES_ERROR)));
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

        if (pathInfo.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(resp));
            return;
        }

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
            resp.getWriter().write(getMessage(ISO_FORMAT_ERROR));
            return;
        }
        if (baseCurrencyCode.equals(targetCurrencyCode)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write((getMessage(SAME_CODES_ERROR)));
            return;
        }

        BigDecimal rates = new BigDecimal(stringRate);

        if (!isValidRateAndAmount(rates)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(NUMBER_INCORRECT_INPUT_ERROR));
            return;
        }

        try {

            ExchangeRateDto updated = exchangeRateService.update(baseCurrencyCode, targetCurrencyCode, rates);
            resp.getWriter().write(objectMapper.writeValueAsString(updated));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(NUMBER_FORMAT_ERROR));
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(getMessage(resp));
        }

    }
}
