package com.project.pc_backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request,100);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        logger.debug(formatRequest(wrappedRequest));

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logger.debug(formatResponse(wrappedResponse));
            wrappedResponse.copyBodyToResponse();
        }
    }

    private String formatRequest(ContentCachingRequestWrapper request) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== REQUEST ===\n");
        sb.append(request.getMethod()).append(" ").append(request.getRequestURI());
        String query = request.getQueryString();
        if (StringUtils.hasText(query)) {
            sb.append('?').append(query);
        }
        sb.append("\nHeaders:\n");

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                String value = request.getHeader(name);
                if (name.equalsIgnoreCase("authorization") || name.toLowerCase().contains("cookie")) {
                    value = "***REDACTED***";
                }
                sb.append("  ").append(name).append(": ").append(value).append('\n');
            }
        }

        String body = getRequestBody(request);
        if (StringUtils.hasText(body)) {
            String lower = body.toLowerCase();
            if (lower.contains("password") || lower.contains("token") || lower.contains("authorization")) {
                sb.append("Body:\n[REDACTED]\n");
            } else {
                sb.append("Body:\n").append(body).append('\n');
            }
        }

        sb.append("=== END REQUEST ===");
        return sb.toString();
    }

    private String formatResponse(ContentCachingResponseWrapper response) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== RESPONSE ===\n");
        sb.append("Status: ").append(response.getStatus()).append('\n');
        sb.append("Headers:\n");
        response.getHeaderNames().forEach(name ->
                sb.append("  ").append(name).append(": ").append(String.join(", ", response.getHeaders(name))).append('\n')
        );

        String body = getResponseBody(response);
        if (StringUtils.hasText(body)) {
            sb.append("Body:\n").append(body).append('\n');
        }

        sb.append("=== END RESPONSE ===");
        return sb.toString();
    }

    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] buf = request.getContentAsByteArray();
        if (buf.length == 0) {
            return "";
        }
        try {
            return new String(buf, (request.getCharacterEncoding() != null ? request.getCharacterEncoding() : StandardCharsets.UTF_8).toString());
        } catch (Exception e) {
            return "[unable to read request body]";
        }
    }

    private String getResponseBody(ContentCachingResponseWrapper response) {
        byte[] buf = response.getContentAsByteArray();
        if (buf.length == 0) {
            return "";
        }
        try {
            return new String(buf, (response.getCharacterEncoding() != null ? response.getCharacterEncoding() : StandardCharsets.UTF_8).toString());
        } catch (Exception e) {
            return "[unable to read response body]";
        }
    }
}
