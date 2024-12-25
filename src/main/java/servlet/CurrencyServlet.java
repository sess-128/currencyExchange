package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;

@WebServlet("/currency")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var currencyId = Integer.valueOf(req.getParameter("currency"));

        try (var printWriter = resp.getWriter()) {
            printWriter.write("<h1>Валюты</h1>");
            printWriter.write("<ul>");
            currencyService.findAllByPair(currencyId).forEach(currencyDto -> printWriter.write("""
                    <li>
                        %s
                    </li>
                    """.formatted(currencyDto.getId())));
            printWriter.write("</ul>");
        }

    }
}
