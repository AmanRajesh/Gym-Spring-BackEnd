//package com.automlhybrid.gymspringbackend;
//
//import com.automlhybrid.gymspringbackend.clients.MlEngineClient;
//import com.automlhybrid.gymspringbackend.controllers.OrchestratorController;
//import com.automlhybrid.gymspringbackend.dto.PipelineRequest;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//import java.util.Map;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//// 1. Force the property to exist so Feign doesn't complain
//@WebMvcTest(controllers = OrchestratorController.class, properties = "app.services.ml-engine-url=http://localhost:8000")
//public class OrchestratorFlowTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    // 2. Use @MockBean to automatically replace the real Feign Client
//    @MockBean
//    private MlEngineClient mlEngineClient;
//
//    // Safety Net: If you didn't remove GpuJobProducer from the Controller code yet,
//    // uncomment the line below to prevent "NoSuchBeanDefinitionException".
//    // @MockBean private com.automlhybrid.gymspringbackend.services.GpuJobProducer gpuJobProducer;
//
//    @Test
//    public void testTraining_CpuPath_Success() throws Exception {
//        // Prepare Data
//        PipelineRequest request = new PipelineRequest();
//        request.setFileKey("user-1/small_data.csv");
//        request.setPreprocessing(List.of("DROP_NULLS"));
//        request.setModelType("RandomForest");
//
//        // Prepare Mock Response
//        Map<String, Object> pythonResponse = Map.of(
//                "status", "COMPLETED",
//                "accuracy", 0.95,
//                "message", "Trained locally on CPU"
//        );
//
//        // Define Behavior
//        Mockito.when(mlEngineClient.startTrainingProcess(any(PipelineRequest.class)))
//                .thenReturn(pythonResponse);
//
//        // Execute Request
//        mockMvc.perform(post("/api/orchestrator/train")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("COMPLETED"))
//                .andExpect(jsonPath("$.accuracy").value(0.95));
//    }
//
//    @Test
//    public void testTraining_GpuPath_Queued() throws Exception {
//        // Prepare Data
//        PipelineRequest request = new PipelineRequest();
//        request.setFileKey("user-1/massive_data.csv");
//        request.setModelType("CNN");
//
//        // Prepare Mock Response
//        Map<String, Object> pythonResponse = Map.of(
//                "status", "QUEUED",
//                "jobId", "job-123",
//                "message", "Heavy load detected, moved to GPU queue."
//        );
//
//        // Define Behavior
//        Mockito.when(mlEngineClient.startTrainingProcess(any(PipelineRequest.class)))
//                .thenReturn(pythonResponse);
//
//        // Execute Request
//        mockMvc.perform(post("/api/orchestrator/train")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value("QUEUED"))
//                .andExpect(jsonPath("$.jobId").value("job-123"));
//    }
//}