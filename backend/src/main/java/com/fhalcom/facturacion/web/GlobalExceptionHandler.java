
package com.fhalcom.facturacion.web;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.context.request.ServletWebRequest;
import java.net.URI; import java.time.OffsetDateTime; import java.util.UUID;
@ControllerAdvice
public class GlobalExceptionHandler {
  private ProblemDetail problem(HttpStatus status, String type, String title, String detail, ServletWebRequest req){
    ProblemDetail p = ProblemDetail.forStatus(status);
    p.setType(URI.create(type)); p.setTitle(title); p.setDetail(detail);
    p.setProperty("timestamp", OffsetDateTime.now().toString());
    p.setProperty("instance", req!=null && req.getRequest()!=null ? req.getRequest().getRequestURI() : "");
    p.setProperty("traceId", UUID.randomUUID().toString());
    return p;
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail illegal(IllegalArgumentException ex, ServletWebRequest req){ return problem(HttpStatus.UNPROCESSABLE_ENTITY, "https://facturacion.ec/errors/illegal-argument","Invalid argument", ex.getMessage(), req); }
  @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
  public ProblemDetail denied(org.springframework.security.access.AccessDeniedException ex, ServletWebRequest req){ return problem(HttpStatus.FORBIDDEN, "https://facturacion.ec/errors/access-denied","Access denied", ex.getMessage(), req); }
  @ExceptionHandler(Exception.class)
  public ProblemDetail generic(Exception ex, ServletWebRequest req){ return problem(HttpStatus.INTERNAL_SERVER_ERROR, "https://facturacion.ec/errors/internal","Internal error", ex.getMessage(), req); }
}
