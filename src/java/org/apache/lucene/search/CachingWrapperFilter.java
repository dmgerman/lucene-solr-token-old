begin_unit
begin_package
DECL|package|org.apache.lucene.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
package|;
end_package
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|IndexReader
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|WeakHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
begin_comment
comment|/**  * Wraps another filters result and caches it.  The caching  * behavior is like {@link QueryFilter}.  The purpose is to allow  * filters to simply filter, and then wrap with this class to add  * caching, keeping the two concerns decoupled yet composable.  */
end_comment
begin_class
DECL|class|CachingWrapperFilter
specifier|public
class|class
name|CachingWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|filter
specifier|private
name|Filter
name|filter
decl_stmt|;
comment|/**    * @todo What about serialization in RemoteSearchable?  Caching won't work.    *       Should transient be removed?    */
DECL|field|cache
specifier|private
specifier|transient
name|Map
name|cache
decl_stmt|;
comment|/**    * @param filter Filter to cache results of    */
DECL|method|CachingWrapperFilter
specifier|public
name|CachingWrapperFilter
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
name|cache
operator|=
operator|new
name|WeakHashMap
argument_list|()
expr_stmt|;
block|}
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// check cache
name|BitSet
name|cached
init|=
operator|(
name|BitSet
operator|)
name|cache
operator|.
name|get
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|cached
operator|!=
literal|null
condition|)
block|{
return|return
name|cached
return|;
block|}
block|}
specifier|final
name|BitSet
name|bits
init|=
name|filter
operator|.
name|bits
argument_list|(
name|reader
argument_list|)
decl_stmt|;
synchronized|synchronized
init|(
name|cache
init|)
block|{
comment|// update cache
name|cache
operator|.
name|put
argument_list|(
name|reader
argument_list|,
name|bits
argument_list|)
expr_stmt|;
block|}
return|return
name|bits
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CachingWrapperFilter("
operator|+
name|filter
operator|+
literal|")"
return|;
block|}
block|}
end_class
end_unit
