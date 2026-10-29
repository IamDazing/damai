package com.damai.filter;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import jakarta.servlet.FilterChain;
import static org.mockito.Mockito.*;

class RequestLoggingTest {
    @Test
    void requestBodyIsLeftForTheApplicationWithoutBeingReadForLogging() throws Exception {
        MockHttpServletRequest request = spy(new MockHttpServletRequest("POST", "/user/login"));
        request.setContent("{\"password\":\"private-example\"}".getBytes());
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        new BaseParameterFilter().doFilter(request, response, chain);
        verify(request, never()).getInputStream();
        verify(chain).doFilter(request, response);
    }
}
