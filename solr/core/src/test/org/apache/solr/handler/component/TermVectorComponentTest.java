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
name|SolrTestCaseJ4
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
name|request
operator|.
name|LocalSolrQueryRequest
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
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|SolrTestCaseJ4
block|{
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"This is a title and another title"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_notv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_postv"
argument_list|,
literal|"The quick reb fox jumped over the lazy brown dogs."
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"This is a document"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"another document"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blue"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blud"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"boue"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"glue"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blee"
argument_list|,
literal|"test_offtv"
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
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_notv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_postv"
argument_list|,
literal|"blah"
argument_list|,
literal|"test_offtv"
argument_list|,
literal|"blah"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|field|tv
specifier|static
name|String
name|tv
init|=
literal|"tvrh"
decl_stmt|;
annotation|@
name|Test
DECL|method|testBasics
specifier|public
name|void
name|testBasics
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors=={'0':{'uniqueKey':'0',"
operator|+
literal|" 'test_basictv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_offtv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_posofftv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_postv':{'anoth':{'tf':1},'titl':{'tf':2}}},"
operator|+
literal|" 'uniqueKeyFieldName':'id'}"
argument_list|)
expr_stmt|;
comment|// tv.fl diff from fl
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"tv.fl"
argument_list|,
literal|"test_basictv,test_offtv"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors=={'0':{'uniqueKey':'0',"
operator|+
literal|" 'test_basictv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_offtv':{'anoth':{'tf':1},'titl':{'tf':2}}},"
operator|+
literal|" 'uniqueKeyFieldName':'id'}"
argument_list|)
expr_stmt|;
comment|// multi-valued tv.fl
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
literal|"tv.fl"
argument_list|,
literal|"test_basictv"
argument_list|,
literal|"tv.fl"
argument_list|,
literal|"test_offtv"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors=={'0':{'uniqueKey':'0',"
operator|+
literal|" 'test_basictv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_offtv':{'anoth':{'tf':1},'titl':{'tf':2}}},"
operator|+
literal|" 'uniqueKeyFieldName':'id'}"
argument_list|)
expr_stmt|;
comment|// re-use fl glob
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
literal|"fl"
argument_list|,
literal|"*,score"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors=={'0':{'uniqueKey':'0',"
operator|+
literal|" 'test_basictv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_offtv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_posofftv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_postv':{'anoth':{'tf':1},'titl':{'tf':2}}},"
operator|+
literal|" 'uniqueKeyFieldName':'id'}"
argument_list|)
expr_stmt|;
comment|// re-use fl, ignore things we can't handle
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
literal|"fl"
argument_list|,
literal|"score,test_basictv,[docid],test_postv,val:sum(3,4)"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors=={'0':{'uniqueKey':'0',"
operator|+
literal|" 'test_basictv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_postv':{'anoth':{'tf':1},'titl':{'tf':2}}},"
operator|+
literal|" 'uniqueKeyFieldName':'id'}"
argument_list|)
expr_stmt|;
comment|// re-use (multi-valued) fl, ignore things we can't handle
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
literal|"fl"
argument_list|,
literal|"score,test_basictv"
argument_list|,
literal|"fl"
argument_list|,
literal|"[docid],test_postv,val:sum(3,4)"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors=={'0':{'uniqueKey':'0',"
operator|+
literal|" 'test_basictv':{'anoth':{'tf':1},'titl':{'tf':2}},"
operator|+
literal|" 'test_postv':{'anoth':{'tf':1},'titl':{'tf':2}}},"
operator|+
literal|" 'uniqueKeyFieldName':'id'}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testOptions
specifier|public
name|void
name|testOptions
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors/0/test_posofftv/anoth=={'tf':1, 'offsets':{'start':20, 'end':27}, 'positions':{'position':1}, 'df':2, 'tf-idf':0.5}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|ALL
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"/termVectors/0/test_posofftv/anoth=={'tf':1, 'offsets':{'start':20, 'end':27}, 'positions':{'position':1}, 'df':2, 'tf-idf':0.5}"
argument_list|)
expr_stmt|;
comment|// test each combination at random
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|list
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|String
index|[]
index|[]
name|options
init|=
operator|new
name|String
index|[]
index|[]
block|{
block|{
name|TermVectorParams
operator|.
name|TF
block|,
literal|"'tf':1"
block|}
block|,
block|{
name|TermVectorParams
operator|.
name|OFFSETS
block|,
literal|"'offsets':{'start':20, 'end':27}"
block|}
block|,
block|{
name|TermVectorParams
operator|.
name|POSITIONS
block|,
literal|"'positions':{'position':1}"
block|}
block|,
block|{
name|TermVectorParams
operator|.
name|DF
block|,
literal|"'df':2"
block|}
block|,
block|{
name|TermVectorParams
operator|.
name|TF_IDF
block|,
literal|"'tf-idf':0.5"
block|}
block|}
decl_stmt|;
name|StringBuilder
name|expected
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"/termVectors/0/test_posofftv/anoth=={"
argument_list|)
decl_stmt|;
name|boolean
name|first
init|=
literal|true
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
name|options
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|boolean
name|use
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|use
condition|)
block|{
if|if
condition|(
operator|!
name|first
condition|)
block|{
name|expected
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|first
operator|=
literal|false
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
name|options
index|[
name|i
index|]
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|options
index|[
name|i
index|]
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|use
condition|?
literal|"true"
else|:
literal|"false"
argument_list|)
expr_stmt|;
block|}
name|expected
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|,
name|expected
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPerField
specifier|public
name|void
name|testPerField
parameter_list|()
throws|throws
name|Exception
block|{
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"json.nl"
argument_list|,
literal|"map"
argument_list|,
literal|"qt"
argument_list|,
name|tv
argument_list|,
literal|"q"
argument_list|,
literal|"id:0"
argument_list|,
name|TermVectorComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"true"
argument_list|,
name|TermVectorParams
operator|.
name|FIELDS
argument_list|,
literal|"test_basictv,test_notv,test_postv,test_offtv,test_posofftv"
argument_list|,
literal|"f.test_posofftv."
operator|+
name|TermVectorParams
operator|.
name|POSITIONS
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_offtv."
operator|+
name|TermVectorParams
operator|.
name|OFFSETS
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|DF
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|TF
argument_list|,
literal|"false"
argument_list|,
literal|"f.test_basictv."
operator|+
name|TermVectorParams
operator|.
name|TF_IDF
argument_list|,
literal|"false"
argument_list|)
argument_list|,
literal|"/termVectors/0/test_basictv=={'anoth':{},'titl':{}}"
argument_list|,
literal|"/termVectors/0/test_postv/anoth=={'tf':1, 'positions':{'position':1}, 'df':2, 'tf-idf':0.5}"
argument_list|,
literal|"/termVectors/0/test_offtv/anoth=={'tf':1, 'df':2, 'tf-idf':0.5}"
argument_list|,
literal|"/termVectors/warnings=={ 'noTermVectors':['test_notv'], 'noPositions':['test_basictv', 'test_offtv'], 'noOffsets':['test_basictv', 'test_postv']}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
begin_comment
comment|/* *<field name="test_basictv" type="text" termVectors="true"/><field name="test_notv" type="text" termVectors="false"/><field name="test_postv" type="text" termVectors="true" termPositions="true"/><field name="test_offtv" type="text" termVectors="true" termOffsets="true"/><field name="test_posofftv" type="text" termVectors="true"      termPositions="true" termOffsets="true"/> * * */
end_comment
end_unit
