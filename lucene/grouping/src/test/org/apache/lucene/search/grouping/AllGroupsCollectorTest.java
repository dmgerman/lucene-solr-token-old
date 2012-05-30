begin_unit
begin_package
DECL|package|org.apache.lucene.search.grouping
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|grouping
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License")); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|index
operator|.
name|DocValues
operator|.
name|Type
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
name|queries
operator|.
name|function
operator|.
name|ValueSource
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
name|queries
operator|.
name|function
operator|.
name|valuesource
operator|.
name|BytesRefFieldSource
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
name|IndexSearcher
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
name|search
operator|.
name|grouping
operator|.
name|function
operator|.
name|FunctionAllGroupsCollector
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
name|grouping
operator|.
name|dv
operator|.
name|DVAllGroupsCollector
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
name|grouping
operator|.
name|term
operator|.
name|TermAllGroupsCollector
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
name|util
operator|.
name|BytesRef
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
begin_class
DECL|class|AllGroupsCollectorTest
specifier|public
class|class
name|AllGroupsCollectorTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|testTotalGroupCount
specifier|public
name|void
name|testTotalGroupCount
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|groupField
init|=
literal|"author"
decl_stmt|;
name|FieldType
name|customType
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
name|customType
operator|.
name|setStored
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|RandomIndexWriter
name|w
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|dir
argument_list|,
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
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|boolean
name|canUseIDV
init|=
literal|true
decl_stmt|;
comment|// 0
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author1"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"random text"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 1
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author1"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"some more random text blob"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 2
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author1"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"some more random textual data"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|w
operator|.
name|commit
argument_list|()
expr_stmt|;
comment|// To ensure a second segment
comment|// 3
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author2"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"some random text"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 4
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author3"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"some more random text"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 5
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|addGroupField
argument_list|(
name|doc
argument_list|,
name|groupField
argument_list|,
literal|"author3"
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"random blob"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|// 6 -- no author field
name|doc
operator|=
operator|new
name|Document
argument_list|()
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"content"
argument_list|,
literal|"random word stuck in alot of other text"
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
name|customType
argument_list|)
argument_list|)
expr_stmt|;
name|w
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|w
operator|.
name|getReader
argument_list|()
argument_list|)
decl_stmt|;
name|w
operator|.
name|close
argument_list|()
expr_stmt|;
name|AbstractAllGroupsCollector
argument_list|<
name|?
argument_list|>
name|allGroupsCollector
init|=
name|createRandomCollector
argument_list|(
name|groupField
argument_list|,
name|canUseIDV
argument_list|)
decl_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"random"
argument_list|)
argument_list|)
argument_list|,
name|allGroupsCollector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|allGroupsCollector
operator|.
name|getGroupCount
argument_list|()
argument_list|)
expr_stmt|;
name|allGroupsCollector
operator|=
name|createRandomCollector
argument_list|(
name|groupField
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"some"
argument_list|)
argument_list|)
argument_list|,
name|allGroupsCollector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|allGroupsCollector
operator|.
name|getGroupCount
argument_list|()
argument_list|)
expr_stmt|;
name|allGroupsCollector
operator|=
name|createRandomCollector
argument_list|(
name|groupField
argument_list|,
name|canUseIDV
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|search
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"content"
argument_list|,
literal|"blob"
argument_list|)
argument_list|)
argument_list|,
name|allGroupsCollector
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|allGroupsCollector
operator|.
name|getGroupCount
argument_list|()
argument_list|)
expr_stmt|;
name|indexSearcher
operator|.
name|getIndexReader
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|addGroupField
specifier|private
name|void
name|addGroupField
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|groupField
parameter_list|,
name|String
name|value
parameter_list|,
name|boolean
name|canUseIDV
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|Field
argument_list|(
name|groupField
argument_list|,
name|value
argument_list|,
name|TextField
operator|.
name|TYPE_STORED
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|canUseIDV
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedBytesDocValuesField
argument_list|(
name|groupField
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createRandomCollector
specifier|private
name|AbstractAllGroupsCollector
argument_list|<
name|?
argument_list|>
name|createRandomCollector
parameter_list|(
name|String
name|groupField
parameter_list|,
name|boolean
name|canUseIDV
parameter_list|)
throws|throws
name|IOException
block|{
name|AbstractAllGroupsCollector
argument_list|<
name|?
argument_list|>
name|selected
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
operator|&&
name|canUseIDV
condition|)
block|{
name|boolean
name|diskResident
init|=
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
decl_stmt|;
name|selected
operator|=
name|DVAllGroupsCollector
operator|.
name|create
argument_list|(
name|groupField
argument_list|,
name|Type
operator|.
name|BYTES_VAR_SORTED
argument_list|,
name|diskResident
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|selected
operator|=
operator|new
name|TermAllGroupsCollector
argument_list|(
name|groupField
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ValueSource
name|vs
init|=
operator|new
name|BytesRefFieldSource
argument_list|(
name|groupField
argument_list|)
decl_stmt|;
name|selected
operator|=
operator|new
name|FunctionAllGroupsCollector
argument_list|(
name|vs
argument_list|,
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Selected implementation: "
operator|+
name|selected
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|selected
return|;
block|}
block|}
end_class
end_unit
