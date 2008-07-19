begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.client.solrj.request
package|package
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
name|request
package|;
end_package
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
name|Collection
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
name|SolrServer
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
name|SolrServerException
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
name|SolrRequest
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
name|LukeResponse
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
name|params
operator|.
name|ModifiableSolrParams
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
name|SolrParams
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
name|ContentStream
import|;
end_import
begin_comment
comment|/**  *   * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|LukeRequest
specifier|public
class|class
name|LukeRequest
extends|extends
name|SolrRequest
block|{
DECL|field|fields
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|fields
decl_stmt|;
DECL|field|numTerms
specifier|private
name|int
name|numTerms
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|showSchema
specifier|private
name|boolean
name|showSchema
init|=
literal|false
decl_stmt|;
DECL|method|LukeRequest
specifier|public
name|LukeRequest
parameter_list|()
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
literal|"/admin/luke"
argument_list|)
expr_stmt|;
block|}
DECL|method|LukeRequest
specifier|public
name|LukeRequest
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|METHOD
operator|.
name|GET
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------------------
comment|//---------------------------------------------------------------------------------
DECL|method|addField
specifier|public
name|void
name|addField
parameter_list|(
name|String
name|f
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|fields
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
DECL|method|setFields
specifier|public
name|void
name|setFields
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|f
parameter_list|)
block|{
name|fields
operator|=
name|f
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------------------
comment|//---------------------------------------------------------------------------------
DECL|method|isShowSchema
specifier|public
name|boolean
name|isShowSchema
parameter_list|()
block|{
return|return
name|showSchema
return|;
block|}
DECL|method|setShowSchema
specifier|public
name|void
name|setShowSchema
parameter_list|(
name|boolean
name|showSchema
parameter_list|)
block|{
name|this
operator|.
name|showSchema
operator|=
name|showSchema
expr_stmt|;
block|}
DECL|method|getNumTerms
specifier|public
name|int
name|getNumTerms
parameter_list|()
block|{
return|return
name|numTerms
return|;
block|}
comment|/**    * the number of terms to return for a given field.  If the number is 0, it will not traverse the terms.      */
DECL|method|setNumTerms
specifier|public
name|void
name|setNumTerms
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|this
operator|.
name|numTerms
operator|=
name|count
expr_stmt|;
block|}
comment|//---------------------------------------------------------------------------------
comment|//---------------------------------------------------------------------------------
annotation|@
name|Override
DECL|method|getContentStreams
specifier|public
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|getContentStreams
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getParams
specifier|public
name|SolrParams
name|getParams
parameter_list|()
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|fields
operator|!=
literal|null
operator|&&
name|fields
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|FL
argument_list|,
name|fields
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|fields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|numTerms
operator|>=
literal|0
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"numTerms"
argument_list|,
name|numTerms
operator|+
literal|""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|showSchema
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"show"
argument_list|,
literal|"schema"
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
annotation|@
name|Override
DECL|method|process
specifier|public
name|LukeResponse
name|process
parameter_list|(
name|SolrServer
name|server
parameter_list|)
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|long
name|startTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|LukeResponse
name|res
init|=
operator|new
name|LukeResponse
argument_list|(
name|server
operator|.
name|request
argument_list|(
name|this
argument_list|)
argument_list|)
decl_stmt|;
name|res
operator|.
name|setElapsedTime
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
return|return
name|res
return|;
block|}
block|}
end_class
end_unit
