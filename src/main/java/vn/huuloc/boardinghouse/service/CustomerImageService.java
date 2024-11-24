package vn.huuloc.boardinghouse.service;

import org.springframework.web.multipart.MultipartFile;
import vn.huuloc.boardinghouse.model.dto.CustomerImageDto;

import java.util.List;

public interface CustomerImageService {
    List<CustomerImageDto> getImages(Long id);

    CustomerImageDto uploadOne(Long id, String type, MultipartFile file);

    void delete(Long id);
}
