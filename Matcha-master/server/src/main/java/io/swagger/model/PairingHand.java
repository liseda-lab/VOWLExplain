package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * PairingHand
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class PairingHand   {
  @JsonProperty("lexicalizationSet")
  private String lexicalizationSet = null;

  public PairingHand lexicalizationSet(String lexicalizationSet) {
    this.lexicalizationSet = lexicalizationSet;
    return this;
  }

  /**
   * the IRI of a support dataset that is a lexicalization set
   * @return lexicalizationSet
   **/
  @Schema(required = true, description = "the IRI of a support dataset that is a lexicalization set")
      @NotNull

    public String getLexicalizationSet() {
    return lexicalizationSet;
  }

  public void setLexicalizationSet(String lexicalizationSet) {
    this.lexicalizationSet = lexicalizationSet;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PairingHand pairingHand = (PairingHand) o;
    return Objects.equals(this.lexicalizationSet, pairingHand.lexicalizationSet);
  }

  @Override
  public int hashCode() {
    return Objects.hash(lexicalizationSet);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PairingHand {\n");
    
    sb.append("    lexicalizationSet: ").append(toIndentedString(lexicalizationSet)).append("\n");
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
