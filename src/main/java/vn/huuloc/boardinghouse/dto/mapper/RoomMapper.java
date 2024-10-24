package vn.huuloc.boardinghouse.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import vn.huuloc.boardinghouse.dto.request.RoomRequest;
import vn.huuloc.boardinghouse.dto.response.RoomResponse;
import vn.huuloc.boardinghouse.entity.Contract;
import vn.huuloc.boardinghouse.entity.Room;
import vn.huuloc.boardinghouse.enums.ContractStatus;

import java.util.List;

@Mapper
public interface RoomMapper {
    RoomMapper INSTANCE = Mappers.getMapper(RoomMapper.class);

    Room toEntity(RoomRequest roomRequest);

    @Mapping(target = "numberOfPeople", expression = "java(toNumberOfPeople(room))")
    RoomResponse toDto(Room room);

    default Integer toNumberOfPeople(Room room) {
        if (room.getContracts() == null) {
            return 0;
        }
        return room.getContracts().stream()
                .filter(contract -> contract.getStatus() == ContractStatus.OPENING)
                .mapToInt(Contract::getNumberOfPeople)
                .findFirst()
                .orElse(0);
    }


    List<RoomResponse> toDtos(List<Room> rooms);
}
