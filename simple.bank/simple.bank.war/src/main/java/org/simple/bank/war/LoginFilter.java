package org.simple.bank.war;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter("/secure/*")
public class LoginFilter implements Filter {

    @Inject
    private UserIdentity userIdentity;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (userIdentity.isLoggedIn()) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/login.xhtml").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        
    }

}
