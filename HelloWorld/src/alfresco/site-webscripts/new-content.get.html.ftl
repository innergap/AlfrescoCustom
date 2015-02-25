<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js"></script>
<div>
    <div>
         <div class="document-links document-details-panel">
            <h2 class="thin dark alfresco-twister alfresco-twister-open">File Url:</h2>
            <div class="panel-body" style="display: block;">
               <div class="link-info">
                  <input id="fileUrl" value="">
               </div>
            </div>
         </div>
    </div>
</div>
<div>
    <div>
         <div class="document-links document-details-panel">
            <h2 class="thin dark alfresco-twister alfresco-twister-open">Workflow/Sync</h2>
            <div class="panel-body" style="display: block;">
               <div class="link-info">
                  <table>
                    <tr style="border-bottom: solid 1px;">
                        <td>
                            <table>
                                <tr>
                                    <td>
                                        <div id="diff"></div>
                                    </td>
                                    <td>
                                        <div id="fileLink"></div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    <tr>
                        <td>
                             <table>
                                <tr>
                                    <td>
                                        <button id="syncButton" onclick="sync()">Sync</button>
                                    </td>
                                    <td>
                                        <div style='color: red; padding-left: 10px;' id="workflow"></div>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                  </table>
               </div>
            </div>
         </div>
    </div>
</div>
<script>
    function getUrlVars()
    {
            var vars = [], hash;
            var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
            for(var i = 0; i < hashes.length; i++)
            {
                    hash = hashes[i].split('=');
                    vars.push(hash[0]);
                    vars[hash[0]] = hash[1];
            }
            return vars;
    }

    function sync()
    {
        //call Alfresco custom webscript to get path of file and sync files in environment
        var result = confirm("This will overwrite the content in Design. Proceed?");

        if(result)
        {
            $.get("http://" + location.host + "/alfresco/service/javadir?nodeRef="+getUrlVars()["nodeRef"], function(msg){
                var jsoned = jQuery.parseJSON(msg);

                //check for outstanding workflows
                if(jsoned.count > 0)
                {
                    alert('Outstanding Workflows Exist!');
                }
                else
                {
                    //call BMWUSA API to sync
                    $.get(jsoned.apiUrl + "/api/sys/sync?fileName="+deserialize(jsoned.filePath), function(msg){

                        var result = confirm("File Synced!");
                        if(result == true || result == false)
                            location.reload();
                    });
                }
            });
        }
    }

    function deserialize(str)
    {
        str = str.replace(/_x0030_/g, "0");
        str = str.replace(/_x0031_/g, "1");
        str = str.replace(/_x0032_/g, "2");
        str = str.replace(/_x0033_/g, "3");
        str = str.replace(/_x0034_/g, "4");
        str = str.replace(/_x0035_/g, "5");
        str = str.replace(/_x0036_/g, "6");
        str = str.replace(/_x0037_/g, "7");
        str = str.replace(/_x0038_/g, "8");
        str = str.replace(/_x0039_/g, "9");
        return str;
    }

    function compare()
    {
        $.get("http://" + location.host + "/alfresco/service/javadir?nodeRef="+getUrlVars()["nodeRef"], function(msg){
            var jsoned = jQuery.parseJSON(msg);

            $("#fileUrl").val(jsoned.versionUrl);

            //call BMWUSA API to sync
            $.get(jsoned.apiUrl + "/api/sys/compare?fileName="+deserialize(jsoned.filePath), function(msg){

                var jsoned2 = msg;

                if(jsoned2.Result == "false")
                {
                    if(jsoned2.Message == "failure")
                    {
                        $('#diff').html("<span style='color: red;'>File does not exist in desgin</span>");
                    }
                    else
                    {
                        $('#diff').html("<span style='color: red;'>File differs from design</span>");
                        $('#fileLink').html("<a target='_blank' style='text-decoration: underline;' href='"+jsoned.designUrl+
                        "/alfresco/service/api/path/content/workspace/SpacesStore//Sites/bmwusa/documentLibrary/"+
                        deserialize(jsoned.filePath)+"'>File in Design</a>");

                        if(jsoned.count > 0)
                        {
                            $("#syncButton").prop('disabled', true);
                            $("#workflow").html("The workflow on this file needs completion in order to sync file with next environment.");
                        }
                    }
                }
                else
                {
                    $('#diff').html("<span style='color: green;'>File equals design</span>");
                    $('#fileLink').html("<a target='_blank' style='text-decoration: underline;' href='"+jsoned.designUrl+
                    "/alfresco/service/api/path/content/workspace/SpacesStore//Sites/bmwusa/documentLibrary/"+
                    deserialize(jsoned.filePath)+"'>File in Design</a>");
                    $("#syncButton").prop('disabled', true);
                }
            });
        });
    }

    compare();

/*
$.get("http://" + location.host + "/alfresco/service/api/version?nodeRef="+getUrlVars()["nodeRef"], function(msg){
    var url = "http://"+location.host + "/share/proxy/alfresco/api/node/content/"+msg[0].nodeRef.replace(/:\/\//g, "\/")+"?attach=false";
    $("#fileUrl").val(url);
    });*/
    
</script>