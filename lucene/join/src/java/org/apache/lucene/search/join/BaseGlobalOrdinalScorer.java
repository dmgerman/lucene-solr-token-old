begin_unit
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
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
name|index
operator|.
name|SortedDocValues
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
name|Scorer
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
name|TwoPhaseIterator
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
name|Weight
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
begin_class
DECL|class|BaseGlobalOrdinalScorer
specifier|abstract
class|class
name|BaseGlobalOrdinalScorer
extends|extends
name|Scorer
block|{
DECL|field|values
specifier|final
name|SortedDocValues
name|values
decl_stmt|;
DECL|field|approximation
specifier|final
name|DocIdSetIterator
name|approximation
decl_stmt|;
DECL|field|score
name|float
name|score
decl_stmt|;
DECL|method|BaseGlobalOrdinalScorer
specifier|public
name|BaseGlobalOrdinalScorer
parameter_list|(
name|Weight
name|weight
parameter_list|,
name|SortedDocValues
name|values
parameter_list|,
name|DocIdSetIterator
name|approximationScorer
parameter_list|)
block|{
name|super
argument_list|(
name|weight
argument_list|)
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|approximation
operator|=
name|approximationScorer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|score
specifier|public
name|float
name|score
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|score
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|approximation
operator|.
name|docID
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
block|{
return|return
name|TwoPhaseIterator
operator|.
name|asDocIdSetIterator
argument_list|(
name|twoPhaseIterator
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|twoPhaseIterator
specifier|public
name|TwoPhaseIterator
name|twoPhaseIterator
parameter_list|()
block|{
return|return
name|createTwoPhaseIterator
argument_list|(
name|approximation
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
throws|throws
name|IOException
block|{
return|return
literal|1
return|;
block|}
DECL|method|createTwoPhaseIterator
specifier|protected
specifier|abstract
name|TwoPhaseIterator
name|createTwoPhaseIterator
parameter_list|(
name|DocIdSetIterator
name|approximation
parameter_list|)
function_decl|;
block|}
end_class
end_unit
