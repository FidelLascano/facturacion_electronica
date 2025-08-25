
package com.fhalcom.facturacion.sign;
import com.fhalcom.facturacion.kms.KmsService;
import com.fhalcom.facturacion.storage.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignerFactory {
  @Bean
  public XmlSigner signer(S3Service s3, KmsService kms, @Value("${app.signature.mode:xmldsig}") String mode){
    XmlSigner xs = new XmlSigner(s3.getClient(), s3.getBucket(), kms);
    xs.setMode(mode);
    return xs;
  }
}
