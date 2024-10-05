package vn.huuloc.boardinghouse.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.huuloc.boardinghouse.dto.mapper.RoomMapper;
import vn.huuloc.boardinghouse.dto.request.RoomRequest;
import vn.huuloc.boardinghouse.dto.response.RoomResponse;
import vn.huuloc.boardinghouse.dto.sort.filter.RoomSearchRequest;
import vn.huuloc.boardinghouse.dto.sort.filter.SearchSpecification;
import vn.huuloc.boardinghouse.entity.Branch;
import vn.huuloc.boardinghouse.entity.Room;
import vn.huuloc.boardinghouse.enums.RoomDisplayType;
import vn.huuloc.boardinghouse.enums.RoomStatus;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.repository.BranchRepository;
import vn.huuloc.boardinghouse.repository.RoomRepository;
import vn.huuloc.boardinghouse.service.RoomService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final BranchRepository branchRepository;

    @Override
    public RoomResponse add(RoomRequest roomRequest) {
        roomRequest.setId(null);
        Branch branch = branchRepository.findById(roomRequest.getBranchId()).orElseThrow(() -> BadRequestException.message("Không tìm thấy chi nhánh"));

        Room room = RoomMapper.INSTANCE.toEntity(roomRequest);
        room.setBranch(branch);
        return RoomMapper.INSTANCE.toDto(roomRepository.save(room));
    }

    @Override
    public RoomResponse update(RoomRequest roomRequest) {
        Room room = roomRepository.findById(roomRequest.getId()).orElseThrow(() -> BadRequestException.message("Không tìm thấy phòng trọ"));

        Branch branch = branchRepository.findById(roomRequest.getBranchId()).orElseThrow(() -> BadRequestException.message("Không tìm thấy chi nhánh"));

        room.setBranch(branch);
        room.setName(roomRequest.getName());
        room.setDescription(roomRequest.getDescription());
        room.setPrice(roomRequest.getPrice());
        room.setStatus(roomRequest.getStatus());
        room.setCapacity(roomRequest.getCapacity());
        room.setType(roomRequest.getType());

        return RoomMapper.INSTANCE.toDto(roomRepository.save(room));
    }

    @Override
    public void delete(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> BadRequestException.message("Không tìm thấy phòng trọ"));
        roomRepository.delete(room);
    }

    @Override
    public RoomResponse findById(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> BadRequestException.message("Không tìm thấy phòng trọ"));
        return RoomMapper.INSTANCE.toDto(room);
    }

    @Override
    public List<RoomResponse> findAll() {
        return RoomMapper.INSTANCE.toDtos(roomRepository.findAll());
    }

    @Override
    public Page<RoomResponse> search(RoomSearchRequest request) {
        Specification<Room> specification = new SearchSpecification<>(request);

        String displayType = request.getDisplayType();
        if (RoomDisplayType.RENTED.getValue().equalsIgnoreCase(displayType)) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), RoomStatus.RENTED));
        }
        if (RoomDisplayType.AVAILABLE.getValue().equalsIgnoreCase(displayType)) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), RoomStatus.AVAILABLE));
        }
        Pageable pageable = SearchSpecification.getPageable(request.getPage(), request.getSize());
        Page<Room> entities = roomRepository.findAll(specification, pageable);
        return entities.map(RoomMapper.INSTANCE::toDto);
    }
}
