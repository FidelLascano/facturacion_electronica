package com.fhalcom.facturacion.signature;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

public class LocalKeystoreProvider implements KeyProvider {
  private final String path, password, alias;
  public LocalKeystoreProvider(String path, String password, String alias){
    this.path=path; this.password=password; this.alias=alias;
  }
  public PrivateKey privateKey(){
    try{
      KeyStore ks = KeyStore.getInstance("PKCS12");
      try(FileInputStream in = new FileInputStream(path)){ ks.load(in, password.toCharArray()); }
      String a = alias!=null? alias : ks.aliases().nextElement();
      return (PrivateKey) ks.getKey(a, password.toCharArray());
    }catch(Exception e){ throw new RuntimeException(e); }
  }
  public X509Certificate certificate(){
    try{
      KeyStore ks = KeyStore.getInstance("PKCS12");
      try(FileInputStream in = new FileInputStream(path)){ ks.load(in, password.toCharArray()); }
      String a = alias!=null? alias : ks.aliases().nextElement();
      return (X509Certificate) ks.getCertificate(a);
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
