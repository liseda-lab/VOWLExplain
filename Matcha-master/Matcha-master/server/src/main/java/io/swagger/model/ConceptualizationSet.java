package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.DataService;
import io.swagger.model.Dataset;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ConceptualizationSet
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class ConceptualizationSet extends Dataset  {
  @JsonProperty("lexiconDataset")
  private String lexiconDataset = null;

  @JsonProperty("conceptualDataset")
  private String conceptualDataset = null;

  @JsonProperty("conceptualizations")
  private Integer conceptualizations = null;

  @JsonProperty("concepts")
  private Integer concepts = null;

  @JsonProperty("lexicalEntries")
  private Integer lexicalEntries = null;

  @JsonProperty("avgSynonymy")
  private Double avgSynonymy = null;

  @JsonProperty("avgAmbiguity")
  private Double avgAmbiguity = null;

  public ConceptualizationSet lexiconDataset(String lexiconDataset) {
    this.lexiconDataset = lexiconDataset;
    return this;
  }

  /**
   * Get lexiconDataset
   * @return lexiconDataset
   **/
  @Schema(description = "")
  
    public String getLexiconDataset() {
    return lexiconDataset;
  }

  public void setLexiconDataset(String lexiconDataset) {
    this.lexiconDataset = lexiconDataset;
  }

  public ConceptualizationSet conceptualDataset(String conceptualDataset) {
    this.conceptualDataset = conceptualDataset;
    return this;
  }

  /**
   * Get conceptualDataset
   * @return conceptualDataset
   **/
  @Schema(description = "")
  
    public String getConceptualDataset() {
    return conceptualDataset;
  }

  public void setConceptualDataset(String conceptualDataset) {
    this.conceptualDataset = conceptualDataset;
  }

  public ConceptualizationSet conceptualizations(Integer conceptualizations) {
    this.conceptualizations = conceptualizations;
    return this;
  }

  /**
   * Get conceptualizations
   * @return conceptualizations
   **/
  @Schema(description = "")
  
    public Integer getConceptualizations() {
    return conceptualizations;
  }

  public void setConceptualizations(Integer conceptualizations) {
    this.conceptualizations = conceptualizations;
  }

  public ConceptualizationSet concepts(Integer concepts) {
    this.concepts = concepts;
    return this;
  }

  /**
   * Get concepts
   * @return concepts
   **/
  @Schema(description = "")
  
    public Integer getConcepts() {
    return concepts;
  }

  public void setConcepts(Integer concepts) {
    this.concepts = concepts;
  }

  public ConceptualizationSet lexicalEntries(Integer lexicalEntries) {
    this.lexicalEntries = lexicalEntries;
    return this;
  }

  /**
   * Get lexicalEntries
   * @return lexicalEntries
   **/
  @Schema(description = "")
  
    public Integer getLexicalEntries() {
    return lexicalEntries;
  }

  public void setLexicalEntries(Integer lexicalEntries) {
    this.lexicalEntries = lexicalEntries;
  }

  public ConceptualizationSet avgSynonymy(Double avgSynonymy) {
    this.avgSynonymy = avgSynonymy;
    return this;
  }

  /**
   * Get avgSynonymy
   * @return avgSynonymy
   **/
  @Schema(description = "")
  
    public Double getAvgSynonymy() {
    return avgSynonymy;
  }

  public void setAvgSynonymy(Double avgSynonymy) {
    this.avgSynonymy = avgSynonymy;
  }

  public ConceptualizationSet avgAmbiguity(Double avgAmbiguity) {
    this.avgAmbiguity = avgAmbiguity;
    return this;
  }

  /**
   * Get avgAmbiguity
   * @return avgAmbiguity
   **/
  @Schema(description = "")
  
    public Double getAvgAmbiguity() {
    return avgAmbiguity;
  }

  public void setAvgAmbiguity(Double avgAmbiguity) {
    this.avgAmbiguity = avgAmbiguity;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConceptualizationSet conceptualizationSet = (ConceptualizationSet) o;
    return Objects.equals(this.lexiconDataset, conceptualizationSet.lexiconDataset) &&
        Objects.equals(this.conceptualDataset, conceptualizationSet.conceptualDataset) &&
        Objects.equals(this.conceptualizations, conceptualizationSet.conceptualizations) &&
        Objects.equals(this.concepts, conceptualizationSet.concepts) &&
        Objects.equals(this.lexicalEntries, conceptualizationSet.lexicalEntries) &&
        Objects.equals(this.avgSynonymy, conceptualizationSet.avgSynonymy) &&
        Objects.equals(this.avgAmbiguity, conceptualizationSet.avgAmbiguity) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lexiconDataset, conceptualDataset, conceptualizations, concepts, lexicalEntries, avgSynonymy, avgAmbiguity, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConceptualizationSet {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    lexiconDataset: ").append(toIndentedString(lexiconDataset)).append("\n");
    sb.append("    conceptualDataset: ").append(toIndentedString(conceptualDataset)).append("\n");
    sb.append("    conceptualizations: ").append(toIndentedString(conceptualizations)).append("\n");
    sb.append("    concepts: ").append(toIndentedString(concepts)).append("\n");
    sb.append("    lexicalEntries: ").append(toIndentedString(lexicalEntries)).append("\n");
    sb.append("    avgSynonymy: ").append(toIndentedString(avgSynonymy)).append("\n");
    sb.append("    avgAmbiguity: ").append(toIndentedString(avgAmbiguity)).append("\n");
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
