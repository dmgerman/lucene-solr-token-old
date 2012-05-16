begin_unit
begin_package
DECL|package|org.apache.lucene.facet.taxonomy.writercache.cl2o
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
operator|.
name|cl2o
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|facet
operator|.
name|taxonomy
operator|.
name|CategoryPath
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
name|facet
operator|.
name|taxonomy
operator|.
name|writercache
operator|.
name|TaxonomyWriterCache
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * {@link TaxonomyWriterCache} using {@link CompactLabelToOrdinal}. Although  * called cache, it maintains in memory all the mappings from category to  * ordinal, relying on that {@link CompactLabelToOrdinal} is an efficient  * mapping for this purpose.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|Cl2oTaxonomyWriterCache
specifier|public
class|class
name|Cl2oTaxonomyWriterCache
implements|implements
name|TaxonomyWriterCache
block|{
DECL|field|lock
specifier|private
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
DECL|field|cache
specifier|private
name|CompactLabelToOrdinal
name|cache
decl_stmt|;
DECL|method|Cl2oTaxonomyWriterCache
specifier|public
name|Cl2oTaxonomyWriterCache
parameter_list|(
name|int
name|initialCapcity
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|int
name|numHashArrays
parameter_list|)
block|{
name|this
operator|.
name|cache
operator|=
operator|new
name|CompactLabelToOrdinal
argument_list|(
name|initialCapcity
argument_list|,
name|loadFactor
argument_list|,
name|numHashArrays
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
name|cache
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasRoom
specifier|public
name|boolean
name|hasRoom
parameter_list|(
name|int
name|n
parameter_list|)
block|{
comment|// This cache is unlimited, so we always have room for remembering more:
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|cache
operator|.
name|getOrdinal
argument_list|(
name|categoryPath
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|int
name|get
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
argument_list|<
literal|0
operator|||
name|length
argument_list|>
name|categoryPath
operator|.
name|length
argument_list|()
condition|)
block|{
name|length
operator|=
name|categoryPath
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|cache
operator|.
name|getOrdinal
argument_list|(
name|categoryPath
argument_list|,
name|length
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|boolean
name|put
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|cache
operator|.
name|addLabel
argument_list|(
name|categoryPath
argument_list|,
name|ordinal
argument_list|)
expr_stmt|;
comment|// Tell the caller we didn't clear part of the cache, so it doesn't
comment|// have to flush its on-disk index now
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|put
specifier|public
name|boolean
name|put
parameter_list|(
name|CategoryPath
name|categoryPath
parameter_list|,
name|int
name|prefixLen
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|cache
operator|.
name|addLabel
argument_list|(
name|categoryPath
argument_list|,
name|prefixLen
argument_list|,
name|ordinal
argument_list|)
expr_stmt|;
comment|// Tell the caller we didn't clear part of the cache, so it doesn't
comment|// have to flush its on-disk index now
return|return
literal|false
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Returns the number of bytes in memory used by this object.    * @return Number of bytes in memory used by this object.    */
DECL|method|getMemoryUsage
specifier|public
name|int
name|getMemoryUsage
parameter_list|()
block|{
return|return
name|cache
operator|==
literal|null
condition|?
literal|0
else|:
name|cache
operator|.
name|getMemoryUsage
argument_list|()
return|;
block|}
block|}
end_class
end_unit
