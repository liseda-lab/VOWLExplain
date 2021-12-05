package io.swagger.api;

import io.swagger.model.Matcher;
import io.swagger.model.ScenarioDefinition;
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
public class MatchersApiController implements MatchersApi {

    private static final Logger log = LoggerFactory.getLogger(MatchersApiController.class);

    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    @org.springframework.beans.factory.annotation.Autowired
    public MatchersApiController(ObjectMapper objectMapper, HttpServletRequest request) {
        this.objectMapper = objectMapper;
        this.request = request;
    }

    public ResponseEntity<Matcher> getMatcherByID(@Parameter(in = ParameterIn.PATH, description = "Matcher ID", required=true, schema=@Schema()) @PathVariable("id") String id) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<Matcher>(objectMapper.readValue("{\r\n  \"id\" : \"example-matcher\",\r\n  \"description\" : \"example matcher\",\r\n  \"settings\" : {\r\n    \"type\" : \"object\",\r\n    \"properties\" : {\r\n      \"structuralFeatures\" : {\r\n        \"description\" : \"whether to use structural features or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      },\r\n      \"synonymExpansion\" : {\r\n        \"description\" : \"whether to do synonym expansion or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      }\r\n    }\r\n  }\r\n}", Matcher.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<Matcher>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Matcher>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Matcher>> getMatchers() {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Matcher>>(objectMapper.readValue("[ {\r\n  \"id\" : \"example-matcher\",\r\n  \"description\" : \"example matcher\",\r\n  \"settings\" : {\r\n    \"type\" : \"object\",\r\n    \"properties\" : {\r\n      \"structuralFeatures\" : {\r\n        \"description\" : \"whether to use structural features or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      },\r\n      \"synonymExpansion\" : {\r\n        \"description\" : \"whether to do synonym expansion or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      }\r\n    }\r\n  }\r\n}, {\r\n  \"id\" : \"example-matcher\",\r\n  \"description\" : \"example matcher\",\r\n  \"settings\" : {\r\n    \"type\" : \"object\",\r\n    \"properties\" : {\r\n      \"structuralFeatures\" : {\r\n        \"description\" : \"whether to use structural features or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      },\r\n      \"synonymExpansion\" : {\r\n        \"description\" : \"whether to do synonym expansion or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      }\r\n    }\r\n  }\r\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Matcher>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Matcher>>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<List<Matcher>> searchMatchers(@Parameter(in = ParameterIn.DEFAULT, description = "", schema=@Schema()) @Valid @RequestBody ScenarioDefinition body) {
        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            try {
                return new ResponseEntity<List<Matcher>>(objectMapper.readValue("[ {\r\n  \"id\" : \"example-matcher\",\r\n  \"description\" : \"example matcher\",\r\n  \"settings\" : {\r\n    \"type\" : \"object\",\r\n    \"properties\" : {\r\n      \"structuralFeatures\" : {\r\n        \"description\" : \"whether to use structural features or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      },\r\n      \"synonymExpansion\" : {\r\n        \"description\" : \"whether to do synonym expansion or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      }\r\n    }\r\n  }\r\n}, {\r\n  \"id\" : \"example-matcher\",\r\n  \"description\" : \"example matcher\",\r\n  \"settings\" : {\r\n    \"type\" : \"object\",\r\n    \"properties\" : {\r\n      \"structuralFeatures\" : {\r\n        \"description\" : \"whether to use structural features or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      },\r\n      \"synonymExpansion\" : {\r\n        \"description\" : \"whether to do synonym expansion or not\",\r\n        \"type\" : \"boolean\",\r\n        \"default\" : true\r\n      }\r\n    }\r\n  }\r\n} ]", List.class), HttpStatus.NOT_IMPLEMENTED);
            } catch (IOException e) {
                log.error("Couldn't serialize response for content type application/json", e);
                return new ResponseEntity<List<Matcher>>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<List<Matcher>>(HttpStatus.NOT_IMPLEMENTED);
    }

}
