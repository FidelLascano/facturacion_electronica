
package com.fhalcom.facturacion.company;
import com.fhalcom.facturacion.storage.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;

@RestController
@org.springframework.security.access.prepost.PreAuthorize("@perm.hasPermission(authentication, null, 'ADMIN.MANAGE')") @RequestMapping("/companies")
public class CompanyController {
  private final CompanyRepo repo; private final S3Service s3;
  public CompanyController(CompanyRepo repo, S3Service s3){ this.repo = repo; this.s3 = s3; }

  @GetMapping public List<Company> list(){ return repo.findAll(); }

  @PostMapping public Company create(@RequestBody Map<String,String> b){
    Company c = new Company();
    c.id = java.util.UUID.randomUUID();
    c.tenant_id = java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");
    c.ruc = b.getOrDefault("ruc","0000000000000");
    c.razon_social = b.getOrDefault("razonSocial","Nueva Empresa");
    return repo.save(c);
  }

  @PostMapping("/{id}/cert")
  public ResponseEntity<?> uploadCert(@PathVariable java.util.UUID id, @RequestParam("file") MultipartFile file){
    try{
      String key = "certs/"+id.toString()+".p12";
      s3.getClient().putObject(io.minio.PutObjectArgs.builder().bucket(s3.getBucket()).object(key)
        .stream(file.getInputStream(), file.getSize(), -1).contentType(file.getContentType()).build());
      return ResponseEntity.ok(Map.of("key", key));
    }catch(Exception e){
      return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
    }
  }
}
