package com.example.documentupload;


import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.AccessTier;
import com.azure.storage.blob.models.BlobHttpHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(originPatterns = "*")
@RequiredArgsConstructor
public class Controller {
    private final BlobServiceClient blobServiceClient;
    private final CosmosClient cosmosClient;


    @Value("${blob.container}")
    private String blobContainer;

    @Value("${cosmos.database}")
    private String cosmosDatabase;

    @Value("${blob.container}")
    private String cosmosContainer;


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity uploadFile(@RequestParam MultipartFile file, @RequestParam String department) throws IOException {

        String localPath = "./data/";
        new File(localPath).mkdirs();

        String fileName = "FILE_" + java.util.UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Paths.get(localPath + fileName).toAbsolutePath();
        file.transferTo(path.toFile());

        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(blobContainer);

        BlobClient blobClient = blobContainerClient.getBlobClient(fileName);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("department", department);
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentMd5("data".getBytes(StandardCharsets.UTF_8))
                .setContentLanguage("en-US")
                .setContentType("binary");
        blobClient.uploadFromFile(localPath + fileName, null, null, metadata, AccessTier.HOT, null, Duration.ofSeconds(10));
        Files.deleteIfExists(path);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/documentByTag")
    public List<CosmosItemVO> getBLobLinkByTag(@RequestParam String tag) {
        CosmosDatabase database = cosmosClient.getDatabase(cosmosDatabase);
        CosmosContainer container = database.getContainer(cosmosContainer);

        // Query to find documents with specific conditions
        String query = "SELECT * FROM c where c.tag='" + tag + "'"; // Modify query based on your needs

        // Execute the query
        CosmosPagedIterable<CosmosItemVO> queryResults = container.queryItems(query, null, CosmosItemVO.class);
        List<CosmosItemVO> cosmosItemVOList = new ArrayList<>();
        for (CosmosItemVO item : queryResults) {
            System.out.println(item.toString());
            cosmosItemVOList.add(item);
        }
        return cosmosItemVOList;
    }

    @GetMapping
    public Map<String, String> getEnvVars() {
        Map<String, String> env = System.getenv();
        for (String envName : env.keySet()) {
            System.out.format("%s=%s%n", envName, env.get(envName));
        }
        return env
    }

}
