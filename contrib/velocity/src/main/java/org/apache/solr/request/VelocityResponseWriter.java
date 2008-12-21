begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.request
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|request
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|QueryResponse
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SolrResponseBase
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|embedded
operator|.
name|EmbeddedSolrServer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|Template
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|VelocityContext
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|ComparisonDateTool
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|DateTool
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|EscapeTool
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|MathTool
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|NumberTool
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|tools
operator|.
name|generic
operator|.
name|SortTool
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|velocity
operator|.
name|app
operator|.
name|VelocityEngine
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_class
DECL|class|VelocityResponseWriter
specifier|public
class|class
name|VelocityResponseWriter
implements|implements
name|QueryResponseWriter
block|{
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Writer
name|writer
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
name|VelocityEngine
name|engine
init|=
name|getEngine
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// TODO: have HTTP headers available for configuring engine
comment|// TODO: Add layout capability, render to string buffer, then render layout
name|Template
name|template
init|=
name|getTemplate
argument_list|(
name|engine
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|VelocityContext
name|context
init|=
operator|new
name|VelocityContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"request"
argument_list|,
name|request
argument_list|)
expr_stmt|;
comment|// Turn the SolrQueryResponse into a SolrResponse.
comment|// QueryResponse has lots of conveniences suitable for a view
comment|// Problem is, which SolrResponse class to use?
comment|// One patch to SOLR-620 solved this by passing in a class name as
comment|// as a parameter and using reflection and Solr's class loader to
comment|// create a new instance.  But for now the implementation simply
comment|// uses QueryResponse, and if it chokes in a known way, fall back
comment|// to bare bones SolrResponseBase.
comment|// TODO: Can this writer know what the handler class is?  With echoHandler=true it can get its string name at least
name|SolrResponse
name|rsp
init|=
operator|new
name|QueryResponse
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|Object
argument_list|>
name|parsedResponse
init|=
operator|new
name|EmbeddedSolrServer
argument_list|(
name|request
operator|.
name|getCore
argument_list|()
argument_list|)
operator|.
name|getParsedResponse
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
try|try
block|{
name|rsp
operator|.
name|setResponse
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
comment|// page only injected if QueryResponse works
name|context
operator|.
name|put
argument_list|(
literal|"page"
argument_list|,
operator|new
name|PageTool
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
argument_list|)
expr_stmt|;
comment|// page tool only makes sense for a SearchHandler request... *sigh*
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
comment|// known edge case where QueryResponse's extraction assumes "response" is a SolrDocumentList
comment|// (AnalysisRequestHandler emits a "response")
name|rsp
operator|=
operator|new
name|SolrResponseBase
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|setResponse
argument_list|(
name|parsedResponse
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Velocity context tools - TODO: make these pluggable
name|context
operator|.
name|put
argument_list|(
literal|"esc"
argument_list|,
operator|new
name|EscapeTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"sort"
argument_list|,
operator|new
name|SortTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"number"
argument_list|,
operator|new
name|NumberTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"date"
argument_list|,
operator|new
name|ComparisonDateTool
argument_list|()
argument_list|)
expr_stmt|;
name|context
operator|.
name|put
argument_list|(
literal|"math"
argument_list|,
operator|new
name|MathTool
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|layout_template
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.layout"
argument_list|)
decl_stmt|;
name|String
name|json_wrapper
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.json"
argument_list|)
decl_stmt|;
name|boolean
name|wrap_response
init|=
operator|(
name|layout_template
operator|!=
literal|null
operator|)
operator|||
operator|(
name|json_wrapper
operator|!=
literal|null
operator|)
decl_stmt|;
comment|// create output, optionally wrap it into a json object
if|if
condition|(
name|wrap_response
condition|)
block|{
name|StringWriter
name|stringWriter
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|template
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|stringWriter
argument_list|)
expr_stmt|;
if|if
condition|(
name|layout_template
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|put
argument_list|(
literal|"content"
argument_list|,
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stringWriter
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
try|try
block|{
name|engine
operator|.
name|getTemplate
argument_list|(
name|layout_template
operator|+
literal|".vm"
argument_list|)
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|stringWriter
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|json_wrapper
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|write
argument_list|(
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.json"
argument_list|)
operator|+
literal|"("
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|getJSONWrap
argument_list|(
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|write
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// using a layout, but not JSON wrapping
name|writer
operator|.
name|write
argument_list|(
name|stringWriter
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|template
operator|.
name|merge
argument_list|(
name|context
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getEngine
specifier|private
name|VelocityEngine
name|getEngine
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|)
block|{
name|VelocityEngine
name|engine
init|=
operator|new
name|VelocityEngine
argument_list|()
decl_stmt|;
name|String
name|template_root
init|=
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.base_dir"
argument_list|)
decl_stmt|;
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
operator|.
name|getConfigDir
argument_list|()
argument_list|,
literal|"velocity"
argument_list|)
decl_stmt|;
if|if
condition|(
name|template_root
operator|!=
literal|null
condition|)
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|template_root
argument_list|)
expr_stmt|;
block|}
name|engine
operator|.
name|setProperty
argument_list|(
name|VelocityEngine
operator|.
name|FILE_RESOURCE_LOADER_PATH
argument_list|,
name|baseDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
literal|"params.resource.loader.instance"
argument_list|,
operator|new
name|SolrParamResourceLoader
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
literal|"solr.resource.loader.instance"
argument_list|,
operator|new
name|SolrVelocityResourceLoader
argument_list|(
name|request
operator|.
name|getCore
argument_list|()
operator|.
name|getSolrConfig
argument_list|()
operator|.
name|getResourceLoader
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|engine
operator|.
name|setProperty
argument_list|(
name|VelocityEngine
operator|.
name|RESOURCE_LOADER
argument_list|,
literal|"params,file,solr"
argument_list|)
expr_stmt|;
return|return
name|engine
return|;
block|}
DECL|method|getTemplate
specifier|private
name|Template
name|getTemplate
parameter_list|(
name|VelocityEngine
name|engine
parameter_list|,
name|SolrQueryRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|Template
name|template
decl_stmt|;
try|try
block|{
name|template
operator|=
name|engine
operator|.
name|getTemplate
argument_list|(
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.template"
argument_list|,
literal|"browse"
argument_list|)
operator|+
literal|".vm"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|template
return|;
block|}
DECL|method|getContentType
specifier|public
name|String
name|getContentType
parameter_list|(
name|SolrQueryRequest
name|request
parameter_list|,
name|SolrQueryResponse
name|response
parameter_list|)
block|{
return|return
name|request
operator|.
name|getParams
argument_list|()
operator|.
name|get
argument_list|(
literal|"v.contentType"
argument_list|,
literal|"text/html"
argument_list|)
return|;
block|}
DECL|method|getJSONWrap
specifier|private
name|String
name|getJSONWrap
parameter_list|(
name|String
name|xmlResult
parameter_list|)
block|{
comment|// TODO: maybe noggit or Solr's JSON utilities can make this cleaner?
comment|// escape the double quotes and backslashes
name|String
name|replace1
init|=
name|xmlResult
operator|.
name|replaceAll
argument_list|(
literal|"\\\\"
argument_list|,
literal|"\\\\\\\\"
argument_list|)
decl_stmt|;
name|replace1
operator|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|"\\\\n"
argument_list|)
expr_stmt|;
name|replace1
operator|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\\r"
argument_list|,
literal|"\\\\r"
argument_list|)
expr_stmt|;
name|String
name|replaced
init|=
name|replace1
operator|.
name|replaceAll
argument_list|(
literal|"\""
argument_list|,
literal|"\\\\\""
argument_list|)
decl_stmt|;
comment|// wrap it in a JSON object
return|return
literal|"{\"result\":\""
operator|+
name|replaced
operator|+
literal|"\"}"
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{        }
block|}
end_class
end_unit
