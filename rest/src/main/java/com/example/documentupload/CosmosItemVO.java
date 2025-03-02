package com.example.documentupload;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CosmosItemVO {
    private String id;
    private String blobName;
    private String tag;
}
