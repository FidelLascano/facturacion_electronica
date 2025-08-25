package com.fhalcom.facturacion.validation;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateXsd {
  /** Doc type hint: factura, notaCredito, etc. Leave empty to auto-infer from XML root or from a method param named 'docType'. */
  String docType() default "";
  /** Optional param name that carries docType in the controller method. If empty, tries 'docType'. */
  String docTypeParam() default "docType";
  /** Method parameter name that contains the XML if not in body; otherwise the aspect scans for byte[]/String/Document. */
  String xmlParam() default "";
}
