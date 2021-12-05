package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeId;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.model.DataService;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Dataset
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type", visible = true )
@JsonSubTypes({
        @JsonSubTypes.Type(value = VoidDataset.class, name = "http://rdfs.org/ns/void#Dataset"),
        @JsonSubTypes.Type(value = ConceptSet.class, name = "http://www.w3.org/ns/lemon/ontolex#ConceptSet"),
        @JsonSubTypes.Type(value = Lexicon.class, name = "http://www.w3.org/ns/lemon/lime#Lexicon"),
        @JsonSubTypes.Type(value = LexicalizationSet.class, name = "http://www.w3.org/ns/lemon/lime#LexicalizationSet"),
        @JsonSubTypes.Type(value = ConceptualizationSet.class, name = "http://www.w3.org/ns/lemon/lime#ConceptualizationSet"),
})


public class Dataset   {
  @JsonProperty("@id")
  private String _atId = null;

  @JsonTypeId
  private String _atType = null;

  @JsonProperty("uriSpace")
  private String uriSpace = null;

  @JsonProperty("sparqlEndpoint")
  private DataService sparqlEndpoint = null;

  @JsonProperty("conformsTo")
  private String conformsTo = null;

  public Dataset _atId(String _atId) {
    this._atId = _atId;
    return this;
  }

  /**
   * Get _atId
   * @return _atId
   **/
  @Schema(required = true, description = "")
      @NotNull

    public String getAtId() {
    return _atId;
  }

  public void setAtId(String _atId) {
    this._atId = _atId;
  }

  public Dataset _atType(String _atType) {
    this._atType = _atType;
    return this;
  }

  /**
   * Get _atType
   * @return _atType
   **/
  @Schema(required = true, description = "")
      @NotNull

    public String getAtType() {
    return _atType;
  }

  public void setAtType(String _atType) {
    this._atType = _atType;
  }

  public Dataset uriSpace(String uriSpace) {
    this.uriSpace = uriSpace;
    return this;
  }

  /**
   * Get uriSpace
   * @return uriSpace
   **/
  @Schema(description = "")
  
    public String getUriSpace() {
    return uriSpace;
  }

  public void setUriSpace(String uriSpace) {
    this.uriSpace = uriSpace;
  }

  public Dataset sparqlEndpoint(DataService sparqlEndpoint) {
    this.sparqlEndpoint = sparqlEndpoint;
    return this;
  }

  /**
   * Get sparqlEndpoint
   * @return sparqlEndpoint
   **/
  @Schema(description = "")
  
    @Valid
    public DataService getSparqlEndpoint() {
    return sparqlEndpoint;
  }

  public void setSparqlEndpoint(DataService sparqlEndpoint) {
    this.sparqlEndpoint = sparqlEndpoint;
  }

  public Dataset conformsTo(String conformsTo) {
    this.conformsTo = conformsTo;
    return this;
  }

  /**
   * Get conformsTo
   * @return conformsTo
   **/
  @Schema(description = "")
  
    public String getConformsTo() {
    return conformsTo;
  }

  public void setConformsTo(String conformsTo) {
    this.conformsTo = conformsTo;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Dataset dataset = (Dataset) o;
    return Objects.equals(this._atId, dataset._atId) &&
        Objects.equals(this._atType, dataset._atType) &&
        Objects.equals(this.uriSpace, dataset.uriSpace) &&
        Objects.equals(this.sparqlEndpoint, dataset.sparqlEndpoint) &&
        Objects.equals(this.conformsTo, dataset.conformsTo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(_atId, _atType, uriSpace, sparqlEndpoint, conformsTo);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Dataset {\n");
    
    sb.append("    _atId: ").append(toIndentedString(_atId)).append("\n");
    sb.append("    _atType: ").append(toIndentedString(_atType)).append("\n");
    sb.append("    uriSpace: ").append(toIndentedString(uriSpace)).append("\n");
    sb.append("    sparqlEndpoint: ").append(toIndentedString(sparqlEndpoint)).append("\n");
    sb.append("    conformsTo: ").append(toIndentedString(conformsTo)).append("\n");
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
