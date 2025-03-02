package org.example.functions;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BlobObjectVo {
    private String blobName;
    private String tag;
    private String id;

}
