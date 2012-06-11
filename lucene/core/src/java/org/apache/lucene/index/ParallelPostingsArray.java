begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|RamUsageEstimator
import|;
end_import
begin_class
DECL|class|ParallelPostingsArray
class|class
name|ParallelPostingsArray
block|{
DECL|field|BYTES_PER_POSTING
specifier|final
specifier|static
name|int
name|BYTES_PER_POSTING
init|=
literal|3
operator|*
name|RamUsageEstimator
operator|.
name|NUM_BYTES_INT
decl_stmt|;
DECL|field|size
specifier|final
name|int
name|size
decl_stmt|;
DECL|field|textStarts
specifier|final
name|int
index|[]
name|textStarts
decl_stmt|;
DECL|field|intStarts
specifier|final
name|int
index|[]
name|intStarts
decl_stmt|;
DECL|field|byteStarts
specifier|final
name|int
index|[]
name|byteStarts
decl_stmt|;
DECL|method|ParallelPostingsArray
name|ParallelPostingsArray
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|textStarts
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|intStarts
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
name|byteStarts
operator|=
operator|new
name|int
index|[
name|size
index|]
expr_stmt|;
block|}
DECL|method|bytesPerPosting
name|int
name|bytesPerPosting
parameter_list|()
block|{
return|return
name|BYTES_PER_POSTING
return|;
block|}
DECL|method|newInstance
name|ParallelPostingsArray
name|newInstance
parameter_list|(
name|int
name|size
parameter_list|)
block|{
return|return
operator|new
name|ParallelPostingsArray
argument_list|(
name|size
argument_list|)
return|;
block|}
DECL|method|grow
specifier|final
name|ParallelPostingsArray
name|grow
parameter_list|()
block|{
name|int
name|newSize
init|=
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|size
operator|+
literal|1
argument_list|,
name|bytesPerPosting
argument_list|()
argument_list|)
decl_stmt|;
name|ParallelPostingsArray
name|newArray
init|=
name|newInstance
argument_list|(
name|newSize
argument_list|)
decl_stmt|;
name|copyTo
argument_list|(
name|newArray
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|newArray
return|;
block|}
DECL|method|copyTo
name|void
name|copyTo
parameter_list|(
name|ParallelPostingsArray
name|toArray
parameter_list|,
name|int
name|numToCopy
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|textStarts
argument_list|,
literal|0
argument_list|,
name|toArray
operator|.
name|textStarts
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|intStarts
argument_list|,
literal|0
argument_list|,
name|toArray
operator|.
name|intStarts
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|byteStarts
argument_list|,
literal|0
argument_list|,
name|toArray
operator|.
name|byteStarts
argument_list|,
literal|0
argument_list|,
name|numToCopy
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
