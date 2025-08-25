
package com.fhalcom.facturacion.web;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import com.fhalcom.facturacion.repo.EDocRepository;
import com.fhalcom.facturacion.domain.EDoc;
import com.fhalcom.facturacion.storage.S3Storage;
import java.util.*;
import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.ByteArrayOutputStream;

@RestController @RequestMapping("/edoc")
public class EDocListController {
  private final EDocRepository repo; private final S3Storage s3;
  public EDocListController(EDocRepository r, S3Storage s3){ this.repo=r; this.s3=s3; }

  @GetMapping public List<EDoc> list(){ return repo.findAll(); }

  @PostMapping("/export")
  public ResponseEntity<byte[]> exportZip(@RequestBody List<Long> ids, @RequestParam(defaultValue="xml") String type){
    try{
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      try(ZipOutputStream zos = new ZipOutputStream(bos)){
        for(Long id: ids){
          EDoc e = repo.findById(id).orElse(null); if(e==null) continue;
          String key = "xml".equalsIgnoreCase(type)? e.xmlPath : e.pdfPath;
          if(key==null) continue;
          byte[] data = s3.getBytes(key);
          String name = e.tipo.toLowerCase()+"-"+e.claveAcceso + ("xml".equalsIgnoreCase(type)?".xml":".pdf");
          zos.putNextEntry(new ZipEntry(name)); zos.write(data); zos.closeEntry();
        }
      }
      return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=edocs-"+type+".zip")
        .contentType(MediaType.parseMediaType("application/zip")).body(bos.toByteArray());
    }catch(Exception e){ throw new RuntimeException(e); }
  }
}
