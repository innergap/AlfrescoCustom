package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
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
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path;
import org.alfresco.service.cmr.repository.Path.Element;
import org.alfresco.service.cmr.search.SearchService;
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
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException; 
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

import org.springframework.extensions.surf.exception.PlatformRuntimeException;

//https://wiki.alfresco.com/wiki/Java-backed_Web_Scripts_Samples
public class GetByVersion extends DeclarativeWebScript 
{
    private ContentService contentService;

    public void setContentService(ContentService contentService)
    {
            this.contentService = contentService;
    }
    
    public void executeImpl(WebScriptRequest req, WebScriptResponse res) throws IOException {
		// stream child back
 
		output(res, new NodeRef("workspace://SpacesStore/e295fe2d-56c7-4fad-8b55-eaf8d8432d89"));

    }

    protected void output(WebScriptResponse res, NodeRef nodeRef) throws IOException {
            // stream back 
          try {
			ContentReader reader = contentService.getReader(
					nodeRef, ContentModel.PROP_CONTENT);
			reader.getContent(res.getOutputStream());
		} catch (Exception ex) {
			throw new WebScriptException("Unable to stream output");
		}
    }
    
    /*
    protected void test()
    {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("contentNode", new NodeRef("workspace://SpacesStore/e295fe2d-56c7-4fad-8b55-eaf8d8432d89"));
        try
        {
            //String nodeRefStr = req.getParameter("nodeRef");
            //String versionLabel = req.getParameter("v");
          
            VersionHistory versionHistory = versionService.getVersionHistory(new NodeRef(nodeRefStr));
  
            if(null!= versionHistory){

                Iterator<Version> versionIterator = versionHistory.getAllVersions().iterator();

                Version version;

                while(versionIterator.hasNext())
                {
                    version = versionIterator.next();
                    if(version.getVersionLabel().equals(versionLabel)){
                        //model.put("contentNode", repository.findNodeRef(version.getVersionedNodeRef().toString()));
                        
                    }
                }
            }
        }
        catch(Exception ex){
            model.put("error", ex.toString());
            model.put("errorMessage", ex.getMessage());
            model.put("errorStackTrace", ex.getStackTrace());
        }
      
    }*/
    
}