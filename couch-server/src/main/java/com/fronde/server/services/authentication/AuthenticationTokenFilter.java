package com.fronde.server.services.authentication;

import com.fronde.server.utils.JsonUtils;
import com.fronde.server.utils.RestError;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

public class AuthenticationTokenFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";

  // This needs to be the REST API call that is used to login, and so needs to be exempted from authentication checks itself.
  public static final String AUTHENTICATION_PATH = "/api/authenticate";

  @Autowired
  protected JwtTokenService tokenService;

  @Autowired
  protected JsonUtils jsonUtils;


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return request.getServletPath().equals(AUTHENTICATION_PATH);
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    if (SecurityContextHolder.getContext().getAuthentication() == null) {
      String authToken = request.getHeader(TOKEN_HEADER);
      if (authToken == null) {
        authToken = request.getParameter("jwsToken");
      }

      if (authToken != null) {
        try {
          // If the authToken has been supplied then parse it and deal with any exceptions
          UserDetails userDetails = tokenService.parseToken(authToken);

          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
              userDetails, authToken, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception ex) {
          // Don't do this. See below...
          //writeExceptionResponse(request, response, new JwtAuthenticationException("Invalid token: " + ex.getMessage(), ex));
          // return early and don't continue with the execution

          // Hmm, after some usage it seems the best thing to do with an invalid token is to simply ignore it and NOT set
          // it into the SecurityContextHolder. That way the rest of the system will either get a valid token or not get
          // any token at all. If the method itself is a public method then having no token is ok and we shouldn't throw
          // an error at this point in the processing. If the method being called is protected then the security of the
          // method itself will throw the Unauthorized exception.
        }
      }

      // else there's no authToken or a valid authToken...
      //  ...if there's no authToken, then that's fine because many lower methods don't require authorisation
      //  the methods that do require authorisation will check the token and throw an exception if authorisation is invalid
      chain.doFilter(request, response);

    } else {
      writeExceptionResponse(request, response,
          new JwtAuthenticationException("Invalid state. SecurityContext has already been set."));
    }
  }

  /**
   * This class can't use the RestResponseEntityExceptionHandler approach since it operates at the
   * Filter level and above the Controller level.
   *
   * @param response
   * @param ex
   * @throws IOException
   */
  private void writeExceptionResponse(HttpServletRequest request, HttpServletResponse response,
      Exception ex) throws IOException {
    RestError error = new RestError(request.getRequestURI(), HttpStatus.UNAUTHORIZED.value(), ex);
    response.setStatus(error.getStatus());
    response.getWriter().write(jsonUtils.toJson(error));
  }


}
