package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;
import service.errorHandler.ErrorsHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorsHandler errorsHandler = ErrorsHandler.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            float amount = Float.parseFloat(req.getParameter("amount"));

            ExchangeDto converted = exchangeRateService.convert(from, to, amount);

            String jsonResponse = new ObjectMapper().writeValueAsString(converted);

            try (PrintWriter writer = resp.getWriter()) {
                writer.write(jsonResponse);
            }
        }catch (RuntimeException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(errorsHandler.getMessage(resp));
        }
    }
}
