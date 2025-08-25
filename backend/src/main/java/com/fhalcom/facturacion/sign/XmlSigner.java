
package com.fhalcom.facturacion.sign;
import org.w3c.dom.Document;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.List;

public class XmlSigner {
  public static Document enveloped(Document doc, PrivateKey key, X509Certificate cert){
    try{
      XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
      Reference ref = fac.newReference("", fac.newDigestMethod(DigestMethod.SHA256, null),
        List.of(fac.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)), null, null);
      SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,(C14NMethodParameterSpec)null),
        fac.newSignatureMethod(SignatureMethod.RSA_SHA256, null), List.of(ref));
      KeyInfoFactory kif = fac.getKeyInfoFactory();
      KeyInfo ki = kif.newKeyInfo(List.of(kif.newX509Data(List.of(cert))));
      DOMSignContext dsc = new DOMSignContext(key, doc.getDocumentElement());
      fac.newXMLSignature(si, ki).sign(dsc);
      return doc;
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
