begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.morphlines.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|morphlines
operator|.
name|solr
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|UpdateResponse
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
name|CoreContainer
import|;
end_import
begin_comment
comment|/**  * An EmbeddedSolrServer that supresses close and rollback requests as  * necessary for testing  */
end_comment
begin_class
DECL|class|EmbeddedTestSolrServer
specifier|public
class|class
name|EmbeddedTestSolrServer
extends|extends
name|EmbeddedSolrServer
block|{
DECL|method|EmbeddedTestSolrServer
specifier|public
name|EmbeddedTestSolrServer
parameter_list|(
name|CoreContainer
name|coreContainer
parameter_list|,
name|String
name|coreName
parameter_list|)
block|{
name|super
argument_list|(
name|coreContainer
argument_list|,
name|coreName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
empty_stmt|;
comment|// NOP
block|}
annotation|@
name|Override
DECL|method|rollback
specifier|public
name|UpdateResponse
name|rollback
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
return|return
operator|new
name|UpdateResponse
argument_list|()
return|;
block|}
block|}
end_class
end_unit
