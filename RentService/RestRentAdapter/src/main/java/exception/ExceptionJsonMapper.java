package exception;

import com.nimbusds.jose.JOSEException;
import domain.exceptions.LogicException;
import domain.exceptions.ReservationException;
import domain.exceptions.RoomException;
import domain.exceptions.UserException;
import rest.dto.ErrorResponseDTO;

import javax.validation.ValidationException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.NoSuchElementException;

@Provider
public class ExceptionJsonMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable throwable) {
        int statusCode;
        if (throwable instanceof RoomException | throwable instanceof ReservationException) {
            statusCode = 409;
        } else if (throwable instanceof WebApplicationException | throwable instanceof ClientErrorException) {
            statusCode = ((WebApplicationException) throwable).getResponse().getStatus();
        } else if (throwable instanceof NoSuchElementException) {
            statusCode = 404;
        } else if (throwable instanceof JOSEException
                | throwable instanceof ValidationException
                | throwable instanceof IllegalArgumentException
                | throwable instanceof LogicException) {
            statusCode = 400;
        } else statusCode = 500;

        return Response.status(statusCode)
                .entity(new ErrorResponseDTO(statusCode, throwable.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();

    }
}
