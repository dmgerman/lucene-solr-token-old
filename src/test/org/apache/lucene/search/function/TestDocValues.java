begin_unit
begin_package
DECL|package|org.apache.lucene.search.function
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|function
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|LuceneTestCaseJ4
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import
begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import
begin_comment
comment|/**  * DocValues TestCase    */
end_comment
begin_class
DECL|class|TestDocValues
specifier|public
class|class
name|TestDocValues
extends|extends
name|LuceneTestCaseJ4
block|{
annotation|@
name|Test
DECL|method|testGetMinValue
specifier|public
name|void
name|testGetMinValue
parameter_list|()
block|{
name|float
index|[]
name|innerArray
init|=
operator|new
name|float
index|[]
block|{
literal|1.0f
block|,
literal|2.0f
block|,
operator|-
literal|1.0f
block|,
literal|100.0f
block|}
decl_stmt|;
name|DocValuesTestImpl
name|docValues
init|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"-1.0f is the min value in the source array"
argument_list|,
operator|-
literal|1.0f
argument_list|,
name|docValues
operator|.
name|getMinValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// test with without values - NaN
name|innerArray
operator|=
operator|new
name|float
index|[]
block|{}
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"max is NaN - no values in inner array"
argument_list|,
name|Float
operator|.
name|isNaN
argument_list|(
name|docValues
operator|.
name|getMinValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetMaxValue
specifier|public
name|void
name|testGetMaxValue
parameter_list|()
block|{
name|float
index|[]
name|innerArray
init|=
operator|new
name|float
index|[]
block|{
literal|1.0f
block|,
literal|2.0f
block|,
operator|-
literal|1.0f
block|,
literal|10.0f
block|}
decl_stmt|;
name|DocValuesTestImpl
name|docValues
init|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"10.0f is the max value in the source array"
argument_list|,
literal|10.0f
argument_list|,
name|docValues
operator|.
name|getMaxValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|innerArray
operator|=
operator|new
name|float
index|[]
block|{
operator|-
literal|3.0f
block|,
operator|-
literal|1.0f
block|,
operator|-
literal|100.0f
block|}
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"-1.0f is the max value in the source array"
argument_list|,
operator|-
literal|1.0f
argument_list|,
name|docValues
operator|.
name|getMaxValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|innerArray
operator|=
operator|new
name|float
index|[]
block|{
operator|-
literal|3.0f
block|,
operator|-
literal|1.0f
block|,
literal|100.0f
block|,
name|Float
operator|.
name|MAX_VALUE
block|,
name|Float
operator|.
name|MAX_VALUE
operator|-
literal|1
block|}
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Float
operator|.
name|MAX_VALUE
operator|+
literal|" is the max value in the source array"
argument_list|,
name|Float
operator|.
name|MAX_VALUE
argument_list|,
name|docValues
operator|.
name|getMaxValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// test with without values - NaN
name|innerArray
operator|=
operator|new
name|float
index|[]
block|{}
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"max is NaN - no values in inner array"
argument_list|,
name|Float
operator|.
name|isNaN
argument_list|(
name|docValues
operator|.
name|getMaxValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetAverageValue
specifier|public
name|void
name|testGetAverageValue
parameter_list|()
block|{
name|float
index|[]
name|innerArray
init|=
operator|new
name|float
index|[]
block|{
literal|1.0f
block|,
literal|1.0f
block|,
literal|1.0f
block|,
literal|1.0f
block|}
decl_stmt|;
name|DocValuesTestImpl
name|docValues
init|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"the average is 1.0f"
argument_list|,
literal|1.0f
argument_list|,
name|docValues
operator|.
name|getAverageValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|innerArray
operator|=
operator|new
name|float
index|[]
block|{
literal|1.0f
block|,
literal|2.0f
block|,
literal|3.0f
block|,
literal|4.0f
block|,
literal|5.0f
block|,
literal|6.0f
block|}
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the average is 3.5f"
argument_list|,
literal|3.5f
argument_list|,
name|docValues
operator|.
name|getAverageValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// test with negative values
name|innerArray
operator|=
operator|new
name|float
index|[]
block|{
operator|-
literal|1.0f
block|,
literal|2.0f
block|}
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"the average is 0.5f"
argument_list|,
literal|0.5f
argument_list|,
name|docValues
operator|.
name|getAverageValue
argument_list|()
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// test with without values - NaN
name|innerArray
operator|=
operator|new
name|float
index|[]
block|{}
expr_stmt|;
name|docValues
operator|=
operator|new
name|DocValuesTestImpl
argument_list|(
name|innerArray
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"the average is NaN - no values in inner array"
argument_list|,
name|Float
operator|.
name|isNaN
argument_list|(
name|docValues
operator|.
name|getAverageValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|DocValuesTestImpl
specifier|static
class|class
name|DocValuesTestImpl
extends|extends
name|DocValues
block|{
DECL|field|innerArray
name|float
index|[]
name|innerArray
decl_stmt|;
DECL|method|DocValuesTestImpl
name|DocValuesTestImpl
parameter_list|(
name|float
index|[]
name|innerArray
parameter_list|)
block|{
name|this
operator|.
name|innerArray
operator|=
name|innerArray
expr_stmt|;
block|}
comment|/**      * @see org.apache.lucene.search.function.DocValues#floatVal(int)      */
annotation|@
name|Override
DECL|method|floatVal
specifier|public
name|float
name|floatVal
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|innerArray
index|[
name|doc
index|]
return|;
block|}
comment|/**      * @see org.apache.lucene.search.function.DocValues#toString(int)      */
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|doc
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit
