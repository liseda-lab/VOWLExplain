package io.swagger.api;

import io.swagger.model.AlignmentPlan;
import org.springframework.core.io.Resource;
import io.swagger.model.Task;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.*;
import javax.validation.Valid;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")
@RestController
public class TasksApiController implements TasksApi {

    private static final Logger log = LoggerFactory.getLogger(TasksApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public TasksApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Void> deleteTaskByID(@Parameter(in = ParameterIn.PATH, description = "Task ID", required=true, schema=@Schema()) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Resource> downloadAlignment(@Parameter(in = ParameterIn.PATH, description = "Task ID", required=true, schema=@Schema()) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Resource>(objectMapper.readValue("\"\"", Resource.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Resource>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Resource>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Task> getTaskByID(@Parameter(in = ParameterIn.PATH, description = "Task ID", required=true, schema=@Schema()) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Task>(objectMapper.readValue("{\r\n  \"id\" : \"c27d77380cf4d0bcdd5529eef1f020871d5f95c2\",\r\n  \"leftDataset\" : \"http://example.org/void.ttl#EuroVoc\",\r\n  \"rightDataset\" : \"http://example.org/void.ttl#TESEO\",\r\n  \"submissionTime\" : \"202-02-10T18:00:00+01:00\",\r\n  \"startTime\" : \"202-02-10T18:00:30+01:00\",\r\n  \"status\" : \"running\",\r\n  \"progress\" : 60\r\n}", Task.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Task>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Task>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Task>> getTasks() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Task>>(objectMapper.readValue("[ {\r\n  \"id\" : \"c27d77380cf4d0bcdd5529eef1f020871d5f95c2\",\r\n  \"leftDataset\" : \"http://example.org/void.ttl#EuroVoc\",\r\n  \"rightDataset\" : \"http://example.org/void.ttl#TESEO\",\r\n  \"submissionTime\" : \"202-02-10T18:00:00+01:00\",\r\n  \"startTime\" : \"202-02-10T18:00:30+01:00\",\r\n  \"status\" : \"running\",\r\n  \"progress\" : 60\r\n}, {\r\n  \"id\" : \"c27d77380cf4d0bcdd5529eef1f020871d5f95c2\",\r\n  \"leftDataset\" : \"http://example.org/void.ttl#EuroVoc\",\r\n  \"rightDataset\" : \"http://example.org/void.ttl#TESEO\",\r\n  \"submissionTime\" : \"202-02-10T18:00:00+01:00\",\r\n  \"startTime\" : \"202-02-10T18:00:30+01:00\",\r\n  \"status\" : \"running\",\r\n  \"progress\" : 60\r\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Task>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Task>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Task> submitTask(@Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema()) @Valid @RequestBody AlignmentPlan body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Task>(objectMapper.readValue("{\r\n  \"id\" : \"c27d77380cf4d0bcdd5529eef1f020871d5f95c2\",\r\n  \"leftDataset\" : \"http://example.org/void.ttl#EuroVoc\",\r\n  \"rightDataset\" : \"http://example.org/void.ttl#TESEO\",\r\n  \"submissionTime\" : \"202-02-10T18:00:00+01:00\",\r\n  \"startTime\" : \"202-02-10T18:00:30+01:00\",\r\n  \"status\" : \"running\",\r\n  \"progress\" : 60\r\n}", Task.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Task>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Task>(HttpStatus.NOT_IMPLEMENTED);
    }

}
