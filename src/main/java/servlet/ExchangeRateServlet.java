package servlet;

import dto.ExchangeRateDto;
import exception.ExchangeRateNotFoundException;
import filters.UniMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var codes = req.getPathInfo().replaceAll("/", "");

        if (codes.length() != 6) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        String baseCode = codes.substring(0, 3);
        String targetCode = codes.substring(3);

//        if (!isValidCurrencyCode(baseCode) || !isValidCurrencyCode(targetCode)) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write(errorsHandler.getMessage(4217));
//            return;
//        }

        Optional<ExchangeRateDto> optionalExchangeRateDto = exchangeRateService.findByPair(codes);

        if (optionalExchangeRateDto.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        try (var printWriter = resp.getWriter()) {
            printWriter.write(UniMapper.toJSON(optionalExchangeRateDto));
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
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

//        if (!isValidCurrencyCode(baseCurrencyCode) || !isValidCurrencyCode(targetCurrencyCode)) {
//            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            resp.getWriter().write(errorsHandler.getMessage(4217));
//            return;
//        }

        float rates = Float.parseFloat((stringRate));

        try {
            ExchangeRateDto updated = exchangeRateService.update(baseCurrencyCode, targetCurrencyCode, rates);

            try (var printWriter = resp.getWriter()) {
                printWriter.write(UniMapper.toJSON(updated));
            }
        } catch (ExchangeRateNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
        }

    }
}
