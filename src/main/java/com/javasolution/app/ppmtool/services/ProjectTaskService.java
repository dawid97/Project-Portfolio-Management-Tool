package com.javasolution.app.ppmtool.services;

import com.javasolution.app.ppmtool.domain.Backlog;
import com.javasolution.app.ppmtool.domain.Project;
import com.javasolution.app.ppmtool.domain.ProjectTask;
import com.javasolution.app.ppmtool.exceptions.ProjectNotFoundException;
import com.javasolution.app.ppmtool.repositories.BacklogRepository;
import com.javasolution.app.ppmtool.repositories.ProjectRepository;
import com.javasolution.app.ppmtool.repositories.ProjectTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectTaskService {

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public ProjectTask addProjectTask(String projectIdentifier,ProjectTask projectTask,String username){

            //exceptions: Project not found
            //PTs to be added to a specific project, project != null, Backlog exists
            Backlog backlog = projectService.findProjectByIdentifier(projectIdentifier,username).getBacklog();

            //set the Backlog to ProjectTask
            projectTask.setBacklog(backlog);

            //we want our project sequence to be like this: IDPRO-1 IDPRO-2 ...100 101
            Integer backlogSequence = backlog.getPTSequence();

            //update the Backlog sequence
            backlogSequence++;

            backlog.setPTSequence(backlogSequence);

            //add sequence to ProjectTask
            projectTask.setProjectSequence(projectIdentifier+"-"+backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            //initial priority when priority is null
            if(projectTask.getPriority() == null || projectTask.getPriority() == 0){
                projectTask.setPriority(3);
            }

            //Initial status when status is null
            if(projectTask.getStatus()=="" || projectTask.getStatus() == null){
                projectTask.setStatus("TO_DO");
            }

            return projectTaskRepository.save(projectTask);
    }

    public Iterable<ProjectTask> findBacklogById(String id,String username) {
        projectService.findProjectByIdentifier(id,username);
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id,String pt_id,String username){

        projectService.findProjectByIdentifier(backlog_id,username);

        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if(projectTask == null){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' not found");
        }

        if(!projectTask.getProjectIdentifier().equals(backlog_id)){
            throw new ProjectNotFoundException("Project Task '"+pt_id+"' does not exist in project: "+backlog_id);
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask,String backlog_id, String pt_id,String username){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id,pt_id,username);
        projectTask = updatedTask;
        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id, String pt_id,String username){
        ProjectTask projectTask = findPTByProjectSequence(backlog_id,pt_id,username);
        projectTaskRepository.delete(projectTask);
    }
}
