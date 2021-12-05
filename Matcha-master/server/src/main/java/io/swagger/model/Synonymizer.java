package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Synonymizer
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class Synonymizer   {
  @JsonProperty("lexicon")
  private String lexicon = null;

  @JsonProperty("conceptualizationSet")
  private String conceptualizationSet = null;

  public Synonymizer lexicon(String lexicon) {
    this.lexicon = lexicon;
    return this;
  }

  /**
   * the IRI of a support dataset that is a lexicalization set
   * @return lexicon
   **/
  @Schema(required = true, description = "the IRI of a support dataset that is a lexicalization set")
      @NotNull

    public String getLexicon() {
    return lexicon;
  }

  public void setLexicon(String lexicon) {
    this.lexicon = lexicon;
  }

  public Synonymizer conceptualizationSet(String conceptualizationSet) {
    this.conceptualizationSet = conceptualizationSet;
    return this;
  }

  /**
   * the IRI of a support dataset that is a conceptualization set
   * @return conceptualizationSet
   **/
  @Schema(required = true, description = "the IRI of a support dataset that is a conceptualization set")
      @NotNull

    public String getConceptualizationSet() {
    return conceptualizationSet;
  }

  public void setConceptualizationSet(String conceptualizationSet) {
    this.conceptualizationSet = conceptualizationSet;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Synonymizer synonymizer = (Synonymizer) o;
    return Objects.equals(this.lexicon, synonymizer.lexicon) &&
        Objects.equals(this.conceptualizationSet, synonymizer.conceptualizationSet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lexicon, conceptualizationSet);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Synonymizer {\n");
    
    sb.append("    lexicon: ").append(toIndentedString(lexicon)).append("\n");
    sb.append("    conceptualizationSet: ").append(toIndentedString(conceptualizationSet)).append("\n");
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
