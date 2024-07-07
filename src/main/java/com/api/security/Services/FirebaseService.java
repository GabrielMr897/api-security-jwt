package com.api.security.Services;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobTargetOption;
import com.google.cloud.storage.StorageOptions;

@Service
public class FirebaseService {
  
  private Storage storage;

 
  @EventListener
  public void init(ApplicationReadyEvent event) {
      try {
          ClassPathResource serviceAccount = new ClassPathResource("api-security-23723-firebase-adminsdk-mipfr-a63008f4d4.json");
          storage = StorageOptions.newBuilder()
                  .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                  .setProjectId("api-security-23723").build().getService();
      } catch (Exception ex) {
          ex.printStackTrace();
      }
  }


  public String saveFile(MultipartFile file) throws IOException {
      String imageName = generateFileName(file.getOriginalFilename());
      Map<String, String> map = new HashMap<>();
      map.put("firebaseStorageDownloadTokens", imageName);
      BlobId blobId = BlobId.of("api-security-23723.appspot.com", imageName);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
              .setMetadata(map)
              .setContentType(file.getContentType())
              .build();
      BlobTargetOption option = BlobTargetOption.predefinedAcl(Storage.PredefinedAcl.PUBLIC_READ);
      storage.create(blobInfo, file.getBytes(), option);
      return imageName;
  }

 
  private String generateFileName(String originalFileName) {
      return UUID.randomUUID().toString() + "." + getExtension(originalFileName);
  }

 
  private String getExtension(String originalFileName) {
      return StringUtils.getFilenameExtension(originalFileName);
  }

  public Boolean deletFile(String fileName) {
      int startIndex = fileName.lastIndexOf("/") + 1;
      int endIndex = fileName.lastIndexOf("?");
      String imageName = fileName.substring(startIndex, endIndex);
      return storage.delete("api-security-23723.appspot.com", imageName);
  }
}