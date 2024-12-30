package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final CurrencyService currencyService = CurrencyService.getInstance();
    private static final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();
    private static final int CURRENCY_LENGHT_W_SLASH = 4;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.length() < CURRENCY_LENGHT_W_SLASH) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        var code = pathInfo.substring(1).trim();

        Optional<CurrencyDto> optionalCurrencyDto = currencyService.findByCode(code);
        if (optionalCurrencyDto.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        try (var writer = resp.getWriter()) {
            String jsonResponse = new ObjectMapper().writeValueAsString(optionalCurrencyDto.get());
            writer.write(jsonResponse);
        }

    }

}
