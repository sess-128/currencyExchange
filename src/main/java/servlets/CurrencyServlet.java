package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;
import java.util.Optional;

import static errorHandle.ErrorHandler.getMessage;
import static errorHandle.Validation.isValidCurrencyCode;

@WebServlet(name = "CurrencyServlet", urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private static final CurrencyService currencyService = CurrencyService.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        var code = req.getPathInfo().replaceAll("/", "");

        if (code.isBlank() || code == null){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(resp));
            return;
        }

        if (!isValidCurrencyCode(code)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(getMessage(4217));
            return;
        }

        try {
            Optional<CurrencyDto> optionalCurrencyDto = currencyService.findByCode(code);

            if (optionalCurrencyDto.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(getMessage(resp));
                return;
            }
            resp.getWriter().write(objectMapper.writeValueAsString(optionalCurrencyDto.get()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
