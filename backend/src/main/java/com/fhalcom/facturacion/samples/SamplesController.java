
package com.fhalcom.facturacion.samples;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.io.InputStream; import java.nio.charset.StandardCharsets;
@RestController @RequestMapping("/samples")
public class SamplesController {
  @GetMapping(value="/jrxml/{docType}", produces=MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> jrxml(@PathVariable String docType) throws Exception {
    try(InputStream in = getClass().getResourceAsStream("/samples/jrxml/"+docType+".jrxml")){
      if(in==null) return ResponseEntity.notFound().build();
      return ResponseEntity.ok(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    }
  }
  @GetMapping(value="/xml/{docType}", produces=MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<String> xml(@PathVariable String docType) throws Exception {
    try(InputStream in = getClass().getResourceAsStream("/samples/xml/"+docType+".xml")){
      if(in==null) return ResponseEntity.notFound().build();
      return ResponseEntity.ok(new String(in.readAllBytes(), StandardCharsets.UTF_8));
    }
  }
}
