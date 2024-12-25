package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRateService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/exchange-rates")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (var printWriter = resp.getWriter()) {
            printWriter.write("<h1>Список валют</h1>");
            printWriter.write("<ul>");

            exchangeRateService.findAll().forEach(exchangeRateDto -> {
                printWriter.write(  
                        """
                                <li>
                                    <a href="/exchange-rates?currency=%d">%s</a>
                                </li>
                                """.formatted(exchangeRateDto.getId(), exchangeRateDto.getDescription())
                );
            });
        }
    }
}
