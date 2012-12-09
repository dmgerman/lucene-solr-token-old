begin_unit
begin_package
DECL|package|org.apache.solr.common.cloud
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|cloud
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
name|solr
operator|.
name|common
operator|.
name|SolrException
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|SolrInputDocument
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
operator|.
name|SolrParams
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|Hash
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_class
DECL|class|HashBasedRouter
specifier|public
specifier|abstract
class|class
name|HashBasedRouter
extends|extends
name|DocRouter
block|{
annotation|@
name|Override
DECL|method|getTargetSlice
specifier|public
name|Slice
name|getTargetSlice
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|sdoc
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|null
condition|)
name|id
operator|=
name|getId
argument_list|(
name|sdoc
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|int
name|hash
init|=
name|sliceHash
argument_list|(
name|id
argument_list|,
name|sdoc
argument_list|,
name|params
argument_list|)
decl_stmt|;
return|return
name|hashToSlice
argument_list|(
name|hash
argument_list|,
name|collection
argument_list|)
return|;
block|}
DECL|method|sliceHash
specifier|protected
name|int
name|sliceHash
parameter_list|(
name|String
name|id
parameter_list|,
name|SolrInputDocument
name|sdoc
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
return|return
name|Hash
operator|.
name|murmurhash3_x86_32
argument_list|(
name|id
argument_list|,
literal|0
argument_list|,
name|id
operator|.
name|length
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|method|getId
specifier|protected
name|String
name|getId
parameter_list|(
name|SolrInputDocument
name|sdoc
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|Object
name|idObj
init|=
name|sdoc
operator|.
name|getFieldValue
argument_list|(
literal|"id"
argument_list|)
decl_stmt|;
comment|// blech
name|String
name|id
init|=
name|idObj
operator|!=
literal|null
condition|?
name|idObj
operator|.
name|toString
argument_list|()
else|:
literal|"null"
decl_stmt|;
comment|// should only happen on client side
return|return
name|id
return|;
block|}
DECL|method|hashToSlice
specifier|protected
name|Slice
name|hashToSlice
parameter_list|(
name|int
name|hash
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
for|for
control|(
name|Slice
name|slice
range|:
name|collection
operator|.
name|getSlices
argument_list|()
control|)
block|{
name|Range
name|range
init|=
name|slice
operator|.
name|getRange
argument_list|()
decl_stmt|;
if|if
condition|(
name|range
operator|!=
literal|null
operator|&&
name|range
operator|.
name|includes
argument_list|(
name|hash
argument_list|)
condition|)
return|return
name|slice
return|;
block|}
throw|throw
operator|new
name|SolrException
argument_list|(
name|SolrException
operator|.
name|ErrorCode
operator|.
name|BAD_REQUEST
argument_list|,
literal|"No slice servicing hash code "
operator|+
name|Integer
operator|.
name|toHexString
argument_list|(
name|hash
argument_list|)
operator|+
literal|" in "
operator|+
name|collection
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getSearchSlicesSingle
specifier|public
name|Collection
argument_list|<
name|Slice
argument_list|>
name|getSearchSlicesSingle
parameter_list|(
name|String
name|shardKey
parameter_list|,
name|SolrParams
name|params
parameter_list|,
name|DocCollection
name|collection
parameter_list|)
block|{
if|if
condition|(
name|shardKey
operator|==
literal|null
condition|)
block|{
comment|// search across whole collection
comment|// TODO: this may need modification in the future when shard splitting could cause an overlap
return|return
name|collection
operator|.
name|getSlices
argument_list|()
return|;
block|}
comment|// use the shardKey as an id for plain hashing
name|Slice
name|slice
init|=
name|getTargetSlice
argument_list|(
name|shardKey
argument_list|,
literal|null
argument_list|,
name|params
argument_list|,
name|collection
argument_list|)
decl_stmt|;
return|return
name|slice
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|Slice
operator|>
name|emptyList
argument_list|()
else|:
name|Collections
operator|.
name|singletonList
argument_list|(
name|slice
argument_list|)
return|;
block|}
block|}
end_class
end_unit
