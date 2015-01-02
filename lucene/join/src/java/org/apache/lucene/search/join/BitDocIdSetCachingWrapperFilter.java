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
name|Collection
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
name|LeafReader
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
name|LeafReaderContext
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
name|CachingWrapperFilter
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
name|DocIdSet
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
name|Filter
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
name|FilterCachingPolicy
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
name|Accountable
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
name|BitDocIdSet
import|;
end_import
begin_comment
comment|/**  * A filter wrapper that transforms the produces doc id sets into  * {@link BitDocIdSet}s if necessary and caches them.  */
end_comment
begin_class
DECL|class|BitDocIdSetCachingWrapperFilter
specifier|public
class|class
name|BitDocIdSetCachingWrapperFilter
extends|extends
name|BitDocIdSetFilter
implements|implements
name|Accountable
block|{
DECL|field|filter
specifier|private
specifier|final
name|CachingWrapperFilter
name|filter
decl_stmt|;
comment|/** Sole constructor. */
DECL|method|BitDocIdSetCachingWrapperFilter
specifier|public
name|BitDocIdSetCachingWrapperFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|filter
operator|=
operator|new
name|CachingWrapperFilter
argument_list|(
name|filter
argument_list|,
name|FilterCachingPolicy
operator|.
name|ALWAYS_CACHE
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|BitDocIdSet
name|docIdSetToCache
parameter_list|(
name|DocIdSet
name|docIdSet
parameter_list|,
name|LeafReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|docIdSet
operator|==
literal|null
operator|||
name|docIdSet
operator|instanceof
name|BitDocIdSet
condition|)
block|{
comment|// this is different from CachingWrapperFilter: even when the DocIdSet is
comment|// cacheable, we convert it to a BitSet since we require all the
comment|// cached filters to be BitSets
return|return
operator|(
name|BitDocIdSet
operator|)
name|docIdSet
return|;
block|}
specifier|final
name|DocIdSetIterator
name|it
init|=
name|docIdSet
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|it
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|BitDocIdSet
operator|.
name|Builder
name|builder
init|=
operator|new
name|BitDocIdSet
operator|.
name|Builder
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|builder
operator|.
name|or
argument_list|(
name|it
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|BitDocIdSet
name|getDocIdSet
parameter_list|(
name|LeafReaderContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|(
name|BitDocIdSet
operator|)
name|filter
operator|.
name|getDocIdSet
argument_list|(
name|context
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|filter
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|BitDocIdSetCachingWrapperFilter
operator|==
literal|false
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|filter
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|BitDocIdSetCachingWrapperFilter
operator|)
name|obj
operator|)
operator|.
name|filter
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|filter
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|ramBytesUsed
specifier|public
name|long
name|ramBytesUsed
parameter_list|()
block|{
return|return
name|filter
operator|.
name|ramBytesUsed
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildResources
specifier|public
name|Collection
argument_list|<
name|Accountable
argument_list|>
name|getChildResources
parameter_list|()
block|{
return|return
name|filter
operator|.
name|getChildResources
argument_list|()
return|;
block|}
block|}
end_class
end_unit
