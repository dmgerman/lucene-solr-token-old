begin_unit
begin_package
DECL|package|org.apache.lucene.index.sorter
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|sorter
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
name|List
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
operator|.
name|Store
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
name|NumericDocValuesField
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
name|StringField
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
name|AtomicReader
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
name|DirectoryReader
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
name|index
operator|.
name|NumericDocValues
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
name|RandomIndexWriter
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
name|Term
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
name|CachingWrapperFilter
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
name|DocIdSet
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
name|DocIdSetIterator
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
name|Filter
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
name|QueryWrapperFilter
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
name|Sort
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
name|SortField
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
name|TermQuery
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
name|ArrayUtil
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
name|FixedBitSet
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
begin_class
DECL|class|TestBlockJoinSorter
specifier|public
class|class
name|TestBlockJoinSorter
extends|extends
name|LuceneTestCase
block|{
DECL|class|FixedBitSetCachingWrapperFilter
specifier|private
specifier|static
class|class
name|FixedBitSetCachingWrapperFilter
extends|extends
name|CachingWrapperFilter
block|{
DECL|method|FixedBitSetCachingWrapperFilter
specifier|public
name|FixedBitSetCachingWrapperFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|super
argument_list|(
name|filter
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|cacheImpl
specifier|protected
name|DocIdSet
name|cacheImpl
parameter_list|(
name|DocIdSetIterator
name|iterator
parameter_list|,
name|AtomicReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FixedBitSet
name|cached
init|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|cached
operator|.
name|or
argument_list|(
name|iterator
argument_list|)
expr_stmt|;
return|return
name|cached
return|;
block|}
block|}
DECL|method|test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|int
name|numParents
init|=
name|atLeast
argument_list|(
literal|200
argument_list|)
decl_stmt|;
name|IndexWriterConfig
name|cfg
init|=
name|newIndexWriterConfig
argument_list|(
name|TEST_VERSION_CURRENT
argument_list|,
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|cfg
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|newDirectory
argument_list|()
argument_list|,
name|cfg
argument_list|)
decl_stmt|;
specifier|final
name|Document
name|parentDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
specifier|final
name|NumericDocValuesField
name|parentVal
init|=
operator|new
name|NumericDocValuesField
argument_list|(
literal|"parent_val"
argument_list|,
literal|0L
argument_list|)
decl_stmt|;
name|parentDoc
operator|.
name|add
argument_list|(
name|parentVal
argument_list|)
expr_stmt|;
specifier|final
name|StringField
name|parent
init|=
operator|new
name|StringField
argument_list|(
literal|"parent"
argument_list|,
literal|"true"
argument_list|,
name|Store
operator|.
name|YES
argument_list|)
decl_stmt|;
name|parentDoc
operator|.
name|add
argument_list|(
name|parent
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
name|numParents
condition|;
operator|++
name|i
control|)
block|{
name|List
argument_list|<
name|Document
argument_list|>
name|documents
init|=
operator|new
name|ArrayList
argument_list|<
name|Document
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|numChildren
init|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
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
name|numChildren
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|Document
name|childDoc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|childDoc
operator|.
name|add
argument_list|(
operator|new
name|NumericDocValuesField
argument_list|(
literal|"child_val"
argument_list|,
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|childDoc
argument_list|)
expr_stmt|;
block|}
name|parentVal
operator|.
name|setLongValue
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|50
argument_list|)
argument_list|)
expr_stmt|;
name|documents
operator|.
name|add
argument_list|(
name|parentDoc
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocuments
argument_list|(
name|documents
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|forceMerge
argument_list|(
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|DirectoryReader
name|indexReader
init|=
name|writer
operator|.
name|getReader
argument_list|()
decl_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
specifier|final
name|AtomicReader
name|reader
init|=
name|getOnlySegmentReader
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
specifier|final
name|Filter
name|parentsFilter
init|=
operator|new
name|FixedBitSetCachingWrapperFilter
argument_list|(
operator|new
name|QueryWrapperFilter
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"parent"
argument_list|,
literal|"true"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|FixedBitSet
name|parentBits
init|=
operator|(
name|FixedBitSet
operator|)
name|parentsFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
operator|.
name|getContext
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|parentValues
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
literal|"parent_val"
argument_list|)
decl_stmt|;
specifier|final
name|NumericDocValues
name|childValues
init|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
literal|"child_val"
argument_list|)
decl_stmt|;
specifier|final
name|Sort
name|parentSort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"parent_val"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Sort
name|childSort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"child_val"
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
literal|"custom"
argument_list|,
operator|new
name|BlockJoinComparatorSource
argument_list|(
name|parentsFilter
argument_list|,
name|parentSort
argument_list|,
name|childSort
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Sorter
name|sorter
init|=
operator|new
name|SortSorter
argument_list|(
name|sort
argument_list|)
decl_stmt|;
specifier|final
name|Sorter
operator|.
name|DocMap
name|docMap
init|=
name|sorter
operator|.
name|sort
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|docMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|int
index|[]
name|children
init|=
operator|new
name|int
index|[
literal|1
index|]
decl_stmt|;
name|int
name|numChildren
init|=
literal|0
decl_stmt|;
name|int
name|previousParent
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
name|docMap
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|int
name|oldID
init|=
name|docMap
operator|.
name|newToOld
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentBits
operator|.
name|get
argument_list|(
name|oldID
argument_list|)
condition|)
block|{
comment|// check that we have the right children
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numChildren
condition|;
operator|++
name|j
control|)
block|{
name|assertEquals
argument_list|(
name|oldID
argument_list|,
name|parentBits
operator|.
name|nextSetBit
argument_list|(
name|children
index|[
name|j
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// check that children are sorted
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|numChildren
condition|;
operator|++
name|j
control|)
block|{
specifier|final
name|int
name|doc1
init|=
name|children
index|[
name|j
operator|-
literal|1
index|]
decl_stmt|;
specifier|final
name|int
name|doc2
init|=
name|children
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|childValues
operator|.
name|get
argument_list|(
name|doc1
argument_list|)
operator|==
name|childValues
operator|.
name|get
argument_list|(
name|doc2
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|doc1
operator|<
name|doc2
argument_list|)
expr_stmt|;
comment|// sort is stable
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|childValues
operator|.
name|get
argument_list|(
name|doc1
argument_list|)
operator|<
name|childValues
operator|.
name|get
argument_list|(
name|doc2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// check that parents are sorted
if|if
condition|(
name|previousParent
operator|!=
operator|-
literal|1
condition|)
block|{
if|if
condition|(
name|parentValues
operator|.
name|get
argument_list|(
name|previousParent
argument_list|)
operator|==
name|parentValues
operator|.
name|get
argument_list|(
name|oldID
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|previousParent
operator|<
name|oldID
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|parentValues
operator|.
name|get
argument_list|(
name|previousParent
argument_list|)
operator|<
name|parentValues
operator|.
name|get
argument_list|(
name|oldID
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// reset
name|previousParent
operator|=
name|oldID
expr_stmt|;
name|numChildren
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|children
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|children
argument_list|,
name|numChildren
operator|+
literal|1
argument_list|)
expr_stmt|;
name|children
index|[
name|numChildren
operator|++
index|]
operator|=
name|oldID
expr_stmt|;
block|}
block|}
name|indexReader
operator|.
name|close
argument_list|()
expr_stmt|;
name|writer
operator|.
name|w
operator|.
name|getDirectory
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class
end_unit
