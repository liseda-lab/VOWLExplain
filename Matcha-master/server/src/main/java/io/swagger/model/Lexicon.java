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
 * Lexicon
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class Lexicon extends Dataset  {
  @JsonProperty("languageTag")
  private String languageTag = null;

  @JsonProperty("languageLexvo")
  private String languageLexvo = null;

  @JsonProperty("languageLOC")
  private String languageLOC = null;

  @JsonProperty("linguisticCatalog")
  private String linguisticCatalog = null;

  @JsonProperty("lexicalEntries")
  private Integer lexicalEntries = null;

  public Lexicon languageTag(String languageTag) {
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

  public Lexicon languageLexvo(String languageLexvo) {
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

  public Lexicon languageLOC(String languageLOC) {
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

  public Lexicon linguisticCatalog(String linguisticCatalog) {
    this.linguisticCatalog = linguisticCatalog;
    return this;
  }

  /**
   * Get linguisticCatalog
   * @return linguisticCatalog
   **/
  @Schema(description = "")
  
    public String getLinguisticCatalog() {
    return linguisticCatalog;
  }

  public void setLinguisticCatalog(String linguisticCatalog) {
    this.linguisticCatalog = linguisticCatalog;
  }

  public Lexicon lexicalEntries(Integer lexicalEntries) {
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


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Lexicon lexicon = (Lexicon) o;
    return Objects.equals(this.languageTag, lexicon.languageTag) &&
        Objects.equals(this.languageLexvo, lexicon.languageLexvo) &&
        Objects.equals(this.languageLOC, lexicon.languageLOC) &&
        Objects.equals(this.linguisticCatalog, lexicon.linguisticCatalog) &&
        Objects.equals(this.lexicalEntries, lexicon.lexicalEntries) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(languageTag, languageLexvo, languageLOC, linguisticCatalog, lexicalEntries, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Lexicon {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    languageTag: ").append(toIndentedString(languageTag)).append("\n");
    sb.append("    languageLexvo: ").append(toIndentedString(languageLexvo)).append("\n");
    sb.append("    languageLOC: ").append(toIndentedString(languageLOC)).append("\n");
    sb.append("    linguisticCatalog: ").append(toIndentedString(linguisticCatalog)).append("\n");
    sb.append("    lexicalEntries: ").append(toIndentedString(lexicalEntries)).append("\n");
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
