begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
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
name|TestUtil
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
name|SuppressCodecs
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
name|request
operator|.
name|SolrQueryRequest
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
name|schema
operator|.
name|SchemaField
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
comment|/**  * This is like TestRandomFaceting, except it does a copyField on each  * indexed field to field_dv, and compares the docvalues facet results  * to the indexed facet results as if it were just another faceting method.  */
end_comment
begin_class
annotation|@
name|Slow
annotation|@
name|SuppressCodecs
argument_list|(
block|{
literal|"Lucene40"
block|,
literal|"Lucene41"
block|,
literal|"Lucene42"
block|}
argument_list|)
DECL|class|TestRandomDVFaceting
specifier|public
class|class
name|TestRandomDVFaceting
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
literal|"solrconfig-basic.xml"
argument_list|,
literal|"schema-docValuesFaceting.xml"
argument_list|)
expr_stmt|;
block|}
DECL|field|indexSize
name|int
name|indexSize
decl_stmt|;
DECL|field|types
name|List
argument_list|<
name|FldType
argument_list|>
name|types
decl_stmt|;
DECL|field|model
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Doc
argument_list|>
name|model
init|=
literal|null
decl_stmt|;
DECL|field|validateResponses
name|boolean
name|validateResponses
init|=
literal|true
decl_stmt|;
DECL|method|init
name|void
name|init
parameter_list|()
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
name|clearIndex
argument_list|()
expr_stmt|;
name|model
operator|=
literal|null
expr_stmt|;
name|indexSize
operator|=
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|?
operator|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1
operator|)
else|:
operator|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|+
literal|10
operator|)
expr_stmt|;
name|types
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"id"
argument_list|,
name|ONE_ONE
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'A'
argument_list|,
literal|'Z'
argument_list|,
literal|4
argument_list|,
literal|4
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"score_f"
argument_list|,
name|ONE_ONE
argument_list|,
operator|new
name|FVal
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"foo_i"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
name|indexSize
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small_s"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
call|(
name|char
call|)
argument_list|(
literal|'c'
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_s"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
call|(
name|char
call|)
argument_list|(
literal|'c'
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_ss"
argument_list|,
name|ZERO_TWO
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
call|(
name|char
call|)
argument_list|(
literal|'c'
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small3_ss"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|25
argument_list|)
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'A'
argument_list|,
literal|'z'
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small4_ss"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
call|(
name|char
call|)
argument_list|(
literal|'c'
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// to test specialization when a multi-valued field is actually single-valued
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small_i"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|5
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_i"
argument_list|,
name|ZERO_ONE
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|5
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small2_is"
argument_list|,
name|ZERO_TWO
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|5
operator|+
name|indexSize
operator|/
literal|3
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"small3_is"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|25
argument_list|)
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"missing_i"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"missing_is"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|100
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"missing_s"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
literal|'b'
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|types
operator|.
name|add
argument_list|(
operator|new
name|FldType
argument_list|(
literal|"missing_ss"
argument_list|,
operator|new
name|IRange
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|SVal
argument_list|(
literal|'a'
argument_list|,
literal|'b'
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// TODO: doubles, multi-floats, ints with precisionStep>0, booleans
block|}
DECL|method|addMoreDocs
name|void
name|addMoreDocs
parameter_list|(
name|int
name|ndocs
parameter_list|)
throws|throws
name|Exception
block|{
name|model
operator|=
name|indexDocs
argument_list|(
name|types
argument_list|,
name|model
argument_list|,
name|ndocs
argument_list|)
expr_stmt|;
block|}
DECL|method|deleteSomeDocs
name|void
name|deleteSomeDocs
parameter_list|()
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|percent
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
decl_stmt|;
if|if
condition|(
name|model
operator|==
literal|null
condition|)
return|return;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|model
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Comparable
name|id
range|:
name|model
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|percent
condition|)
block|{
name|ids
operator|.
name|add
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ids
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"id:("
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|id
range|:
name|ids
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|id
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|model
operator|.
name|remove
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|assertU
argument_list|(
name|delQ
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|0
condition|)
block|{
name|assertU
argument_list|(
name|optimize
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertU
argument_list|(
name|commit
argument_list|(
literal|"softCommit"
argument_list|,
literal|""
operator|+
operator|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|!=
literal|0
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testRandomFaceting
specifier|public
name|void
name|testRandomFaceting
parameter_list|()
throws|throws
name|Exception
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
name|int
name|iter
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|init
argument_list|()
expr_stmt|;
name|addMoreDocs
argument_list|(
literal|0
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|doFacetTests
argument_list|()
expr_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|5
condition|)
block|{
name|init
argument_list|()
expr_stmt|;
block|}
name|addMoreDocs
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|indexSize
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|50
condition|)
block|{
name|deleteSomeDocs
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|doFacetTests
name|void
name|doFacetTests
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|FldType
name|ftype
range|:
name|types
control|)
block|{
name|doFacetTests
argument_list|(
name|ftype
argument_list|)
expr_stmt|;
block|}
block|}
comment|// NOTE: dv is not a "real" facet.method. when we see it, we facet on the dv field (*_dv)
comment|// but alias the result back as if we faceted on the regular indexed field for comparisons.
DECL|field|multiValuedMethods
name|List
argument_list|<
name|String
argument_list|>
name|multiValuedMethods
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"enum"
block|,
literal|"fc"
block|,
literal|"dv"
block|}
argument_list|)
decl_stmt|;
DECL|field|singleValuedMethods
name|List
argument_list|<
name|String
argument_list|>
name|singleValuedMethods
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"enum"
block|,
literal|"fc"
block|,
literal|"fcs"
block|,
literal|"dv"
block|}
argument_list|)
decl_stmt|;
DECL|method|doFacetTests
name|void
name|doFacetTests
parameter_list|(
name|FldType
name|ftype
parameter_list|)
throws|throws
name|Exception
block|{
name|SolrQueryRequest
name|req
init|=
name|req
argument_list|()
decl_stmt|;
try|try
block|{
name|Random
name|rand
init|=
name|random
argument_list|()
decl_stmt|;
name|boolean
name|validate
init|=
name|validateResponses
decl_stmt|;
name|ModifiableSolrParams
name|params
init|=
name|params
argument_list|(
literal|"facet"
argument_list|,
literal|"true"
argument_list|,
literal|"wt"
argument_list|,
literal|"json"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
literal|"omitHeader"
argument_list|,
literal|"true"
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"q"
argument_list|,
literal|"*:*"
argument_list|,
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
comment|// TODO: select subsets
name|params
operator|.
name|add
argument_list|(
literal|"rows"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|SchemaField
name|sf
init|=
name|req
operator|.
name|getSchema
argument_list|()
operator|.
name|getField
argument_list|(
name|ftype
operator|.
name|fname
argument_list|)
decl_stmt|;
name|boolean
name|multiValued
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|multiValuedFieldCache
argument_list|()
decl_stmt|;
name|boolean
name|indexed
init|=
name|sf
operator|.
name|indexed
argument_list|()
decl_stmt|;
name|boolean
name|numeric
init|=
name|sf
operator|.
name|getType
argument_list|()
operator|.
name|getNumericType
argument_list|()
operator|!=
literal|null
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|20
condition|)
block|{
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|offset
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|10
condition|?
name|rand
operator|.
name|nextInt
argument_list|(
name|indexSize
operator|*
literal|2
argument_list|)
else|:
name|rand
operator|.
name|nextInt
argument_list|(
name|indexSize
operator|/
literal|3
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
literal|"facet.offset"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|offset
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|limit
init|=
literal|100
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|20
condition|)
block|{
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|limit
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|10
condition|?
name|rand
operator|.
name|nextInt
argument_list|(
name|indexSize
operator|/
literal|2
operator|+
literal|1
argument_list|)
else|:
name|rand
operator|.
name|nextInt
argument_list|(
name|indexSize
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
name|params
operator|.
name|add
argument_list|(
literal|"facet.limit"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|limit
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// the following two situations cannot work for unindexed single-valued numerics:
comment|// (currently none of the dv fields in this test config)
comment|//     facet.sort = index
comment|//     facet.minCount = 0
if|if
condition|(
operator|!
name|numeric
operator|||
name|sf
operator|.
name|multiValued
argument_list|()
condition|)
block|{
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"facet.sort"
argument_list|,
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|?
literal|"index"
else|:
literal|"count"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rand
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
literal|"facet.mincount"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|params
operator|.
name|add
argument_list|(
literal|"facet.sort"
argument_list|,
literal|"count"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.mincount"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
literal|1
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|ftype
operator|.
name|vals
operator|instanceof
name|SVal
operator|)
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|20
condition|)
block|{
comment|// validate = false;
name|String
name|prefix
init|=
name|ftype
operator|.
name|createValue
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|5
condition|)
name|prefix
operator|=
name|TestUtil
operator|.
name|randomUnicodeString
argument_list|(
name|rand
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|10
condition|)
name|prefix
operator|=
name|Character
operator|.
name|toString
argument_list|(
operator|(
name|char
operator|)
name|rand
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|prefix
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|prefix
operator|=
name|prefix
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"facet.prefix"
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|20
condition|)
block|{
name|params
operator|.
name|add
argument_list|(
literal|"facet.missing"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
comment|// TODO: randomly add other facet params
name|String
name|facet_field
init|=
name|ftype
operator|.
name|fname
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|methods
init|=
name|multiValued
condition|?
name|multiValuedMethods
else|:
name|singleValuedMethods
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|responses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|methods
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|method
range|:
name|methods
control|)
block|{
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"dv"
argument_list|)
condition|)
block|{
name|params
operator|.
name|set
argument_list|(
literal|"facet.field"
argument_list|,
literal|"{!key="
operator|+
name|facet_field
operator|+
literal|"}"
operator|+
name|facet_field
operator|+
literal|"_dv"
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"facet.method"
argument_list|,
operator|(
name|String
operator|)
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|params
operator|.
name|set
argument_list|(
literal|"facet.field"
argument_list|,
name|facet_field
argument_list|)
expr_stmt|;
name|params
operator|.
name|set
argument_list|(
literal|"facet.method"
argument_list|,
name|method
argument_list|)
expr_stmt|;
block|}
comment|// if (random().nextBoolean()) params.set("facet.mincount", "1");  // uncomment to test that validation fails
name|String
name|strResponse
init|=
name|h
operator|.
name|query
argument_list|(
name|req
argument_list|(
name|params
argument_list|)
argument_list|)
decl_stmt|;
comment|// Object realResponse = ObjectBuilder.fromJSON(strResponse);
comment|// System.out.println(strResponse);
name|responses
operator|.
name|add
argument_list|(
name|strResponse
argument_list|)
expr_stmt|;
block|}
comment|/**       String strResponse = h.query(req(params));       Object realResponse = ObjectBuilder.fromJSON(strResponse);       **/
if|if
condition|(
name|validate
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|methods
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|err
init|=
name|JSONTestUtil
operator|.
name|match
argument_list|(
literal|"/"
argument_list|,
name|responses
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|responses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0.0
argument_list|)
decl_stmt|;
if|if
condition|(
name|err
operator|!=
literal|null
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"ERROR: mismatch facet response: "
operator|+
name|err
operator|+
literal|"\n expected ="
operator|+
name|responses
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|+
literal|"\n response = "
operator|+
name|responses
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|+
literal|"\n request = "
operator|+
name|params
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|err
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
finally|finally
block|{
name|req
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
