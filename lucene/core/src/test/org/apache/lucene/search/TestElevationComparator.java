begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
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
name|MockAnalyzer
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
name|document
operator|.
name|SortedDocValuesField
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
name|search
operator|.
name|FieldValueHitQueue
operator|.
name|Entry
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
name|similarities
operator|.
name|ClassicSimilarity
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
name|util
operator|.
name|LuceneTestCase
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
name|BytesRef
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
name|HashMap
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
begin_class
DECL|class|TestElevationComparator
specifier|public
class|class
name|TestElevationComparator
extends|extends
name|LuceneTestCase
block|{
DECL|field|priority
specifier|private
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|priority
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|//@Test
DECL|method|testSorting
specifier|public
name|void
name|testSorting
parameter_list|()
throws|throws
name|Throwable
block|{
name|Directory
name|directory
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
literal|2
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|(
literal|1000
argument_list|)
argument_list|)
operator|.
name|setSimilarity
argument_list|(
operator|new
name|ClassicSimilarity
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|adoc
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"a"
block|,
literal|"title"
block|,
literal|"ipod"
block|,
literal|"str_s"
block|,
literal|"a"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|adoc
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"b"
block|,
literal|"title"
block|,
literal|"ipod ipod"
block|,
literal|"str_s"
block|,
literal|"b"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|adoc
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"c"
block|,
literal|"title"
block|,
literal|"ipod ipod ipod"
block|,
literal|"str_s"
block|,
literal|"c"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|adoc
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"x"
block|,
literal|"title"
block|,
literal|"boosted"
block|,
literal|"str_s"
block|,
literal|"x"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|adoc
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"y"
block|,
literal|"title"
block|,
literal|"boosted boosted"
block|,
literal|"str_s"
block|,
literal|"y"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|adoc
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"z"
block|,
literal|"title"
block|,
literal|"boosted boosted boosted"
block|,
literal|"str_s"
block|,
literal|"z"
block|}
argument_list|)
argument_list|)
expr_stmt|;
name|IndexReader
name|r
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|writer
argument_list|)
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexSearcher
name|searcher
init|=
name|newSearcher
argument_list|(
name|r
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|setSimilarity
argument_list|(
operator|new
name|ClassicSimilarity
argument_list|()
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|searcher
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|runTest
argument_list|(
name|searcher
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|r
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|runTest
specifier|private
name|void
name|runTest
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|Throwable
block|{
name|BooleanQuery
operator|.
name|Builder
name|newq
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"title"
argument_list|,
literal|"ipod"
argument_list|)
argument_list|)
decl_stmt|;
name|newq
operator|.
name|add
argument_list|(
name|query
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|newq
operator|.
name|add
argument_list|(
name|getElevatedQuery
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"a"
block|,
literal|"id"
block|,
literal|"x"
block|}
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"id"
argument_list|,
operator|new
name|ElevationComparatorSource
argument_list|(
name|priority
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|,
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|,
name|reversed
argument_list|)
argument_list|)
decl_stmt|;
name|TopDocsCollector
argument_list|<
name|Entry
argument_list|>
name|topCollector
init|=
name|TopFieldCollector
operator|.
name|create
argument_list|(
name|sort
argument_list|,
literal|50
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|newq
operator|.
name|build
argument_list|()
argument_list|,
name|topCollector
argument_list|)
expr_stmt|;
name|TopDocs
name|topDocs
init|=
name|topCollector
operator|.
name|topDocs
argument_list|(
literal|0
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|int
name|nDocsReturned
init|=
name|topDocs
operator|.
name|scoreDocs
operator|.
name|length
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|nDocsReturned
argument_list|)
expr_stmt|;
comment|// 0& 3 were elevated
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|0
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|1
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|reversed
condition|)
block|{
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|2
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|3
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|2
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|topDocs
operator|.
name|scoreDocs
index|[
literal|3
index|]
operator|.
name|doc
argument_list|)
expr_stmt|;
block|}
comment|/*     for (int i = 0; i< nDocsReturned; i++) {      ScoreDoc scoreDoc = topDocs.scoreDocs[i];      ids[i] = scoreDoc.doc;      scores[i] = scoreDoc.score;      documents[i] = searcher.doc(ids[i]);      System.out.println("ids[i] = " + ids[i]);      System.out.println("documents[i] = " + documents[i]);      System.out.println("scores[i] = " + scores[i]);    }     */
block|}
DECL|method|getElevatedQuery
specifier|private
name|Query
name|getElevatedQuery
parameter_list|(
name|String
index|[]
name|vals
parameter_list|)
block|{
name|BooleanQuery
operator|.
name|Builder
name|b
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|int
name|max
init|=
operator|(
name|vals
operator|.
name|length
operator|/
literal|2
operator|)
operator|+
literal|5
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
name|vals
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|b
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
name|vals
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
argument_list|)
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
name|priority
operator|.
name|put
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|vals
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|max
operator|--
argument_list|)
argument_list|)
expr_stmt|;
comment|// System.out.println(" pri doc=" + vals[i+1] + " pri=" + (1+max));
block|}
name|BooleanQuery
name|q
init|=
name|b
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|BoostQuery
argument_list|(
name|q
argument_list|,
literal|0f
argument_list|)
return|;
block|}
DECL|method|adoc
specifier|private
name|Document
name|adoc
parameter_list|(
name|String
index|[]
name|vals
parameter_list|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
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
name|vals
operator|.
name|length
operator|-
literal|2
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newTextField
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
name|vals
index|[
name|i
operator|+
literal|1
index|]
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|vals
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedDocValuesField
argument_list|(
name|vals
index|[
name|i
index|]
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|vals
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|doc
return|;
block|}
block|}
end_class
begin_class
DECL|class|ElevationComparatorSource
class|class
name|ElevationComparatorSource
extends|extends
name|FieldComparatorSource
block|{
DECL|field|priority
specifier|private
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|priority
decl_stmt|;
DECL|method|ElevationComparatorSource
specifier|public
name|ElevationComparatorSource
parameter_list|(
specifier|final
name|Map
argument_list|<
name|BytesRef
argument_list|,
name|Integer
argument_list|>
name|boosts
parameter_list|)
block|{
name|this
operator|.
name|priority
operator|=
name|boosts
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newComparator
specifier|public
name|FieldComparator
argument_list|<
name|Integer
argument_list|>
name|newComparator
parameter_list|(
specifier|final
name|String
name|fieldname
parameter_list|,
specifier|final
name|int
name|numHits
parameter_list|,
name|int
name|sortPos
parameter_list|,
name|boolean
name|reversed
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|FieldComparator
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|int
index|[]
name|values
init|=
operator|new
name|int
index|[
name|numHits
index|]
decl_stmt|;
name|int
name|bottomVal
decl_stmt|;
annotation|@
name|Override
specifier|public
name|LeafFieldComparator
name|getLeafComparator
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|SortedDocValues
name|idIndex
init|=
name|DocValues
operator|.
name|getSorted
argument_list|(
name|context
operator|.
name|reader
argument_list|()
argument_list|,
name|fieldname
argument_list|)
decl_stmt|;
return|return
operator|new
name|LeafFieldComparator
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|setBottom
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
name|bottomVal
operator|=
name|values
index|[
name|slot
index|]
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTop
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|private
name|int
name|docVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|int
name|ord
init|=
name|idIndex
operator|.
name|getOrd
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|ord
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
specifier|final
name|BytesRef
name|term
init|=
name|idIndex
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|)
decl_stmt|;
name|Integer
name|prio
init|=
name|priority
operator|.
name|get
argument_list|(
name|term
argument_list|)
decl_stmt|;
return|return
name|prio
operator|==
literal|null
condition|?
literal|0
else|:
name|prio
operator|.
name|intValue
argument_list|()
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareBottom
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|docVal
argument_list|(
name|doc
argument_list|)
operator|-
name|bottomVal
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copy
parameter_list|(
name|int
name|slot
parameter_list|,
name|int
name|doc
parameter_list|)
block|{
name|values
index|[
name|slot
index|]
operator|=
name|docVal
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
block|{}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|int
name|slot1
parameter_list|,
name|int
name|slot2
parameter_list|)
block|{
return|return
name|values
index|[
name|slot2
index|]
operator|-
name|values
index|[
name|slot1
index|]
return|;
comment|// values will be small enough that there is no overflow concern
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTopValue
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Integer
name|value
parameter_list|(
name|int
name|slot
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|valueOf
argument_list|(
name|values
index|[
name|slot
index|]
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class
end_unit
