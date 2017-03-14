package com.nokia.ucms.biz.service;

import com.nokia.ucms.biz.constants.EServiceDomain;
import com.nokia.ucms.biz.entity.ProjectInfo;
import com.nokia.ucms.biz.repository.DatabaseAdminRepository;
import com.nokia.ucms.biz.repository.ProjectCategoryRepository;
import com.nokia.ucms.biz.repository.ProjectColumnRepository;
import com.nokia.ucms.biz.repository.ProjectInfoRepository;
import com.nokia.ucms.common.exception.ServiceException;
import com.nokia.ucms.common.service.BaseService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.nokia.ucms.biz.constants.Constants.*;

import java.util.*;

/**
 * Created by x36zhao on 2017/3/5.
 */
@Service
public class ProjectInfoService extends BaseService
{
    private static Logger LOGGER = Logger.getLogger(ProjectInfoService.class);

    @Autowired
    private DatabaseAdminRepository databaseAdminRepository;

    @Autowired
    private ProjectInfoRepository projectInfoRepository;

    @Autowired
    private ProjectColumnRepository projectColumnRepository;

    @Autowired
    private ProjectCategoryRepository projectCategoryRepository;

    public List<ProjectInfo> getProject(String projectName) throws ServiceException
    {
        if (projectName != null && !"".equals(projectName))
        {
            ProjectInfo projectInfo = projectInfoRepository.getProjectInfoByName(projectName);
            return Arrays.asList(projectInfo);
        }
        else
        {
            return projectInfoRepository.getAllProject();
        }
    }

    public ProjectInfo getProjectById (Integer projectId) throws ServiceException
    {
        if (projectId != null && projectId > 0)
        {
            ProjectInfo projectInfo = projectInfoRepository.getProjectInfoById(projectId);
            if (projectInfo != null)
            {
                if (isValidProject(projectInfo))
                {
                    return projectInfo;
                }
                else
                {
                    throw new ServiceException("Invalid project info: " + projectInfo);
                }
            }
            else
            {
                throw new ServiceException(String.format("Project (id: '%d') does not exist", projectId));
            }
        }

        throw new ServiceException("Invalid project id: " + projectId);
    }

    public ProjectInfo getProjectByName (String projectName) throws ServiceException
    {
        if (projectName != null && !"".equals(projectName))
        {
            ProjectInfo projectInfo = projectInfoRepository.getProjectInfoByName(projectName);
            if (projectInfo != null)
            {
                return projectInfo;
            }
            else
            {
                throw new ServiceException(String.format("Project (name: '%s') does not exist", projectName));
            }
        }

        throw new ServiceException("Invalid project name: " + projectName);
    }

    public boolean updateProject (Integer projectId, ProjectInfo projectInfo) throws ServiceException
    {
        if (projectInfo != null && !"".equals(projectInfo.getName()))
        {
            ProjectInfo entity = projectInfoRepository.getProjectInfoById(projectId);
            if (entity != null)
            {
                Integer result = projectInfoRepository.updateProjectInfo(projectInfo);
                return result > 0;
            }

            throw new ServiceException(String.format("Project '%s' does not exist", projectInfo));
        }

        throw new ServiceException("Invalid project info: " + projectInfo);
    }

    @Transactional
    public Integer addProject (ProjectInfo projectInfo, Integer fromProject) throws ServiceException
    {
        // Steps
        // 1. add project info --> generate unique table name for project
        // 2. create project data table
        // 3. clone from other project if applicable
        if (projectInfo != null)
        {
            String projectName = projectInfo.getName();
            ProjectInfo project = projectInfoRepository.getProjectInfoByName(projectName);
            if (project != null)
            {
                throw new ServiceException(String.format("Project '%s' already exists", projectName));
            }

            projectInfo.setTableName(makeProjectDataTableName(projectName));
            projectInfo.setCreationTime(new Date());
            projectInfo.setLastUpdateTime(projectInfo.getCreationTime());
            Integer result = projectInfoRepository.addProjectInfo(projectInfo);
            if (result > 0)
            {
                databaseAdminRepository.createTableIfNotExist(projectInfo.getTableName());

                if (fromProject != null)
                {
                    cloneProject(fromProject, projectInfo);
                }

                return projectInfo.getId();
            }

            return null;
        }

        throw new ServiceException("Empty project cannot be created");
    }



    /**
     * Clone project from another
     *
     * In Scope:
     * 1. Project columns
     * 2. Project table fields
     * 3. Project grants
     *
     * @param fromProject
     * @param targetProject
     * @return
     */
    private boolean cloneProject(Integer fromProject, ProjectInfo targetProject)
    {
        if (fromProject != null && targetProject != null)
        {
            // clone happens only from different project
            if (!fromProject.equals(targetProject.getId()))
            {
                ProjectInfo sourceProject = projectInfoRepository.getProjectInfoById(fromProject);
                if (sourceProject != null)
                {
                    System.out.println(String.format(
                            "Clone project '%s' from '%s'", targetProject.getName(), sourceProject.getName()));

                    // TODO
                    // 1. clone columns from source project
                    // 2. clone table fields from source project
                }
                else
                {
                    throw new ServiceException(String.format("Source project (id: '%d') does not exist", fromProject));
                }
            }
            else
            {
                throw new ServiceException("Project clone is allowed only from another, rather than itself");
            }
        }

        throw new ServiceException("Failed to clone cause of missing source or target project");
    }

    private String makeProjectDataTableName(String projectName)
    {
        return String.format("%s%s", DYNAMIC_TABLE_NAME_PREFIX,
                projectName.trim().replaceAll(" ", KEYWORD_SPLITTER).toLowerCase());
    }

    private boolean isValidProject (final ProjectInfo projectInfo)
    {
        if (projectInfo != null)
        {
            return !"".equals(projectInfo.getName()) && !"".equals(projectInfo.getTableName());
        }

        return false;
    }

    protected String getServiceCategory ()
    {
        return NOT_AVAILABLE;
    }

    protected String getServiceDomain ()
    {
        return EServiceDomain.DOMAIN_PROJECT_INFO.getLabel();
    }
}
