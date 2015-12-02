begin_unit
begin_package
DECL|package|org.apache.solr.search.mlt
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|mlt
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Arrays
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
name|response
operator|.
name|QueryResponse
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
name|cloud
operator|.
name|AbstractFullDistribZkTestBase
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
name|SolrDocument
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
name|SolrDocumentList
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
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|CloudMLTQParserTest
specifier|public
class|class
name|CloudMLTQParserTest
extends|extends
name|AbstractFullDistribZkTestBase
block|{
DECL|method|CloudMLTQParserTest
specifier|public
name|CloudMLTQParserTest
parameter_list|()
block|{
name|sliceCount
operator|=
literal|2
expr_stmt|;
name|configString
operator|=
literal|"solrconfig.xml"
expr_stmt|;
name|schemaString
operator|=
literal|"schema.xml"
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getCloudSolrConfig
specifier|protected
name|String
name|getCloudSolrConfig
parameter_list|()
block|{
return|return
name|configString
return|;
block|}
annotation|@
name|Test
annotation|@
name|ShardsFixed
argument_list|(
name|num
operator|=
literal|2
argument_list|)
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|waitForRecoveriesToFinish
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|String
name|id
init|=
literal|"id"
decl_stmt|;
name|delQ
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|String
name|FIELD1
init|=
literal|"lowerfilt"
decl_stmt|;
name|String
name|FIELD2
init|=
literal|"lowerfilt1"
decl_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
name|FIELD1
argument_list|,
literal|"toyota"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
name|FIELD1
argument_list|,
literal|"chevrolet"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
name|FIELD1
argument_list|,
literal|"bmw usa"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
name|FIELD1
argument_list|,
literal|"ford"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
name|FIELD1
argument_list|,
literal|"ferrari"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
name|FIELD1
argument_list|,
literal|"jaguar"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
name|FIELD1
argument_list|,
literal|"mclaren moon or the moon and moon moon shine and the moon but moon was good foxes too"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
name|FIELD1
argument_list|,
literal|"sonata"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quick red fox jumped over the lazy big and large brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
name|FIELD1
argument_list|,
literal|"blue"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
name|FIELD1
argument_list|,
literal|"glue"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"14"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"15"
argument_list|,
name|FIELD1
argument_list|,
literal|"The fat red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"16"
argument_list|,
name|FIELD1
argument_list|,
literal|"The slim red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"17"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped moon over the lazy brown dogs moon. Of course moon. Foxes and moon come back to the foxes and moon"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"18"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"19"
argument_list|,
name|FIELD1
argument_list|,
literal|"The hose red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"20"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"21"
argument_list|,
name|FIELD1
argument_list|,
literal|"The court red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"22"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"23"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"24"
argument_list|,
name|FIELD1
argument_list|,
literal|"The file red fox jumped over the lazy brown dogs."
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"25"
argument_list|,
name|FIELD1
argument_list|,
literal|"rod fix"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"26"
argument_list|,
name|FIELD1
argument_list|,
literal|"bmw usa 328i"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"27"
argument_list|,
name|FIELD1
argument_list|,
literal|"bmw usa 535i"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"28"
argument_list|,
name|FIELD1
argument_list|,
literal|"bmw 750Li"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"29"
argument_list|,
name|FIELD1
argument_list|,
literal|"bmw usa"
argument_list|,
name|FIELD2
argument_list|,
literal|"red green blue"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"30"
argument_list|,
name|FIELD1
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|,
name|FIELD2
argument_list|,
literal|"red green yellow"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"31"
argument_list|,
name|FIELD1
argument_list|,
literal|"The fat red fox jumped over the lazy brown dogs."
argument_list|,
name|FIELD2
argument_list|,
literal|"green blue yellow"
argument_list|)
argument_list|)
expr_stmt|;
name|indexDoc
argument_list|(
name|sdoc
argument_list|(
name|id
argument_list|,
literal|"32"
argument_list|,
name|FIELD1
argument_list|,
literal|"The slim red fox jumped over the lazy brown dogs."
argument_list|,
name|FIELD2
argument_list|,
literal|"yellow white black"
argument_list|)
argument_list|)
expr_stmt|;
name|commit
argument_list|()
expr_stmt|;
name|handle
operator|.
name|clear
argument_list|()
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"QTime"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"timestamp"
argument_list|,
name|SKIPVAL
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"maxScore"
argument_list|,
name|SKIPVAL
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
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt}17"
argument_list|)
expr_stmt|;
name|QueryResponse
name|queryResponse
init|=
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|solrDocuments
init|=
name|queryResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
name|int
index|[]
name|expectedIds
init|=
operator|new
name|int
index|[]
block|{
literal|7
block|,
literal|13
block|,
literal|14
block|,
literal|15
block|,
literal|16
block|,
literal|20
block|,
literal|22
block|,
literal|24
block|,
literal|32
block|,
literal|9
block|}
decl_stmt|;
name|int
index|[]
name|actualIds
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SolrDocument
name|solrDocument
range|:
name|solrDocuments
control|)
block|{
name|actualIds
index|[
name|i
operator|++
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|expectedIds
argument_list|,
name|actualIds
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt mindf=0 mintf=1}3"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|solrDocuments
operator|=
name|queryResponse
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|expectedIds
operator|=
operator|new
name|int
index|[]
block|{
literal|29
block|,
literal|27
block|,
literal|26
block|,
literal|28
block|}
expr_stmt|;
name|actualIds
operator|=
operator|new
name|int
index|[
name|solrDocuments
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|SolrDocument
name|solrDocument
range|:
name|solrDocuments
control|)
block|{
name|actualIds
index|[
name|i
operator|++
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|expectedIds
argument_list|,
name|actualIds
argument_list|)
expr_stmt|;
name|String
index|[]
name|expectedQueryStrings
init|=
operator|new
name|String
index|[]
block|{
literal|"(+(lowerfilt:bmw lowerfilt:usa) -id:3)/no_coord"
block|,
literal|"(+(lowerfilt:usa lowerfilt:bmw) -id:3)/no_coord"
block|}
decl_stmt|;
name|String
index|[]
name|actualParsedQueries
decl_stmt|;
if|if
condition|(
name|queryResponse
operator|.
name|getDebugMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"parsedquery"
argument_list|)
operator|instanceof
name|String
condition|)
block|{
name|String
name|parsedQueryString
init|=
operator|(
name|String
operator|)
name|queryResponse
operator|.
name|getDebugMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"parsedquery"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|parsedQueryString
operator|.
name|equals
argument_list|(
name|expectedQueryStrings
index|[
literal|0
index|]
argument_list|)
operator|||
name|parsedQueryString
operator|.
name|equals
argument_list|(
name|expectedQueryStrings
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|actualParsedQueries
operator|=
operator|(
operator|(
name|ArrayList
argument_list|<
name|String
argument_list|>
operator|)
name|queryResponse
operator|.
name|getDebugMap
argument_list|()
operator|.
name|get
argument_list|(
literal|"parsedquery"
argument_list|)
operator|)
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|actualParsedQueries
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|expectedQueryStrings
argument_list|,
name|actualParsedQueries
argument_list|)
expr_stmt|;
block|}
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt,lowerfilt1 mindf=0 mintf=1}26"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|solrDocuments
operator|=
name|queryResponse
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|expectedIds
operator|=
operator|new
name|int
index|[]
block|{
literal|27
block|,
literal|3
block|,
literal|29
block|,
literal|28
block|}
expr_stmt|;
name|actualIds
operator|=
operator|new
name|int
index|[
name|solrDocuments
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|SolrDocument
name|solrDocument
range|:
name|solrDocuments
control|)
block|{
name|actualIds
index|[
name|i
operator|++
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|expectedIds
argument_list|,
name|actualIds
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
comment|// Test out a high value of df and make sure nothing matches.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt mindf=20 mintf=1}3"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|solrDocuments
operator|=
name|queryResponse
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected to match 0 documents with a mindf of 20 but found more"
argument_list|,
name|solrDocuments
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
comment|// Test out a high value of wl and make sure nothing matches.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt minwl=4 mintf=1}3"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|solrDocuments
operator|=
name|queryResponse
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected to match 0 documents with a minwl of 4 but found more"
argument_list|,
name|solrDocuments
operator|.
name|size
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
comment|// Test out a low enough value of minwl and make sure we get the expected matches.
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt minwl=3 mintf=1}3"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|DEBUG
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|solrDocuments
operator|=
name|queryResponse
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Expected to match 4 documents with a minwl of 3 but found more"
argument_list|,
literal|4
argument_list|,
name|solrDocuments
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Assert that {!mlt}id does not throw an exception i.e. implicitly, only fields that are stored + have explicit
comment|// analyzer are used for MLT Query construction.
name|params
operator|=
operator|new
name|ModifiableSolrParams
argument_list|()
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt}20"
argument_list|)
expr_stmt|;
name|queryResponse
operator|=
name|queryServer
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|solrDocuments
operator|=
name|queryResponse
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|actualIds
operator|=
operator|new
name|int
index|[
name|solrDocuments
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|expectedIds
operator|=
operator|new
name|int
index|[]
block|{
literal|13
block|,
literal|14
block|,
literal|15
block|,
literal|16
block|,
literal|22
block|,
literal|24
block|,
literal|32
block|,
literal|18
block|,
literal|19
block|,
literal|21
block|}
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|SolrDocument
name|solrDocument
range|:
name|solrDocuments
control|)
block|{
name|actualIds
index|[
name|i
operator|++
index|]
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|solrDocument
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|actualIds
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|assertArrayEquals
argument_list|(
name|expectedIds
argument_list|,
name|actualIds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|SolrException
operator|.
name|class
argument_list|)
DECL|method|testInvalidDocument
specifier|public
name|void
name|testInvalidDocument
parameter_list|()
throws|throws
name|IOException
block|{
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
decl_stmt|;
name|params
operator|.
name|set
argument_list|(
name|CommonParams
operator|.
name|Q
argument_list|,
literal|"{!mlt qf=lowerfilt}999999"
argument_list|)
expr_stmt|;
try|try
block|{
name|cloudClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"The above query is supposed to throw an exception."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
comment|// Do nothing.
block|}
block|}
block|}
end_class
end_unit
