package com.fhalcom.facturacion.xsd;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;

@Component
public class PreValidationPipeline
{
  public static record ResolvedInfo(String version, String classpathPath){}
 {
  private final XsdVersionResolver resolver;
  private final XsdValidator validator;

  public PreValidationPipeline(XsdVersionResolver resolver, XsdValidator validator){
    this.resolver = resolver; this.validator = validator;
  }

  public void validate(String tenantId, String docType, String requestedVersion, Document dom){
    String xsdPath = resolver.resolvePath(tenantId, map(docType), requestedVersion);
    validator.validate(dom, xsdPath);
  }

  private String map(String t){
    if(t==null) return "factura";
    t = t.trim().toUpperCase();
    switch(t){
      case "FACTURA": return "factura";
      case "NOTA_CREDITO": return "notaCredito";
      case "NOTA_DEBITO": return "notaDebito";
      case "GUIA_REMISION": return "guiaRemision";
      case "RETENCION":
      case "COMPROBANTE_RETENCION": return "comprobanteRetencion";
      case "LIQUIDACION":
      case "LIQUIDACION_COMPRA":
      case "LIQUIDACIONDECOMPRA": return "liquidacionCompra";
      default: return "factura";
    }
  }

  public ResolvedInfo validateAndResolve(String tenant, String docType, String requestedVersion, org.w3c.dom.Document dom) {
    String xsdPath = versionResolver.resolvePath(tenant, docType, requestedVersion);
    XsdValidator.validate(dom, xsdPath);
    // derive version from filename
    String version = "unknown";
    int i = xsdPath.lastIndexOf('/');
    if(i>=0){
      String name = xsdPath.substring(i+1);
      int j = name.indexOf('.');
      if(j>0) version = name.substring(0, j);
    }
    return new ResolvedInfo(version, xsdPath);
  }
}
