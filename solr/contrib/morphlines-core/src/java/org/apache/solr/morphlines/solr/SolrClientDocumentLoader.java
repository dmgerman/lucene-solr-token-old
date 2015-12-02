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
name|SolrClient
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
name|impl
operator|.
name|CloudSolrClient
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
name|impl
operator|.
name|ConcurrentUpdateSolrClient
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
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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
begin_comment
comment|/**  * A vehicle to load a list of Solr documents into a local or remote {@link org.apache.solr.client.solrj.SolrClient}.  */
end_comment
begin_class
DECL|class|SolrClientDocumentLoader
specifier|public
class|class
name|SolrClientDocumentLoader
implements|implements
name|DocumentLoader
block|{
DECL|field|client
specifier|private
specifier|final
name|SolrClient
name|client
decl_stmt|;
comment|// proxy to local or remote solr server
DECL|field|numLoadedDocs
specifier|private
name|long
name|numLoadedDocs
init|=
literal|0
decl_stmt|;
comment|// number of documents loaded in the current transaction
DECL|field|batchSize
specifier|private
specifier|final
name|int
name|batchSize
decl_stmt|;
DECL|field|batch
specifier|private
specifier|final
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|batch
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
DECL|field|LOGGER
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MethodHandles
operator|.
name|lookup
argument_list|()
operator|.
name|lookupClass
argument_list|()
argument_list|)
decl_stmt|;
DECL|method|SolrClientDocumentLoader
specifier|public
name|SolrClientDocumentLoader
parameter_list|(
name|SolrClient
name|client
parameter_list|,
name|int
name|batchSize
parameter_list|)
block|{
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"solr server must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|client
operator|=
name|client
expr_stmt|;
if|if
condition|(
name|batchSize
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"batchSize must be a positive number: "
operator|+
name|batchSize
argument_list|)
throw|;
block|}
name|this
operator|.
name|batchSize
operator|=
name|batchSize
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|beginTransaction
specifier|public
name|void
name|beginTransaction
parameter_list|()
block|{
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"beginTransaction"
argument_list|)
expr_stmt|;
name|batch
operator|.
name|clear
argument_list|()
expr_stmt|;
name|numLoadedDocs
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|client
operator|instanceof
name|SafeConcurrentUpdateSolrClient
condition|)
block|{
operator|(
operator|(
name|SafeConcurrentUpdateSolrClient
operator|)
name|client
operator|)
operator|.
name|clearException
argument_list|()
expr_stmt|;
block|}
block|}
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
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"load doc: {}"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|batch
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>=
name|batchSize
condition|)
block|{
name|loadBatch
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|commitTransaction
specifier|public
name|void
name|commitTransaction
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"commitTransaction"
argument_list|)
expr_stmt|;
if|if
condition|(
name|batch
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|loadBatch
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|numLoadedDocs
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|client
operator|instanceof
name|ConcurrentUpdateSolrClient
condition|)
block|{
operator|(
operator|(
name|ConcurrentUpdateSolrClient
operator|)
name|client
operator|)
operator|.
name|blockUntilFinished
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|loadBatch
specifier|private
name|void
name|loadBatch
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|numLoadedDocs
operator|+=
name|batch
operator|.
name|size
argument_list|()
expr_stmt|;
try|try
block|{
name|UpdateResponse
name|rsp
init|=
name|client
operator|.
name|add
argument_list|(
name|batch
argument_list|)
decl_stmt|;
block|}
finally|finally
block|{
name|batch
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|rollbackTransaction
specifier|public
name|UpdateResponse
name|rollbackTransaction
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"rollback"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|client
operator|instanceof
name|CloudSolrClient
operator|)
condition|)
block|{
return|return
name|client
operator|.
name|rollback
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|UpdateResponse
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|shutdown
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ping
specifier|public
name|SolrPingResponse
name|ping
parameter_list|()
throws|throws
name|SolrServerException
throws|,
name|IOException
block|{
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"ping"
argument_list|)
expr_stmt|;
return|return
name|client
operator|.
name|ping
argument_list|()
return|;
block|}
DECL|method|getSolrClient
specifier|public
name|SolrClient
name|getSolrClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
block|}
end_class
end_unit
