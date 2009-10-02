begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.cn.smart.hhmm
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|cn
operator|.
name|smart
operator|.
name|hhmm
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * Graph representing possible tokens at each start offset in the sentence.  *<p>  * For each start offset, a list of possible tokens is stored.  *</p>  *<p><font color="#FF0000">  * WARNING: The status of the analyzers/smartcn<b>analysis.cn.smart</b> package is experimental.   * The APIs and file formats introduced here might change in the future and will not be   * supported anymore in such a case.</font>  *</p>  */
end_comment
begin_class
DECL|class|SegGraph
class|class
name|SegGraph
block|{
comment|/**    * Map of start offsets to ArrayList of tokens at that position    */
DECL|field|tokenListTable
specifier|private
name|Map
comment|/*<Integer, ArrayList<SegToken>> */
name|tokenListTable
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
DECL|field|maxStart
specifier|private
name|int
name|maxStart
init|=
operator|-
literal|1
decl_stmt|;
comment|/**    * Returns true if a mapping for the specified start offset exists    *     * @param s startOffset    * @return true if there are tokens for the startOffset    */
DECL|method|isStartExist
specifier|public
name|boolean
name|isStartExist
parameter_list|(
name|int
name|s
parameter_list|)
block|{
return|return
name|tokenListTable
operator|.
name|get
argument_list|(
operator|new
name|Integer
argument_list|(
name|s
argument_list|)
argument_list|)
operator|!=
literal|null
return|;
block|}
comment|/**    * Get the list of tokens at the specified start offset    *     * @param s startOffset    * @return List of tokens at the specified start offset.    */
DECL|method|getStartList
specifier|public
name|List
name|getStartList
parameter_list|(
name|int
name|s
parameter_list|)
block|{
return|return
operator|(
name|List
operator|)
name|tokenListTable
operator|.
name|get
argument_list|(
operator|new
name|Integer
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Get the highest start offset in the map    *     * @return maximum start offset, or -1 if the map is empty.    */
DECL|method|getMaxStart
specifier|public
name|int
name|getMaxStart
parameter_list|()
block|{
return|return
name|maxStart
return|;
block|}
comment|/**    * Set the {@link SegToken#index} for each token, based upon its order by startOffset.     * @return a {@link List} of these ordered tokens.    */
DECL|method|makeIndex
specifier|public
name|List
name|makeIndex
parameter_list|()
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|int
name|s
init|=
operator|-
literal|1
decl_stmt|,
name|count
init|=
literal|0
decl_stmt|,
name|size
init|=
name|tokenListTable
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
name|tokenList
decl_stmt|;
name|short
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|size
condition|)
block|{
if|if
condition|(
name|isStartExist
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|tokenList
operator|=
operator|(
name|List
operator|)
name|tokenListTable
operator|.
name|get
argument_list|(
operator|new
name|Integer
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|tokenList
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SegToken
name|st
init|=
operator|(
name|SegToken
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|st
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|st
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|s
operator|++
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * Add a {@link SegToken} to the mapping, creating a new mapping at the token's startOffset if one does not exist.     * @param token {@link SegToken}    */
DECL|method|addToken
specifier|public
name|void
name|addToken
parameter_list|(
name|SegToken
name|token
parameter_list|)
block|{
name|int
name|s
init|=
name|token
operator|.
name|startOffset
decl_stmt|;
if|if
condition|(
operator|!
name|isStartExist
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|ArrayList
name|newlist
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|newlist
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|tokenListTable
operator|.
name|put
argument_list|(
call|(
name|Object
call|)
argument_list|(
operator|new
name|Integer
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|,
name|newlist
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|List
name|tokenList
init|=
operator|(
name|List
operator|)
name|tokenListTable
operator|.
name|get
argument_list|(
call|(
name|Object
call|)
argument_list|(
operator|new
name|Integer
argument_list|(
name|s
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|tokenList
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|s
operator|>
name|maxStart
condition|)
name|maxStart
operator|=
name|s
expr_stmt|;
block|}
comment|/**    * Return a {@link List} of all tokens in the map, ordered by startOffset.    *     * @return {@link List} of all tokens in the map.    */
DECL|method|toTokenList
specifier|public
name|List
name|toTokenList
parameter_list|()
block|{
name|List
name|result
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|int
name|s
init|=
operator|-
literal|1
decl_stmt|,
name|count
init|=
literal|0
decl_stmt|,
name|size
init|=
name|tokenListTable
operator|.
name|size
argument_list|()
decl_stmt|;
name|List
name|tokenList
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|size
condition|)
block|{
if|if
condition|(
name|isStartExist
argument_list|(
name|s
argument_list|)
condition|)
block|{
name|tokenList
operator|=
operator|(
name|List
operator|)
name|tokenListTable
operator|.
name|get
argument_list|(
operator|new
name|Integer
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|tokenList
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SegToken
name|st
init|=
operator|(
name|SegToken
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|st
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
name|s
operator|++
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|List
name|tokenList
init|=
name|this
operator|.
name|toTokenList
argument_list|()
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|tokenList
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|SegToken
name|t
init|=
operator|(
name|SegToken
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|t
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
