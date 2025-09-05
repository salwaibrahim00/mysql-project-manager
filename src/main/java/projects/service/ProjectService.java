package projects.service;

import java.util.List;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.entity.Step;

public class ProjectService {
    // This class talks to the database through ProjectDao
    private ProjectDao dao = new ProjectDao();

    // Add a new project
    public void addProject(Project project) {
        System.out.println("Service: Adding new project - " + project.getProjectName());
        dao.addProject(project);
    }

    // Get all projects
    public List<Project> getAllProjects() {
        System.out.println("Service: Getting all projects from database");
        return dao.getAllProjects();
    }

    // Get one specific project
    public Project getProject(Integer projectId) {
        System.out.println("Service: Getting project with ID " + projectId);
        return dao.getProject(projectId);
    }

    // Update an existing project
    public void updateProject(Project project) {
        System.out.println("Service: Updating project - " + project.getProjectName());
        dao.updateProject(project);
    }

    // Delete a project
    public void deleteProject(Integer projectId) {
        System.out.println("Service: Deleting project with ID " + projectId);
        dao.deleteProject(projectId);
    }

    // Add a step to a project
    public void addStep(Step step) {
        System.out.println("Service: Adding step to project ID " + step.getProjectId());
        dao.addStep(step);
    }

    // Get all steps for a project
    public List<Step> getSteps(Integer projectId) {
        System.out.println("Service: Getting steps for project ID " + projectId);
        return dao.getSteps(projectId);
    }
}