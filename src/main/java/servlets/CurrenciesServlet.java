package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDto;
import exceptions.CurrencyAlreadyExistException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import service.CurrencyService;

import java.io.IOException;
import java.util.List;

import static utils.errorHandle.ErrorHandler.getMessage;
import static utils.errorHandle.Validation.*;

@WebServlet(name = "CurrenciesServlet", urlPatterns = "/currencies")
public class CurrenciesServlet extends HttpServlet {
    private static final int MAX_LENGTH_ERROR = 777;
    private static final int NOT_ISO_FORMAT = 4217;
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        List<CurrencyDto> currenciesDto = currencyService.findAll();

        String jsonResponse = new ObjectMapper().writeValueAsString(currenciesDto);

        try (var printWriter = resp.getWriter()) {
            printWriter.write(jsonResponse);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(resp));
            return;
        }

        if (name.isBlank() || code.isBlank() || sign.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(resp));
            return;
        }

        if (!isValidCurrencyCode(code)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(NOT_ISO_FORMAT));
            return;
        }

        if (!isCorrectLength(name, sign)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(MAX_LENGTH_ERROR));
            return;
        }

        try {
            CurrencyDto currencyDto = currencyService.save(new Currency(null, code, name, sign));
            resp.getWriter().write(objectMapper.writeValueAsString(currencyDto));

        } catch (CurrencyAlreadyExistException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(getMessage(resp));
        }
    }
}
