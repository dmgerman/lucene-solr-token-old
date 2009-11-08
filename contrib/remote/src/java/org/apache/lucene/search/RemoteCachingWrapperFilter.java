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
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
begin_comment
comment|/**  * Provides caching of {@link org.apache.lucene.search.Filter}s themselves on the remote end of an RMI connection.  * The cache is keyed on Filter's hashCode(), so if it sees the same filter twice  * it will reuse the original version.  *<p/>  * NOTE: This does NOT cache the Filter bits, but rather the Filter itself.  * Thus, this works hand-in-hand with {@link org.apache.lucene.search.CachingWrapperFilter} to keep both  * file Filter cache and the Filter bits on the remote end, close to the searcher.  *<p/>  * Usage:  *<p/>  * To cache a result you must do something like   * RemoteCachingWrapperFilter f = new RemoteCachingWrapperFilter(new CachingWrapperFilter(myFilter));  *<p/>  *   * @version $Id:$  */
end_comment
begin_class
DECL|class|RemoteCachingWrapperFilter
specifier|public
class|class
name|RemoteCachingWrapperFilter
extends|extends
name|Filter
block|{
DECL|field|filter
specifier|protected
name|Filter
name|filter
decl_stmt|;
DECL|method|RemoteCachingWrapperFilter
specifier|public
name|RemoteCachingWrapperFilter
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
comment|/**    * Uses the {@link org.apache.lucene.search.FilterManager} to keep the cache for a filter on the     * searcher side of a remote connection.    * @param reader the index reader for the Filter    * @return the DocIdSet    */
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|Filter
name|cachedFilter
init|=
name|FilterManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getFilter
argument_list|(
name|filter
argument_list|)
decl_stmt|;
return|return
name|cachedFilter
operator|.
name|getDocIdSet
argument_list|(
name|reader
argument_list|)
return|;
block|}
block|}
end_class
end_unit
