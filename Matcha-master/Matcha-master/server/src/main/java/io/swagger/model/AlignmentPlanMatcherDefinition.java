package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.Settings;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Matcher to use and its settings
 */
@Schema(description = "Matcher to use and its settings")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class AlignmentPlanMatcherDefinition   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("settings")
  private Settings settings = null;

  public AlignmentPlanMatcherDefinition id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of the matcher
   * @return id
   **/
  @Schema(required = true, description = "Identifier of the matcher")
      @NotNull

    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public AlignmentPlanMatcherDefinition settings(Settings settings) {
    this.settings = settings;
    return this;
  }

  /**
   * Get settings
   * @return settings
   **/
  @Schema(description = "")
  
    @Valid
    public Settings getSettings() {
    return settings;
  }

  public void setSettings(Settings settings) {
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
    AlignmentPlanMatcherDefinition alignmentPlanMatcherDefinition = (AlignmentPlanMatcherDefinition) o;
    return Objects.equals(this.id, alignmentPlanMatcherDefinition.id) &&
        Objects.equals(this.settings, alignmentPlanMatcherDefinition.settings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, settings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AlignmentPlanMatcherDefinition {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
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
