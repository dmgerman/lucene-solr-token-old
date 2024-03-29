begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.suggest.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|fst
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|suggest
operator|.
name|InMemorySorter
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
name|BytesRefIterator
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
name|IOUtils
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
name|OfflineSorter
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
DECL|class|BytesRefSortersTest
specifier|public
class|class
name|BytesRefSortersTest
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testExternalRefSorter
specifier|public
name|void
name|testExternalRefSorter
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|tempDir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|ExternalRefSorter
name|s
init|=
operator|new
name|ExternalRefSorter
argument_list|(
operator|new
name|OfflineSorter
argument_list|(
name|tempDir
argument_list|,
literal|"temp"
argument_list|)
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|s
argument_list|,
name|tempDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInMemorySorter
specifier|public
name|void
name|testInMemorySorter
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|InMemorySorter
argument_list|(
name|Comparator
operator|.
name|naturalOrder
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|check
specifier|private
name|void
name|check
parameter_list|(
name|BytesRefSorter
name|sorter
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|current
init|=
operator|new
name|byte
index|[
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|256
argument_list|)
index|]
decl_stmt|;
name|random
argument_list|()
operator|.
name|nextBytes
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|sorter
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
name|current
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Create two iterators and check that they're aligned with each other.
name|BytesRefIterator
name|i1
init|=
name|sorter
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|BytesRefIterator
name|i2
init|=
name|sorter
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// Verify sorter contract.
name|expectThrows
argument_list|(
name|IllegalStateException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|sorter
operator|.
name|add
argument_list|(
operator|new
name|BytesRef
argument_list|(
operator|new
name|byte
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|BytesRef
name|spare1
init|=
name|i1
operator|.
name|next
argument_list|()
decl_stmt|;
name|BytesRef
name|spare2
init|=
name|i2
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|spare1
argument_list|,
name|spare2
argument_list|)
expr_stmt|;
if|if
condition|(
name|spare1
operator|==
literal|null
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
end_class
end_unit
