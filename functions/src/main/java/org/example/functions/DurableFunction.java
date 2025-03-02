package org.example.functions;


import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobProperties;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.durabletask.DurableTaskClient;
import com.microsoft.durabletask.TaskOrchestrationContext;
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger;
import com.microsoft.durabletask.azurefunctions.DurableClientContext;
import com.microsoft.durabletask.azurefunctions.DurableClientInput;
import com.microsoft.durabletask.azurefunctions.DurableOrchestrationTrigger;

import java.util.UUID;

public class DurableFunction {
    @FunctionName("StartOrchestration") //blob triggrt
    @StorageAccount("AzureWebJobsStorage")
    public void startOrchestration(@BlobTrigger(name = "content", path = "departments/{name}", dataType = "binary") byte[] content,
                                   @BindingName("name") String name, @DurableClientInput(name = "durableContext") DurableClientContext durableContext,
                                   final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        BlobClient blobClient = new BlobServiceClientBuilder()
                .connectionString(System.getenv("AzureWebJobsStorage"))
                .buildClient()
                .getBlobContainerClient("departments")
                .getBlobClient(name);

        BlobProperties properties = blobClient.getProperties();
        DurableTaskClient client = durableContext.getClient();
        BlobObjectVo blobObjectVo = BlobObjectVo
                .builder()
                .blobName(name)
                .id(UUID.randomUUID().toString())
                .tag(properties.getMetadata().getOrDefault("department","UNKNOWN"))
                .build();

        String instanceId = client.scheduleNewOrchestrationInstance("orchestrator", blobObjectVo);
        context.getLogger().info("Created new Java orchestration with instance ID = " + instanceId);
    }


    @FunctionName("orchestrator")
    public String orchestrator(@DurableOrchestrationTrigger(name = "ctx") TaskOrchestrationContext ctx) {
        BlobObjectVo blobObjectVo = ctx.getInput(BlobObjectVo.class);
        ctx.callActivity("insertIntoCosmos", blobObjectVo, BlobObjectVo.class).await();
        return "result";

    }

    @FunctionName("insertIntoCosmos")
    @CosmosDBOutput(name = "cosmos",
            databaseName = "documentUpload",
            containerName = "departments",
            connection = "AzureCosmosDBConnection")
    public BlobObjectVo insertIntoCosmos(@DurableActivityTrigger(name = "blobObjectVo") BlobObjectVo blobObjectVo, final ExecutionContext context) {
        context.getLogger().info("insertIntoCosmos=>: " + blobObjectVo);
        return blobObjectVo;
    }

}