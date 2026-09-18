package com.bookstore.online_bookstore.security;

import com.bookstore.entity.AppUser;
import com.bookstore.security.AuthTokenFilter;
import com.bookstore.security.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private TestableAuthTokenFilter authTokenFilter;

    private AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        authTokenFilter = new TestableAuthTokenFilter(tokenService);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        mocks.close();
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsMissing()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn(null);

        authTokenFilter.callDoFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void shouldContinueWhenAuthorizationHeaderIsNotBearer()
            throws ServletException, IOException {

        when(request.getHeader("Authorization"))
                .thenReturn("Basic abc123");

        authTokenFilter.callDoFilterInternal(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(tokenService);

        assertNull(
                SecurityContextHolder.getContext().getAuthentication()
        );
    }

    @Test
    void shouldAuthenticateUserWhenTokenIsValid()
            throws ServletException, IOException {

        String token = "valid-token";

        AppUser user = new AppUser();
        user.setId(1L);
        user.setName("Rana");
        user.setEmail("Rana@example.com");

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(tokenService.findUserByToken(token))
                .thenReturn(user);

        authTokenFilter.callDoFilterInternal(
                request,
                response,
                filterChain
        );

        Authentication authentication =
                SecurityContextHolder.getContext()
                        .getAuthentication();

        assertNotNull(authentication);
        assertSame(user, authentication.getPrincipal());
        assertTrue(authentication.isAuthenticated());
        assertTrue(authentication.getAuthorities().isEmpty());

        verify(tokenService).findUserByToken(token);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticateWhenTokenIsInvalid()
            throws ServletException, IOException {

        String token = "invalid-token";

        when(request.getHeader("Authorization"))
                .thenReturn("Bearer " + token);

        when(tokenService.findUserByToken(token))
                .thenReturn(null);

        authTokenFilter.callDoFilterInternal(
                request,
                response,
                filterChain
        );

        assertNull(
                SecurityContextHolder.getContext()
                        .getAuthentication()
        );

        verify(tokenService).findUserByToken(token);
        verify(filterChain).doFilter(request, response);
    }

    private static class TestableAuthTokenFilter
            extends AuthTokenFilter {

        TestableAuthTokenFilter(TokenService tokenService) {
            super(tokenService);
        }

        public void callDoFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
        ) throws ServletException, IOException {

            doFilterInternal(request, response, filterChain);
        }
    }
}