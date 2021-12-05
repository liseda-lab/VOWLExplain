package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.ServiceMetadataContact;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Metadata about the alignment service
 */
@Schema(description = "Metadata about the alignment service")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class ServiceMetadata   {
  @JsonProperty("service")
  private String service = null;

  @JsonProperty("version")
  private String version = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("specs")
  @Valid
  private List<String> specs = new ArrayList<String>();

  @JsonProperty("contact")
  private ServiceMetadataContact contact = null;

  @JsonProperty("documentation")
  private String documentation = null;

  @JsonProperty("settings")
  private Schema settings = null;

  public ServiceMetadata service(String service) {
    this.service = service;
    return this;
  }

  /**
   * The name of the service
   * @return service
   **/
  @Schema(required = true, description = "The name of the service")
      @NotNull

    public String getService() {
    return service;
  }

  public void setService(String service) {
    this.service = service;
  }

  public ServiceMetadata version(String version) {
    this.version = version;
    return this;
  }

  /**
   * The version of the service
   * @return version
   **/
  @Schema(required = true, description = "The version of the service")
      @NotNull

    public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public ServiceMetadata status(String status) {
    this.status = status;
    return this;
  }

  /**
   * The status of the service
   * @return status
   **/
  @Schema(required = true, description = "The status of the service")
      @NotNull

    public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public ServiceMetadata specs(List<String> specs) {
    this.specs = specs;
    return this;
  }

  public ServiceMetadata addSpecsItem(String specsItem) {
    this.specs.add(specsItem);
    return this;
  }

  /**
   * The specifications implemented by the service. The collection shall contain at least a reference to this alignment services specification.  
   * @return specs
   **/
  @Schema(required = true, description = "The specifications implemented by the service. The collection shall contain at least a reference to this alignment services specification.  ")
      @NotNull

  @Size(min=1)   public List<String> getSpecs() {
    return specs;
  }

  public void setSpecs(List<String> specs) {
    this.specs = specs;
  }

  public ServiceMetadata contact(ServiceMetadataContact contact) {
    this.contact = contact;
    return this;
  }

  /**
   * Get contact
   * @return contact
   **/
  @Schema(description = "")
  
    @Valid
    public ServiceMetadataContact getContact() {
    return contact;
  }

  public void setContact(ServiceMetadataContact contact) {
    this.contact = contact;
  }

  public ServiceMetadata documentation(String documentation) {
    this.documentation = documentation;
    return this;
  }

  /**
   * The address of the documentation of the service
   * @return documentation
   **/
  @Schema(description = "The address of the documentation of the service")
  
    public String getDocumentation() {
    return documentation;
  }

  public void setDocumentation(String documentation) {
    this.documentation = documentation;
  }

  public ServiceMetadata settings(Schema settings) {
    this.settings = settings;
    return this;
  }

  /**
   * Get settings
   * @return settings
   **/
  @Schema(description = "")
  
    @Valid
    public Schema getSettings() {
    return settings;
  }

  public void setSettings(Schema settings) {
    this.settings = settings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceMetadata serviceMetadata = (ServiceMetadata) o;
    return Objects.equals(this.service, serviceMetadata.service) &&
        Objects.equals(this.version, serviceMetadata.version) &&
        Objects.equals(this.status, serviceMetadata.status) &&
        Objects.equals(this.specs, serviceMetadata.specs) &&
        Objects.equals(this.contact, serviceMetadata.contact) &&
        Objects.equals(this.documentation, serviceMetadata.documentation) &&
        Objects.equals(this.settings, serviceMetadata.settings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(service, version, status, specs, contact, documentation, settings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceMetadata {\n");
    
    sb.append("    service: ").append(toIndentedString(service)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    specs: ").append(toIndentedString(specs)).append("\n");
    sb.append("    contact: ").append(toIndentedString(contact)).append("\n");
    sb.append("    documentation: ").append(toIndentedString(documentation)).append("\n");
    sb.append("    settings: ").append(toIndentedString(settings)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
