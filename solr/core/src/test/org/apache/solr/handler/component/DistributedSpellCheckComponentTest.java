begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
operator|.
name|SuppressTempFileChecks
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
name|BaseDistributedSearchTestCase
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
name|SpellingParams
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
begin_comment
comment|/**  * Test for SpellCheckComponent's distributed querying  *  * @since solr 1.5  *  * @see org.apache.solr.handler.component.SpellCheckComponent  */
end_comment
begin_class
annotation|@
name|Slow
annotation|@
name|SuppressTempFileChecks
argument_list|(
name|bugUrl
operator|=
literal|"https://issues.apache.org/jira/browse/SOLR-1877 Spellcheck IndexReader leak bug?"
argument_list|)
DECL|class|DistributedSpellCheckComponentTest
specifier|public
class|class
name|DistributedSpellCheckComponentTest
extends|extends
name|BaseDistributedSearchTestCase
block|{
DECL|method|DistributedSpellCheckComponentTest
specifier|public
name|DistributedSpellCheckComponentTest
parameter_list|()
block|{
comment|//Helpful for debugging
comment|//fixShardCount=true;
comment|//shardCount=2;
comment|//stress=0;
block|}
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
name|useFactory
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// need an FS factory
block|}
DECL|method|q
specifier|private
name|void
name|q
parameter_list|(
name|Object
modifier|...
name|q
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|ModifiableSolrParams
name|params
init|=
operator|new
name|ModifiableSolrParams
argument_list|()
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
name|q
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|params
operator|.
name|add
argument_list|(
name|q
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|,
name|q
index|[
name|i
operator|+
literal|1
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|controlClient
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
comment|// query a random server
name|params
operator|.
name|set
argument_list|(
literal|"shards"
argument_list|,
name|shards
argument_list|)
expr_stmt|;
name|int
name|which
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|clients
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|SolrClient
name|client
init|=
name|clients
operator|.
name|get
argument_list|(
name|which
argument_list|)
decl_stmt|;
name|client
operator|.
name|query
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|validateControlData
specifier|public
name|void
name|validateControlData
parameter_list|(
name|QueryResponse
name|control
parameter_list|)
throws|throws
name|Exception
block|{
name|NamedList
argument_list|<
name|Object
argument_list|>
name|nl
init|=
name|control
operator|.
name|getResponse
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|sc
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|nl
operator|.
name|get
argument_list|(
literal|"spellcheck"
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|NamedList
argument_list|<
name|Object
argument_list|>
name|sug
init|=
operator|(
name|NamedList
argument_list|<
name|Object
argument_list|>
operator|)
name|sc
operator|.
name|get
argument_list|(
literal|"suggestions"
argument_list|)
decl_stmt|;
if|if
condition|(
name|sug
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Control data did not return any suggestions."
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|del
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"toyota"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"chevrolet"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"suzuki"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ford"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"ferrari"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"jaguar"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"7"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"mclaren"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"8"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"sonata"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"9"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quick red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"10"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"blue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"12"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"glue"
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"13"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"14"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"15"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"16"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"17"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"18"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"19"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"20"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"21"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"22"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"23"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"24"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quote red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"25"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"The quicker red fox jumped over the lazy brown dogs."
argument_list|)
expr_stmt|;
name|index
argument_list|(
name|id
argument_list|,
literal|"26"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"rod fix"
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
comment|// we care only about the spellcheck results
name|handle
operator|.
name|put
argument_list|(
literal|"response"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
name|handle
operator|.
name|put
argument_list|(
literal|"grouped"
argument_list|,
name|SKIP
argument_list|)
expr_stmt|;
comment|//Randomly select either IndexBasedSpellChecker or DirectSolrSpellChecker
name|String
name|requestHandlerName
init|=
literal|"spellCheckCompRH_Direct"
decl_stmt|;
name|String
name|reqHandlerWithWordbreak
init|=
literal|"spellCheckWithWordbreak_Direct"
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|requestHandlerName
operator|=
literal|"spellCheckCompRH"
expr_stmt|;
name|reqHandlerWithWordbreak
operator|=
literal|"spellCheckWithWordbreak"
expr_stmt|;
block|}
comment|//Shortcut names
name|String
name|build
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_BUILD
decl_stmt|;
name|String
name|extended
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
decl_stmt|;
name|String
name|count
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
decl_stmt|;
name|String
name|collate
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_COLLATE
decl_stmt|;
name|String
name|collateExtended
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
decl_stmt|;
name|String
name|maxCollationTries
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_MAX_COLLATION_TRIES
decl_stmt|;
name|String
name|maxCollations
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_MAX_COLLATIONS
decl_stmt|;
name|String
name|altTermCount
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_ALTERNATIVE_TERM_COUNT
decl_stmt|;
name|String
name|maxResults
init|=
name|SpellingParams
operator|.
name|SPELLCHECK_MAX_RESULTS_FOR_SUGGEST
decl_stmt|;
comment|//Build the dictionary for IndexBasedSpellChecker
name|q
argument_list|(
name|buildRequest
argument_list|(
literal|"*:*"
argument_list|,
literal|false
argument_list|,
literal|"spellCheckCompRH"
argument_list|,
literal|false
argument_list|,
name|build
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test Basic Functionality
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"toyata"
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
operator|(
name|String
index|[]
operator|)
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"toyata"
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"bluo"
argument_list|,
literal|true
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test Collate functionality
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"The quick reb fox jumped over the lazy brown dogs"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"4"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(+quock +reb)"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"10"
argument_list|,
name|maxCollations
argument_list|,
literal|"10"
argument_list|,
name|collateExtended
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(+quock +reb)"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"10"
argument_list|,
name|maxCollations
argument_list|,
literal|"10"
argument_list|,
name|collateExtended
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(+quock +reb)"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"0"
argument_list|,
name|maxCollations
argument_list|,
literal|"1"
argument_list|,
name|collateExtended
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test context-sensitive collate
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(\"quick red fox\")"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"10"
argument_list|,
name|maxCollations
argument_list|,
literal|"1"
argument_list|,
name|collateExtended
argument_list|,
literal|"false"
argument_list|,
name|altTermCount
argument_list|,
literal|"5"
argument_list|,
name|maxResults
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(\"rod fix\")"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"10"
argument_list|,
name|maxCollations
argument_list|,
literal|"1"
argument_list|,
name|collateExtended
argument_list|,
literal|"false"
argument_list|,
name|altTermCount
argument_list|,
literal|"5"
argument_list|,
name|maxResults
argument_list|,
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(\"rod fix\")"
argument_list|,
literal|false
argument_list|,
name|requestHandlerName
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"10"
argument_list|,
name|maxCollations
argument_list|,
literal|"1"
argument_list|,
name|collateExtended
argument_list|,
literal|"false"
argument_list|,
name|altTermCount
argument_list|,
literal|"5"
argument_list|,
name|maxResults
argument_list|,
literal|".10"
argument_list|,
literal|"fq"
argument_list|,
literal|"id:[13 TO 22]"
argument_list|)
argument_list|)
expr_stmt|;
comment|//Test word-break spellchecker
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(+quock +redfox +jum +ped)"
argument_list|,
literal|false
argument_list|,
name|reqHandlerWithWordbreak
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"0"
argument_list|,
name|maxCollations
argument_list|,
literal|"1"
argument_list|,
name|collateExtended
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(+rodfix)"
argument_list|,
literal|false
argument_list|,
name|reqHandlerWithWordbreak
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"0"
argument_list|,
name|maxCollations
argument_list|,
literal|"1"
argument_list|,
name|collateExtended
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
name|query
argument_list|(
name|buildRequest
argument_list|(
literal|"lowerfilt:(+son +ata)"
argument_list|,
literal|false
argument_list|,
name|reqHandlerWithWordbreak
argument_list|,
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
argument_list|,
name|extended
argument_list|,
literal|"true"
argument_list|,
name|count
argument_list|,
literal|"10"
argument_list|,
name|collate
argument_list|,
literal|"true"
argument_list|,
name|maxCollationTries
argument_list|,
literal|"0"
argument_list|,
name|maxCollations
argument_list|,
literal|"1"
argument_list|,
name|collateExtended
argument_list|,
literal|"true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|buildRequest
specifier|private
name|Object
index|[]
name|buildRequest
parameter_list|(
name|String
name|q
parameter_list|,
name|boolean
name|useSpellcheckQ
parameter_list|,
name|String
name|handlerName
parameter_list|,
name|boolean
name|useGrouping
parameter_list|,
name|String
modifier|...
name|addlParams
parameter_list|)
block|{
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|useSpellcheckQ
condition|?
literal|"*:*"
else|:
name|q
argument_list|)
expr_stmt|;
if|if
condition|(
name|useSpellcheckQ
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"spellcheck.q"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
literal|"fl"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"id,lowerfilt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"qt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|handlerName
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"shards.qt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|handlerName
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"spellcheck"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|useGrouping
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"group"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"group.field"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|addlParams
operator|!=
literal|null
condition|)
block|{
name|params
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|addlParams
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|params
operator|.
name|toArray
argument_list|(
operator|new
name|Object
index|[
name|params
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class
end_unit
