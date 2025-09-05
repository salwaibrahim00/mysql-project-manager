package projects.entity;

public class Step {
  private Integer stepId;
  private Integer projectId;
  private String stepText;
  private Integer stepOrder;

  // ✅ Added no-arg constructor to fix the DAO error
  public Step() {
  }

  // Existing constructor
  public Step(Integer projectId, String stepText, Integer stepOrder) {
    this.projectId = projectId;
    this.stepText = stepText;
    this.stepOrder = stepOrder;
  }

  public Integer getStepId() {
    return stepId;
  }

  public void setStepId(Integer stepId) {
    this.stepId = stepId;
  }

  public Integer getProjectId() {
    return projectId;
  }

  public void setProjectId(Integer projectId) {
    this.projectId = projectId;
  }

  public String getStepText() {
    return stepText;
  }

  public void setStepText(String stepText) {
    this.stepText = stepText;
  }

  public Integer getStepOrder() {
    return stepOrder;
  }

  public void setStepOrder(Integer stepOrder) {
    this.stepOrder = stepOrder;
  }

  @Override
  public String toString() {
    return "ID=" + stepId + ", stepText=" + stepText;
  }
}
