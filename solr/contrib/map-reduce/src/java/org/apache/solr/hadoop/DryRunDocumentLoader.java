begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.hadoop
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|hadoop
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SolrPingResponse
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
name|common
operator|.
name|SolrInputDocument
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
name|morphlines
operator|.
name|solr
operator|.
name|DocumentLoader
import|;
end_import
begin_comment
comment|/**  * Prints documents to stdout instead of loading them into Solr for quicker turnaround during early  * trial& debug sessions.  */
end_comment
begin_class
DECL|class|DryRunDocumentLoader
specifier|final
class|class
name|DryRunDocumentLoader
implements|implements
name|DocumentLoader
block|{
annotation|@
name|Override
DECL|method|beginTransaction
specifier|public
name|void
name|beginTransaction
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|load
specifier|public
name|void
name|load
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"dryrun: "
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|commitTransaction
specifier|public
name|void
name|commitTransaction
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|rollbackTransaction
specifier|public
name|UpdateResponse
name|rollbackTransaction
parameter_list|()
block|{
return|return
operator|new
name|UpdateResponse
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|ping
specifier|public
name|SolrPingResponse
name|ping
parameter_list|()
block|{
return|return
operator|new
name|SolrPingResponse
argument_list|()
return|;
block|}
block|}
end_class
end_unit
