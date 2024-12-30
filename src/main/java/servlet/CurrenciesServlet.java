package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDto;
import entity.Currency;
import exception.CurrencyAlreadyExistException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<CurrencyDto> currenciesDto = currencyService.findAll();

        String jsonResponse = new ObjectMapper().writeValueAsString(currenciesDto);

        try (var printWriter = resp.getWriter()) {
            printWriter.write(jsonResponse);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (code == null || name == null || sign == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(errorsHandler.getMessage(resp));
            return;
        }

        try {
            CurrencyDto currencyDto = currencyService.save(new Currency(null, code, name, sign));
            String jsonResponse = new ObjectMapper().writeValueAsString(currencyDto);

            try (var printWriter = resp.getWriter()) {
                printWriter.write(jsonResponse);
            }

        } catch (CurrencyAlreadyExistException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(errorsHandler.getMessage(resp));
        }
    }
}
