
package com.fhalcom.facturacion.web;

import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import com.fhalcom.facturacion.repo.EInvoiceRepository;
import com.fhalcom.facturacion.domain.EInvoice;
import java.util.List;

@RestController @RequestMapping("/einvoice")
public class EInvoiceQueryController {
  private final EInvoiceRepository repo;
  public EInvoiceQueryController(EInvoiceRepository r){ this.repo=r; }

  @GetMapping
  @PreAuthorize("@perm.hasPermission(authentication, null, 'EINVOICE.READ')")
  public List<EInvoice> list(){ return repo.findAll(); }
}
