package com.fhalcom.facturacion.signature;
import org.w3c.dom.Document;
import xades4j.production.*;
import xades4j.providers.impl.*;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class SignatureService {
  private final KeyProvider provider;
  public SignatureService(KeyProvider provider){ this.provider=provider; }
  public Document signXadesBes(Document doc){
    try{
      PrivateKey pk = provider.privateKey();
      X509Certificate cert = provider.certificate();
      XadesSigningProfile prof = new XadesBesSigningProfile(new FileSystemKeyingDataProvider(pk, cert));
      XadesSigner signer = prof.newSigner();
      SignedDataObjects sdo = new SignedDataObjects(new DataObjectReference("#comprobante"));
      signer.sign(sdo, doc.getDocumentElement());
      return doc;
    }catch(Exception e){ throw new RuntimeException(e); }
  }
  static class FileSystemKeyingDataProvider extends KeyingDataProvider{
    private final PrivateKey pk; private final X509Certificate cert;
    FileSystemKeyingDataProvider(PrivateKey pk, X509Certificate cert){ this.pk=pk; this.cert=cert; }
    public SigningKey getSigningKey(){ return new SigningKey(){ public PrivateKey getPrivateKey(){ return pk; } public X509Certificate getCertificate(){ return cert; } }; }
    public KeyStoreKeyingDataProvider.KeyAndCertificate getKeyAndCertificate(){ return null; }
  }
}
