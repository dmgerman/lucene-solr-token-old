begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|Slow
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
name|core
operator|.
name|SolrEventListener
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
name|UpdateRequestHandler
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
name|response
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
name|search
operator|.
name|SolrIndexSearcher
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
name|RefCounted
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
name|TimeOut
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
begin_class
DECL|class|NewSearcherListener
class|class
name|NewSearcherListener
implements|implements
name|SolrEventListener
block|{
DECL|enum|TriggerOn
DECL|enum constant|Both
DECL|enum constant|Soft
DECL|enum constant|Hard
enum|enum
name|TriggerOn
block|{
name|Both
block|,
name|Soft
block|,
name|Hard
block|}
DECL|field|triggered
specifier|private
specifier|volatile
name|boolean
name|triggered
init|=
literal|false
decl_stmt|;
DECL|field|lastType
specifier|private
specifier|volatile
name|TriggerOn
name|lastType
decl_stmt|;
DECL|field|triggerOnType
specifier|private
specifier|volatile
name|TriggerOn
name|triggerOnType
decl_stmt|;
DECL|field|newSearcher
specifier|private
specifier|volatile
name|SolrIndexSearcher
name|newSearcher
decl_stmt|;
DECL|method|NewSearcherListener
specifier|public
name|NewSearcherListener
parameter_list|()
block|{
name|this
argument_list|(
name|TriggerOn
operator|.
name|Both
argument_list|)
expr_stmt|;
block|}
DECL|method|NewSearcherListener
specifier|public
name|NewSearcherListener
parameter_list|(
name|TriggerOn
name|type
parameter_list|)
block|{
name|this
operator|.
name|triggerOnType
operator|=
name|type
expr_stmt|;
block|}
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
block|{}
annotation|@
name|Override
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
block|{
if|if
condition|(
name|triggerOnType
operator|==
name|TriggerOn
operator|.
name|Soft
operator|&&
name|lastType
operator|==
name|TriggerOn
operator|.
name|Soft
condition|)
block|{
name|triggered
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|triggerOnType
operator|==
name|TriggerOn
operator|.
name|Hard
operator|&&
name|lastType
operator|==
name|TriggerOn
operator|.
name|Hard
condition|)
block|{
name|triggered
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|triggerOnType
operator|==
name|TriggerOn
operator|.
name|Both
condition|)
block|{
name|triggered
operator|=
literal|true
expr_stmt|;
block|}
name|this
operator|.
name|newSearcher
operator|=
name|newSearcher
expr_stmt|;
comment|// log.info("TEST: newSearcher event: triggered="+triggered+" newSearcher="+newSearcher);
block|}
annotation|@
name|Override
DECL|method|postCommit
specifier|public
name|void
name|postCommit
parameter_list|()
block|{
name|lastType
operator|=
name|TriggerOn
operator|.
name|Hard
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|postSoftCommit
specifier|public
name|void
name|postSoftCommit
parameter_list|()
block|{
name|lastType
operator|=
name|TriggerOn
operator|.
name|Soft
expr_stmt|;
block|}
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|triggered
operator|=
literal|false
expr_stmt|;
comment|// log.info("TEST: trigger reset");
block|}
DECL|method|waitForNewSearcher
name|boolean
name|waitForNewSearcher
parameter_list|(
name|int
name|timeoutMs
parameter_list|)
block|{
name|TimeOut
name|timeout
init|=
operator|new
name|TimeOut
argument_list|(
name|timeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|timeout
operator|.
name|hasTimedOut
argument_list|()
condition|)
block|{
if|if
condition|(
name|triggered
condition|)
block|{
comment|// check if the new searcher has been registered yet
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|registeredSearcherH
init|=
name|newSearcher
operator|.
name|getCore
argument_list|()
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|SolrIndexSearcher
name|registeredSearcher
init|=
name|registeredSearcherH
operator|.
name|get
argument_list|()
decl_stmt|;
name|registeredSearcherH
operator|.
name|decref
argument_list|()
expr_stmt|;
if|if
condition|(
name|registeredSearcher
operator|==
name|newSearcher
condition|)
return|return
literal|true
return|;
comment|// log.info("TEST: waiting for searcher " + newSearcher + " to be registered.  current=" + registeredSearcher);
block|}
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
literal|false
return|;
block|}
block|}
end_class
begin_class
annotation|@
name|Slow
DECL|class|AutoCommitTest
specifier|public
class|class
name|AutoCommitTest
extends|extends
name|AbstractSolrTestCase
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
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
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|verbose
specifier|public
specifier|static
name|void
name|verbose
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
if|if
condition|(
operator|!
name|VERBOSE
condition|)
return|return;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"###TEST:"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|args
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|log
operator|.
name|info
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// System.out.println(sb.toString());
block|}
annotation|@
name|Override
DECL|method|setUp
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
comment|// reload the core to clear stats
name|h
operator|.
name|getCoreContainer
argument_list|()
operator|.
name|reload
argument_list|(
name|h
operator|.
name|getCore
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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
argument_list|<>
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
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
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
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|NewSearcherListener
name|trigger
init|=
operator|new
name|NewSearcherListener
argument_list|()
decl_stmt|;
name|DirectUpdateHandler2
name|updateHandler
init|=
operator|(
name|DirectUpdateHandler2
operator|)
name|core
operator|.
name|getUpdateHandler
argument_list|()
decl_stmt|;
name|CommitTracker
name|tracker
init|=
name|updateHandler
operator|.
name|softCommitTracker
decl_stmt|;
name|tracker
operator|.
name|setTimeUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setDocsUpperBound
argument_list|(
literal|14
argument_list|)
expr_stmt|;
name|core
operator|.
name|registerNewSearcherListener
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
name|UpdateRequestHandler
name|handler
init|=
operator|new
name|UpdateRequestHandler
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
comment|// Add documents
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
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
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
literal|"id:1"
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
literal|"14"
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
name|assertTrue
argument_list|(
name|trigger
operator|.
name|waitForNewSearcher
argument_list|(
literal|15000
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
literal|"15"
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
literal|"id:14"
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
literal|"id:15"
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
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|NewSearcherListener
name|trigger
init|=
operator|new
name|NewSearcherListener
argument_list|()
decl_stmt|;
name|core
operator|.
name|registerNewSearcherListener
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
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
name|CommitTracker
name|tracker
init|=
name|updater
operator|.
name|softCommitTracker
decl_stmt|;
comment|// too low of a number can cause a slow host to commit before the test code checks that it
comment|// isn't there... causing a failure at "shouldn't find any"
name|tracker
operator|.
name|setTimeUpperBound
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// updater.commitCallbacks.add(trigger);
name|UpdateRequestHandler
name|handler
init|=
operator|new
name|UpdateRequestHandler
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
name|trigger
operator|.
name|reset
argument_list|()
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
name|waitForNewSearcher
argument_list|(
literal|45000
argument_list|)
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
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
name|waitForNewSearcher
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
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
name|waitForNewSearcher
argument_list|(
literal|45000
argument_list|)
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
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
block|}
DECL|method|testCommitWithin
specifier|public
name|void
name|testCommitWithin
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|NewSearcherListener
name|trigger
init|=
operator|new
name|NewSearcherListener
argument_list|()
decl_stmt|;
name|core
operator|.
name|registerNewSearcherListener
argument_list|(
name|trigger
argument_list|)
expr_stmt|;
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
name|CommitTracker
name|tracker
init|=
name|updater
operator|.
name|softCommitTracker
decl_stmt|;
name|tracker
operator|.
name|setTimeUpperBound
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|tracker
operator|.
name|setDocsUpperBound
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
name|UpdateRequestHandler
name|handler
init|=
operator|new
name|UpdateRequestHandler
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
comment|// Add a single document with commitWithin == 4 second
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
literal|4000
argument_list|,
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
name|trigger
operator|.
name|reset
argument_list|()
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
comment|// Check it isn't in the index
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
comment|// Wait longer than the commitWithin time
name|assertTrue
argument_list|(
literal|"commitWithin failed to commit"
argument_list|,
name|trigger
operator|.
name|waitForNewSearcher
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Add one document without commitWithin
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
name|trigger
operator|.
name|reset
argument_list|()
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
comment|// Check it isn't in the index
name|assertQ
argument_list|(
literal|"shouldn't find any"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// Delete one document with commitWithin
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|delI
argument_list|(
literal|"529"
argument_list|,
literal|"commitWithin"
argument_list|,
literal|"2000"
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
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
comment|// Wait for the commit to happen
name|assertTrue
argument_list|(
literal|"commitWithin failed to commit"
argument_list|,
name|trigger
operator|.
name|waitForNewSearcher
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
comment|// Now we shouldn't find it
name|assertQ
argument_list|(
literal|"should find none"
argument_list|,
name|req
argument_list|(
literal|"id:529"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
argument_list|)
expr_stmt|;
comment|// ... but we should find the new one
name|assertQ
argument_list|(
literal|"should find one"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|trigger
operator|.
name|reset
argument_list|()
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
literal|2000
argument_list|,
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
comment|// the same for the delete
name|req
operator|.
name|setContentStreams
argument_list|(
name|toContentStreams
argument_list|(
name|delI
argument_list|(
literal|"530"
argument_list|,
literal|"commitWithin"
argument_list|,
literal|"1000"
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
literal|"should be there"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=1]"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"commitWithin failed to commit"
argument_list|,
name|trigger
operator|.
name|waitForNewSearcher
argument_list|(
literal|30000
argument_list|)
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
literal|"should be there"
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
literal|"should not be there"
argument_list|,
name|req
argument_list|(
literal|"id:530"
argument_list|)
argument_list|,
literal|"//result[@numFound=0]"
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
block|}
block|}
end_class
end_unit
