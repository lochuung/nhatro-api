package vn.huuloc.boardinghouse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.huuloc.boardinghouse.model.entity.Room;
import vn.huuloc.boardinghouse.model.projection.LatestNumberIndex;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {
    Room findByCode(String code);

    @Query("""
            SELECT new vn.huuloc.boardinghouse.model.projection.LatestNumberIndex(
                i.newElectricityNumber,
                i.newWaterNumber
            )
            FROM Room r JOIN r.contracts c
            JOIN c.invoices i
            WHERE r = :room
            AND i.startDate = (
                SELECT MAX(i2.startDate)
                FROM Invoice i2
                WHERE i2.contract = c
            )
            """)
    LatestNumberIndex findLatestNumberIndex(@Param("room") Room room);
}
