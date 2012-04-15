begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
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
name|OpenBitSet
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
name|request
operator|.
name|SolrQueryRequest
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
name|*
import|;
end_import
begin_class
DECL|class|TestFiltering
specifier|public
class|class
name|TestFiltering
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeTests
specifier|public
specifier|static
name|void
name|beforeTests
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig.xml"
argument_list|,
literal|"schema12.xml"
argument_list|)
expr_stmt|;
block|}
DECL|method|testCaching
specifier|public
name|void
name|testCaching
parameter_list|()
throws|throws
name|Exception
block|{
name|clearIndex
argument_list|()
expr_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"val_i"
argument_list|,
literal|"1"
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
literal|"val_i"
argument_list|,
literal|"2"
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
literal|"val_i"
argument_list|,
literal|"3"
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
literal|"val_i"
argument_list|,
literal|"4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|prevCount
decl_stmt|;
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=3 cache=false cost=100}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// The exact same query the second time will be cached by the queryCache
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=3 cache=false cost=100}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==2"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// cache is true by default
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=4}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// default cost avoids post filtering
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=5 cache=false}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// now re-do the same tests w/ faceting on to get the full docset
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=6 cache=false cost=100}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// since we need the docset and the filter was not cached, the collector will need to be used again
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=6 cache=false cost=100}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// cache is true by default
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=7}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// default cost avoids post filtering
name|prevCount
operator|=
name|DelegatingCollector
operator|.
name|setLastDelegateCount
expr_stmt|;
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"facet.field"
argument_list|,
literal|"id"
argument_list|,
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"fq"
argument_list|,
literal|"{!frange l=2 u=8 cache=false}val_i"
argument_list|)
argument_list|,
literal|"/response/numFound==3"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|DelegatingCollector
operator|.
name|setLastDelegateCount
operator|-
name|prevCount
argument_list|)
expr_stmt|;
comment|// test that offset works when not caching main query
name|assertJQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"{!cache=false}*:*"
argument_list|,
literal|"start"
argument_list|,
literal|"2"
argument_list|,
literal|"rows"
argument_list|,
literal|"1"
argument_list|,
literal|"sort"
argument_list|,
literal|"val_i asc"
argument_list|,
literal|"fl"
argument_list|,
literal|"val_i"
argument_list|)
argument_list|,
literal|"/response/docs==[{'val_i':3}]"
argument_list|)
expr_stmt|;
block|}
DECL|class|Model
class|class
name|Model
block|{
DECL|field|indexSize
name|int
name|indexSize
decl_stmt|;
DECL|field|answer
name|OpenBitSet
name|answer
decl_stmt|;
DECL|field|multiSelect
name|OpenBitSet
name|multiSelect
decl_stmt|;
DECL|field|facetQuery
name|OpenBitSet
name|facetQuery
decl_stmt|;
DECL|method|clear
name|void
name|clear
parameter_list|()
block|{
name|answer
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|indexSize
argument_list|)
expr_stmt|;
name|answer
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|indexSize
argument_list|)
expr_stmt|;
name|multiSelect
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|indexSize
argument_list|)
expr_stmt|;
name|multiSelect
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|indexSize
argument_list|)
expr_stmt|;
name|facetQuery
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|indexSize
argument_list|)
expr_stmt|;
name|facetQuery
operator|.
name|set
argument_list|(
literal|0
argument_list|,
name|indexSize
argument_list|)
expr_stmt|;
block|}
block|}
DECL|field|f
specifier|static
name|String
name|f
init|=
literal|"val_i"
decl_stmt|;
DECL|method|frangeStr
name|String
name|frangeStr
parameter_list|(
name|boolean
name|negative
parameter_list|,
name|int
name|l
parameter_list|,
name|int
name|u
parameter_list|,
name|boolean
name|cache
parameter_list|,
name|int
name|cost
parameter_list|,
name|boolean
name|exclude
parameter_list|)
block|{
name|String
name|topLev
init|=
literal|""
decl_stmt|;
if|if
condition|(
operator|!
name|cache
operator|||
name|exclude
condition|)
block|{
name|topLev
operator|=
literal|""
operator|+
operator|(
name|cache
operator|||
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|" cache="
operator|+
name|cache
else|:
literal|""
operator|)
operator|+
operator|(
name|cost
operator|!=
literal|0
condition|?
literal|" cost="
operator|+
name|cost
else|:
literal|""
operator|)
operator|+
operator|(
operator|(
name|exclude
operator|)
condition|?
literal|" tag=t"
else|:
literal|""
operator|)
expr_stmt|;
block|}
name|String
name|ret
init|=
literal|"{!frange v="
operator|+
name|f
operator|+
literal|" l="
operator|+
name|l
operator|+
literal|" u="
operator|+
name|u
decl_stmt|;
if|if
condition|(
name|negative
condition|)
block|{
name|ret
operator|=
literal|"-_query_:\""
operator|+
name|ret
operator|+
literal|"}\""
expr_stmt|;
if|if
condition|(
name|topLev
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|ret
operator|=
literal|"{!"
operator|+
name|topLev
operator|+
literal|"}"
operator|+
name|ret
expr_stmt|;
comment|// add options at top level (can't be on frange)
block|}
block|}
else|else
block|{
name|ret
operator|+=
name|topLev
operator|+
literal|"}"
expr_stmt|;
comment|// add options right to frange
block|}
return|return
name|ret
return|;
block|}
DECL|method|makeRandomQuery
name|String
name|makeRandomQuery
parameter_list|(
name|Model
name|model
parameter_list|,
name|boolean
name|mainQuery
parameter_list|,
name|boolean
name|facetQuery
parameter_list|)
block|{
name|boolean
name|cache
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|int
name|cost
init|=
name|cache
condition|?
literal|0
else|:
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|?
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|200
argument_list|)
else|:
literal|0
decl_stmt|;
name|boolean
name|positive
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|boolean
name|exclude
init|=
name|facetQuery
condition|?
literal|false
else|:
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
comment|// can't exclude a facet query from faceting
name|OpenBitSet
index|[]
name|sets
init|=
name|facetQuery
condition|?
operator|new
name|OpenBitSet
index|[]
block|{
name|model
operator|.
name|facetQuery
block|}
else|:
operator|(
name|exclude
condition|?
operator|new
name|OpenBitSet
index|[]
block|{
name|model
operator|.
name|answer
block|,
name|model
operator|.
name|facetQuery
block|}
else|:
operator|new
name|OpenBitSet
index|[]
block|{
name|model
operator|.
name|answer
block|,
name|model
operator|.
name|multiSelect
block|,
name|model
operator|.
name|facetQuery
block|}
operator|)
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|50
condition|)
block|{
comment|// frange
name|int
name|l
init|=
literal|0
decl_stmt|;
name|int
name|u
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|positive
condition|)
block|{
comment|// positive frange, make it big by taking the max of 4 tries
name|int
name|n
init|=
operator|-
literal|1
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
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|int
name|ll
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|model
operator|.
name|indexSize
argument_list|)
decl_stmt|;
name|int
name|uu
init|=
name|ll
operator|+
operator|(
operator|(
name|ll
operator|==
name|model
operator|.
name|indexSize
operator|-
literal|1
operator|)
condition|?
literal|0
else|:
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|model
operator|.
name|indexSize
operator|-
name|l
argument_list|)
operator|)
decl_stmt|;
if|if
condition|(
name|uu
operator|-
name|ll
operator|+
literal|1
operator|>
name|n
condition|)
block|{
name|n
operator|=
name|uu
operator|-
name|ll
operator|+
literal|1
expr_stmt|;
name|u
operator|=
name|uu
expr_stmt|;
name|l
operator|=
name|ll
expr_stmt|;
block|}
block|}
for|for
control|(
name|OpenBitSet
name|set
range|:
name|sets
control|)
block|{
name|set
operator|.
name|clear
argument_list|(
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
name|set
operator|.
name|clear
argument_list|(
name|u
operator|+
literal|1
argument_list|,
name|model
operator|.
name|indexSize
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// negative frange.. make it relatively small
name|l
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|model
operator|.
name|indexSize
argument_list|)
expr_stmt|;
name|u
operator|=
name|Math
operator|.
name|max
argument_list|(
name|model
operator|.
name|indexSize
operator|-
literal|1
argument_list|,
name|l
operator|+
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|model
operator|.
name|indexSize
operator|/
literal|10
argument_list|,
literal|2
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|OpenBitSet
name|set
range|:
name|sets
control|)
block|{
name|set
operator|.
name|clear
argument_list|(
name|l
argument_list|,
name|u
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|frangeStr
argument_list|(
operator|!
name|positive
argument_list|,
name|l
argument_list|,
name|u
argument_list|,
name|cache
argument_list|,
name|cost
argument_list|,
name|exclude
argument_list|)
return|;
block|}
else|else
block|{
comment|// term or boolean query
name|OpenBitSet
name|pset
init|=
operator|new
name|OpenBitSet
argument_list|(
name|model
operator|.
name|indexSize
argument_list|)
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
name|pset
operator|.
name|getBits
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|pset
operator|.
name|getBits
argument_list|()
index|[
name|i
index|]
operator|=
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
expr_stmt|;
comment|// set 50% of the bits on average
block|}
if|if
condition|(
name|positive
condition|)
block|{
for|for
control|(
name|OpenBitSet
name|set
range|:
name|sets
control|)
block|{
name|set
operator|.
name|and
argument_list|(
name|pset
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
for|for
control|(
name|OpenBitSet
name|set
range|:
name|sets
control|)
block|{
name|set
operator|.
name|andNot
argument_list|(
name|pset
argument_list|)
expr_stmt|;
block|}
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|doc
init|=
operator|-
literal|1
init|;
condition|;
control|)
block|{
name|doc
operator|=
name|pset
operator|.
name|nextSetBit
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|<
literal|0
operator|||
name|doc
operator|>=
name|model
operator|.
name|indexSize
condition|)
break|break;
name|sb
operator|.
name|append
argument_list|(
operator|(
name|positive
condition|?
literal|" "
else|:
literal|" -"
operator|)
operator|+
name|f
operator|+
literal|":"
operator|+
name|doc
argument_list|)
expr_stmt|;
block|}
name|String
name|ret
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|ret
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|ret
operator|=
operator|(
name|positive
condition|?
literal|""
else|:
literal|"-"
operator|)
operator|+
literal|"id:99999999"
expr_stmt|;
if|if
condition|(
operator|!
name|cache
operator|||
name|exclude
operator|||
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|ret
operator|=
literal|"{!cache="
operator|+
name|cache
operator|+
operator|(
operator|(
name|cost
operator|!=
literal|0
operator|)
condition|?
literal|" cost="
operator|+
name|cost
else|:
literal|""
operator|)
operator|+
operator|(
operator|(
name|exclude
operator|)
condition|?
literal|" tag=t"
else|:
literal|""
operator|)
operator|+
literal|"}"
operator|+
name|ret
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRandomFiltering
specifier|public
name|void
name|testRandomFiltering
parameter_list|()
throws|throws
name|Exception
block|{
name|int
name|indexIter
init|=
literal|5
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|int
name|queryIter
init|=
literal|250
operator|*
name|RANDOM_MULTIPLIER
decl_stmt|;
name|Model
name|model
init|=
operator|new
name|Model
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|iiter
init|=
literal|0
init|;
name|iiter
operator|<
name|indexIter
condition|;
name|iiter
operator|++
control|)
block|{
name|model
operator|.
name|indexSize
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
operator|*
name|RANDOM_MULTIPLIER
argument_list|)
operator|+
literal|1
expr_stmt|;
name|clearIndex
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
name|model
operator|.
name|indexSize
condition|;
name|i
operator|++
control|)
block|{
name|String
name|val
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|val
argument_list|,
name|f
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|20
condition|)
block|{
comment|// duplicate doc 20% of the time (makes deletions)
name|assertU
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
name|val
argument_list|,
name|f
argument_list|,
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|10
condition|)
block|{
comment|// commit 10% of the time (forces a new segment)
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertU
argument_list|(
name|commit
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|totalMatches
init|=
literal|0
decl_stmt|;
name|int
name|nonZeros
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|qiter
init|=
literal|0
init|;
name|qiter
operator|<
name|queryIter
condition|;
name|qiter
operator|++
control|)
block|{
name|model
operator|.
name|clear
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
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
name|makeRandomQuery
argument_list|(
name|model
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|int
name|nFilters
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
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
name|nFilters
condition|;
name|i
operator|++
control|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"fq"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|makeRandomQuery
argument_list|(
name|model
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|facet
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|facet
condition|)
block|{
comment|// basic facet.query tests getDocListAndSet
name|params
operator|.
name|add
argument_list|(
literal|"facet"
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
literal|"facet.query"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.query"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"{!key=multiSelect ex=t}*:*"
argument_list|)
expr_stmt|;
name|String
name|facetQuery
init|=
name|makeRandomQuery
argument_list|(
name|model
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetQuery
operator|.
name|startsWith
argument_list|(
literal|"{!"
argument_list|)
condition|)
block|{
name|facetQuery
operator|=
literal|"{!key=facetQuery "
operator|+
name|facetQuery
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|facetQuery
operator|=
literal|"{!key=facetQuery}"
operator|+
name|facetQuery
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
literal|"facet.query"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|facetQuery
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|10
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
literal|"group.main"
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
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"group.cache.percent"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"100"
argument_list|)
expr_stmt|;
block|}
block|}
name|SolrQueryRequest
name|sreq
init|=
name|req
argument_list|(
name|params
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|params
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|expected
init|=
name|model
operator|.
name|answer
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|long
name|expectedMultiSelect
init|=
name|model
operator|.
name|multiSelect
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|long
name|expectedFacetQuery
init|=
name|model
operator|.
name|facetQuery
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|totalMatches
operator|+=
name|expected
expr_stmt|;
if|if
condition|(
name|expected
operator|>
literal|0
condition|)
block|{
name|nonZeros
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|iiter
operator|==
operator|-
literal|1
operator|&&
name|qiter
operator|==
operator|-
literal|1
condition|)
block|{
comment|// set breakpoint here to debug a specific issue
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"request="
operator|+
name|params
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|assertJQ
argument_list|(
name|sreq
argument_list|,
literal|"/response/numFound=="
operator|+
name|expected
argument_list|,
name|facet
condition|?
literal|"/facet_counts/facet_queries/*:*/=="
operator|+
name|expected
else|:
literal|null
argument_list|,
name|facet
condition|?
literal|"/facet_counts/facet_queries/multiSelect/=="
operator|+
name|expectedMultiSelect
else|:
literal|null
argument_list|,
name|facet
condition|?
literal|"/facet_counts/facet_queries/facetQuery/=="
operator|+
name|expectedFacetQuery
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// show the indexIter and queryIter for easier debugging
name|SolrException
operator|.
name|log
argument_list|(
name|log
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|String
name|s
init|=
literal|"FAILURE: iiter="
operator|+
name|iiter
operator|+
literal|" qiter="
operator|+
name|qiter
operator|+
literal|" request="
operator|+
name|params
decl_stmt|;
name|log
operator|.
name|error
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
comment|// After making substantial changes to this test, make sure that we still get a
comment|// decent number of queries that match some documents
comment|// System.out.println("totalMatches=" + totalMatches + " nonZeroQueries="+nonZeros);
block|}
block|}
block|}
end_class
end_unit
