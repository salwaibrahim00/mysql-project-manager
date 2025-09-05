package projects.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import projects.entity.Project;
import projects.entity.Step;
import projects.entity.Material;
import projects.entity.Category;
import projects.exception.DbException;

public class ProjectDao {

    // Add a new project to the database
    public void addProject(Project project) {
        // SQL command to insert a new project
        String sql = "INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fill in the values
            stmt.setString(1, project.getProjectName());
            stmt.setBigDecimal(2, project.getEstimatedHours());
            stmt.setBigDecimal(3, project.getActualHours());
            
            // Handle null difficulty
            if (project.getDifficulty() != null) {
                stmt.setInt(4, project.getDifficulty());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setString(5, project.getNotes());
            
            // Execute the insert
            stmt.executeUpdate();
            System.out.println("✓ Project added to database");
            
        } catch (SQLException e) {
            System.out.println("✗ Failed to add project: " + e.getMessage());
            throw new DbException("Could not add project", e);
        }
    }

    // Get all projects from the database
    public List<Project> getAllProjects() {
        // SQL command to get all projects
        String sql = "SELECT * FROM project ORDER BY project_name";
        List<Project> projects = new ArrayList<>();
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Read each project from the results
            while (rs.next()) {
                Project project = new Project();
                project.setProjectId(rs.getInt("project_id"));
                project.setProjectName(rs.getString("project_name"));
                project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
                project.setActualHours(rs.getBigDecimal("actual_hours"));
                project.setDifficulty(rs.getInt("difficulty"));
                project.setNotes(rs.getString("notes"));
                projects.add(project);
            }
            
            System.out.println("✓ Found " + projects.size() + " projects");
            
        } catch (SQLException e) {
            System.out.println("✗ Failed to get projects: " + e.getMessage());
            throw new DbException("Could not get projects", e);
        }
        
        return projects;
    }

    // FIXED: Get one specific project by its ID
    public Project getProject(Integer projectId) {
        // SQL command to get one project
        String sql = "SELECT * FROM project WHERE project_id = ?";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fill in the project ID
            stmt.setInt(1, projectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Found the project - create it
                    Project project = new Project();
                    project.setProjectId(rs.getInt("project_id"));
                    project.setProjectName(rs.getString("project_name"));
                    project.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
                    project.setActualHours(rs.getBigDecimal("actual_hours"));
                    project.setDifficulty(rs.getInt("difficulty"));
                    project.setNotes(rs.getString("notes"));
                    
                    // FIXED: Now fetch all related data
                    project.setSteps(getSteps(projectId));
                    project.setMaterials(getMaterialsForProject(projectId));
                    project.setCategories(getCategoriesForProject(projectId));
                    
                    System.out.println("✓ Found project: " + project.getProjectName());
                    return project;
                } else {
                    // Project not found
                    System.out.println("✗ Project with ID " + projectId + " not found");
                    return null;
                }
            }
            
        } catch (SQLException e) {
            System.out.println("✗ Failed to get project: " + e.getMessage());
            throw new DbException("Could not get project", e);
        }
    }

    // Update an existing project
    public void updateProject(Project project) {
        // SQL command to update a project
        String sql = "UPDATE project SET project_name=?, estimated_hours=?, actual_hours=?, difficulty=?, notes=? WHERE project_id=?";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fill in the new values
            stmt.setString(1, project.getProjectName());
            stmt.setBigDecimal(2, project.getEstimatedHours());
            stmt.setBigDecimal(3, project.getActualHours());
            stmt.setInt(4, project.getDifficulty());
            stmt.setString(5, project.getNotes());
            stmt.setInt(6, project.getProjectId());
            
            // Execute the update
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✓ Project updated successfully");
            } else {
                System.out.println("✗ No project was updated");
            }
            
        } catch (SQLException e) {
            System.out.println("✗ Failed to update project: " + e.getMessage());
            throw new DbException("Could not update project", e);
        }
    }

    // FIXED: Delete a project
    public void deleteProject(Integer projectId) {
        // FIXED: Check if project exists first
        String checkSql = "SELECT COUNT(*) FROM project WHERE project_id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, projectId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    throw new DbException("Project with ID " + projectId + " does not exist");
                }
            }
        } catch (SQLException e) {
            throw new DbException("Could not check project existence", e);
        }
        
        // SQL command to delete a project
        String sql = "DELETE FROM project WHERE project_id = ?";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fill in the project ID
            stmt.setInt(1, projectId);
            
            // Execute the delete
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("✓ Project deleted successfully");
            } else {
                System.out.println("✗ No project was deleted");
            }
            
        } catch (SQLException e) {
            System.out.println("✗ Failed to delete project: " + e.getMessage());
            throw new DbException("Could not delete project", e);
        }
    }

    // Add a step to a project
    public void addStep(Step step) {
        // SQL command to insert a new step
        String sql = "INSERT INTO step (project_id, step_text, step_order) VALUES (?, ?, ?)";
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fill in the values
            stmt.setInt(1, step.getProjectId());
            stmt.setString(2, step.getStepText());
            stmt.setInt(3, step.getStepOrder());
            
            // Execute the insert
            stmt.executeUpdate();
            System.out.println("✓ Step added to project");
            
        } catch (SQLException e) {
            System.out.println("✗ Failed to add step: " + e.getMessage());
            throw new DbException("Could not add step", e);
        }
    }

    // Get all steps for a specific project
    public List<Step> getSteps(Integer projectId) {
        // SQL command to get steps for a project, ordered by step number
        String sql = "SELECT * FROM step WHERE project_id = ? ORDER BY step_order";
        List<Step> steps = new ArrayList<>();
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Fill in the project ID
            stmt.setInt(1, projectId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                // Read each step from the results
                while (rs.next()) {
                    Step step = new Step();
                    step.setStepId(rs.getInt("step_id"));
                    step.setProjectId(rs.getInt("project_id"));
                    step.setStepText(rs.getString("step_text"));
                    step.setStepOrder(rs.getInt("step_order"));
                    steps.add(step);
                }
            }
            
            System.out.println("✓ Found " + steps.size() + " steps for project");
            
        } catch (SQLException e) {
            System.out.println("✗ Failed to get steps: " + e.getMessage());
            throw new DbException("Could not get steps", e);
        }
        
        return steps;
    }

    // ADDED: Get materials for a project
    private List<Material> getMaterialsForProject(Integer projectId) {
        String sql = "SELECT * FROM material WHERE project_id = ?";
        List<Material> materials = new ArrayList<>();
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Material material = new Material();
                    material.setMaterialId(rs.getInt("material_id"));
                    material.setProjectId(rs.getInt("project_id"));
                    material.setMaterialName(rs.getString("material_name"));
                    material.setNumRequired(rs.getInt("num_required"));
                    material.setCost(rs.getBigDecimal("cost"));
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            throw new DbException("Could not get materials", e);
        }
        return materials;
    }

    // ADDED: Get categories for a project
    private List<Category> getCategoriesForProject(Integer projectId) {
        String sql = "SELECT c.* FROM category c JOIN project_category pc ON c.category_id = pc.category_id WHERE pc.project_id = ?";
        List<Category> categories = new ArrayList<>();
        
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, projectId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setCategoryId(rs.getInt("category_id"));
                    category.setCategoryName(rs.getString("category_name"));
                    categories.add(category);
                }
            }
        } catch (SQLException e) {
            throw new DbException("Could not get categories", e);
        }
        return categories;
    }
}