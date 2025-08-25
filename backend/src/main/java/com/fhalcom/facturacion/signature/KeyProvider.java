package com.fhalcom.facturacion.signature;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
public interface KeyProvider {
  PrivateKey privateKey();
  X509Certificate certificate();
}
