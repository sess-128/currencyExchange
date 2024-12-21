package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Disposition", "attachment; filename=\"filename.jpg\"");
        resp.setContentType("text/plain");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try (var printWriter = resp.getWriter()) {
            printWriter.write("Data from servlet");
        }
    }
}
