package vn.huuloc.boardinghouse.service.impl;

import com.google.api.client.auth.oauth2.Credential;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.FirebaseApp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.huuloc.boardinghouse.dto.CustomerImageDto;
import vn.huuloc.boardinghouse.dto.FileDto;
import vn.huuloc.boardinghouse.dto.mapper.CustomerImageMapper;
import vn.huuloc.boardinghouse.entity.Customer;
import vn.huuloc.boardinghouse.entity.CustomerImage;
import vn.huuloc.boardinghouse.enums.CustomerFileType;
import vn.huuloc.boardinghouse.exception.BadRequestException;
import vn.huuloc.boardinghouse.external.client.FileClient;
import vn.huuloc.boardinghouse.repository.CustomerImageRepository;
import vn.huuloc.boardinghouse.repository.CustomerRepository;
import vn.huuloc.boardinghouse.service.CustomerImageService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class CustomerImageServiceImpl implements CustomerImageService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CustomerImageRepository customerImageRepository;
    @Autowired
    private FileClient fileClient;

    @Value("${app.file.apiKey}")
    private String fileApiKey;

    @Value("${app.file.type:dev}")
    private String fileUploadType;

    @Override
    public CustomerImageDto uploadOne(Long id, String type, MultipartFile file) {

        if (!CustomerFileType.isFileTypeValid(type)) {
            throw BadRequestException.message("type không hỗ trợ");
        }

        Customer customer = customerRepository.findById(id).orElseThrow(
                () -> BadRequestException.message("Không tìm thấy khách hàng")
        );


        Map<String, String> headers = Map.of("cnj", fileApiKey);
        FileDto fileDto = fileClient.uploadOne(headers, file, fileUploadType);

        CustomerImage customerImage = CustomerImage.builder()
                .customer(customer)
                .imageKey(fileDto.getKey())
                .fileType(type)
                .fileName(fileDto.getFileName())
                .build();
        return CustomerImageMapper.INSTANCE.toDto(customerImageRepository.save(customerImage));
    }

    @Override
    public List<CustomerImageDto> getImages(Long customerId) {
        return CustomerImageMapper.INSTANCE.toDtos(customerImageRepository.findByCustomerId(customerId));
    }

    @Override
    public void delete(Long id) {
        CustomerImage customerImage = customerImageRepository.findById(id).orElseThrow(
                () -> BadRequestException.message("Không tìm thấy ảnh")
        );
        customerImageRepository.delete(customerImage);
    }
//    private final CustomerImageRepository customerImageRepository;
//    private final CustomerRepository customerRepository;
//
//    private final GoogleCredentials credential;
//    @Value("${app.firebase.bucket}")
//    private String bucketName;
//
//    @Override
//    public List<CustomerImageDto> getImages(Long id) {
//        return CustomerImageMapper.INSTANCE.toDtos(customerImageRepository.findAllByCustomerId(id));
//    }
//
//    @Override
//    public CustomerImageDto uploadOne(Long id, String type, MultipartFile file) {
//        Customer customer = customerRepository.findById(id).orElseThrow(() -> BadRequestException.message("Không tìm thấy khách hàng"));
//
//        String fileName = file.getOriginalFilename();
//        String imageKey = UUID.randomUUID().toString();
//        try {
//            assert fileName != null;
//            String newFileName = imageKey + getExtension(fileName);
//            File tempFile = convertToFile(file, newFileName);
//            if (!Objects.requireNonNull(file.getContentType()).startsWith("image")) {
//                throw BadRequestException.message("Chỉ chấp nhận ảnh");
//            }
//
//            uploadFile(tempFile, newFileName);
//            tempFile.delete();
//            CustomerImage customerImage = CustomerImage.builder()
//                    .customer(customer)
//                    .imageKey(imageKey)
//                    .type(type)
//                    .fileType(type)
//                    .fileName(fileName).build();
//            return CustomerImageMapper.INSTANCE.toDto(customerImageRepository.save(customerImage));
//        } catch (Exception e) {
//            throw BadRequestException.message("Lỗi khi tải ảnh lên");
//        }
//    }
//
//    @Override
//    public void delete(Long id) {
//        CustomerImage customerImage = customerImageRepository.findById(id).orElseThrow(() -> BadRequestException.message("Không tìm thấy ảnh"));
//        customerImageRepository.delete(customerImage);
//    }
//
//    private void uploadFile(File file, String fileName) throws IOException {
//        BlobId blobId = BlobId.of(bucketName, fileName);
//        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
//        Storage storage = StorageOptions.newBuilder().setCredentials(credential).build().getService();
//        storage.create(blobInfo, Files.readAllBytes(file.toPath()));
//    }
//
//    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
//        File tempFile = new File(fileName);
//        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
//            fos.write(multipartFile.getBytes());
//        }
//        return tempFile;
//    }
//
//    private String getExtension(String fileName) {
//        return fileName.substring(fileName.lastIndexOf("."));
//    }
}
