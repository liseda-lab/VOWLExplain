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
 * ConceptSet
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class ConceptSet extends Dataset  {
  @JsonProperty("concepts")
  private Long concepts = null;

  public ConceptSet concepts(Long concepts) {
    this.concepts = concepts;
    return this;
  }

  /**
   * Get concepts
   * @return concepts
   **/
  @Schema(description = "")
  
    public Long getConcepts() {
    return concepts;
  }

  public void setConcepts(Long concepts) {
    this.concepts = concepts;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConceptSet conceptSet = (ConceptSet) o;
    return Objects.equals(this.concepts, conceptSet.concepts) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(concepts, super.hashCode());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConceptSet {\n");
    sb.append("    ").append(toIndentedString(super.toString())).append("\n");
    sb.append("    concepts: ").append(toIndentedString(concepts)).append("\n");
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
