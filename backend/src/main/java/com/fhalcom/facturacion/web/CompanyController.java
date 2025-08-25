
package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import java.util.*;
import com.fhalcom.facturacion.repo.CompanyRepository;
import com.fhalcom.facturacion.domain.Company;
import com.fhalcom.facturacion.storage.S3Storage;

@RestController @RequestMapping("/companies")
@PreAuthorize("hasRole('ADMIN')")
public class CompanyController {
  private final CompanyRepository repo; private final S3Storage s3;
  public CompanyController(CompanyRepository r, S3Storage s3){ this.repo=r; this.s3=s3; }

  @GetMapping public List<Company> list(){ return repo.findAll(); }
  @PostMapping public Company create(@RequestBody Map<String,String> b){
    Company c = new Company(); c.ruc=b.get("ruc"); c.razonSocial=b.getOrDefault("razonSocial","Empresa"); return repo.save(c);
  }
  @PostMapping("/{id}/cert")
  public ResponseEntity<?> upload(@PathVariable java.util.UUID id, @RequestParam("file") MultipartFile f){
    try{
      String key = "certs/"+id.toString()+".p12";
      s3.putBytes(key, f.getBytes(), f.getContentType());
      Company c = repo.findById(id).orElseThrow(); c.certKey = key; repo.save(c);
      return ResponseEntity.ok(Map.of("key", key));
    }catch(Exception e){ return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage())); }
  }
}
