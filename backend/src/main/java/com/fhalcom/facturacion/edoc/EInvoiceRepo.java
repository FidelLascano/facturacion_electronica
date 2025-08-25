
package com.fhalcom.facturacion.edoc;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface EInvoiceRepo extends JpaRepository<EInvoice, UUID> {}
