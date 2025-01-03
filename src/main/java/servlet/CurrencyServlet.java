package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDto;
import filters.UniMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.util.Optional;

import static filters.Validator.isValidCurrencyCode;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final CurrencyService currencyService = CurrencyService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var code = req.getPathInfo().replaceAll("/", "");

        if (!isValidCurrencyCode(code)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(4217));
            return;
        }

        Optional<CurrencyDto> optionalCurrencyDto = currencyService.findByCode(code);

        if (optionalCurrencyDto.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        try (var writer = resp.getWriter()) {
            writer.write(UniMapper.toJSON(optionalCurrencyDto));
        }
    }
}
