
package com.fhalcom.facturacion.common;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestControllerAdvice
public class RestExceptionHandler {
  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ApiError> handle(ResponseStatusException ex){
    return ResponseEntity.status(ex.getStatusCode())
      .body(new ApiError(ex.getStatusCode().toString(), ex.getReason()));
  }
}
