begin_unit
begin_package
DECL|package|org.apache.solr.update
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|update
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Map
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CompletionService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorCompletionService
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|SynchronousQueue
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadPoolExecutor
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|DefaultHttpClient
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|conn
operator|.
name|tsccm
operator|.
name|ThreadSafeClientConnManager
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
name|HttpSolrServer
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
name|request
operator|.
name|AbstractUpdateRequest
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
name|request
operator|.
name|UpdateRequestExt
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
name|cloud
operator|.
name|ZkCoreNodeProps
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
name|util
operator|.
name|DefaultSolrThreadFactory
import|;
end_import
begin_class
DECL|class|SolrCmdDistributor
specifier|public
class|class
name|SolrCmdDistributor
block|{
comment|// TODO: shut this thing down
comment|// TODO: this cannot be per instance...
DECL|field|commExecutor
specifier|static
name|ThreadPoolExecutor
name|commExecutor
init|=
operator|new
name|ThreadPoolExecutor
argument_list|(
literal|0
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|5
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
operator|new
name|SynchronousQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
argument_list|,
operator|new
name|DefaultSolrThreadFactory
argument_list|(
literal|"cmdDistribExecutor"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|client
specifier|static
name|HttpClient
name|client
decl_stmt|;
static|static
block|{
name|ThreadSafeClientConnManager
name|mgr
init|=
operator|new
name|ThreadSafeClientConnManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|setDefaultMaxPerRoute
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|setMaxTotal
argument_list|(
literal|200
argument_list|)
expr_stmt|;
name|client
operator|=
operator|new
name|DefaultHttpClient
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
block|}
DECL|field|completionService
name|CompletionService
argument_list|<
name|Request
argument_list|>
name|completionService
decl_stmt|;
DECL|field|pending
name|Set
argument_list|<
name|Future
argument_list|<
name|Request
argument_list|>
argument_list|>
name|pending
decl_stmt|;
DECL|field|maxBufferedAddsPerServer
name|int
name|maxBufferedAddsPerServer
init|=
literal|10
decl_stmt|;
DECL|field|maxBufferedDeletesPerServer
name|int
name|maxBufferedDeletesPerServer
init|=
literal|10
decl_stmt|;
DECL|field|response
specifier|private
name|Response
name|response
init|=
operator|new
name|Response
argument_list|()
decl_stmt|;
DECL|field|adds
specifier|private
specifier|final
name|Map
argument_list|<
name|Node
argument_list|,
name|List
argument_list|<
name|AddRequest
argument_list|>
argument_list|>
name|adds
init|=
operator|new
name|HashMap
argument_list|<
name|Node
argument_list|,
name|List
argument_list|<
name|AddRequest
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|deletes
specifier|private
specifier|final
name|Map
argument_list|<
name|Node
argument_list|,
name|List
argument_list|<
name|DeleteRequest
argument_list|>
argument_list|>
name|deletes
init|=
operator|new
name|HashMap
argument_list|<
name|Node
argument_list|,
name|List
argument_list|<
name|DeleteRequest
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|class|AddRequest
class|class
name|AddRequest
block|{
DECL|field|cmd
name|AddUpdateCommand
name|cmd
decl_stmt|;
DECL|field|params
name|ModifiableSolrParams
name|params
decl_stmt|;
block|}
DECL|class|DeleteRequest
class|class
name|DeleteRequest
block|{
DECL|field|cmd
name|DeleteUpdateCommand
name|cmd
decl_stmt|;
DECL|field|params
name|ModifiableSolrParams
name|params
decl_stmt|;
block|}
DECL|method|SolrCmdDistributor
specifier|public
name|SolrCmdDistributor
parameter_list|()
block|{       }
DECL|method|finish
specifier|public
name|void
name|finish
parameter_list|()
block|{
comment|// piggyback on any outstanding adds or deletes if possible.
name|flushAdds
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|flushDeletes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|checkResponses
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|distribDelete
specifier|public
name|void
name|distribDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|urls
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|checkResponses
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|isDeleteById
argument_list|()
condition|)
block|{
name|doDelete
argument_list|(
name|cmd
argument_list|,
name|urls
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doDelete
argument_list|(
name|cmd
argument_list|,
name|urls
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|distribAdd
specifier|public
name|void
name|distribAdd
parameter_list|(
name|AddUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|checkResponses
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|// make sure any pending deletes are flushed
name|flushDeletes
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// TODO: this is brittle
comment|// need to make a clone since these commands may be reused
name|AddUpdateCommand
name|clone
init|=
operator|new
name|AddUpdateCommand
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|clone
operator|.
name|solrDoc
operator|=
name|cmd
operator|.
name|solrDoc
expr_stmt|;
name|clone
operator|.
name|commitWithin
operator|=
name|cmd
operator|.
name|commitWithin
expr_stmt|;
name|clone
operator|.
name|overwrite
operator|=
name|cmd
operator|.
name|overwrite
expr_stmt|;
name|clone
operator|.
name|setVersion
argument_list|(
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
name|AddRequest
name|addRequest
init|=
operator|new
name|AddRequest
argument_list|()
decl_stmt|;
name|addRequest
operator|.
name|cmd
operator|=
name|clone
expr_stmt|;
name|addRequest
operator|.
name|params
operator|=
name|params
expr_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|List
argument_list|<
name|AddRequest
argument_list|>
name|alist
init|=
name|adds
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|alist
operator|==
literal|null
condition|)
block|{
name|alist
operator|=
operator|new
name|ArrayList
argument_list|<
name|AddRequest
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|adds
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|alist
argument_list|)
expr_stmt|;
block|}
name|alist
operator|.
name|add
argument_list|(
name|addRequest
argument_list|)
expr_stmt|;
block|}
name|flushAdds
argument_list|(
name|maxBufferedAddsPerServer
argument_list|)
expr_stmt|;
block|}
DECL|method|distribCommit
specifier|public
name|void
name|distribCommit
parameter_list|(
name|CommitUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Wait for all outstanding responses to make sure that a commit
comment|// can't sneak in ahead of adds or deletes we already sent.
comment|// We could do this on a per-server basis, but it's more complex
comment|// and this solution will lead to commits happening closer together.
name|checkResponses
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// currently, we dont try to piggy back on outstanding adds or deletes
name|UpdateRequestExt
name|ureq
init|=
operator|new
name|UpdateRequestExt
argument_list|()
decl_stmt|;
name|ureq
operator|.
name|setParams
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|addCommit
argument_list|(
name|ureq
argument_list|,
name|cmd
argument_list|)
expr_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|submit
argument_list|(
name|ureq
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
comment|// if the command wanted to block until everything was committed,
comment|// then do that here.
if|if
condition|(
name|cmd
operator|.
name|waitSearcher
condition|)
block|{
name|checkResponses
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doDelete
specifier|private
name|void
name|doDelete
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|,
name|ModifiableSolrParams
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|flushAdds
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|DeleteUpdateCommand
name|clonedCmd
init|=
name|clone
argument_list|(
name|cmd
argument_list|)
decl_stmt|;
name|DeleteRequest
name|deleteRequest
init|=
operator|new
name|DeleteRequest
argument_list|()
decl_stmt|;
name|deleteRequest
operator|.
name|cmd
operator|=
name|clonedCmd
expr_stmt|;
name|deleteRequest
operator|.
name|params
operator|=
name|params
expr_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|List
argument_list|<
name|DeleteRequest
argument_list|>
name|dlist
init|=
name|deletes
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|dlist
operator|==
literal|null
condition|)
block|{
name|dlist
operator|=
operator|new
name|ArrayList
argument_list|<
name|DeleteRequest
argument_list|>
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|deletes
operator|.
name|put
argument_list|(
name|node
argument_list|,
name|dlist
argument_list|)
expr_stmt|;
block|}
name|dlist
operator|.
name|add
argument_list|(
name|deleteRequest
argument_list|)
expr_stmt|;
block|}
name|flushDeletes
argument_list|(
name|maxBufferedDeletesPerServer
argument_list|)
expr_stmt|;
block|}
DECL|method|addCommit
name|void
name|addCommit
parameter_list|(
name|UpdateRequestExt
name|ureq
parameter_list|,
name|CommitUpdateCommand
name|cmd
parameter_list|)
block|{
if|if
condition|(
name|cmd
operator|==
literal|null
condition|)
return|return;
name|ureq
operator|.
name|setAction
argument_list|(
name|cmd
operator|.
name|optimize
condition|?
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|OPTIMIZE
else|:
name|AbstractUpdateRequest
operator|.
name|ACTION
operator|.
name|COMMIT
argument_list|,
literal|false
argument_list|,
name|cmd
operator|.
name|waitSearcher
argument_list|)
expr_stmt|;
block|}
DECL|method|flushAdds
name|boolean
name|flushAdds
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
comment|// check for pending deletes
name|Set
argument_list|<
name|Node
argument_list|>
name|removeNodes
init|=
operator|new
name|HashSet
argument_list|<
name|Node
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
name|adds
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|List
argument_list|<
name|AddRequest
argument_list|>
name|alist
init|=
name|adds
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|alist
operator|==
literal|null
operator|||
name|alist
operator|.
name|size
argument_list|()
operator|<
name|limit
condition|)
return|return
literal|false
return|;
name|UpdateRequestExt
name|ureq
init|=
operator|new
name|UpdateRequestExt
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|combinedParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|AddRequest
name|aReq
range|:
name|alist
control|)
block|{
name|AddUpdateCommand
name|cmd
init|=
name|aReq
operator|.
name|cmd
decl_stmt|;
name|combinedParams
operator|.
name|add
argument_list|(
name|aReq
operator|.
name|params
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|cmd
operator|.
name|solrDoc
argument_list|,
name|cmd
operator|.
name|commitWithin
argument_list|,
name|cmd
operator|.
name|overwrite
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ureq
operator|.
name|getParams
argument_list|()
operator|==
literal|null
condition|)
name|ureq
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|getParams
argument_list|()
operator|.
name|add
argument_list|(
name|combinedParams
argument_list|)
expr_stmt|;
name|removeNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|ureq
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Node
name|node
range|:
name|removeNodes
control|)
block|{
name|adds
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|flushDeletes
name|boolean
name|flushDeletes
parameter_list|(
name|int
name|limit
parameter_list|)
block|{
comment|// check for pending deletes
name|Set
argument_list|<
name|Node
argument_list|>
name|removeNodes
init|=
operator|new
name|HashSet
argument_list|<
name|Node
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|Node
argument_list|>
name|nodes
init|=
name|deletes
operator|.
name|keySet
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|List
argument_list|<
name|DeleteRequest
argument_list|>
name|dlist
init|=
name|deletes
operator|.
name|get
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|dlist
operator|==
literal|null
operator|||
name|dlist
operator|.
name|size
argument_list|()
operator|<
name|limit
condition|)
return|return
literal|false
return|;
name|UpdateRequestExt
name|ureq
init|=
operator|new
name|UpdateRequestExt
argument_list|()
decl_stmt|;
name|ModifiableSolrParams
name|combinedParams
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
for|for
control|(
name|DeleteRequest
name|dReq
range|:
name|dlist
control|)
block|{
name|DeleteUpdateCommand
name|cmd
init|=
name|dReq
operator|.
name|cmd
decl_stmt|;
name|combinedParams
operator|.
name|add
argument_list|(
name|dReq
operator|.
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmd
operator|.
name|isDeleteById
argument_list|()
condition|)
block|{
name|ureq
operator|.
name|deleteById
argument_list|(
name|cmd
operator|.
name|getId
argument_list|()
argument_list|,
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ureq
operator|.
name|deleteByQuery
argument_list|(
name|cmd
operator|.
name|query
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ureq
operator|.
name|getParams
argument_list|()
operator|==
literal|null
condition|)
name|ureq
operator|.
name|setParams
argument_list|(
operator|new
name|ModifiableSolrParams
argument_list|()
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|getParams
argument_list|()
operator|.
name|add
argument_list|(
name|combinedParams
argument_list|)
expr_stmt|;
block|}
name|removeNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|ureq
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Node
name|node
range|:
name|removeNodes
control|)
block|{
name|deletes
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|clone
specifier|private
name|DeleteUpdateCommand
name|clone
parameter_list|(
name|DeleteUpdateCommand
name|cmd
parameter_list|)
block|{
name|DeleteUpdateCommand
name|c
init|=
operator|(
name|DeleteUpdateCommand
operator|)
name|cmd
operator|.
name|clone
argument_list|()
decl_stmt|;
comment|// TODO: shouldnt the clone do this?
name|c
operator|.
name|setFlags
argument_list|(
name|cmd
operator|.
name|getFlags
argument_list|()
argument_list|)
expr_stmt|;
name|c
operator|.
name|setVersion
argument_list|(
name|cmd
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
DECL|class|Request
specifier|public
specifier|static
class|class
name|Request
block|{
DECL|field|node
specifier|public
name|Node
name|node
decl_stmt|;
DECL|field|ureq
name|UpdateRequestExt
name|ureq
decl_stmt|;
DECL|field|ursp
name|NamedList
argument_list|<
name|Object
argument_list|>
name|ursp
decl_stmt|;
DECL|field|rspCode
name|int
name|rspCode
decl_stmt|;
DECL|field|exception
specifier|public
name|Exception
name|exception
decl_stmt|;
DECL|field|retries
name|int
name|retries
decl_stmt|;
block|}
DECL|method|submit
name|void
name|submit
parameter_list|(
name|UpdateRequestExt
name|ureq
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
name|Request
name|sreq
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|sreq
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|sreq
operator|.
name|ureq
operator|=
name|ureq
expr_stmt|;
name|submit
argument_list|(
name|sreq
argument_list|)
expr_stmt|;
block|}
DECL|method|submit
specifier|public
name|void
name|submit
parameter_list|(
specifier|final
name|Request
name|sreq
parameter_list|)
block|{
if|if
condition|(
name|completionService
operator|==
literal|null
condition|)
block|{
name|completionService
operator|=
operator|new
name|ExecutorCompletionService
argument_list|<
name|Request
argument_list|>
argument_list|(
name|commExecutor
argument_list|)
expr_stmt|;
name|pending
operator|=
operator|new
name|HashSet
argument_list|<
name|Future
argument_list|<
name|Request
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|url
init|=
name|sreq
operator|.
name|node
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|Callable
argument_list|<
name|Request
argument_list|>
name|task
init|=
operator|new
name|Callable
argument_list|<
name|Request
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Request
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|Request
name|clonedRequest
init|=
operator|new
name|Request
argument_list|()
decl_stmt|;
name|clonedRequest
operator|.
name|node
operator|=
name|sreq
operator|.
name|node
expr_stmt|;
name|clonedRequest
operator|.
name|ureq
operator|=
name|sreq
operator|.
name|ureq
expr_stmt|;
name|clonedRequest
operator|.
name|retries
operator|=
name|sreq
operator|.
name|retries
expr_stmt|;
try|try
block|{
name|String
name|fullUrl
decl_stmt|;
if|if
condition|(
operator|!
name|url
operator|.
name|startsWith
argument_list|(
literal|"http://"
argument_list|)
operator|&&
operator|!
name|url
operator|.
name|startsWith
argument_list|(
literal|"https://"
argument_list|)
condition|)
block|{
name|fullUrl
operator|=
literal|"http://"
operator|+
name|url
expr_stmt|;
block|}
else|else
block|{
name|fullUrl
operator|=
name|url
expr_stmt|;
block|}
name|HttpSolrServer
name|server
init|=
operator|new
name|HttpSolrServer
argument_list|(
name|fullUrl
argument_list|,
name|client
argument_list|)
decl_stmt|;
name|clonedRequest
operator|.
name|ursp
operator|=
name|server
operator|.
name|request
argument_list|(
name|clonedRequest
operator|.
name|ureq
argument_list|)
expr_stmt|;
comment|// currently no way to get the request body.
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|clonedRequest
operator|.
name|exception
operator|=
name|e
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrException
condition|)
block|{
name|clonedRequest
operator|.
name|rspCode
operator|=
operator|(
operator|(
name|SolrException
operator|)
name|e
operator|)
operator|.
name|code
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|clonedRequest
operator|.
name|rspCode
operator|=
operator|-
literal|1
expr_stmt|;
block|}
block|}
return|return
name|clonedRequest
return|;
block|}
block|}
decl_stmt|;
name|pending
operator|.
name|add
argument_list|(
name|completionService
operator|.
name|submit
argument_list|(
name|task
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|checkResponses
name|void
name|checkResponses
parameter_list|(
name|boolean
name|block
parameter_list|)
block|{
while|while
condition|(
name|pending
operator|!=
literal|null
operator|&&
name|pending
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Future
argument_list|<
name|Request
argument_list|>
name|future
init|=
name|block
condition|?
name|completionService
operator|.
name|take
argument_list|()
else|:
name|completionService
operator|.
name|poll
argument_list|()
decl_stmt|;
if|if
condition|(
name|future
operator|==
literal|null
condition|)
return|return;
name|pending
operator|.
name|remove
argument_list|(
name|future
argument_list|)
expr_stmt|;
try|try
block|{
name|Request
name|sreq
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|sreq
operator|.
name|rspCode
operator|!=
literal|0
condition|)
block|{
comment|// error during request
comment|// if there is a retry url, we want to retry...
comment|// TODO: but we really should only retry on connection errors...
if|if
condition|(
name|sreq
operator|.
name|retries
operator|<
literal|5
operator|&&
name|sreq
operator|.
name|node
operator|.
name|checkRetry
argument_list|()
condition|)
block|{
name|sreq
operator|.
name|retries
operator|++
expr_stmt|;
name|sreq
operator|.
name|rspCode
operator|=
literal|0
expr_stmt|;
name|sreq
operator|.
name|exception
operator|=
literal|null
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
argument_list|)
expr_stmt|;
name|submit
argument_list|(
name|sreq
argument_list|)
expr_stmt|;
name|checkResponses
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Exception
name|e
init|=
name|sreq
operator|.
name|exception
decl_stmt|;
name|Error
name|error
init|=
operator|new
name|Error
argument_list|()
decl_stmt|;
name|error
operator|.
name|e
operator|=
name|e
expr_stmt|;
name|error
operator|.
name|node
operator|=
name|sreq
operator|.
name|node
expr_stmt|;
name|response
operator|.
name|errors
operator|.
name|add
argument_list|(
name|error
argument_list|)
expr_stmt|;
name|response
operator|.
name|sreq
operator|=
name|sreq
expr_stmt|;
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"shard update error "
operator|+
name|sreq
operator|.
name|node
argument_list|,
name|sreq
operator|.
name|exception
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
comment|// shouldn't happen since we catch exceptions ourselves
name|SolrException
operator|.
name|log
argument_list|(
name|SolrCore
operator|.
name|log
argument_list|,
literal|"error sending update request to shard"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|SERVICE_UNAVAILABLE
argument_list|,
literal|"interrupted waiting for shard update response"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|class|Response
specifier|public
specifier|static
class|class
name|Response
block|{
DECL|field|sreq
specifier|public
name|Request
name|sreq
decl_stmt|;
DECL|field|errors
specifier|public
name|List
argument_list|<
name|Error
argument_list|>
name|errors
init|=
operator|new
name|ArrayList
argument_list|<
name|Error
argument_list|>
argument_list|()
decl_stmt|;
block|}
DECL|class|Error
specifier|public
specifier|static
class|class
name|Error
block|{
DECL|field|node
specifier|public
name|Node
name|node
decl_stmt|;
DECL|field|e
specifier|public
name|Exception
name|e
decl_stmt|;
block|}
DECL|method|getResponse
specifier|public
name|Response
name|getResponse
parameter_list|()
block|{
return|return
name|response
return|;
block|}
DECL|class|Node
specifier|public
specifier|static
specifier|abstract
class|class
name|Node
block|{
DECL|method|getUrl
specifier|public
specifier|abstract
name|String
name|getUrl
parameter_list|()
function_decl|;
DECL|method|checkRetry
specifier|public
specifier|abstract
name|boolean
name|checkRetry
parameter_list|()
function_decl|;
DECL|method|getCoreName
specifier|public
specifier|abstract
name|String
name|getCoreName
parameter_list|()
function_decl|;
DECL|method|getBaseUrl
specifier|public
specifier|abstract
name|String
name|getBaseUrl
parameter_list|()
function_decl|;
DECL|method|getNodeProps
specifier|public
specifier|abstract
name|ZkCoreNodeProps
name|getNodeProps
parameter_list|()
function_decl|;
block|}
DECL|class|StdNode
specifier|public
specifier|static
class|class
name|StdNode
extends|extends
name|Node
block|{
DECL|field|url
specifier|protected
name|String
name|url
decl_stmt|;
DECL|field|baseUrl
specifier|protected
name|String
name|baseUrl
decl_stmt|;
DECL|field|coreName
specifier|protected
name|String
name|coreName
decl_stmt|;
DECL|field|nodeProps
specifier|private
name|ZkCoreNodeProps
name|nodeProps
decl_stmt|;
DECL|method|StdNode
specifier|public
name|StdNode
parameter_list|(
name|ZkCoreNodeProps
name|nodeProps
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|nodeProps
operator|.
name|getCoreUrl
argument_list|()
expr_stmt|;
name|this
operator|.
name|baseUrl
operator|=
name|nodeProps
operator|.
name|getBaseUrl
argument_list|()
expr_stmt|;
name|this
operator|.
name|coreName
operator|=
name|nodeProps
operator|.
name|getCoreName
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeProps
operator|=
name|nodeProps
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUrl
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": "
operator|+
name|url
return|;
block|}
annotation|@
name|Override
DECL|method|checkRetry
specifier|public
name|boolean
name|checkRetry
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getBaseUrl
specifier|public
name|String
name|getBaseUrl
parameter_list|()
block|{
return|return
name|baseUrl
return|;
block|}
annotation|@
name|Override
DECL|method|getCoreName
specifier|public
name|String
name|getCoreName
parameter_list|()
block|{
return|return
name|coreName
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|baseUrl
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|baseUrl
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|coreName
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|coreName
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|url
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|url
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
name|StdNode
name|other
init|=
operator|(
name|StdNode
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|baseUrl
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|baseUrl
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|baseUrl
operator|.
name|equals
argument_list|(
name|other
operator|.
name|baseUrl
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|coreName
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|coreName
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|coreName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|coreName
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|url
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|url
operator|.
name|equals
argument_list|(
name|other
operator|.
name|url
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
DECL|method|getNodeProps
specifier|public
name|ZkCoreNodeProps
name|getNodeProps
parameter_list|()
block|{
return|return
name|nodeProps
return|;
block|}
block|}
block|}
end_class
end_unit
