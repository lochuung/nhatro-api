package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.huuloc.boardinghouse.model.entity.Invoice;

import java.util.Collection;
import java.math.BigDecimal;

public interface InvoiceRepository extends JpaRepository<Invoice, Long>, JpaSpecificationExecutor<Invoice> {

    @Query("SELECT i FROM Invoice i WHERE i.contract.id = :contractId AND i.paidAmount < i.totalAmount")
    Collection<Invoice> findUnpaidInvoicesByContract(@Param("contractId") Long id);

    @Query("SELECT SUM(i.paidAmount) FROM Invoice i")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.paidAmount < i.totalAmount")
    Long countPendingInvoices();

    @Query("SELECT SUM(i.paidAmount) FROM Invoice i WHERE YEAR(i.createdDate) = :year AND MONTH(i.createdDate) = :month")
    BigDecimal calculateMonthlyIncome(@Param("year") int year, @Param("month") int month);
}
