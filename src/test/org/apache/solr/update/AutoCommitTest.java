begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|HashMap
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
name|MapSolrParams
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
name|ContentStreamBase
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
name|*
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
name|search
operator|.
name|*
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
name|XmlUpdateRequestHandler
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
name|SolrQueryRequestBase
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
name|AbstractSolrTestCase
import|;
end_import
begin_comment
comment|/** Catch commit notifications  *  * It is tricky to be correctly notified when commits occur: Solr's post-commit  * hook is called after commit has completed but before the search is opened.  The  * best that can be done is wait for a post commit hook, then add a document (which  * will block while the searcher is opened)  */
end_comment
begin_class
DECL|class|CommitListener
class|class
name|CommitListener
implements|implements
name|SolrEventListener
block|{
DECL|field|triggered
specifier|public
name|boolean
name|triggered
init|=
literal|false
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{}
DECL|method|newSearcher
specifier|public
name|void
name|newSearcher
parameter_list|(
name|SolrIndexSearcher
name|newSearcher
parameter_list|,
name|SolrIndexSearcher
name|currentSearcher
parameter_list|)
block|{}
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
block|{
name|triggered
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|waitForCommit
specifier|public
name|boolean
name|waitForCommit
parameter_list|(
name|int
name|timeout
parameter_list|)
block|{
name|triggered
operator|=
literal|false
expr_stmt|;
for|for
control|(
name|int
name|towait
init|=
name|timeout
init|;
name|towait
operator|>
literal|0
condition|;
name|towait
operator|-=
literal|250
control|)
block|{
if|if
condition|(
name|triggered
condition|)
break|break;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|250
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
return|return
name|triggered
return|;
block|}
block|}
end_class
begin_class
DECL|class|AutoCommitTest
specifier|public
class|class
name|AutoCommitTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|method|getSchemaFile
specifier|public
name|String
name|getSchemaFile
parameter_list|()
block|{
return|return
literal|"schema.xml"
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|"solrconfig.xml"
return|;
block|}
comment|/**    * Take a string and make it an iterable ContentStream    *     * This should be moved to a helper class. (it is useful for the client too!)    */
DECL|method|toContentStreams
specifier|public
specifier|static
name|Collection
argument_list|<
name|ContentStream
argument_list|>
name|toContentStreams
parameter_list|(
specifier|final
name|String
name|str
parameter_list|,
specifier|final
name|String
name|contentType
parameter_list|)
block|{
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
name|streams
init|=
operator|new
name|ArrayList
argument_list|<
name|ContentStream
argument_list|>
argument_list|()
decl_stmt|;
name|ContentStreamBase
name|stream
init|=
operator|new
name|ContentStreamBase
operator|.
name|StringStream
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|stream
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
name|streams
operator|.
name|add
argument_list|(
name|stream
argument_list|)
expr_stmt|;
return|return
name|streams
return|;
block|}
DECL|method|testMaxDocs
specifier|public
name|void
name|testMaxDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|CommitListener
name|trigger
init|=
operator|new
name|CommitListener
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|DirectUpdateHandler2
name|updater
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|DirectUpdateHandler2
operator|.
name|CommitTracker
name|tracker
init|=
name|updater
operator|.
name|tracker
decl_stmt|;
name|tracker
operator|.
name|timeUpperBound
operator|=
literal|100000
expr_stmt|;
name|tracker
operator|.
name|docsUpperBound
operator|=
literal|14
expr_stmt|;
name|updater
operator|.
name|commitCallbacks
operator|.
name|add
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
name|XmlUpdateRequestHandler
name|handler
init|=
operator|new
name|XmlUpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|MapSolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add a single document
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|14
condition|;
name|i
operator|++
control|)
block|{
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"A"
operator|+
name|i
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
comment|// It should not be there right away
name|assertQ
argument_list|(
literal|"shouldn't find any"
argument_list|,
name|req
argument_list|(
literal|"id:A1"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tracker
operator|.
name|getCommitCount
argument_list|()
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"A14"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Wait longer than the autocommit time
name|assertTrue
argument_list|(
name|trigger
operator|.
name|waitForCommit
argument_list|(
literal|20000
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"A15"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Now make sure we can find it
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"id:A14"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tracker
operator|.
name|getCommitCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// But not the one added afterward
name|assertQ
argument_list|(
literal|"should not find one"
argument_list|,
name|req
argument_list|(
literal|"id:A15"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|tracker
operator|.
name|getCommitCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxTime
specifier|public
name|void
name|testMaxTime
parameter_list|()
throws|throws
name|Exception
block|{
name|CommitListener
name|trigger
init|=
operator|new
name|CommitListener
argument_list|()
decl_stmt|;
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|DirectUpdateHandler2
name|updater
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|DirectUpdateHandler2
operator|.
name|CommitTracker
name|tracker
init|=
name|updater
operator|.
name|tracker
decl_stmt|;
name|tracker
operator|.
name|timeUpperBound
operator|=
literal|500
expr_stmt|;
name|tracker
operator|.
name|docsUpperBound
operator|=
operator|-
literal|1
expr_stmt|;
name|updater
operator|.
name|commitCallbacks
operator|.
name|add
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
name|XmlUpdateRequestHandler
name|handler
init|=
operator|new
name|XmlUpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|MapSolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add a single document
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{}
decl_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"529"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Check it it is in the index
name|assertQ
argument_list|(
literal|"shouldn't find any"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// Wait longer than the autocommit time
name|assertTrue
argument_list|(
name|trigger
operator|.
name|waitForCommit
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"530"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
comment|// Now make sure we can find it
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|// But not this one
name|assertQ
argument_list|(
literal|"should find none"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// Delete the document
name|assertU
argument_list|(
name|delI
argument_list|(
literal|"529"
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"deleted, but should still be there"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
comment|// Wait longer than the autocommit time
name|assertTrue
argument_list|(
name|trigger
operator|.
name|waitForCommit
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"550"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tracker
operator|.
name|getCommitCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"deleted and time has passed"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// now make the call 10 times really fast and make sure it
comment|// only commits once
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"500"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
name|assertQ
argument_list|(
literal|"should not be there yet"
argument_list|,
name|req
argument_list|(
literal|"id:500"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// Wait longer than the autocommit time
name|assertTrue
argument_list|(
name|trigger
operator|.
name|waitForCommit
argument_list|(
literal|10000
argument_list|)
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"531"
argument_list|,
literal|"field_t"
argument_list|,
literal|"what's inside?"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tracker
operator|.
name|getCommitCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"now it should"
argument_list|,
name|req
argument_list|(
literal|"id:500"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"but not this"
argument_list|,
name|req
argument_list|(
literal|"id:531"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxPending
specifier|public
name|void
name|testMaxPending
parameter_list|()
throws|throws
name|Exception
block|{
name|DirectUpdateHandler2
name|updater
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|updater
operator|.
name|maxPendingDeletes
operator|=
literal|14
expr_stmt|;
name|XmlUpdateRequestHandler
name|handler
init|=
operator|new
name|XmlUpdateRequestHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|init
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|SolrCore
name|core
init|=
name|SolrCore
operator|.
name|getSolrCore
argument_list|()
decl_stmt|;
name|MapSolrParams
name|params
init|=
operator|new
name|MapSolrParams
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
comment|// Add a single document
name|SolrQueryResponse
name|rsp
init|=
operator|new
name|SolrQueryResponse
argument_list|()
decl_stmt|;
name|SolrQueryRequestBase
name|req
init|=
operator|new
name|SolrQueryRequestBase
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
block|{}
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|14
condition|;
name|i
operator|++
control|)
block|{
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"A"
operator|+
name|i
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|updater
operator|.
name|numDocsPending
operator|.
name|get
argument_list|()
argument_list|,
literal|14
argument_list|)
expr_stmt|;
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"A14"
argument_list|,
literal|"subject"
argument_list|,
literal|"info"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
name|req
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|updater
operator|.
name|numDocsPending
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|updater
operator|.
name|commitCommands
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
