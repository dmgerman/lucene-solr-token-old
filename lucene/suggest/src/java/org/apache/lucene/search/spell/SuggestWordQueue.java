begin_unit
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
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
name|PriorityQueue
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import
begin_comment
comment|/**  * Sorts SuggestWord instances  *  * @see org.apache.lucene.search.spell.SuggestWordScoreComparator  * @see org.apache.lucene.search.spell.SuggestWordFrequencyComparator  *  */
end_comment
begin_class
DECL|class|SuggestWordQueue
specifier|public
specifier|final
class|class
name|SuggestWordQueue
extends|extends
name|PriorityQueue
argument_list|<
name|SuggestWord
argument_list|>
block|{
DECL|field|DEFAULT_COMPARATOR
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|DEFAULT_COMPARATOR
init|=
operator|new
name|SuggestWordScoreComparator
argument_list|()
decl_stmt|;
DECL|field|comparator
specifier|private
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|comparator
decl_stmt|;
comment|/**    * Use the {@link #DEFAULT_COMPARATOR}    * @param size The size of the queue    */
DECL|method|SuggestWordQueue
specifier|public
name|SuggestWordQueue
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|comparator
operator|=
name|DEFAULT_COMPARATOR
expr_stmt|;
block|}
comment|/**    * Specify the size of the queue and the comparator to use for sorting.    * @param size The size    * @param comparator The comparator.    */
DECL|method|SuggestWordQueue
specifier|public
name|SuggestWordQueue
parameter_list|(
name|int
name|size
parameter_list|,
name|Comparator
argument_list|<
name|SuggestWord
argument_list|>
name|comparator
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|)
expr_stmt|;
name|this
operator|.
name|comparator
operator|=
name|comparator
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|lessThan
specifier|protected
specifier|final
name|boolean
name|lessThan
parameter_list|(
name|SuggestWord
name|wa
parameter_list|,
name|SuggestWord
name|wb
parameter_list|)
block|{
name|int
name|val
init|=
name|comparator
operator|.
name|compare
argument_list|(
name|wa
argument_list|,
name|wb
argument_list|)
decl_stmt|;
return|return
name|val
operator|<
literal|0
return|;
block|}
block|}
end_class
end_unit
