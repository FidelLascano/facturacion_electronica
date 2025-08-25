package com.fhalcom.facturacion.validation;

import com.fhalcom.facturacion.xsd.XsdValidator;
import com.fhalcom.facturacion.validation.util.XmlUtil;
import com.fhalcom.facturacion.xsd.XsdVersionResolver;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Parameter;

@Aspect
@Component
public class ValidateXsdAspect {

  private final XsdVersionResolver resolver;
  private final XsdValidator validator;
  private final HttpServletRequest request;

  public ValidateXsdAspect(XsdVersionResolver resolver, XsdValidator validator, HttpServletRequest request){
    this.resolver = resolver;
    this.validator = validator;
    this.request = request;
  }

  @Around("@annotation(ann)")
  public Object around(ProceedingJoinPoint pjp, ValidateXsd ann) throws Throwable {
    MethodSignature ms = (MethodSignature) pjp.getSignature();
    Object[] args = pjp.getArgs();
    Parameter[] params = ms.getMethod().getParameters();

    // Try to find XML payload
    byte[] xmlBytes = null;
    String xmlParamName = ann.xmlParam();
    if(!xmlParamName.isEmpty()){
      for(int i=0;i<params.length;i++){
        if(params[i].getName().equals(xmlParamName)){
          xmlBytes = XmlUtil.asBytes(args[i]);
          break;
        }
      }
    }
    if(xmlBytes == null){
      for(int i=0;i<args.length;i++){
        byte[] b = XmlUtil.asBytes(args[i]);
        if(b!=null){ xmlBytes = b; break; }
      }
    }

    // If there's no XML in the call (e.g., preview sin body), skip.
    if(xmlBytes == null || xmlBytes.length==0){
      return pjp.proceed();
    }

    // Determine tenant and docType
    String tenant = request.getHeader("X-Tenant-Id");
    if(tenant == null || tenant.isBlank()) tenant = "default";

    String docType = ann.docType();
    if(docType == null || docType.isBlank()){
      // Try param named docTypeParam
      String maybe = XmlUtil.findStringParam(params, args, ann.docTypeParam());
      if(maybe != null && !maybe.isBlank()) docType = maybe;
    }
    if(docType == null || docType.isBlank()){
      // Infer from XML root element name
      String root = XmlUtil.peekRootElementName(xmlBytes);
      if(root != null) docType = XmlUtil.mapRootToDocType(root);
    }
    if(docType == null || docType.isBlank()){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot determine document type for XSD validation");
    }

    Document dom = XmlUtil.parse(xmlBytes);
    try{
      String xsdPath = resolver.resolvePath(tenant, docType, null);
      validator.validate(dom, xsdPath);
    }catch(Exception e){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "XSD validation failed: " + e.getMessage(), e);
    }
    return pjp.proceed();
  }
}
