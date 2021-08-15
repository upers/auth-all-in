package com.msavchuk.filter.security.abstraction;

import com.msavchuk.config.constant.ApiVersion;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Create {@link RequestMatcher} object that match {@link HttpServletRequest}
 * and if URI and http method do not match then return true and does not execute
 * filter body
 */
public abstract class RequestMatcherFilter implements AuthenticationFilter {
    protected final RequestMatcher requiresAuthenticationRequestMatcher;

    /**
     * Create {@link RequestMatcher} object that match {@link HttpServletRequest}
     * and if URI and http method do not match then return true and does not execute
     * filter body
     *
     * @param uri    uri after {@link ApiVersion::API_PREFIX} (example '/oauth/login')
     * @param method http methods type
     */
    protected RequestMatcherFilter(String uri, String method) {
        requiresAuthenticationRequestMatcher =
                new AntPathRequestMatcher(ApiVersion.API_PREFIX + uri, method);
    }

    /**
     * Check is request matches to URI and Method and execute 'executeInternal' method
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return true if request does not match the URI and Method
     * @throws Exception
     */
    public boolean execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //If request is from URI that provided for refresh token then true.
        boolean match = this.requiresAuthentication(request);
        if (!match)
            return true;

        return executeInternal(request, response);
    }

    /**
     * Implements your filter logic hear.
     *
     * @param request  {@link HttpServletRequest}
     * @param response {@link HttpServletResponse}
     * @return Usually always false or exception. Because if the filter is executing no other filters should be execute.
     * @throws Exception
     */
    protected abstract boolean executeInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    protected boolean requiresAuthentication(HttpServletRequest request) {
        return requiresAuthenticationRequestMatcher.matches(request);
    }
}
