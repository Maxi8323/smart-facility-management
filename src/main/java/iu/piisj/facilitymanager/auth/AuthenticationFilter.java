package iu.piisj.facilitymanager.auth;

import iu.piisj.facilitymanager.user.UserRole;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@WebFilter("*.xhtml")
public class AuthenticationFilter implements Filter {

    private static final Set<String> PUBLIC_PAGES = Set.of(
            "/index.xhtml",
            "/login.xhtml",
            "/register.xhtml"
    );

    private static final Set<String> ADMIN_ONLY = Set.of(
            "/users.xhtml"
    );

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String contextPath = request.getContextPath();
        String path = request.getRequestURI().substring(contextPath.length());

        if (isPublic(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        SessionUser user = session != null
                ? (SessionUser) session.getAttribute(AuthController.SESSION_USER_KEY)
                : null;

        if (user == null) {
            String encodedPath = URLEncoder.encode(path, StandardCharsets.UTF_8);
            response.sendRedirect(contextPath + "/login.xhtml?redirect=" + encodedPath);
            return;
        }

        if (ADMIN_ONLY.contains(path) && user.getRole() != UserRole.ADMIN) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Kein Zugriff");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublic(String path) {
        return PUBLIC_PAGES.contains(path) || path.startsWith("/jakarta.faces.resource/");
    }
}
