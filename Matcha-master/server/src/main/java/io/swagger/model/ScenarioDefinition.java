package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.Dataset;
import io.swagger.model.Pairing;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * A report about an alignment task conforming to MAPLE (Mapping based on Linguistic Evidences)
 */
@Schema(description = "A report about an alignment task conforming to MAPLE (Mapping based on Linguistic Evidences)")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class ScenarioDefinition   {
  @JsonProperty("leftDataset")
  private Dataset leftDataset = null;

  @JsonProperty("rightDataset")
  private Dataset rightDataset = null;

  @JsonProperty("supportDatasets")
  @Valid
  private List<Dataset> supportDatasets = new ArrayList<Dataset>();

  @JsonProperty("pairings")
  @Valid
  private List<Pairing> pairings = new ArrayList<Pairing>();

  public ScenarioDefinition leftDataset(Dataset leftDataset) {
    this.leftDataset = leftDataset;
    return this;
  }

  /**
   * Get leftDataset
   * @return leftDataset
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public Dataset getLeftDataset() {
    return leftDataset;
  }

  public void setLeftDataset(Dataset leftDataset) {
    this.leftDataset = leftDataset;
  }

  public ScenarioDefinition rightDataset(Dataset rightDataset) {
    this.rightDataset = rightDataset;
    return this;
  }

  /**
   * Get rightDataset
   * @return rightDataset
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public Dataset getRightDataset() {
    return rightDataset;
  }

  public void setRightDataset(Dataset rightDataset) {
    this.rightDataset = rightDataset;
  }

  public ScenarioDefinition supportDatasets(List<Dataset> supportDatasets) {
    this.supportDatasets = supportDatasets;
    return this;
  }

  public ScenarioDefinition addSupportDatasetsItem(Dataset supportDatasetsItem) {
    this.supportDatasets.add(supportDatasetsItem);
    return this;
  }

  /**
   * Get supportDatasets
   * @return supportDatasets
   **/
  @Schema(required = true, description = "")
      @NotNull
    @Valid
    public List<Dataset> getSupportDatasets() {
    return supportDatasets;
  }

  public void setSupportDatasets(List<Dataset> supportDatasets) {
    this.supportDatasets = supportDatasets;
  }

  public ScenarioDefinition pairings(List<Pairing> pairings) {
    this.pairings = pairings;
    return this;
  }

  public ScenarioDefinition addPairingsItem(Pairing pairingsItem) {
    this.pairings.add(pairingsItem);
    return this;
  }

  /**
   * Get pairings
   * @return pairings
   **/
  @Schema(required = true, description = "")
      @NotNull
    @Valid
    public List<Pairing> getPairings() {
    return pairings;
  }

  public void setPairings(List<Pairing> pairings) {
    this.pairings = pairings;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ScenarioDefinition scenarioDefinition = (ScenarioDefinition) o;
    return Objects.equals(this.leftDataset, scenarioDefinition.leftDataset) &&
        Objects.equals(this.rightDataset, scenarioDefinition.rightDataset) &&
        Objects.equals(this.supportDatasets, scenarioDefinition.supportDatasets) &&
        Objects.equals(this.pairings, scenarioDefinition.pairings);
  }

  @Override
  public int hashCode() {
    return Objects.hash(leftDataset, rightDataset, supportDatasets, pairings);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ScenarioDefinition {\n");
    
    sb.append("    leftDataset: ").append(toIndentedString(leftDataset)).append("\n");
    sb.append("    rightDataset: ").append(toIndentedString(rightDataset)).append("\n");
    sb.append("    supportDatasets: ").append(toIndentedString(supportDatasets)).append("\n");
    sb.append("    pairings: ").append(toIndentedString(pairings)).append("\n");
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
