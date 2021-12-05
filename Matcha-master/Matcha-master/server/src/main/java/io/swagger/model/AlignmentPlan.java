package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.AlignmentPlanMatcherDefinition;
import io.swagger.model.ScenarioDefinition;
import io.swagger.model.Settings;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Description of a task submission
 */
@Schema(description = "Description of a task submission")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class AlignmentPlan   {
  @JsonProperty("scenarioDefinition")
  private ScenarioDefinition scenarioDefinition = null;

  @JsonProperty("settings")
  private Settings settings = null;

  @JsonProperty("matcherDefinition")
  private AlignmentPlanMatcherDefinition matcherDefinition = null;

  public AlignmentPlan scenarioDefinition(ScenarioDefinition scenarioDefinition) {
    this.scenarioDefinition = scenarioDefinition;
    return this;
  }

  /**
   * Get scenarioDefinition
   * @return scenarioDefinition
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public ScenarioDefinition getScenarioDefinition() {
    return scenarioDefinition;
  }

  public void setScenarioDefinition(ScenarioDefinition scenarioDefinition) {
    this.scenarioDefinition = scenarioDefinition;
  }

  public AlignmentPlan settings(Settings settings) {
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

  public AlignmentPlan matcherDefinition(AlignmentPlanMatcherDefinition matcherDefinition) {
    this.matcherDefinition = matcherDefinition;
    return this;
  }

  /**
   * Get matcherDefinition
   * @return matcherDefinition
   **/
  @Schema(description = "")
  
    @Valid
    public AlignmentPlanMatcherDefinition getMatcherDefinition() {
    return matcherDefinition;
  }

  public void setMatcherDefinition(AlignmentPlanMatcherDefinition matcherDefinition) {
    this.matcherDefinition = matcherDefinition;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AlignmentPlan alignmentPlan = (AlignmentPlan) o;
    return Objects.equals(this.scenarioDefinition, alignmentPlan.scenarioDefinition) &&
        Objects.equals(this.settings, alignmentPlan.settings) &&
        Objects.equals(this.matcherDefinition, alignmentPlan.matcherDefinition);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scenarioDefinition, settings, matcherDefinition);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AlignmentPlan {\n");
    
    sb.append("    scenarioDefinition: ").append(toIndentedString(scenarioDefinition)).append("\n");
    sb.append("    settings: ").append(toIndentedString(settings)).append("\n");
    sb.append("    matcherDefinition: ").append(toIndentedString(matcherDefinition)).append("\n");
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
