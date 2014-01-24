begin_unit
begin_package
DECL|package|org.apache.lucene.spatial
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import
begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Rectangle
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
name|spatial
operator|.
name|query
operator|.
name|SpatialArgsParser
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
name|spatial
operator|.
name|query
operator|.
name|SpatialOperation
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
name|junit
operator|.
name|Assert
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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|Iterator
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
begin_comment
comment|/**  * Make sure we are reading the tests as expected  */
end_comment
begin_class
DECL|class|TestTestFramework
specifier|public
class|class
name|TestTestFramework
extends|extends
name|LuceneTestCase
block|{
annotation|@
name|Test
DECL|method|testQueries
specifier|public
name|void
name|testQueries
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|name
init|=
name|StrategyTestCase
operator|.
name|QTEST_Cities_Intersects_BBox
decl_stmt|;
name|InputStream
name|in
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|SpatialContext
name|ctx
init|=
name|SpatialContext
operator|.
name|GEO
decl_stmt|;
name|Iterator
argument_list|<
name|SpatialTestQuery
argument_list|>
name|iter
init|=
name|SpatialTestQuery
operator|.
name|getTestQueries
argument_list|(
operator|new
name|SpatialArgsParser
argument_list|()
argument_list|,
name|ctx
argument_list|,
name|name
argument_list|,
name|in
argument_list|)
decl_stmt|;
comment|//closes the InputStream
name|List
argument_list|<
name|SpatialTestQuery
argument_list|>
name|tests
init|=
operator|new
name|ArrayList
argument_list|<
name|SpatialTestQuery
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|tests
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|tests
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SpatialTestQuery
name|sf
init|=
name|tests
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// assert
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sf
operator|.
name|ids
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sf
operator|.
name|ids
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
literal|"G5391959"
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|sf
operator|.
name|args
operator|.
name|getShape
argument_list|()
operator|instanceof
name|Rectangle
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|SpatialOperation
operator|.
name|Intersects
argument_list|,
name|sf
operator|.
name|args
operator|.
name|getOperation
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|spatialExample
specifier|public
name|void
name|spatialExample
parameter_list|()
throws|throws
name|Exception
block|{
comment|//kind of a hack so that SpatialExample is tested despite
comment|// it not starting or ending with "Test".
name|SpatialExample
operator|.
name|main
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
