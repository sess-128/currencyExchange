package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDto;
import exceptions.CurrencyNotFoundException;
import exceptions.ExchangeRateAlreadyExistException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import static utils.errorHandle.ErrorHandler.getMessage;
import static utils.errorHandle.Validation.isValidCurrencyCode;
import static utils.errorHandle.Validation.isValidRateAndAmount;

@WebServlet(name = "ExchangeRatesServlet", urlPatterns = "/exchangeRates/*")
public class ExchangeRatesServlet extends HttpServlet {
    private static final int NOT_ISO_FORMAT = 4217;
    private static final int SAME_CODES_ERROR = 1000;
    private static final int NUMBER_INCORRECT_INPUT_ERROR = 888;
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateDto> exchangeRateDto = exchangeRateService.findAll();

        String jsonResponse = new ObjectMapper().writeValueAsString(exchangeRateDto);

        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write(jsonResponse);
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate2 = req.getParameter("rate");

        BigDecimal rate = new BigDecimal(rate2);



        if (baseCurrencyCode == null || targetCurrencyCode == null) {
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
        if (!isValidRateAndAmount(rate)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(NUMBER_INCORRECT_INPUT_ERROR));
            return;
        }

        try {
            ExchangeRateDto save = exchangeRateService.save(baseCurrencyCode, targetCurrencyCode, rate);
            resp.getWriter().write(objectMapper.writeValueAsString(save));

        } catch (ExchangeRateAlreadyExistException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(getMessage(resp));
        } catch (CurrencyNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(getMessage(resp));
        }

    }
}