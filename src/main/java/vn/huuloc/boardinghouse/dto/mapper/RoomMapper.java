package vn.huuloc.boardinghouse.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import vn.huuloc.boardinghouse.dto.request.RoomRequest;
import vn.huuloc.boardinghouse.dto.response.RoomResponse;
import vn.huuloc.boardinghouse.entity.Room;

import java.util.List;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    Room toEntity(RoomRequest roomRequest);

    RoomResponse toDto(Room room);


    List<RoomResponse> toDtos(List<Room> rooms);
}
