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
begin_comment
comment|/** This document router is for custom sharding  */
end_comment
begin_class
DECL|class|ImplicitDocRouter
specifier|public
class|class
name|ImplicitDocRouter
extends|extends
name|DocRouter
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"implicit"
decl_stmt|;
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
name|String
name|shard
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sdoc
operator|!=
literal|null
condition|)
block|{
name|Object
name|o
init|=
name|sdoc
operator|.
name|getFieldValue
argument_list|(
literal|"_shard_"
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|shard
operator|=
name|o
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shard
operator|==
literal|null
condition|)
block|{
name|shard
operator|=
name|params
operator|.
name|get
argument_list|(
literal|"_shard_"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|shard
operator|!=
literal|null
condition|)
block|{
name|Slice
name|slice
init|=
name|collection
operator|.
name|getSlice
argument_list|(
name|shard
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
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
literal|"No _shard_="
operator|+
name|shard
operator|+
literal|" in "
operator|+
name|collection
argument_list|)
throw|;
block|}
return|return
name|slice
return|;
block|}
return|return
literal|null
return|;
comment|// no shard specified... use default.
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
return|return
name|collection
operator|.
name|getSlices
argument_list|()
return|;
block|}
comment|// assume the shardKey is just a slice name
name|Slice
name|slice
init|=
name|collection
operator|.
name|getSlice
argument_list|(
name|shardKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|slice
operator|==
literal|null
condition|)
block|{
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
literal|"implicit router can't find shard "
operator|+
name|shardKey
operator|+
literal|" in collection "
operator|+
name|collection
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|slice
argument_list|)
return|;
block|}
block|}
end_class
end_unit
