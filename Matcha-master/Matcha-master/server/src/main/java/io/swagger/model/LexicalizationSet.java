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
 * LexicalizationSet
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class LexicalizationSet extends Dataset  {
  @JsonProperty("lexiconDataset")
  private String lexiconDataset = null;

  @JsonProperty("referenceDataset")
  private String referenceDataset = null;

  @JsonProperty("lexicalizationModel")
  private String lexicalizationModel = null;

  @JsonProperty("lexicalizations")
  private Integer lexicalizations = null;

  @JsonProperty("references")
  private Integer references = null;

  @JsonProperty("lexicalEntries")
  private Integer lexicalEntries = null;

  @JsonProperty("avgNumOfLexicalizations")
  private Double avgNumOfLexicalizations = null;

  @JsonProperty("percentage")
  private Double percentage = null;

  @JsonProperty("languageTag")
  private String languageTag = null;

  @JsonProperty("languageLexvo")
  private String languageLexvo = null;

  @JsonProperty("languageLOC")
  private String languageLOC = null;

  public LexicalizationSet lexiconDataset(String lexiconDataset) {
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

  public LexicalizationSet referenceDataset(String referenceDataset) {
    this.referenceDataset = referenceDataset;
    return this;
  }

  /**
   * Get referenceDataset
   * @return referenceDataset
   **/
  @Schema(description = "")
  
    public String getReferenceDataset() {
    return referenceDataset;
  }

  public void setReferenceDataset(String referenceDataset) {
    this.referenceDataset = referenceDataset;
  }

  public LexicalizationSet lexicalizationModel(String lexicalizationModel) {
    this.lexicalizationModel = lexicalizationModel;
    return this;
  }

  /**
   * Get lexicalizationModel
   * @return lexicalizationModel
   **/
  @Schema(description = "")
  
    public String getLexicalizationModel() {
    return lexicalizationModel;
  }

  public void setLexicalizationModel(String lexicalizationModel) {
    this.lexicalizationModel = lexicalizationModel;
  }

  public LexicalizationSet lexicalizations(Integer lexicalizations) {
    this.lexicalizations = lexicalizations;
    return this;
  }

  /**
   * Get lexicalizations
   * @return lexicalizations
   **/
  @Schema(description = "")
  
    public Integer getLexicalizations() {
    return lexicalizations;
  }

  public void setLexicalizations(Integer lexicalizations) {
    this.lexicalizations = lexicalizations;
  }

  public LexicalizationSet references(Integer references) {
    this.references = references;
    return this;
  }

  /**
   * Get references
   * @return references
   **/
  @Schema(description = "")
  
    public Integer getReferences() {
    return references;
  }

  public void setReferences(Integer references) {
    this.references = references;
  }

  public LexicalizationSet lexicalEntries(Integer lexicalEntries) {
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

  public LexicalizationSet avgNumOfLexicalizations(Double avgNumOfLexicalizations) {
    this.avgNumOfLexicalizations = avgNumOfLexicalizations;
    return this;
  }

  /**
   * Get avgNumOfLexicalizations
   * @return avgNumOfLexicalizations
   **/
  @Schema(description = "")
  
    public Double getAvgNumOfLexicalizations() {
    return avgNumOfLexicalizations;
  }

  public void setAvgNumOfLexicalizations(Double avgNumOfLexicalizations) {
    this.avgNumOfLexicalizations = avgNumOfLexicalizations;
  }

  public LexicalizationSet percentage(Double percentage) {
    this.percentage = percentage;
    return this;
  }

  /**
   * Get percentage
   * @return percentage
   **/
  @Schema(description = "")
  
    public Double getPercentage() {
    return percentage;
  }

  public void setPercentage(Double percentage) {
    this.percentage = percentage;
  }

  public LexicalizationSet languageTag(String languageTag) {
    this.languageTag = languageTag;
    return this;
  }

  /**
   * Get languageTag
   * @return languageTag
   **/
  @Schema(description = "")
  
    public String getLanguageTag() {
    return languageTag;
  }

  public void setLanguageTag(String languageTag) {
    this.languageTag = languageTag;
  }

  public LexicalizationSet languageLexvo(String languageLexvo) {
    this.languageLexvo = languageLexvo;
    return this;
  }

  /**
   * Get languageLexvo
   * @return languageLexvo
   **/
  @Schema(description = "")
  
    public String getLanguageLexvo() {
    return languageLexvo;
  }

  public void setLanguageLexvo(String languageLexvo) {
    this.languageLexvo = languageLexvo;
  }

  public LexicalizationSet languageLOC(String languageLOC) {
    this.languageLOC = languageLOC;
    return this;
  }

  /**
   * Get languageLOC
   * @return languageLOC
   **/
  @Schema(description = "")
  
    public String getLanguageLOC() {
    return languageLOC;
  }

  public void setLanguageLOC(String languageLOC) {
    this.languageLOC = languageLOC;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LexicalizationSet lexicalizationSet = (LexicalizationSet) o;
    return Objects.equals(this.lexiconDataset, lexicalizationSet.lexiconDataset) &&
        Objects.equals(this.referenceDataset, lexicalizationSet.referenceDataset) &&
        Objects.equals(this.lexicalizationModel, lexicalizationSet.lexicalizationModel) &&
        Objects.equals(this.lexicalizations, lexicalizationSet.lexicalizations) &&
        Objects.equals(this.references, lexicalizationSet.references) &&
        Objects.equals(this.lexicalEntries, lexicalizationSet.lexicalEntries) &&
        Objects.equals(this.avgNumOfLexicalizations, lexicalizationSet.avgNumOfLexicalizations) &&
        Objects.equals(this.percentage, lexicalizationSet.percentage) &&
        Objects.equals(this.languageTag, lexicalizationSet.languageTag) &&
        Objects.equals(this.languageLexvo, lexicalizationSet.languageLexvo) &&
        Objects.equals(this.languageLOC, lexicalizationSet.languageLOC) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lexiconDataset, referenceDataset, lexicalizationModel, lexicalizations, references, lexicalEntries, avgNumOfLexicalizations, percentage, languageTag, languageLexvo, languageLOC, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LexicalizationSet {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    lexiconDataset: ").append(toIndentedString(lexiconDataset)).append("\n");
    sb.append("    referenceDataset: ").append(toIndentedString(referenceDataset)).append("\n");
    sb.append("    lexicalizationModel: ").append(toIndentedString(lexicalizationModel)).append("\n");
    sb.append("    lexicalizations: ").append(toIndentedString(lexicalizations)).append("\n");
    sb.append("    references: ").append(toIndentedString(references)).append("\n");
    sb.append("    lexicalEntries: ").append(toIndentedString(lexicalEntries)).append("\n");
    sb.append("    avgNumOfLexicalizations: ").append(toIndentedString(avgNumOfLexicalizations)).append("\n");
    sb.append("    percentage: ").append(toIndentedString(percentage)).append("\n");
    sb.append("    languageTag: ").append(toIndentedString(languageTag)).append("\n");
    sb.append("    languageLexvo: ").append(toIndentedString(languageLexvo)).append("\n");
    sb.append("    languageLOC: ").append(toIndentedString(languageLOC)).append("\n");
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
