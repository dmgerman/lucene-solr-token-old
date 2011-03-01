begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.spelling.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
operator|.
name|suggest
package|;
end_package
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
name|RamUsageEstimator
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
name|spelling
operator|.
name|suggest
operator|.
name|Lookup
operator|.
name|LookupResult
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
name|spelling
operator|.
name|suggest
operator|.
name|jaspell
operator|.
name|JaspellLookup
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
name|spelling
operator|.
name|suggest
operator|.
name|tst
operator|.
name|TSTLookup
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
name|TermFreqIterator
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
name|io
operator|.
name|File
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
name|Random
import|;
end_import
begin_class
DECL|class|SuggesterTest
specifier|public
class|class
name|SuggesterTest
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
literal|"solrconfig-spellchecker.xml"
argument_list|,
literal|"schema-spellchecker.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|addDocs
specifier|public
specifier|static
name|void
name|addDocs
parameter_list|()
throws|throws
name|Exception
block|{
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"text"
argument_list|,
literal|"acceptable accidentally accommodate acquire"
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
literal|"text"
argument_list|,
literal|"believe bellwether accommodate acquire"
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
literal|"text"
argument_list|,
literal|"cemetery changeable conscientious consensus acquire bellwether"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSuggestions
specifier|public
name|void
name|testSuggestions
parameter_list|()
throws|throws
name|Exception
block|{
name|addDocs
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
comment|// configured to do a rebuild on commit
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/suggest"
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[1][.='acquire']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[2][.='accommodate']"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReload
specifier|public
name|void
name|testReload
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|leaveData
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|)
decl_stmt|;
if|if
condition|(
name|leaveData
operator|==
literal|null
condition|)
name|leaveData
operator|=
literal|""
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|addDocs
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|File
name|data
init|=
name|dataDir
decl_stmt|;
name|String
name|config
init|=
name|configString
decl_stmt|;
name|deleteCore
argument_list|()
expr_stmt|;
name|dataDir
operator|=
name|data
expr_stmt|;
name|configString
operator|=
name|config
expr_stmt|;
name|initCore
argument_list|()
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/suggest"
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[1][.='acquire']"
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/arr[@name='suggestion']/str[2][.='accommodate']"
argument_list|)
expr_stmt|;
comment|// restore the property
name|System
operator|.
name|setProperty
argument_list|(
literal|"solr.test.leavedatadir"
argument_list|,
name|leaveData
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRebuild
specifier|public
name|void
name|testRebuild
parameter_list|()
throws|throws
name|Exception
block|{
name|addDocs
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/suggest"
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"text"
argument_list|,
literal|"actually"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"qt"
argument_list|,
literal|"/suggest"
argument_list|,
literal|"q"
argument_list|,
literal|"ac"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_COUNT
argument_list|,
literal|"2"
argument_list|,
name|SpellingParams
operator|.
name|SPELLCHECK_ONLY_MORE_POPULAR
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='spellcheck']/lst[@name='suggestions']/lst[@name='ac']/int[@name='numFound'][.='2']"
argument_list|)
expr_stmt|;
block|}
DECL|method|getTFIT
specifier|private
name|TermFreqIterator
name|getTFIT
parameter_list|()
block|{
specifier|final
name|int
name|count
init|=
literal|100000
decl_stmt|;
name|TermFreqIterator
name|tfit
init|=
operator|new
name|TermFreqIterator
argument_list|()
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|(
literal|1234567890L
argument_list|)
decl_stmt|;
name|Random
name|r1
init|=
operator|new
name|Random
argument_list|(
literal|1234567890L
argument_list|)
decl_stmt|;
name|int
name|pos
decl_stmt|;
specifier|public
name|float
name|freq
parameter_list|()
block|{
return|return
name|r1
operator|.
name|nextInt
argument_list|(
literal|4
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|pos
operator|<
name|count
return|;
block|}
specifier|public
name|String
name|next
parameter_list|()
block|{
name|pos
operator|++
expr_stmt|;
return|return
name|Long
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
decl_stmt|;
return|return
name|tfit
return|;
block|}
DECL|method|_benchmark
specifier|private
name|void
name|_benchmark
parameter_list|(
name|Lookup
name|lookup
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ref
parameter_list|,
name|boolean
name|estimate
parameter_list|,
name|Bench
name|bench
parameter_list|)
throws|throws
name|Exception
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|lookup
operator|.
name|build
argument_list|(
name|getTFIT
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|buildTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
decl_stmt|;
name|TermFreqIterator
name|tfit
init|=
name|getTFIT
argument_list|()
decl_stmt|;
name|long
name|elapsed
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|tfit
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|key
init|=
name|tfit
operator|.
name|next
argument_list|()
decl_stmt|;
comment|// take only the first part of the key
name|int
name|len
init|=
name|key
operator|.
name|length
argument_list|()
operator|>
literal|4
condition|?
name|key
operator|.
name|length
argument_list|()
operator|/
literal|3
else|:
literal|2
decl_stmt|;
name|String
name|prefix
init|=
name|key
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|len
argument_list|)
decl_stmt|;
name|start
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|LookupResult
argument_list|>
name|res
init|=
name|lookup
operator|.
name|lookup
argument_list|(
name|prefix
argument_list|,
literal|true
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|elapsed
operator|+=
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|start
expr_stmt|;
name|assertTrue
argument_list|(
name|res
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|LookupResult
name|lr
range|:
name|res
control|)
block|{
name|assertTrue
argument_list|(
name|lr
operator|.
name|key
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|ref
operator|!=
literal|null
condition|)
block|{
comment|// verify the counts
name|Integer
name|Cnt
init|=
name|ref
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|Cnt
operator|==
literal|null
condition|)
block|{
comment|// first pass
name|ref
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|res
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|key
operator|+
literal|", prefix: "
operator|+
name|prefix
argument_list|,
name|Cnt
operator|.
name|intValue
argument_list|()
argument_list|,
name|res
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|estimate
condition|)
block|{
name|RamUsageEstimator
name|rue
init|=
operator|new
name|RamUsageEstimator
argument_list|()
decl_stmt|;
name|long
name|size
init|=
name|rue
operator|.
name|estimateRamUsage
argument_list|(
name|lookup
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|lookup
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" - size="
operator|+
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|bench
operator|!=
literal|null
condition|)
block|{
name|bench
operator|.
name|buildTime
operator|+=
name|buildTime
expr_stmt|;
name|bench
operator|.
name|lookupTime
operator|+=
name|elapsed
expr_stmt|;
block|}
block|}
DECL|class|Bench
class|class
name|Bench
block|{
DECL|field|buildTime
name|long
name|buildTime
decl_stmt|;
DECL|field|lookupTime
name|long
name|lookupTime
decl_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBenchmark
specifier|public
name|void
name|testBenchmark
parameter_list|()
throws|throws
name|Exception
block|{
comment|// this benchmark is very time consuming
name|boolean
name|doTest
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|doTest
condition|)
block|{
return|return;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|ref
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|JaspellLookup
name|jaspell
init|=
operator|new
name|JaspellLookup
argument_list|()
decl_stmt|;
name|TSTLookup
name|tst
init|=
operator|new
name|TSTLookup
argument_list|()
decl_stmt|;
name|_benchmark
argument_list|(
name|tst
argument_list|,
name|ref
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|_benchmark
argument_list|(
name|jaspell
argument_list|,
name|ref
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|jaspell
operator|=
literal|null
expr_stmt|;
name|tst
operator|=
literal|null
expr_stmt|;
name|int
name|count
init|=
literal|100
decl_stmt|;
name|Bench
name|b
init|=
name|runBenchmark
argument_list|(
name|JaspellLookup
operator|.
name|class
argument_list|,
name|count
argument_list|)
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|JaspellLookup
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": buildTime[ms]="
operator|+
operator|(
name|b
operator|.
name|buildTime
operator|/
name|count
operator|)
operator|+
literal|" lookupTime[ms]="
operator|+
operator|(
name|b
operator|.
name|lookupTime
operator|/
name|count
operator|/
literal|1000000
operator|)
argument_list|)
expr_stmt|;
name|b
operator|=
name|runBenchmark
argument_list|(
name|TSTLookup
operator|.
name|class
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|TSTLookup
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|": buildTime[ms]="
operator|+
operator|(
name|b
operator|.
name|buildTime
operator|/
name|count
operator|)
operator|+
literal|" lookupTime[ms]="
operator|+
operator|(
name|b
operator|.
name|lookupTime
operator|/
name|count
operator|/
literal|1000000
operator|)
argument_list|)
expr_stmt|;
block|}
DECL|method|runBenchmark
specifier|private
name|Bench
name|runBenchmark
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Lookup
argument_list|>
name|cls
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|Exception
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"* Running "
operator|+
name|count
operator|+
literal|" iterations for "
operator|+
name|cls
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" ..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"  - warm-up 10 iterations..."
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
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|Lookup
name|lookup
init|=
name|cls
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|_benchmark
argument_list|(
name|lookup
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|lookup
operator|=
literal|null
expr_stmt|;
block|}
name|Bench
name|b
init|=
operator|new
name|Bench
argument_list|()
decl_stmt|;
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|"  - main iterations:"
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|runFinalization
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|Lookup
name|lookup
init|=
name|cls
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|_benchmark
argument_list|(
name|lookup
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|b
argument_list|)
expr_stmt|;
name|lookup
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
operator|(
name|i
operator|%
literal|10
operator|==
literal|0
operator|)
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|print
argument_list|(
literal|" "
operator|+
name|i
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
name|System
operator|.
name|err
operator|.
name|println
argument_list|()
expr_stmt|;
return|return
name|b
return|;
block|}
block|}
end_class
end_unit
