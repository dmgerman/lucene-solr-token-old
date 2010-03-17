begin_unit
begin_package
DECL|package|org.apache.lucene.util.cache
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|cache
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
begin_comment
comment|/**  * Simple LRU cache implementation that uses a LinkedHashMap.  * This cache is not synchronized, use {@link Cache#synchronizedCache(Cache)}  * if needed.  *   */
end_comment
begin_class
DECL|class|SimpleLRUCache
specifier|public
class|class
name|SimpleLRUCache
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|SimpleMapCache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|LOADFACTOR
specifier|private
specifier|final
specifier|static
name|float
name|LOADFACTOR
init|=
literal|0.75f
decl_stmt|;
comment|/**    * Creates a last-recently-used cache with the specified size.     */
DECL|method|SimpleLRUCache
specifier|public
name|SimpleLRUCache
parameter_list|(
specifier|final
name|int
name|cacheSize
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|LinkedHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|cacheSize
operator|/
name|LOADFACTOR
argument_list|)
operator|+
literal|1
argument_list|,
name|LOADFACTOR
argument_list|,
literal|true
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|cacheSize
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
