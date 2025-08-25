
package com.fhalcom.facturacion.storage;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class S3Storage {
  private final S3Client s3;
  private final String bucket;
  public S3Storage(@Value("${app.s3.endpoint}") String endpoint,
                   @Value("${app.s3.accessKey}") String access,
                   @Value("${app.s3.secretKey}") String secret,
                   @Value("${app.s3.bucket}") String bucket){
    this.bucket = bucket;
    this.s3 = S3Client.builder()
      .endpointOverride(URI.create(endpoint))
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(access, secret)))
      .region(Region.US_EAST_1)
      .forcePathStyle(true)
      .build();
    try{ s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build()); }catch(Exception ignored){}
  }
  public void putBytes(String key, byte[] data, String contentType){
    s3.putObject(PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType).build(), RequestBody.fromBytes(data));
  }
  public byte[] getBytes(String key){
    GetObjectResponse[] meta = new GetObjectResponse[1];
    var path = Path.of(System.getProperty("java.io.tmpdir"), "dl-"+key.replace("/","_"));
    s3.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build(), path);
    try{ return Files.readAllBytes(path); }catch(Exception e){ throw new RuntimeException(e); }
    finally{ try{ Files.deleteIfExists(path);}catch(Exception ignored){} }
  }
  public String getBucket(){ return bucket; }
}
