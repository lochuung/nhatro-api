package vn.huuloc.boardinghouse.service;

import vn.huuloc.boardinghouse.dto.request.RoomRequest;
import vn.huuloc.boardinghouse.dto.response.RoomResponse;

import java.util.List;

public interface RoomService {
    RoomResponse add(RoomRequest roomRequest);

    RoomResponse update(RoomRequest roomRequest);

    void delete(Long id);

    RoomResponse findById(Long id);

    List<RoomResponse> findAll();
}
