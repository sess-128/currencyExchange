package filters;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter(urlPatterns = "/*")
public class ResponseFilter implements Filter {
    private static final String[] API_SERVLETS = {
            "/currency/",
            "/currencies",
            "/exchangeRate/",
            "/exchangeRates/",
            "/exchange",

    };
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.setCharacterEncoding("UTF-8");
        servletRequest.setCharacterEncoding("UTF-8");

        String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();

        if (requestURI.endsWith(".css") || requestURI.endsWith(".js")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (isAPIServlet(requestURI)){
            servletResponse.setContentType("application/json");
        } else{
            servletResponse.setContentType("text/html");
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean isAPIServlet (String URI) {
        for (String servlet : API_SERVLETS){
            if (URI.startsWith(servlet)){
                return true;
            }
        }
        return false;
    }
}
