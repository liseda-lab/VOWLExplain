package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.PairingHand;
import io.swagger.model.Synonymizer;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Pairing
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class Pairing   {
  @JsonProperty("score")
  private Double score = null;

  @JsonProperty("source")
  private PairingHand source = null;

  @JsonProperty("target")
  private PairingHand target = null;

  @JsonProperty("synonymizer")
  private Synonymizer synonymizer = null;

  public Pairing score(Double score) {
    this.score = score;
    return this;
  }

  /**
   * Get score
   * @return score
   **/
  @Schema(required = true, description = "")
      @NotNull

    public Double getScore() {
    return score;
  }

  public void setScore(Double score) {
    this.score = score;
  }

  public Pairing source(PairingHand source) {
    this.source = source;
    return this;
  }

  /**
   * Get source
   * @return source
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public PairingHand getSource() {
    return source;
  }

  public void setSource(PairingHand source) {
    this.source = source;
  }

  public Pairing target(PairingHand target) {
    this.target = target;
    return this;
  }

  /**
   * Get target
   * @return target
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public PairingHand getTarget() {
    return target;
  }

  public void setTarget(PairingHand target) {
    this.target = target;
  }

  public Pairing synonymizer(Synonymizer synonymizer) {
    this.synonymizer = synonymizer;
    return this;
  }

  /**
   * Get synonymizer
   * @return synonymizer
   **/
  @Schema(description = "")
  
    @Valid
    public Synonymizer getSynonymizer() {
    return synonymizer;
  }

  public void setSynonymizer(Synonymizer synonymizer) {
    this.synonymizer = synonymizer;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pairing pairing = (Pairing) o;
    return Objects.equals(this.score, pairing.score) &&
        Objects.equals(this.source, pairing.source) &&
        Objects.equals(this.target, pairing.target) &&
        Objects.equals(this.synonymizer, pairing.synonymizer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(score, source, target, synonymizer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Pairing {\n");
    
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
    sb.append("    source: ").append(toIndentedString(source)).append("\n");
    sb.append("    target: ").append(toIndentedString(target)).append("\n");
    sb.append("    synonymizer: ").append(toIndentedString(synonymizer)).append("\n");
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
