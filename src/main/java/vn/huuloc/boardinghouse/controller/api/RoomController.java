package vn.huuloc.boardinghouse.controller.api;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.huuloc.boardinghouse.model.dto.request.RoomRequest;
import vn.huuloc.boardinghouse.model.dto.response.RoomResponse;
import vn.huuloc.boardinghouse.model.dto.sort.filter.RoomSearchRequest;
import vn.huuloc.boardinghouse.model.projection.LatestNumberIndex;
import vn.huuloc.boardinghouse.service.RoomService;
import vn.huuloc.boardinghouse.util.ResponseUtils;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<RoomResponse> add(@Valid @RequestBody RoomRequest roomRequest) {
        return ResponseEntity.ok(roomService.add(roomRequest));
    }

    @PutMapping
    public ResponseEntity<RoomResponse> update(@Valid @RequestBody RoomRequest roomRequest) {
        return ResponseEntity.ok(roomService.update(roomRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") Long id) {
        roomService.delete(id);
        return ResponseEntity.ok(ResponseUtils.success());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(roomService.findById(id));
    }

    @GetMapping("/latest-number-index/{id}")
    public ResponseEntity<LatestNumberIndex> findLatestNumberIndex(@PathVariable Long id) {
        return ResponseEntity.ok(roomService.findLatestNumberIndex(id));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> findAll() {
        return ResponseEntity.ok(roomService.findAll());
    }

    @PostMapping("/search")
    public ResponseEntity<Page<RoomResponse>> search (@RequestBody RoomSearchRequest request) {
        return ResponseEntity.ok(roomService.search(request));
    }
}
