package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.huuloc.boardinghouse.entity.Branch;
import vn.huuloc.boardinghouse.entity.Invoice;

import java.math.BigDecimal;
import java.util.Collection;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @Query("SELECT i FROM Invoice i WHERE i.contract.id = :contractId AND i.paidAmount < i.totalAmount")
    Collection<Invoice> findUnpaidInvoicesByContract(@Param("contractId") Long id);
}
