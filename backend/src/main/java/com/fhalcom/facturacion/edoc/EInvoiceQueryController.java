
package com.fhalcom.facturacion.edoc;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/einvoice")
public class EInvoiceQueryController {
  private final EInvoiceRepo repo;
  public EInvoiceQueryController(EInvoiceRepo repo){ this.repo=repo; }

  @GetMapping
  public List<EInvoice> list(){ return repo.findAll(); }
}
