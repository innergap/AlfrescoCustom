package org.example;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.spi.ServiceRegistry;
import org.alfresco.model.ContentModel;

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.workflow.WorkflowModel;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.Path.Element;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.cmr.workflow.WorkflowDefinition;
import org.alfresco.service.cmr.workflow.WorkflowInstance;
import org.alfresco.service.cmr.workflow.WorkflowPath;
import org.alfresco.service.cmr.workflow.WorkflowService;
import org.alfresco.service.cmr.workflow.WorkflowTask;
import org.alfresco.service.namespace.DynamicNamespacePrefixResolver;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

public class JavaDir extends DeclarativeWebScript
{
    private Repository repository;
    private NodeService nodeservice;
    private WorkflowService workflowService;
    private VersionService versionService;
    private ApplicationContext CONTEXT;

    public void setRepository(Repository repository)
    {
            this.repository = repository;
    }

    public void setNodeService(NodeService nodeservice)
    {
            this.nodeservice = nodeservice;
    }

    public void setWorkflowService(WorkflowService workflowService)
    {
            this.workflowService = workflowService;
    }
    
    public void setVersionService(VersionService versionService)
    {
            this.versionService = versionService;
    }

    //Get version of latest file for nodeRef
    //directions: https://forums.alfresco.com/forum/developer-discussions/other-apis/correct-way-get-version-document-02212011-0746
    public String getVersion(String nodeRef)
    {              
        VersionHistory versionHistory = versionService.getVersionHistory(new NodeRef(nodeRef));

        String versionUrl = "";
        
        if(null!= versionHistory){

            Iterator<Version> versionIterator = versionHistory.getAllVersions().iterator();

            Version version;

            while(versionIterator.hasNext())
            {
                version = versionIterator.next();
                versionUrl = "?nodeRef="+nodeRef+"&v="+version.getVersionLabel();
                version.getVersionedNodeRef();
            }
        }
        
        
        return versionUrl;
    }
    
    protected Map<String, Object> executeImpl(WebScriptRequest req,
                    Status status, Cache cache)
    {
        //Path folderPath = nodeService.getPath("FOLDER_NODE_REF");
        String nodeRefStr = req.getParameter("nodeRef");
        NodeRef nodeRef = new NodeRef(nodeRefStr);
        Path thePath = nodeservice.getPath(nodeRef);
        

        //this.CONTEXT = new ClassPathXmlApplicationContext("web-client-application-context.xml");
        //this.nodeservice = (NodeService)this.CONTEXT.getBean("nodeService");
       String path = "";
       CharSequence a = "{http://www.alfresco.org/model/content/1.0}";
       CharSequence b = "{http://www.alfresco.org/model/site/1.0}";

        for(int i=5; i < thePath.size(); i++){

            if( i != 5 )
                path += "/";
            path += thePath.get(i).toString().replace(a, "").replace(b, "");
        }

        // construct model for response template to render
        Map<String, Object> model = new HashMap<String, Object>();

        try
        {
            String apiUrl = "";
            apiUrl = System.getenv("BMW_API_URL");
            String designUrl = "";
            designUrl = System.getenv("ALFRESCO_LEVEL_UP_URL");

            List<WorkflowInstance> workflowInstances = new ArrayList<WorkflowInstance>();
            workflowInstances = workflowService.getWorkflowsForContent(nodeRef, true);
            
            /*
            List<WorkflowTask> tasks = new ArrayList<WorkflowTask>();
            WorkflowInstance workflow = null;
            List<WorkflowPath> paths = null;
            Map<QName, Serializable> props = new HashMap<QName, Serializable>();
            TypeDefinition typeDefinition = null;
            Map<QName, PropertyDefinition> propertyDefs = null;
            PropertyDefinition priorDef = null;
            
            for(WorkflowInstance obj : workflowInstances){

                paths  = workflowService.getWorkflowPaths(obj.id);
                for(WorkflowPath pathOne : paths)
                    for(WorkflowTask task : workflowService.getTasksForWorkflowPath(pathOne.getId()))
                    {
                        tasks.add(task);
                        props = task.getProperties();
                        typeDefinition = task.getDefinition().getMetadata();
                        propertyDefs = typeDefinition.getProperties();        
                        priorDef =  propertyDefs.get(WorkflowModel.PROP_PRIORITY);

                    }
            }
            */
            
            String versionUrl = getVersion(nodeRefStr);
            
            model.put("versionUrl", versionUrl);
            model.put("filePath", URLEncoder.encode(path));
            model.put("apiUrl", apiUrl);
            model.put("activeWFCount", workflowInstances.size());
            model.put("nextAlfUrl", designUrl);
           
            /*
            model.put("workflows", workflowInstances);
            model.put("tasks", tasks);
            model.put("completed", completedWorkflowInstances);
            model.put("allWorkflows", workflows);
            model.put("props", props);
            model.put("typeDefinition", typeDefinition);
            model.put("propertyDefs", propertyDefs);
            model.put("priorDef", priorDef);
            
            model.put("paths", paths);
            */
        }
        catch(Exception ex){
            model.put("error", ex.toString());
            model.put("errorMessage", ex.getMessage());
            model.put("errorStackTrace", ex.getStackTrace());
        }

        return model;
    }
}