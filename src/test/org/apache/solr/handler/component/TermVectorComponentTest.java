begin_unit
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
name|TermVectorParams
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
name|SimpleOrderedMap
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
name|SolrRequestHandler
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  *  *  **/
end_comment
begin_class
DECL|class|TermVectorComponentTest
specifier|public
class|class
name|TermVectorComponentTest
extends|extends
name|AbstractSolrTestCase
block|{
annotation|@
name|Override
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
annotation|@
name|Override
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
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"This is a title and another title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"This is a document"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"another document"
argument_list|)
argument_list|)
expr_stmt|;
comment|//bunch of docs that are variants on blue
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blud"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"boue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"7"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"glue"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"8"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blee"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"9"
argument_list|,
literal|"test_posofftv"
argument_list|,
literal|"blah"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
literal|"commit"
argument_list|,
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|testBasics
specifier|public
name|void
name|testBasics
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
name|SearchComponent
name|tvComp
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"tvComponent"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tvComp is null and it shouldn't be"
argument_list|,
name|tvComp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:0"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"tvrh"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"tvrh"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|NamedList
name|termVectors
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
name|TermVectorComponent
operator|.
name|TERM_VECTORS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"termVectors is null and it shouldn't be"
argument_list|,
name|termVectors
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TVs:"
operator|+
name|termVectors
argument_list|)
expr_stmt|;
name|NamedList
name|doc
init|=
operator|(
name|NamedList
operator|)
name|termVectors
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"doc is null and it shouldn't be"
argument_list|,
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|size
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|doc
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|NamedList
name|field
init|=
operator|(
name|NamedList
operator|)
name|doc
operator|.
name|get
argument_list|(
literal|"test_posofftv"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"field is null and it shouldn't be"
argument_list|,
name|field
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|field
operator|.
name|size
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|field
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|NamedList
name|titl
init|=
operator|(
name|NamedList
operator|)
name|field
operator|.
name|get
argument_list|(
literal|"titl"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"titl is null and it shouldn't be"
argument_list|,
name|titl
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|titl
operator|.
name|get
argument_list|(
literal|"freq"
argument_list|)
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
operator|(
operator|(
name|Integer
operator|)
name|titl
operator|.
name|get
argument_list|(
literal|"freq"
argument_list|)
operator|)
operator|==
literal|2
argument_list|)
expr_stmt|;
name|String
name|uniqueKeyFieldName
init|=
operator|(
name|String
operator|)
name|termVectors
operator|.
name|getVal
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"uniqueKeyFieldName is null and it shouldn't be"
argument_list|,
name|uniqueKeyFieldName
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|uniqueKeyFieldName
operator|+
literal|" is not equal to "
operator|+
literal|"id"
argument_list|,
name|uniqueKeyFieldName
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
operator|==
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|testOptions
specifier|public
name|void
name|testOptions
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
name|SearchComponent
name|tvComp
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"tvComponent"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tvComp is null and it shouldn't be"
argument_list|,
name|tvComp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:0"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"tvrh"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|IDF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"tvrh"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|NamedList
name|termVectors
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
name|TermVectorComponent
operator|.
name|TERM_VECTORS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"termVectors is null and it shouldn't be"
argument_list|,
name|termVectors
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TVs: "
operator|+
name|termVectors
argument_list|)
expr_stmt|;
name|NamedList
name|doc
init|=
operator|(
name|NamedList
operator|)
name|termVectors
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"doc is null and it shouldn't be"
argument_list|,
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|size
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|2
argument_list|,
name|doc
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|method|testNoFields
specifier|public
name|void
name|testNoFields
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
name|SearchComponent
name|tvComp
init|=
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"tvComponent"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tvComp is null and it shouldn't be"
argument_list|,
name|tvComp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:0"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"tvrh"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
comment|//Pass in a field that doesn't exist on the doc, thus, no vectors should be returned
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|FIELDS
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|SolrRequestHandler
name|handler
init|=
name|core
operator|.
name|getRequestHandler
argument_list|(
literal|"tvrh"
argument_list|)
decl_stmt|;
name|SolrQueryResponse
name|rsp
decl_stmt|;
name|rsp
operator|=
operator|new
name|SolrQueryResponse
argument_list|()
expr_stmt|;
name|rsp
operator|.
name|add
argument_list|(
literal|"responseHeader"
argument_list|,
operator|new
name|SimpleOrderedMap
argument_list|()
argument_list|)
expr_stmt|;
name|handler
operator|.
name|handleRequest
argument_list|(
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
argument_list|,
name|rsp
argument_list|)
expr_stmt|;
name|NamedList
name|values
init|=
name|rsp
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|NamedList
name|termVectors
init|=
operator|(
name|NamedList
operator|)
name|values
operator|.
name|get
argument_list|(
name|TermVectorComponent
operator|.
name|TERM_VECTORS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"termVectors is null and it shouldn't be"
argument_list|,
name|termVectors
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|NamedList
name|doc
init|=
operator|(
name|NamedList
operator|)
name|termVectors
operator|.
name|getVal
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"doc is null and it shouldn't be"
argument_list|,
name|doc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc
operator|.
name|size
argument_list|()
operator|+
literal|" does not equal: "
operator|+
literal|1
argument_list|,
name|doc
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|testDistributed
specifier|public
name|void
name|testDistributed
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
name|TermVectorComponent
name|tvComp
init|=
operator|(
name|TermVectorComponent
operator|)
name|core
operator|.
name|getSearchComponent
argument_list|(
literal|"tvComponent"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"tvComp is null and it shouldn't be"
argument_list|,
name|tvComp
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|ResponseBuilder
name|rb
init|=
operator|new
name|ResponseBuilder
argument_list|()
decl_stmt|;
name|rb
operator|.
name|stage
operator|=
name|ResponseBuilder
operator|.
name|STAGE_GET_FIELDS
expr_stmt|;
name|rb
operator|.
name|shards
operator|=
operator|new
name|String
index|[]
block|{
literal|"localhost:0"
block|,
literal|"localhost:1"
block|,
literal|"localhost:2"
block|,
literal|"localhost:3"
block|}
expr_stmt|;
comment|//we don't actually call these, since we are going to invoke distributedProcess directly
name|rb
operator|.
name|resultIds
operator|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|ShardDoc
argument_list|>
argument_list|()
expr_stmt|;
name|rb
operator|.
name|components
operator|=
operator|new
name|ArrayList
argument_list|<
name|SearchComponent
argument_list|>
argument_list|()
expr_stmt|;
name|rb
operator|.
name|components
operator|.
name|add
argument_list|(
name|tvComp
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"id:0"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|CommonParams
operator|.
name|QT
argument_list|,
literal|"tvrh"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|IDF
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|rb
operator|.
name|req
operator|=
operator|new
name|LocalSolrQueryRequest
argument_list|(
name|core
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|rb
operator|.
name|outgoing
operator|=
operator|new
name|ArrayList
argument_list|<
name|ShardRequest
argument_list|>
argument_list|()
expr_stmt|;
comment|//one doc per shard, but make sure there are enough docs to go around
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rb
operator|.
name|shards
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ShardDoc
name|doc
init|=
operator|new
name|ShardDoc
argument_list|()
decl_stmt|;
name|doc
operator|.
name|id
operator|=
name|i
expr_stmt|;
comment|//must be a valid doc that was indexed.
name|doc
operator|.
name|score
operator|=
literal|1
operator|-
operator|(
name|i
operator|/
operator|(
name|float
operator|)
name|rb
operator|.
name|shards
operator|.
name|length
operator|)
expr_stmt|;
name|doc
operator|.
name|positionInResponse
operator|=
name|i
expr_stmt|;
name|doc
operator|.
name|shard
operator|=
name|rb
operator|.
name|shards
index|[
name|i
index|]
expr_stmt|;
name|doc
operator|.
name|orderInShard
operator|=
literal|0
expr_stmt|;
name|rb
operator|.
name|resultIds
operator|.
name|put
argument_list|(
name|doc
operator|.
name|id
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|int
name|result
init|=
name|tvComp
operator|.
name|distributedProcess
argument_list|(
name|rb
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|result
operator|+
literal|" does not equal: "
operator|+
name|ResponseBuilder
operator|.
name|STAGE_DONE
argument_list|,
name|result
operator|==
name|ResponseBuilder
operator|.
name|STAGE_DONE
argument_list|)
expr_stmt|;
comment|//one outgoing per shard
name|assertTrue
argument_list|(
literal|"rb.outgoing Size: "
operator|+
name|rb
operator|.
name|outgoing
operator|.
name|size
argument_list|()
operator|+
literal|" is not: "
operator|+
name|rb
operator|.
name|shards
operator|.
name|length
argument_list|,
name|rb
operator|.
name|outgoing
operator|.
name|size
argument_list|()
operator|==
name|rb
operator|.
name|shards
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|ShardRequest
name|request
range|:
name|rb
operator|.
name|outgoing
control|)
block|{
name|ModifiableSolrParams
name|solrParams
init|=
name|request
operator|.
name|params
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Shard: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|request
operator|.
name|shards
argument_list|)
operator|+
literal|" Params: "
operator|+
name|solrParams
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
begin_comment
comment|/* *<field name="test_basictv" type="text" termVectors="true"/><field name="test_notv" type="text" termVectors="false"/><field name="test_postv" type="text" termVectors="true" termPositions="true"/><field name="test_offtv" type="text" termVectors="true" termOffsets="true"/><field name="test_posofftv" type="text" termVectors="true"      termPositions="true" termOffsets="true"/> * * */
end_comment
end_unit
