package com.javasolution.app.ppmtool.services;

import com.javasolution.app.ppmtool.domain.Backlog;
import com.javasolution.app.ppmtool.domain.Project;
import com.javasolution.app.ppmtool.domain.User;
import com.javasolution.app.ppmtool.exceptions.ProjectIdException;
import com.javasolution.app.ppmtool.exceptions.ProjectNotFoundException;
import com.javasolution.app.ppmtool.repositories.BacklogRepository;
import com.javasolution.app.ppmtool.repositories.ProjectRepository;
import com.javasolution.app.ppmtool.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private UserRepository userRepository;

    public Project saveOrUpdateProject(Project project,String username){

        if(project.getId() !=null){
            Project exsistingProject = projectRepository.findByProjectIdentifier(project.getProjectIdentifier());

            if(exsistingProject !=null && (!exsistingProject.getProjectLeader().equals(username)) ){
                throw new ProjectNotFoundException("Project not found in your account");
            }else if(exsistingProject==null){
                throw new ProjectNotFoundException("Project with ID: '"+project.getProjectIdentifier()+"' does not exist");
            }
        }

        try{
            User user = userRepository.findByUsername(username);
            project.setUser(user);
            project.setProjectLeader(user.getUsername());
            project.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());

            if(project.getId()==null){
                Backlog backlog = new Backlog();
                project.setBacklog(backlog);
                backlog.setProject(project);
                backlog.setProjectIdentifier(project.getProjectIdentifier().toUpperCase());
            }

            if(project.getId()!=null){
                project.setBacklog(backlogRepository.findByProjectIdentifier(project.getProjectIdentifier().toUpperCase()));
            }

            return projectRepository.save(project);
        }catch (Exception ex){
            throw new ProjectIdException("Project ID '"+project.getProjectIdentifier().toUpperCase()+"' already exists");
        }
    }

    public Project findProjectByIdentifier(String projectId,String username){

        Project project = projectRepository.findByProjectIdentifier(projectId.toUpperCase());

        if(project == null){
            throw new ProjectIdException("Project ID '"+projectId+"' does not exist");
        }

        if(!project.getProjectLeader().equals(username)){
            throw new ProjectNotFoundException("Project not found in your account");
        }

        return project;
    }

    public Iterable<Project> findAllProjects(String username){
        return projectRepository.findAllByProjectLeader(username);
    }

    public void deleteProjectByIdentifier(String projectId,String username){

        projectRepository.delete(findProjectByIdentifier(projectId,username));
    }
}
