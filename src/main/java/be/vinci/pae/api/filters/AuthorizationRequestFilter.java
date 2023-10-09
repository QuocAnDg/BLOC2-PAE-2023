package be.vinci.pae.api.filters;

import be.vinci.pae.domain.User;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;
import java.lang.reflect.Method;

/**
 * AuthorizationRequestFilter class.
 */
@Singleton
@Provider
@Authorize
public class AuthorizationRequestFilter implements ContainerRequestFilter {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final JWTVerifier jwtVerifier = JWT.require(this.jwtAlgorithm).withIssuer("auth0")
      .build();
  @Context
  ResourceInfo resourceInfo;

  @Inject
  private UserUCC myUserUCC;

  /**
   * filter method.
   *
   * @param requestContext the request context
   */
  @Override
  public void filter(ContainerRequestContext requestContext) {
    String token = requestContext.getHeaderString("Authorization");
    if (token == null) {
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
          .entity("A token is needed to access this resource").build());
    } else {
      DecodedJWT decodedToken;
      try {
        decodedToken = this.jwtVerifier.verify(token);
      } catch (Exception e) {
        throw new WebApplicationException("Malformed token : " + e.getMessage(),
            Status.UNAUTHORIZED);
      }
      User authenticatedUser;
      try {
        authenticatedUser = (User) myUserUCC.findOne(decodedToken.getClaim("user").asInt());
      } catch (FatalException | UnauthorizedException e) {
        throw new RuntimeException(e);
      }
      if (authenticatedUser == null) {
        requestContext.abortWith(Response.status(Status.FORBIDDEN)
            .entity("You are forbidden to access this resource").build());
      } else {
        Method method = resourceInfo.getResourceMethod();
        Authorize authorize = method.getAnnotation(Authorize.class);

        boolean authorized = false;
        String[] roles = authorize.roles();

        if (roles.length == 0) {
          authorized = true;
        } else {
          for (String role : roles) {
            if (authenticatedUser.getRole().equals(role)) {
              authorized = true;
              break;
            }
          }
        }

        if (!authorized) {
          requestContext.abortWith(Response.status(Status.FORBIDDEN)
              .entity("You don't have permission to do this request").build());
        }
      }
      requestContext.setProperty("user", authenticatedUser);
    }
  }
}