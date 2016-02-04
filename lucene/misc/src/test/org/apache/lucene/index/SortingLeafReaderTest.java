begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
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
name|util
operator|.
name|Bits
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
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_class
DECL|class|SortingLeafReaderTest
specifier|public
class|class
name|SortingLeafReaderTest
extends|extends
name|SorterTestBase
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClassSortingLeafReaderTest
specifier|public
specifier|static
name|void
name|beforeClassSortingLeafReaderTest
parameter_list|()
throws|throws
name|Exception
block|{
comment|// NOTE: index was created by by super's @BeforeClass
comment|// sort the index by id (as integer, in NUMERIC_DV_FIELD)
name|Sort
name|sort
init|=
operator|new
name|Sort
argument_list|(
operator|new
name|SortField
argument_list|(
name|NUMERIC_DV_FIELD
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|INT
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Sorter
operator|.
name|DocMap
name|docMap
init|=
operator|new
name|Sorter
argument_list|(
name|sort
argument_list|)
operator|.
name|sort
argument_list|(
name|unsortedReader
argument_list|)
decl_stmt|;
comment|// Sorter.compute also sorts the values
name|NumericDocValues
name|dv
init|=
name|unsortedReader
operator|.
name|getNumericDocValues
argument_list|(
name|NUMERIC_DV_FIELD
argument_list|)
decl_stmt|;
name|sortedValues
operator|=
operator|new
name|Integer
index|[
name|unsortedReader
operator|.
name|maxDoc
argument_list|()
index|]
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
name|unsortedReader
operator|.
name|maxDoc
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|sortedValues
index|[
name|docMap
operator|.
name|oldToNew
argument_list|(
name|i
argument_list|)
index|]
operator|=
operator|(
name|int
operator|)
name|dv
operator|.
name|get
argument_list|(
name|i
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
literal|"docMap: "
operator|+
name|docMap
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"sortedValues: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|sortedValues
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// sort the index by id (as integer, in NUMERIC_DV_FIELD)
name|sortedReader
operator|=
name|SortingLeafReader
operator|.
name|wrap
argument_list|(
name|unsortedReader
argument_list|,
name|sort
argument_list|)
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"mapped-deleted-docs: "
argument_list|)
expr_stmt|;
name|Bits
name|mappedLiveDocs
init|=
name|sortedReader
operator|.
name|getLiveDocs
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
name|mappedLiveDocs
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|mappedLiveDocs
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|i
operator|+
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|TestUtil
operator|.
name|checkReader
argument_list|(
name|sortedReader
argument_list|)
expr_stmt|;
block|}
DECL|method|testBadSort
specifier|public
name|void
name|testBadSort
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|SortingLeafReader
operator|.
name|wrap
argument_list|(
name|sortedReader
argument_list|,
name|Sort
operator|.
name|RELEVANCE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Cannot sort an index with a Sort that refers to the relevance score"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
