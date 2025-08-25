
package com.fhalcom.facturacion.storage;
import io.minio.*;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import lombok.Getter;

@Service
public class S3Service {
  @Getter private final MinioClient client;
  @Getter private final String bucket;
  public S3Service(@org.springframework.beans.factory.annotation.Value("${app.s3.endpoint}") String endpoint,
                   @org.springframework.beans.factory.annotation.Value("${app.s3.access}") String access,
                   @org.springframework.beans.factory.annotation.Value("${app.s3.secret}") String secret,
                   @org.springframework.beans.factory.annotation.Value("${app.s3.bucket}") String bucket) {
    this.client = MinioClient.builder().endpoint(endpoint).credentials(access, secret).build();
    this.bucket = bucket;
    try { client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build()); } catch(Exception ignored){}
  }
  public void put(String key, String content) {
    try {
      client.putObject(PutObjectArgs.builder().bucket(bucket).object(key)
        .stream(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), content.length(), -1)
        .contentType("application/xml").build());
    } catch (Exception e){ throw new RuntimeException(e); }
  }
  public byte[] getBytes(String key){
    try(InputStream is = client.getObject(GetObjectArgs.builder().bucket(bucket).object(key).build())){
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      is.transferTo(bos);
      return bos.toByteArray();
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
