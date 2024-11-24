package vn.huuloc.boardinghouse.service;

import org.springframework.data.domain.Page;
import vn.huuloc.boardinghouse.model.dto.request.RoomRequest;
import vn.huuloc.boardinghouse.model.dto.response.RoomResponse;
import vn.huuloc.boardinghouse.model.dto.sort.filter.RoomSearchRequest;
import vn.huuloc.boardinghouse.model.projection.LatestNumberIndex;

import java.util.List;

public interface RoomService {
    RoomResponse add(RoomRequest roomRequest);

    RoomResponse update(RoomRequest roomRequest);

    void delete(Long id);

    RoomResponse findById(Long id);

    List<RoomResponse> findAll();

    Page<RoomResponse> search(RoomSearchRequest request);

    LatestNumberIndex findLatestNumberIndex(Long id);
}
