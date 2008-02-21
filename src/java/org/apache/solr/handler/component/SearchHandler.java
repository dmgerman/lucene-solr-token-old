begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.component
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|ParseException
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
name|common
operator|.
name|SolrException
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
name|common
operator|.
name|params
operator|.
name|CommonParams
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
name|common
operator|.
name|util
operator|.
name|RTimer
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
name|core
operator|.
name|SolrCore
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
name|handler
operator|.
name|RequestHandlerBase
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
name|request
operator|.
name|SolrQueryRequest
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
name|request
operator|.
name|SolrQueryResponse
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
name|util
operator|.
name|plugin
operator|.
name|SolrCoreAware
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
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Logger
import|;
end_import
begin_comment
comment|/**  *  * Refer SOLR-281  *  * @since solr 1.3  */
end_comment
begin_class
DECL|class|SearchHandler
specifier|public
class|class
name|SearchHandler
extends|extends
name|RequestHandlerBase
implements|implements
name|SolrCoreAware
block|{
DECL|field|RESPONSE_BUILDER_CONTEXT_KEY
specifier|static
specifier|final
name|String
name|RESPONSE_BUILDER_CONTEXT_KEY
init|=
literal|"ResponseBuilder"
decl_stmt|;
DECL|field|INIT_COMPONENTS
specifier|static
specifier|final
name|String
name|INIT_COMPONENTS
init|=
literal|"components"
decl_stmt|;
DECL|field|INIT_FIRST_COMPONENTS
specifier|static
specifier|final
name|String
name|INIT_FIRST_COMPONENTS
init|=
literal|"first-components"
decl_stmt|;
DECL|field|INIT_LAST_COMPONENTS
specifier|static
specifier|final
name|String
name|INIT_LAST_COMPONENTS
init|=
literal|"last-components"
decl_stmt|;
DECL|field|log
specifier|protected
specifier|static
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SearchHandler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|components
specifier|protected
name|List
argument_list|<
name|SearchComponent
argument_list|>
name|components
init|=
literal|null
decl_stmt|;
DECL|field|initArgs
specifier|protected
name|NamedList
name|initArgs
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|initArgs
operator|=
name|args
expr_stmt|;
block|}
DECL|method|getDefaultComponets
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultComponets
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|names
operator|.
name|add
argument_list|(
name|QueryComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|FacetComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|MoreLikeThisComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|HighlightComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
name|names
operator|.
name|add
argument_list|(
name|DebugComponent
operator|.
name|COMPONENT_NAME
argument_list|)
expr_stmt|;
return|return
name|names
return|;
block|}
comment|/**    * Initialize the components based on name    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|SolrCore
name|core
parameter_list|)
block|{
name|Object
name|declaredComponents
init|=
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_COMPONENTS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|first
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_FIRST_COMPONENTS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|last
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|initArgs
operator|.
name|get
argument_list|(
name|INIT_LAST_COMPONENTS
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|declaredComponents
operator|==
literal|null
condition|)
block|{
comment|// Use the default component list
name|list
operator|=
name|getDefaultComponets
argument_list|()
expr_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|clist
init|=
name|first
decl_stmt|;
name|clist
operator|.
name|addAll
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|list
operator|=
name|clist
expr_stmt|;
block|}
if|if
condition|(
name|last
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|last
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|list
operator|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|declaredComponents
expr_stmt|;
if|if
condition|(
name|first
operator|!=
literal|null
operator|||
name|last
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"First/Last components only valid if you do not declare 'components'"
argument_list|)
throw|;
block|}
block|}
comment|// Build the component list
name|components
operator|=
operator|new
name|ArrayList
argument_list|<
name|SearchComponent
argument_list|>
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|c
range|:
name|list
control|)
block|{
name|SearchComponent
name|comp
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|components
operator|.
name|add
argument_list|(
name|comp
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Adding  component:"
operator|+
name|comp
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getComponents
specifier|public
name|List
argument_list|<
name|SearchComponent
argument_list|>
name|getComponents
parameter_list|()
block|{
return|return
name|components
return|;
block|}
DECL|method|getResponseBuilder
specifier|public
specifier|static
name|ResponseBuilder
name|getResponseBuilder
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|)
block|{
return|return
operator|(
name|ResponseBuilder
operator|)
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|get
argument_list|(
name|RESPONSE_BUILDER_CONTEXT_KEY
argument_list|)
return|;
block|}
comment|//---------------------------------------------------------------------------------------
comment|// SolrRequestHandler
comment|//---------------------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|handleRequestBody
specifier|public
name|void
name|handleRequestBody
parameter_list|(
name|SolrQueryRequest
name|req
parameter_list|,
name|SolrQueryResponse
name|rsp
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
block|{
name|ResponseBuilder
name|builder
init|=
operator|new
name|ResponseBuilder
argument_list|()
decl_stmt|;
name|req
operator|.
name|getContext
argument_list|()
operator|.
name|put
argument_list|(
name|RESPONSE_BUILDER_CONTEXT_KEY
argument_list|,
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|components
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"SearchHandler not initialized properly.  No components registered."
argument_list|)
throw|;
block|}
comment|// The semantics of debugging vs not debugging are distinct enough
comment|// to justify two control loops
if|if
condition|(
operator|!
name|req
operator|.
name|getParams
argument_list|()
operator|.
name|getBool
argument_list|(
name|CommonParams
operator|.
name|DEBUG_QUERY
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// Prepare
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|c
operator|.
name|prepare
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
comment|// Process
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|c
operator|.
name|process
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|builder
operator|.
name|setDebug
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|RTimer
name|timer
init|=
operator|new
name|RTimer
argument_list|()
decl_stmt|;
comment|// Prepare
name|RTimer
name|subt
init|=
name|timer
operator|.
name|sub
argument_list|(
literal|"prepare"
argument_list|)
decl_stmt|;
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|builder
operator|.
name|setTimer
argument_list|(
name|subt
operator|.
name|sub
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|prepare
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getTimer
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|subt
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// Process
name|subt
operator|=
name|timer
operator|.
name|sub
argument_list|(
literal|"process"
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|builder
operator|.
name|setTimer
argument_list|(
name|subt
operator|.
name|sub
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|c
operator|.
name|process
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|builder
operator|.
name|getTimer
argument_list|()
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|subt
operator|.
name|stop
argument_list|()
expr_stmt|;
name|timer
operator|.
name|stop
argument_list|()
expr_stmt|;
comment|// add the timing info
name|builder
operator|.
name|addDebugInfo
argument_list|(
literal|"timing"
argument_list|,
name|timer
operator|.
name|asNamedList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//---------------------------------------------------------------------------------------
comment|// SolrInfoMBeans
comment|//---------------------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"Search using components: "
argument_list|)
expr_stmt|;
for|for
control|(
name|SearchComponent
name|c
range|:
name|components
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
end_unit
