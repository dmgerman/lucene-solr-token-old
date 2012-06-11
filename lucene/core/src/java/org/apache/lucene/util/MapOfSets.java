begin_unit
begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|HashSet
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
comment|/**  * Helper class for keeping Lists of Objects associated with keys.<b>WARNING: THIS CLASS IS NOT THREAD SAFE</b>  * @lucene.internal  */
end_comment
begin_class
DECL|class|MapOfSets
specifier|public
class|class
name|MapOfSets
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
DECL|field|theMap
specifier|private
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|Set
argument_list|<
name|V
argument_list|>
argument_list|>
name|theMap
decl_stmt|;
comment|/**    * @param m the backing store for this object    */
DECL|method|MapOfSets
specifier|public
name|MapOfSets
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|Set
argument_list|<
name|V
argument_list|>
argument_list|>
name|m
parameter_list|)
block|{
name|theMap
operator|=
name|m
expr_stmt|;
block|}
comment|/**    * @return direct access to the map backing this object.    */
DECL|method|getMap
specifier|public
name|Map
argument_list|<
name|K
argument_list|,
name|Set
argument_list|<
name|V
argument_list|>
argument_list|>
name|getMap
parameter_list|()
block|{
return|return
name|theMap
return|;
block|}
comment|/**    * Adds val to the Set associated with key in the Map.  If key is not     * already in the map, a new Set will first be created.    * @return the size of the Set associated with key once val is added to it.    */
DECL|method|put
specifier|public
name|int
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|val
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|V
argument_list|>
name|theSet
decl_stmt|;
if|if
condition|(
name|theMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|theSet
operator|=
name|theMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|theSet
operator|=
operator|new
name|HashSet
argument_list|<
name|V
argument_list|>
argument_list|(
literal|23
argument_list|)
expr_stmt|;
name|theMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|theSet
argument_list|)
expr_stmt|;
block|}
name|theSet
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
return|return
name|theSet
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Adds multiple vals to the Set associated with key in the Map.      * If key is not     * already in the map, a new Set will first be created.    * @return the size of the Set associated with key once val is added to it.    */
DECL|method|putAll
specifier|public
name|int
name|putAll
parameter_list|(
name|K
name|key
parameter_list|,
name|Collection
argument_list|<
name|?
extends|extends
name|V
argument_list|>
name|vals
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|V
argument_list|>
name|theSet
decl_stmt|;
if|if
condition|(
name|theMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|theSet
operator|=
name|theMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|theSet
operator|=
operator|new
name|HashSet
argument_list|<
name|V
argument_list|>
argument_list|(
literal|23
argument_list|)
expr_stmt|;
name|theMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|theSet
argument_list|)
expr_stmt|;
block|}
name|theSet
operator|.
name|addAll
argument_list|(
name|vals
argument_list|)
expr_stmt|;
return|return
name|theSet
operator|.
name|size
argument_list|()
return|;
block|}
block|}
end_class
end_unit
