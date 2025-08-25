package com.fhalcom.facturacion.validation;

import com.fhalcom.facturacion.xsd.PreValidationPipeline;
import com.fhalcom.facturacion.validation.util.XmlUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Order(5) // early, but after security filters if any
public class GlobalXmlValidationFilter extends org.springframework.web.filter.OncePerRequestFilter {
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }

  private final PreValidationPipeline pipeline;
  private final boolean enabled;

  public GlobalXmlValidationFilter(PreValidationPipeline pipeline,
                                   @Value("${app.validation.filter.enabled:true}") boolean enabled) {
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
    this.pipeline = pipeline;
    this.enabled = enabled;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
    if(!enabled) return true;
    String ct = request.getContentType();
    if(ct == null) return true;
    ct = ct.toLowerCase();
    return !(ct.contains("application/xml") || ct.contains("text/xml"));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }

    HttpCachedBodyRequest wrapped = new HttpCachedBodyRequest(request);
    byte[] body = wrapped.getCachedBody();
    if(body == null || body.length == 0){
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
      filterChain.doFilter(wrapped, response);
      return;
    }

    String docType = request.getHeader("X-Doc-Type");
    try {
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
      if(docType == null || docType.isBlank()){
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
        String root = XmlUtil.rootName(body);
        docType = XmlUtil.docTypeFromRoot(root);
      }
      if(docType == null) docType = "factura"; // fallback
      // validate, and get version/path chosen by pipeline
      var info = pipeline.validateAndResolve(request.getHeader("X-Tenant-Id") != null ? request.getHeader("X-Tenant-Id") : "default",
                                            docType, null, XmlUtil.toDocument(body));
      // expose details via response headers
      if(info != null){
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
        response.setHeader("X-XSD-DocType", docType);
        response.setHeader("X-XSD-Version", info.version());
        response.setHeader("X-XSD-Path", info.classpathPath());
      }
      filterChain.doFilter(wrapped, response);
    } catch (Exception ex){
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.setContentType("application/json");
      String msg = "{"error":"XSD validation failed","detail":"" + escape(ex.getMessage()) + ""}";
      response.getOutputStream().write(msg.getBytes(StandardCharsets.UTF_8));
    }
  }

  private static String escape(String s){
  private final com.facturacion.validation.metrics.XsdValidationMetrics metrics;
  private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalXmlValidationFilter.class);
  public GlobalXmlValidationFilter(com.facturacion.validation.metrics.XsdValidationMetrics metrics){ this.metrics = metrics; }
    if(s==null) return "";
    return s.replace("\", "\\").replace(""", "\"").replace("
", " ").replace("
", " ");
  }
}
