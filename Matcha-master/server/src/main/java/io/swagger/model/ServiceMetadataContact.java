package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * An entity to contact for inquiries about the service
 */
@Schema(description = "An entity to contact for inquiries about the service")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class ServiceMetadataContact   {
  @JsonProperty("name")
  private String name = null;

  @JsonProperty("email")
  private String email = null;

  public ServiceMetadataContact name(String name) {
    this.name = name;
    return this;
  }

  /**
   * The name of the contact
   * @return name
   **/
  @Schema(required = true, description = "The name of the contact")
      @NotNull

    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ServiceMetadataContact email(String email) {
    this.email = email;
    return this;
  }

  /**
   * The email address of the contact
   * @return email
   **/
  @Schema(required = true, description = "The email address of the contact")
      @NotNull

    public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ServiceMetadataContact serviceMetadataContact = (ServiceMetadataContact) o;
    return Objects.equals(this.name, serviceMetadataContact.name) &&
        Objects.equals(this.email, serviceMetadataContact.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, email);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ServiceMetadataContact {\n");
    
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
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
