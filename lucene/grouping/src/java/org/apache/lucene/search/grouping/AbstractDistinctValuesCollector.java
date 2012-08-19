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
name|search
operator|.
name|Collector
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
name|*
import|;
end_import
begin_comment
comment|/**  * A second pass grouping collector that keeps track of distinct values for a specified field for the top N group.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|AbstractDistinctValuesCollector
specifier|public
specifier|abstract
class|class
name|AbstractDistinctValuesCollector
parameter_list|<
name|GC
extends|extends
name|AbstractDistinctValuesCollector
operator|.
name|GroupCount
parameter_list|<
name|?
parameter_list|>
parameter_list|>
extends|extends
name|Collector
block|{
comment|/**    * Returns all unique values for each top N group.    *    * @return all unique values for each top N group    */
DECL|method|getGroups
specifier|public
specifier|abstract
name|List
argument_list|<
name|GC
argument_list|>
name|getGroups
parameter_list|()
function_decl|;
DECL|method|acceptsDocsOutOfOrder
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|setScorer
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{   }
comment|/**    * Returned by {@link AbstractDistinctValuesCollector#getGroups()},    * representing the value and set of distinct values for the group.    */
DECL|class|GroupCount
specifier|public
specifier|abstract
specifier|static
class|class
name|GroupCount
parameter_list|<
name|GROUP_VALUE_TYPE
parameter_list|>
block|{
DECL|field|groupValue
specifier|public
specifier|final
name|GROUP_VALUE_TYPE
name|groupValue
decl_stmt|;
DECL|field|uniqueValues
specifier|public
specifier|final
name|Set
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
name|uniqueValues
decl_stmt|;
DECL|method|GroupCount
specifier|public
name|GroupCount
parameter_list|(
name|GROUP_VALUE_TYPE
name|groupValue
parameter_list|)
block|{
name|this
operator|.
name|groupValue
operator|=
name|groupValue
expr_stmt|;
name|this
operator|.
name|uniqueValues
operator|=
operator|new
name|HashSet
argument_list|<
name|GROUP_VALUE_TYPE
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
