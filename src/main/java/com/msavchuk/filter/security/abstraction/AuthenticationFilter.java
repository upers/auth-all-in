package com.msavchuk.filter.security.abstraction;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthenticationFilter {

    boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
