
package com.fhalcom.facturacion.edoc;
import com.fhalcom.facturacion.storage.S3Service;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
public class DownloadController {
  private final EInvoiceRepo repo; private final S3Service s3;
  public DownloadController(EInvoiceRepo repo, S3Service s3){ this.repo=repo; this.s3=s3; }

  @GetMapping("/einvoice/{id}/download")
  public ResponseEntity<byte[]> download(@PathVariable UUID id, @RequestParam String type){
    EInvoice d = repo.findById(id).orElseThrow();
    String key = "xml".equalsIgnoreCase(type) ? d.xml_s3_key : d.ride_s3_key;
    if(key==null) throw new org.springframework.web.server.ResponseStatusException(HttpStatus.NOT_FOUND, "File not available");
    byte[] bytes = s3.getBytes(key);
    String ct = "xml".equalsIgnoreCase(type) ? "application/xml" : "application/pdf";
    return ResponseEntity.ok().contentType(MediaType.parseMediaType(ct)).body(bytes);
  }
}
