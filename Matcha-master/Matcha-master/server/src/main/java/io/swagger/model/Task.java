package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.TaskReason;
import io.swagger.v3.oas.annotations.media.Schema;
import org.threeten.bp.OffsetDateTime;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * The task of aligning &#x60;leftDataset&#x60; and &#x60;rightDataset&#x60;
 */
@Schema(description = "The task of aligning `leftDataset` and `rightDataset`")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-06-21T17:56:07.345478100+01:00[Europe/London]")


public class Task   {
  @JsonProperty("id")
  private String id = null;

  @JsonProperty("leftDataset")
  private String leftDataset = null;

  @JsonProperty("rightDataset")
  private String rightDataset = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("progress")
  private Integer progress = null;

  @JsonProperty("reason")
  private TaskReason reason = null;

  @JsonProperty("submissionTime")
  private OffsetDateTime submissionTime = null;

  @JsonProperty("startTime")
  private OffsetDateTime startTime = null;

  @JsonProperty("endTime")
  private OffsetDateTime endTime = null;

  public Task id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Identifier of the task
   * @return id
   **/
  @Schema(required = true, description = "Identifier of the task")
      @NotNull

    public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Task leftDataset(String leftDataset) {
    this.leftDataset = leftDataset;
    return this;
  }

  /**
   * The dataset containing the left-hand side of the semantic correspondences to compute
   * @return leftDataset
   **/
  @Schema(required = true, description = "The dataset containing the left-hand side of the semantic correspondences to compute")
      @NotNull

    public String getLeftDataset() {
    return leftDataset;
  }

  public void setLeftDataset(String leftDataset) {
    this.leftDataset = leftDataset;
  }

  public Task rightDataset(String rightDataset) {
    this.rightDataset = rightDataset;
    return this;
  }

  /**
   * The dataset containing the right-hand side of the semantic correspondences to compute
   * @return rightDataset
   **/
  @Schema(required = true, description = "The dataset containing the right-hand side of the semantic correspondences to compute")
      @NotNull

    public String getRightDataset() {
    return rightDataset;
  }

  public void setRightDataset(String rightDataset) {
    this.rightDataset = rightDataset;
  }

  public Task status(String status) {
    this.status = status;
    return this;
  }

  /**
   * Different stages of a task lifecycle
   * @return status
   **/
  @Schema(required = true, description = "Different stages of a task lifecycle")
      @NotNull

    public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Task progress(Integer progress) {
    this.progress = progress;
    return this;
  }

  /**
   * the percentage of work done by `running` tasks
   * minimum: 0
   * maximum: 100
   * @return progress
   **/
  @Schema(description = "the percentage of work done by `running` tasks")
  
  @Min(0) @Max(100)   public Integer getProgress() {
    return progress;
  }

  public void setProgress(Integer progress) {
    this.progress = progress;
  }

  public Task reason(TaskReason reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
   **/
  @Schema(description = "")
  
    @Valid
    public TaskReason getReason() {
    return reason;
  }

  public void setReason(TaskReason reason) {
    this.reason = reason;
  }

  public Task submissionTime(OffsetDateTime submissionTime) {
    this.submissionTime = submissionTime;
    return this;
  }

  /**
   * The instant at which the task was submitted
   * @return submissionTime
   **/
  @Schema(required = true, description = "The instant at which the task was submitted")
      @NotNull

    @Valid
    public OffsetDateTime getSubmissionTime() {
    return submissionTime;
  }

  public void setSubmissionTime(OffsetDateTime submissionTime) {
    this.submissionTime = submissionTime;
  }

  public Task startTime(OffsetDateTime startTime) {
    this.startTime = startTime;
    return this;
  }

  /**
   * The instant at which the execution of the task actually started
   * @return startTime
   **/
  @Schema(description = "The instant at which the execution of the task actually started")
  
    @Valid
    public OffsetDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(OffsetDateTime startTime) {
    this.startTime = startTime;
  }

  public Task endTime(OffsetDateTime endTime) {
    this.endTime = endTime;
    return this;
  }

  /**
   * The instant at which the execution of the task ended, because of successful completion or a failure
   * @return endTime
   **/
  @Schema(description = "The instant at which the execution of the task ended, because of successful completion or a failure")
  
    @Valid
    public OffsetDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(OffsetDateTime endTime) {
    this.endTime = endTime;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Task task = (Task) o;
    return Objects.equals(this.id, task.id) &&
        Objects.equals(this.leftDataset, task.leftDataset) &&
        Objects.equals(this.rightDataset, task.rightDataset) &&
        Objects.equals(this.status, task.status) &&
        Objects.equals(this.progress, task.progress) &&
        Objects.equals(this.reason, task.reason) &&
        Objects.equals(this.submissionTime, task.submissionTime) &&
        Objects.equals(this.startTime, task.startTime) &&
        Objects.equals(this.endTime, task.endTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, leftDataset, rightDataset, status, progress, reason, submissionTime, startTime, endTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Task {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    leftDataset: ").append(toIndentedString(leftDataset)).append("\n");
    sb.append("    rightDataset: ").append(toIndentedString(rightDataset)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    progress: ").append(toIndentedString(progress)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("    submissionTime: ").append(toIndentedString(submissionTime)).append("\n");
    sb.append("    startTime: ").append(toIndentedString(startTime)).append("\n");
    sb.append("    endTime: ").append(toIndentedString(endTime)).append("\n");
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
