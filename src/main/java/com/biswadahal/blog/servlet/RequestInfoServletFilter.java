package com.biswadahal.blog.servlet;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This filter exists due to the following reason:
 * [Appengine Workaround] Issue: 
 * 	http://stackoverflow.com/questions/31354363/google-appengine-with-jersey-2-1x-works-fine-in-dev-server-but-not-in-appengine?lq=1
 */
public class RequestInfoServletFilter implements Filter{
	public static final String REQUEST_INFO_REQUEST_ATTRIBUTE_NAME = "REQUEST_INFO";
	public static final Logger logger = LoggerFactory.getLogger(RequestInfoServletFilter.class);

	@Override
	public void destroy() {
		logger.trace(String.format("%s destroyed", this.getClass().getName()));
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) servletRequest;
		httpReq.setAttribute(REQUEST_INFO_REQUEST_ATTRIBUTE_NAME, new RequestInfo(httpReq));
		chain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {
		logger.trace(String.format("%s initialized", this.getClass().getName()));
	}

	public static final class RequestInfo implements Serializable {
		private static final long serialVersionUID = 8701829161L;
		private String remoteAddress;
		private int remotePort;
		private String remoteHost;
		private String remoteUser;
		private String requestUri;
		private String requestUrl;
		private String requestMethod;

		public RequestInfo(HttpServletRequest req) {
			this.remoteAddress = req.getRemoteAddr();
			this.remotePort = req.getRemotePort();
			this.remoteHost = req.getRemoteHost();
			this.remoteUser = req.getRemoteUser();
			this.requestUri = req.getRequestURI();
			this.requestUrl = req.getRequestURL().toString();
			this.requestMethod = req.getMethod();
		}

		public String getRemoteAddress() {
			return remoteAddress;
		}

		public int getRemotePort() {
			return remotePort;
		}

		public String getRemoteHost() {
			return remoteHost;
		}

		public String getRemoteUser() {
			return remoteUser;
		}

		public String getRequestUri() {
			return requestUri;
		}

		public String getRequestUrl() {
			return requestUrl;
		}

		public String getRequestMethod() {
			return requestMethod;
		}

	}

}
