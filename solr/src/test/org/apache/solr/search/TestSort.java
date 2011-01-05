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
name|analysis
operator|.
name|core
operator|.
name|SimpleAnalyzer
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
name|document
operator|.
name|Document
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
name|document
operator|.
name|Field
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
name|index
operator|.
name|IndexReader
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
name|index
operator|.
name|IndexReader
operator|.
name|ReaderContext
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|IndexWriterConfig
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
name|lucene
operator|.
name|store
operator|.
name|Directory
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
name|store
operator|.
name|RAMDirectory
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
name|util
operator|.
name|AbstractSolrTestCase
import|;
end_import
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
name|*
import|;
end_import
begin_class
DECL|class|TestSort
specifier|public
class|class
name|TestSort
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
literal|null
return|;
block|}
DECL|method|getSolrConfigFile
specifier|public
name|String
name|getSolrConfigFile
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|field|r
name|Random
name|r
init|=
name|random
decl_stmt|;
DECL|field|ndocs
name|int
name|ndocs
init|=
literal|77
decl_stmt|;
DECL|field|iter
name|int
name|iter
init|=
literal|50
decl_stmt|;
DECL|field|qiter
name|int
name|qiter
init|=
literal|1000
decl_stmt|;
DECL|field|commitCount
name|int
name|commitCount
init|=
name|ndocs
operator|/
literal|5
operator|+
literal|1
decl_stmt|;
DECL|field|maxval
name|int
name|maxval
init|=
name|ndocs
operator|*
literal|2
decl_stmt|;
DECL|class|MyDoc
specifier|static
class|class
name|MyDoc
block|{
DECL|field|doc
name|int
name|doc
decl_stmt|;
DECL|field|val
name|String
name|val
decl_stmt|;
DECL|field|val2
name|String
name|val2
decl_stmt|;
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{id="
operator|+
name|doc
operator|+
literal|" val1="
operator|+
name|val
operator|+
literal|" val2="
operator|+
name|val2
operator|+
literal|"}"
return|;
block|}
block|}
DECL|method|testSort
specifier|public
name|void
name|testSort
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
operator|new
name|RAMDirectory
argument_list|()
decl_stmt|;
name|Field
name|f
init|=
operator|new
name|Field
argument_list|(
literal|"f"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
decl_stmt|;
name|Field
name|f2
init|=
operator|new
name|Field
argument_list|(
literal|"f2"
argument_list|,
literal|"0"
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED_NO_NORMS
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iterCnt
init|=
literal|0
init|;
name|iterCnt
operator|<
name|iter
condition|;
name|iterCnt
operator|++
control|)
block|{
name|IndexWriter
name|iw
init|=
operator|new
name|IndexWriter
argument_list|(
name|dir
argument_list|,
operator|new
name|IndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|SimpleAnalyzer
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|)
argument_list|)
operator|.
name|setOpenMode
argument_list|(
name|IndexWriterConfig
operator|.
name|OpenMode
operator|.
name|CREATE
argument_list|)
operator|.
name|setMaxFieldLength
argument_list|(
name|IndexWriterConfig
operator|.
name|UNLIMITED_FIELD_LENGTH
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|MyDoc
index|[]
name|mydocs
init|=
operator|new
name|MyDoc
index|[
name|ndocs
index|]
decl_stmt|;
name|int
name|v1EmptyPercent
init|=
literal|50
decl_stmt|;
name|int
name|v2EmptyPercent
init|=
literal|50
decl_stmt|;
name|int
name|commitCountdown
init|=
name|commitCount
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
name|ndocs
condition|;
name|i
operator|++
control|)
block|{
name|MyDoc
name|mydoc
init|=
operator|new
name|MyDoc
argument_list|()
decl_stmt|;
name|mydoc
operator|.
name|doc
operator|=
name|i
expr_stmt|;
name|mydocs
index|[
name|i
index|]
operator|=
name|mydoc
expr_stmt|;
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|v1EmptyPercent
condition|)
block|{
name|mydoc
operator|.
name|val
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|maxval
argument_list|)
argument_list|)
expr_stmt|;
name|f
operator|.
name|setValue
argument_list|(
name|mydoc
operator|.
name|val
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
name|v2EmptyPercent
condition|)
block|{
name|mydoc
operator|.
name|val2
operator|=
name|Integer
operator|.
name|toString
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|maxval
argument_list|)
argument_list|)
expr_stmt|;
name|f2
operator|.
name|setValue
argument_list|(
name|mydoc
operator|.
name|val2
argument_list|)
expr_stmt|;
name|document
operator|.
name|add
argument_list|(
name|f2
argument_list|)
expr_stmt|;
block|}
name|iw
operator|.
name|addDocument
argument_list|(
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
operator|--
name|commitCountdown
operator|<=
literal|0
condition|)
block|{
name|commitCountdown
operator|=
name|commitCount
expr_stmt|;
name|iw
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
name|iw
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// System.out.println("segments="+searcher.getIndexReader().getSequentialSubReaders().length);
name|assertTrue
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|getSequentialSubReaders
argument_list|()
operator|.
name|length
operator|>
literal|1
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
name|qiter
condition|;
name|i
operator|++
control|)
block|{
name|Filter
name|filt
init|=
operator|new
name|Filter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|ReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|randSet
argument_list|(
name|context
operator|.
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|int
name|top
init|=
name|r
operator|.
name|nextInt
argument_list|(
operator|(
name|ndocs
operator|>>
literal|3
operator|)
operator|+
literal|1
argument_list|)
operator|+
literal|1
decl_stmt|;
specifier|final
name|boolean
name|luceneSort
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingLast
init|=
operator|!
name|luceneSort
operator|&&
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingFirst
init|=
operator|!
name|luceneSort
operator|&&
operator|!
name|sortMissingLast
decl_stmt|;
specifier|final
name|boolean
name|reverse
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SortField
argument_list|>
name|sfields
init|=
operator|new
name|ArrayList
argument_list|<
name|SortField
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|secondary
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|luceneSort2
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingLast2
init|=
operator|!
name|luceneSort2
operator|&&
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|sortMissingFirst2
init|=
operator|!
name|luceneSort2
operator|&&
operator|!
name|sortMissingLast2
decl_stmt|;
specifier|final
name|boolean
name|reverse2
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
name|sfields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
comment|// hit both use-cases of sort-missing-last
name|sfields
operator|.
name|add
argument_list|(
name|Sorting
operator|.
name|getStringSortField
argument_list|(
literal|"f"
argument_list|,
name|reverse
argument_list|,
name|sortMissingLast
argument_list|,
name|sortMissingFirst
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|secondary
condition|)
block|{
name|sfields
operator|.
name|add
argument_list|(
name|Sorting
operator|.
name|getStringSortField
argument_list|(
literal|"f2"
argument_list|,
name|reverse2
argument_list|,
name|sortMissingLast2
argument_list|,
name|sortMissingFirst2
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|r
operator|.
name|nextBoolean
argument_list|()
condition|)
name|sfields
operator|.
name|add
argument_list|(
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|SCORE
argument_list|)
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
name|sfields
operator|.
name|toArray
argument_list|(
operator|new
name|SortField
index|[
name|sfields
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|nullRep
init|=
name|luceneSort
operator|||
name|sortMissingFirst
operator|&&
operator|!
name|reverse
operator|||
name|sortMissingLast
operator|&&
name|reverse
condition|?
literal|""
else|:
literal|"zzz"
decl_stmt|;
specifier|final
name|String
name|nullRep2
init|=
name|luceneSort2
operator|||
name|sortMissingFirst2
operator|&&
operator|!
name|reverse2
operator|||
name|sortMissingLast2
operator|&&
name|reverse2
condition|?
literal|""
else|:
literal|"zzz"
decl_stmt|;
name|boolean
name|trackScores
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|boolean
name|trackMaxScores
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|boolean
name|scoreInOrder
init|=
name|r
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
specifier|final
name|TopFieldCollector
name|topCollector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
name|top
argument_list|,
literal|true
argument_list|,
name|trackScores
argument_list|,
name|trackMaxScores
argument_list|,
name|scoreInOrder
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|MyDoc
argument_list|>
name|collectedDocs
init|=
operator|new
name|ArrayList
argument_list|<
name|MyDoc
argument_list|>
argument_list|()
decl_stmt|;
comment|// delegate and collect docs ourselves
name|Collector
name|myCollector
init|=
operator|new
name|Collector
argument_list|()
block|{
name|int
name|docBase
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|topCollector
operator|.
name|setScorer
argument_list|(
name|scorer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
throws|throws
name|IOException
block|{
name|topCollector
operator|.
name|collect
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|collectedDocs
operator|.
name|add
argument_list|(
name|mydocs
index|[
name|doc
operator|+
name|docBase
index|]
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docBase
parameter_list|)
throws|throws
name|IOException
block|{
name|topCollector
operator|.
name|setNextReader
argument_list|(
name|reader
argument_list|,
name|docBase
argument_list|)
expr_stmt|;
name|this
operator|.
name|docBase
operator|=
name|docBase
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
name|topCollector
operator|.
name|acceptsDocsOutOfOrder
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
operator|new
name|MatchAllDocsQuery
argument_list|()
argument_list|,
name|filt
argument_list|,
name|myCollector
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|collectedDocs
argument_list|,
operator|new
name|Comparator
argument_list|<
name|MyDoc
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|MyDoc
name|o1
parameter_list|,
name|MyDoc
name|o2
parameter_list|)
block|{
name|String
name|v1
init|=
name|o1
operator|.
name|val
operator|==
literal|null
condition|?
name|nullRep
else|:
name|o1
operator|.
name|val
decl_stmt|;
name|String
name|v2
init|=
name|o2
operator|.
name|val
operator|==
literal|null
condition|?
name|nullRep
else|:
name|o2
operator|.
name|val
decl_stmt|;
name|int
name|cmp
init|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
decl_stmt|;
if|if
condition|(
name|reverse
condition|)
name|cmp
operator|=
operator|-
name|cmp
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
return|return
name|cmp
return|;
if|if
condition|(
name|secondary
condition|)
block|{
name|v1
operator|=
name|o1
operator|.
name|val2
operator|==
literal|null
condition|?
name|nullRep2
else|:
name|o1
operator|.
name|val2
expr_stmt|;
name|v2
operator|=
name|o2
operator|.
name|val2
operator|==
literal|null
condition|?
name|nullRep2
else|:
name|o2
operator|.
name|val2
expr_stmt|;
name|cmp
operator|=
name|v1
operator|.
name|compareTo
argument_list|(
name|v2
argument_list|)
expr_stmt|;
if|if
condition|(
name|reverse2
condition|)
name|cmp
operator|=
operator|-
name|cmp
expr_stmt|;
block|}
name|cmp
operator|=
name|cmp
operator|==
literal|0
condition|?
name|o1
operator|.
name|doc
operator|-
name|o2
operator|.
name|doc
else|:
name|cmp
expr_stmt|;
return|return
name|cmp
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|topCollector
operator|.
name|topDocs
argument_list|()
decl_stmt|;
name|ScoreDoc
index|[]
name|sdocs
init|=
name|topDocs
operator|.
name|scoreDocs
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|sdocs
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|id
init|=
name|sdocs
index|[
name|j
index|]
operator|.
name|doc
decl_stmt|;
if|if
condition|(
name|id
operator|!=
name|collectedDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|doc
condition|)
block|{
name|log
operator|.
name|error
argument_list|(
literal|"Error at pos "
operator|+
name|j
operator|+
literal|"\n\tsortMissingFirst="
operator|+
name|sortMissingFirst
operator|+
literal|" sortMissingLast="
operator|+
name|sortMissingLast
operator|+
literal|" reverse="
operator|+
name|reverse
operator|+
literal|"\n\tEXPECTED="
operator|+
name|collectedDocs
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|id
argument_list|,
name|collectedDocs
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
name|searcher
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|randSet
specifier|public
name|DocIdSet
name|randSet
parameter_list|(
name|int
name|sz
parameter_list|)
block|{
name|OpenBitSet
name|obs
init|=
operator|new
name|OpenBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
name|int
name|n
init|=
name|r
operator|.
name|nextInt
argument_list|(
name|sz
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
name|n
condition|;
name|i
operator|++
control|)
block|{
name|obs
operator|.
name|fastSet
argument_list|(
name|r
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|obs
return|;
block|}
block|}
end_class
end_unit
