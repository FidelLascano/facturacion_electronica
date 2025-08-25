
package com.fhalcom.facturacion.sign;
import org.w3c.dom.Document;
import xades4j.production.*;
import xades4j.providers.impl.FileSystemKeyStoreKeyingDataProvider;
import xades4j.utils.DOMHelper;

public class XadesSigner {
  public static Document sign(Document doc, String p12Path, String password){
    try{
      FileSystemKeyStoreKeyingDataProvider kp = new FileSystemKeyStoreKeyingDataProvider(
        "pkcs12", p12Path, aliases -> aliases.hasMoreElements()?aliases.nextElement():null,
        () -> password.toCharArray(), () -> password.toCharArray(), true);
      XadesSigner signer = new XadesBesSigningProfile(kp).newSigner();
      signer.sign(new SignedDataObjects(new Enveloped(doc)), doc.getDocumentElement());
      return doc;
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
