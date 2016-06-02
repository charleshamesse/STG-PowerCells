package servlets;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import beans.User;

public class Access implements Filter {

    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession();

        
        //System.out.println(session.getAttribute("user"));
        if(session.getAttribute("user") != null) { // session.getAttribute("user") != null
        	if (((User) session.getAttribute("user")).connected()) {
        	chain.doFilter(request, response);
        	}
        }
        else {
            request.getRequestDispatcher("/login.html").forward(request, response);
        }
    }

    public void destroy() {
    }
}
