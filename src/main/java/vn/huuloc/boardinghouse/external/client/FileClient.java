package vn.huuloc.boardinghouse.external.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import vn.huuloc.boardinghouse.model.dto.FileDto;

import java.util.Map;

@FeignClient(name = "file-client", url = "${api.file.url}")
public interface FileClient {

    @PostMapping(value = "/v1/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    FileDto uploadOne(@RequestHeader Map<String, String> headers, @RequestPart("file") MultipartFile file, @RequestParam("type") String type);
}
