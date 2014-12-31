begin_unit
begin_package
DECL|package|org.apache.solr.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|cloud
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
name|Set
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
name|request
operator|.
name|UpdateRequest
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_class
DECL|class|StopableIndexingThread
specifier|public
class|class
name|StopableIndexingThread
extends|extends
name|AbstractFullDistribZkTestBase
operator|.
name|StopableThread
block|{
DECL|field|t1
specifier|private
specifier|static
name|String
name|t1
init|=
literal|"a_t"
decl_stmt|;
DECL|field|i1
specifier|private
specifier|static
name|String
name|i1
init|=
literal|"a_i"
decl_stmt|;
DECL|field|stop
specifier|private
specifier|volatile
name|boolean
name|stop
init|=
literal|false
decl_stmt|;
DECL|field|id
specifier|protected
specifier|final
name|String
name|id
decl_stmt|;
DECL|field|deletes
specifier|protected
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|deletes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|addFails
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|addFails
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|deleteFails
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|deleteFails
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|doDeletes
specifier|protected
name|boolean
name|doDeletes
decl_stmt|;
DECL|field|numCycles
specifier|private
name|int
name|numCycles
decl_stmt|;
DECL|field|controlClient
specifier|private
name|SolrClient
name|controlClient
decl_stmt|;
DECL|field|cloudClient
specifier|private
name|SolrClient
name|cloudClient
decl_stmt|;
DECL|field|numDeletes
specifier|private
name|int
name|numDeletes
decl_stmt|;
DECL|field|numAdds
specifier|private
name|int
name|numAdds
decl_stmt|;
DECL|method|StopableIndexingThread
specifier|public
name|StopableIndexingThread
parameter_list|(
name|SolrClient
name|controlClient
parameter_list|,
name|SolrClient
name|cloudClient
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|doDeletes
parameter_list|)
block|{
name|this
argument_list|(
name|controlClient
argument_list|,
name|cloudClient
argument_list|,
name|id
argument_list|,
name|doDeletes
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|StopableIndexingThread
specifier|public
name|StopableIndexingThread
parameter_list|(
name|SolrClient
name|controlClient
parameter_list|,
name|SolrClient
name|cloudClient
parameter_list|,
name|String
name|id
parameter_list|,
name|boolean
name|doDeletes
parameter_list|,
name|int
name|numCycles
parameter_list|)
block|{
name|super
argument_list|(
literal|"StopableIndexingThread"
argument_list|)
expr_stmt|;
name|this
operator|.
name|controlClient
operator|=
name|controlClient
expr_stmt|;
name|this
operator|.
name|cloudClient
operator|=
name|cloudClient
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|doDeletes
operator|=
name|doDeletes
expr_stmt|;
name|this
operator|.
name|numCycles
operator|=
name|numCycles
expr_stmt|;
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run
specifier|public
name|void
name|run
parameter_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|int
name|numDone
init|=
literal|0
decl_stmt|;
name|numDeletes
operator|=
literal|0
expr_stmt|;
name|numAdds
operator|=
literal|0
expr_stmt|;
while|while
condition|(
literal|true
operator|&&
operator|!
name|stop
condition|)
block|{
if|if
condition|(
name|numCycles
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|numDone
operator|>
name|numCycles
condition|)
block|{
break|break;
block|}
block|}
operator|++
name|numDone
expr_stmt|;
name|String
name|id
init|=
name|this
operator|.
name|id
operator|+
literal|"-"
operator|+
name|i
decl_stmt|;
operator|++
name|i
expr_stmt|;
name|boolean
name|addFailed
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|doDeletes
operator|&&
name|AbstractFullDistribZkTestBase
operator|.
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|deletes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|deleteId
init|=
name|deletes
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|numDeletes
operator|++
expr_stmt|;
if|if
condition|(
name|controlClient
operator|!=
literal|null
condition|)
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|deleteById
argument_list|(
name|deleteId
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"CONTROL"
argument_list|,
literal|"TRUE"
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|controlClient
argument_list|)
expr_stmt|;
block|}
name|cloudClient
operator|.
name|deleteById
argument_list|(
name|deleteId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"REQUEST FAILED for id="
operator|+
name|deleteId
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrServerException
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ROOT CAUSE for id="
operator|+
name|deleteId
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SolrServerException
operator|)
name|e
operator|)
operator|.
name|getRootCause
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|deleteFails
operator|.
name|add
argument_list|(
name|deleteId
argument_list|)
expr_stmt|;
block|}
block|}
try|try
block|{
name|numAdds
operator|++
expr_stmt|;
name|indexr
argument_list|(
literal|"id"
argument_list|,
name|id
argument_list|,
name|i1
argument_list|,
literal|50
argument_list|,
name|t1
argument_list|,
literal|"to come to the aid of their country."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|addFailed
operator|=
literal|true
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"REQUEST FAILED for id="
operator|+
name|id
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|SolrServerException
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ROOT CAUSE for id="
operator|+
name|id
argument_list|)
expr_stmt|;
operator|(
operator|(
name|SolrServerException
operator|)
name|e
operator|)
operator|.
name|getRootCause
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|addFails
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|addFailed
operator|&&
name|doDeletes
operator|&&
name|AbstractFullDistribZkTestBase
operator|.
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|deletes
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|sleep
argument_list|(
name|AbstractFullDistribZkTestBase
operator|.
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"added docs:"
operator|+
name|numAdds
operator|+
literal|" with "
operator|+
operator|(
name|addFails
operator|.
name|size
argument_list|()
operator|+
name|deleteFails
operator|.
name|size
argument_list|()
operator|)
operator|+
literal|" fails"
operator|+
literal|" deletes:"
operator|+
name|numDeletes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|safeStop
specifier|public
name|void
name|safeStop
parameter_list|()
block|{
name|stop
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getAddFails
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAddFails
parameter_list|()
block|{
return|return
name|addFails
return|;
block|}
DECL|method|getDeleteFails
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getDeleteFails
parameter_list|()
block|{
return|return
name|deleteFails
return|;
block|}
DECL|method|getFailCount
specifier|public
name|int
name|getFailCount
parameter_list|()
block|{
return|return
name|addFails
operator|.
name|size
argument_list|()
operator|+
name|deleteFails
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|addFields
specifier|protected
name|void
name|addFields
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|,
name|Object
modifier|...
name|fields
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|doc
operator|.
name|addField
argument_list|(
call|(
name|String
call|)
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|)
argument_list|,
name|fields
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|indexr
specifier|protected
name|void
name|indexr
parameter_list|(
name|Object
modifier|...
name|fields
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrInputDocument
name|doc
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
name|fields
argument_list|)
expr_stmt|;
name|addFields
argument_list|(
name|doc
argument_list|,
literal|"rnd_b"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
DECL|method|indexDoc
specifier|protected
name|void
name|indexDoc
parameter_list|(
name|SolrInputDocument
name|doc
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
if|if
condition|(
name|controlClient
operator|!=
literal|null
condition|)
block|{
name|UpdateRequest
name|req
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|req
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|req
operator|.
name|setParam
argument_list|(
literal|"CONTROL"
argument_list|,
literal|"TRUE"
argument_list|)
expr_stmt|;
name|req
operator|.
name|process
argument_list|(
name|controlClient
argument_list|)
expr_stmt|;
block|}
name|UpdateRequest
name|ureq
init|=
operator|new
name|UpdateRequest
argument_list|()
decl_stmt|;
name|ureq
operator|.
name|add
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|ureq
operator|.
name|process
argument_list|(
name|cloudClient
argument_list|)
expr_stmt|;
block|}
DECL|method|getNumDeletes
specifier|public
name|int
name|getNumDeletes
parameter_list|()
block|{
return|return
name|numDeletes
return|;
block|}
DECL|method|getNumAdds
specifier|public
name|int
name|getNumAdds
parameter_list|()
block|{
return|return
name|numAdds
return|;
block|}
block|}
end_class
end_unit
