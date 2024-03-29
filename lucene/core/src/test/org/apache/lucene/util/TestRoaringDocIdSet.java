begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
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
name|BitSet
import|;
end_import
begin_class
DECL|class|TestRoaringDocIdSet
specifier|public
class|class
name|TestRoaringDocIdSet
extends|extends
name|BaseDocIdSetTestCase
argument_list|<
name|RoaringDocIdSet
argument_list|>
block|{
annotation|@
name|Override
DECL|method|copyOf
specifier|public
name|RoaringDocIdSet
name|copyOf
parameter_list|(
name|BitSet
name|bs
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|RoaringDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|RoaringDocIdSet
operator|.
name|Builder
argument_list|(
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|bs
operator|.
name|nextSetBit
argument_list|(
literal|0
argument_list|)
init|;
name|i
operator|!=
operator|-
literal|1
condition|;
name|i
operator|=
name|bs
operator|.
name|nextSetBit
argument_list|(
name|i
operator|+
literal|1
argument_list|)
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|assertEquals
specifier|public
name|void
name|assertEquals
parameter_list|(
name|int
name|numBits
parameter_list|,
name|BitSet
name|ds1
parameter_list|,
name|RoaringDocIdSet
name|ds2
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|assertEquals
argument_list|(
name|numBits
argument_list|,
name|ds1
argument_list|,
name|ds2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ds1
operator|.
name|cardinality
argument_list|()
argument_list|,
name|ds2
operator|.
name|cardinality
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
