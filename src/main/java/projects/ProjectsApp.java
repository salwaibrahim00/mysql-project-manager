package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;
import projects.entity.Project;
import projects.entity.Step;
import projects.service.ProjectService;

public class ProjectsApp {
    private Scanner scanner = new Scanner(System.in);
    private ProjectService service = new ProjectService();
    private Project currentProject = null;

    public static void main(String[] args) {
        System.out.println("Starting Project Manager Application");
        new ProjectsApp().runApplication();
    }

    private void runApplication() {
        boolean keepRunning = true;
        
        while (keepRunning) {
            try {
                showMainMenu();
                int userChoice = getUserChoice();
                
                switch (userChoice) {
                    case 0:
                        keepRunning = false;
                        System.out.println("Goodbye! Thanks for using Project Manager!");
                        break;
                    case 1:
                        addNewProject();
                        break;
                    case 2:
                        showAllProjects();
                        break;
                    case 3:
                        selectProject();
                        break;
                    case 4:
                        addStepToProject();
                        break;
                    case 5:
                        viewProjectDetails();
                        break;
                    case 6:
                        updateProjectDetails();
                        break;
                    case 7:
                        deleteProject();
                        break;
                    default:
                        System.out.println("That's not a valid option. Please try again.");
                        break;
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Something went wrong: " + e.getMessage());
                System.out.println("Let's try again...");
            }
        }
    }

    private void showMainMenu() {
        System.out.println("\n==================================================");
        System.out.println("PROJECT MANAGER - Main Menu");
        System.out.println("==================================================");
        
        if (currentProject != null) {
            System.out.println("Currently working on: " + currentProject.getProjectName());
            System.out.println("--------------------------------------------------");
        }
        
        System.out.println("1) Add a new project");
        System.out.println("2) List all projects");
        System.out.println("3) Select a project to work on");
        System.out.println("4) Add step to current project");
        System.out.println("5) View project details");
        System.out.println("6) Update project details");
        System.out.println("7) Delete a project");
        System.out.println("0) Exit");
        System.out.println("==================================================");
        System.out.print("Your choice: ");
    }

    private int getUserChoice() {
        String input = scanner.nextLine().trim();
        return Integer.parseInt(input);
    }

    private String getTextInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private void addNewProject() {
        System.out.println("\nAdding a New Project");
        System.out.println("------------------------------");
        
        String projectName = getTextInput("Project name: ");
        if (projectName.isEmpty()) {
            System.out.println("Project name is required!");
            return;
        }
        
        String hoursInput = getTextInput("Estimated hours (or press Enter to skip): ");
        BigDecimal estimatedHours = null;
        if (!hoursInput.isEmpty()) {
            try {
                estimatedHours = new BigDecimal(hoursInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number for hours. Skipping...");
            }
        }
        
        String actualHoursInput = getTextInput("Actual hours (or press Enter to skip): ");
        BigDecimal actualHours = null;
        if (!actualHoursInput.isEmpty()) {
            try {
                actualHours = new BigDecimal(actualHoursInput);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number for actual hours. Skipping...");
            }
        }
        
        String difficultyInput = getTextInput("Difficulty level 1-5 (or press Enter to skip): ");
        Integer difficulty = null;
        if (!difficultyInput.isEmpty()) {
            try {
                difficulty = Integer.parseInt(difficultyInput);
                if (difficulty < 1 || difficulty > 5) {
                    System.out.println("Difficulty should be 1-5. Skipping...");
                    difficulty = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number for difficulty. Skipping...");
            }
        }
        
        String notes = getTextInput("Notes (or press Enter to skip): ");
        if (notes.isEmpty()) {
            notes = null;
        }
        
        Project newProject = new Project(projectName, estimatedHours, actualHours, difficulty, notes);
        service.addProject(newProject);
        
        System.out.println("Project '" + projectName + "' has been added successfully!");
    }

    private void showAllProjects() {
        System.out.println("\nAll Projects");
        System.out.println("------------------------------");
        
        List<Project> allProjects = service.getAllProjects();
        
        if (allProjects.isEmpty()) {
            System.out.println("No projects found. Why not add one?");
            return;
        }
        
        System.out.printf("%-5s %-30s %-12s %-10s%n", "ID", "Project Name", "Est. Hours", "Difficulty");
        System.out.println("------------------------------------------------------------");
        
        for (Project project : allProjects) {
            System.out.printf("%-5d %-30s %-12s %-10s%n",
                project.getProjectId(),
                project.getProjectName(),
                project.getEstimatedHours() != null ? project.getEstimatedHours().toString() : "Not set",
                project.getDifficulty() != null ? project.getDifficulty().toString() : "Not set"
            );
        }
    }

    private void selectProject() {
        System.out.println("\nSelect a Project");
        System.out.println("------------------------------");
        
        showAllProjects();
        
        String idInput = getTextInput("\nEnter the project ID you want to work on: ");
        try {
            int projectId = Integer.parseInt(idInput);
            Project selectedProject = service.getProject(projectId);
            
            if (selectedProject != null) {
                currentProject = selectedProject;
                System.out.println("Now working on: " + currentProject.getProjectName());
            } else {
                System.out.println("Project with ID " + projectId + " not found.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid project ID number.");
        }
    }

    private void addStepToProject() {
        System.out.println("\nAdd Step to Project");
        System.out.println("------------------------------");
        
        if (currentProject == null) {
            System.out.println("You need to select a project first! Use option 3.");
            return;
        }
        
        System.out.println("Adding step to: " + currentProject.getProjectName());
        
        List<Step> existingSteps = service.getSteps(currentProject.getProjectId());
        if (!existingSteps.isEmpty()) {
            System.out.println("\nCurrent steps:");
            for (Step step : existingSteps) {
                System.out.println("  " + step.toString());
            }
        }
        
        String stepText = getTextInput("\nEnter the step description: ");
        if (stepText.isEmpty()) {
            System.out.println("Step description is required!");
            return;
        }
        
        int nextStepNumber = existingSteps.size() + 1;
        
        Step newStep = new Step(currentProject.getProjectId(), stepText, nextStepNumber);
        service.addStep(newStep);
        
        System.out.println("Step " + nextStepNumber + " added successfully!");
    }

    private void viewProjectDetails() {
        System.out.println("\nProject Details");
        System.out.println("------------------------------");
        
        if (currentProject == null) {
            System.out.println("You need to select a project first! Use option 3.");
            return;
        }
        
        System.out.println("ID: " + currentProject.getProjectId());
        System.out.println("Name: " + currentProject.getProjectName());
        System.out.println("Estimated Hours: " + (currentProject.getEstimatedHours() != null ? currentProject.getEstimatedHours() : "Not set"));
        System.out.println("Actual Hours: " + (currentProject.getActualHours() != null ? currentProject.getActualHours() : "Not set"));
        System.out.println("Difficulty: " + (currentProject.getDifficulty() != null ? currentProject.getDifficulty() + "/5" : "Not set"));
        System.out.println("Notes: " + (currentProject.getNotes() != null ? currentProject.getNotes() : "No notes"));
        
        List<Step> projectSteps = service.getSteps(currentProject.getProjectId());
        if (!projectSteps.isEmpty()) {
            System.out.println("\nSteps:");
            for (Step step : projectSteps) {
                System.out.println("   " + step.toString());
            }
        } else {
            System.out.println("\nSteps: No steps added yet");
        }
        
        System.out.println("--------------------------------------------------");
    }

    private void updateProjectDetails() {
        System.out.println("\nUpdate Project Details");
        System.out.println("------------------------------");
        
        if (currentProject == null) {
            System.out.println("You need to select a project first! Use option 3.");
            return;
        }
        
        System.out.println("Updating: " + currentProject.getProjectName());
        System.out.println("Press Enter to keep current value");
        System.out.println();
        
        String currentName = currentProject.getProjectName();
        String newName = getTextInput("Project name [" + currentName + "]: ");
        if (!newName.isEmpty()) {
            currentProject.setProjectName(newName);
        }
        
        String currentEstimated = currentProject.getEstimatedHours() != null ? currentProject.getEstimatedHours().toString() : "Not set";
        String newEstimated = getTextInput("Estimated hours [" + currentEstimated + "]: ");
        if (!newEstimated.isEmpty()) {
            try {
                currentProject.setEstimatedHours(new BigDecimal(newEstimated));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number for estimated hours. Keeping current value.");
            }
        }
        
        String currentActual = currentProject.getActualHours() != null ? currentProject.getActualHours().toString() : "Not set";
        String newActual = getTextInput("Actual hours [" + currentActual + "]: ");
        if (!newActual.isEmpty()) {
            try {
                currentProject.setActualHours(new BigDecimal(newActual));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number for actual hours. Keeping current value.");
            }
        }
        
        String currentDifficulty = currentProject.getDifficulty() != null ? currentProject.getDifficulty().toString() : "Not set";
        String newDifficulty = getTextInput("Difficulty 1-5 [" + currentDifficulty + "]: ");
        if (!newDifficulty.isEmpty()) {
            try {
                int difficulty = Integer.parseInt(newDifficulty);
                if (difficulty >= 1 && difficulty <= 5) {
                    currentProject.setDifficulty(difficulty);
                } else {
                    System.out.println("Difficulty must be 1-5. Keeping current value.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number for difficulty. Keeping current value.");
            }
        }
        
        String currentNotes = currentProject.getNotes() != null ? currentProject.getNotes() : "No notes";
        String newNotes = getTextInput("Notes [" + currentNotes + "]: ");
        if (!newNotes.isEmpty()) {
            currentProject.setNotes(newNotes);
        }
        
        service.updateProject(currentProject);
        
        System.out.println("Project updated successfully!");
    }

    private void deleteProject() {
        System.out.println("\nDelete a Project");
        System.out.println("------------------------------");
        
        showAllProjects();
        
        String idInput = getTextInput("\nEnter the project ID to delete: ");
        try {
            int projectId = Integer.parseInt(idInput);
            
            String confirm = getTextInput("Are you sure you want to delete this project? (yes/no): ");
            if (!confirm.equalsIgnoreCase("yes")) {
                System.out.println("Deletion cancelled.");
                return;
            }
            
            service.deleteProject(projectId);
            
            System.out.println("Project with ID " + projectId + " has been deleted successfully!");
            
            if (currentProject != null && currentProject.getProjectId().equals(projectId)) {
                currentProject = null;
                System.out.println("Current project selection cleared.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid project ID number.");
        }
    }
}