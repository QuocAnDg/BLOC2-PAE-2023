package be.vinci.pae.utils;

import be.vinci.pae.utils.exceptions.BadRequestException;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.InternalServerException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.logging.Level;

/**
 * ExceptionHandler class.
 */
@Provider
public class ExceptionHandler implements ExceptionMapper<Throwable> {

  @Override
  public Response toResponse(Throwable exception) {

    MyLogger.getInstance().getLogger().log(Level.SEVERE, exception.toString());

    exception.printStackTrace();
    if (exception instanceof WebApplicationException) {
      return ((WebApplicationException) exception).getResponse();
    }
    if (exception instanceof IllegalBusinessException) {
      Throwable exceptionCause = exception.getCause();
      if (exceptionCause instanceof BadRequestException) {
        return Response.status(Status.BAD_REQUEST)
            .entity(exception.getMessage())
            .build();
      }
      if (exceptionCause instanceof ElementNotFoundException) {
        return Response.status(Status.NOT_FOUND)
            .entity(exception.getMessage())
            .build();
      }
      if (exceptionCause instanceof InternalServerException) {
        return Response.status(Status.INTERNAL_SERVER_ERROR)
            .entity(exception.getMessage())
            .build();
      }
      if (exceptionCause instanceof NoContentException) {
        return Response.status(Status.NO_CONTENT)
            .entity(exception.getMessage())
            .build();
      }
      if (exceptionCause instanceof NotAllowedException) {
        return Response.status(Status.METHOD_NOT_ALLOWED)
            .entity(exception.getMessage())
            .build();
      }
      if (exceptionCause instanceof UnauthorizedException) {
        return Response.status(Status.UNAUTHORIZED)
            .entity(exception.getMessage())
            .build();
      }
    }
    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
        .entity(exception.getMessage())
        .type(MediaType.TEXT_PLAIN)
        .build();
  }
}