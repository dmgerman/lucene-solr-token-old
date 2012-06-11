begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.tst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|tst
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
name|*
import|;
end_import
begin_comment
comment|/**  * Ternary Search Trie implementation.  *   * @see TernaryTreeNode  */
end_comment
begin_class
DECL|class|TSTAutocomplete
specifier|public
class|class
name|TSTAutocomplete
block|{
comment|/**    * Inserting keys in TST in the order middle,small,big (lexicographic measure)    * recursively creates a balanced tree which reduces insertion and search    * times significantly.    *     * @param tokens    *          Sorted list of keys to be inserted in TST.    * @param lo    *          stores the lower index of current list.    * @param hi    *          stores the higher index of current list.    * @param root    *          a reference object to root of TST.    */
DECL|method|balancedTree
specifier|public
name|void
name|balancedTree
parameter_list|(
name|Object
index|[]
name|tokens
parameter_list|,
name|Object
index|[]
name|vals
parameter_list|,
name|int
name|lo
parameter_list|,
name|int
name|hi
parameter_list|,
name|TernaryTreeNode
name|root
parameter_list|)
block|{
if|if
condition|(
name|lo
operator|>
name|hi
condition|)
return|return;
name|int
name|mid
init|=
operator|(
name|lo
operator|+
name|hi
operator|)
operator|/
literal|2
decl_stmt|;
name|root
operator|=
name|insert
argument_list|(
name|root
argument_list|,
operator|(
name|String
operator|)
name|tokens
index|[
name|mid
index|]
argument_list|,
name|vals
index|[
name|mid
index|]
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|balancedTree
argument_list|(
name|tokens
argument_list|,
name|vals
argument_list|,
name|lo
argument_list|,
name|mid
operator|-
literal|1
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|balancedTree
argument_list|(
name|tokens
argument_list|,
name|vals
argument_list|,
name|mid
operator|+
literal|1
argument_list|,
name|hi
argument_list|,
name|root
argument_list|)
expr_stmt|;
block|}
comment|/**    * Inserts a key in TST creating a series of Binary Search Trees at each node.    * The key is actually stored across the eqKid of each node in a successive    * manner.    *     * @param currentNode    *          a reference node where the insertion will take currently.    * @param s    *          key to be inserted in TST.    * @param x    *          index of character in key to be inserted currently.    * @return currentNode The new reference to root node of TST    */
DECL|method|insert
specifier|public
name|TernaryTreeNode
name|insert
parameter_list|(
name|TernaryTreeNode
name|currentNode
parameter_list|,
name|CharSequence
name|s
parameter_list|,
name|Object
name|val
parameter_list|,
name|int
name|x
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
operator|||
name|s
operator|.
name|length
argument_list|()
operator|<=
name|x
condition|)
block|{
return|return
name|currentNode
return|;
block|}
if|if
condition|(
name|currentNode
operator|==
literal|null
condition|)
block|{
name|TernaryTreeNode
name|newNode
init|=
operator|new
name|TernaryTreeNode
argument_list|()
decl_stmt|;
name|newNode
operator|.
name|splitchar
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|x
argument_list|)
expr_stmt|;
name|currentNode
operator|=
name|newNode
expr_stmt|;
if|if
condition|(
name|x
operator|<
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|currentNode
operator|.
name|eqKid
operator|=
name|insert
argument_list|(
name|currentNode
operator|.
name|eqKid
argument_list|,
name|s
argument_list|,
name|val
argument_list|,
name|x
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentNode
operator|.
name|token
operator|=
name|s
operator|.
name|toString
argument_list|()
expr_stmt|;
name|currentNode
operator|.
name|val
operator|=
name|val
expr_stmt|;
return|return
name|currentNode
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|currentNode
operator|.
name|splitchar
operator|>
name|s
operator|.
name|charAt
argument_list|(
name|x
argument_list|)
condition|)
block|{
name|currentNode
operator|.
name|loKid
operator|=
name|insert
argument_list|(
name|currentNode
operator|.
name|loKid
argument_list|,
name|s
argument_list|,
name|val
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|currentNode
operator|.
name|splitchar
operator|==
name|s
operator|.
name|charAt
argument_list|(
name|x
argument_list|)
condition|)
block|{
if|if
condition|(
name|x
operator|<
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
name|currentNode
operator|.
name|eqKid
operator|=
name|insert
argument_list|(
name|currentNode
operator|.
name|eqKid
argument_list|,
name|s
argument_list|,
name|val
argument_list|,
name|x
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|currentNode
operator|.
name|token
operator|=
name|s
operator|.
name|toString
argument_list|()
expr_stmt|;
name|currentNode
operator|.
name|val
operator|=
name|val
expr_stmt|;
return|return
name|currentNode
return|;
block|}
block|}
else|else
block|{
name|currentNode
operator|.
name|hiKid
operator|=
name|insert
argument_list|(
name|currentNode
operator|.
name|hiKid
argument_list|,
name|s
argument_list|,
name|val
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
return|return
name|currentNode
return|;
block|}
comment|/**    * Auto-completes a given prefix query using Depth-First Search with the end    * of prefix as source node each time finding a new leaf to get a complete key    * to be added in the suggest list.    *     * @param root    *          a reference to root node of TST.    * @param s    *          prefix query to be auto-completed.    * @param x    *          index of current character to be searched while traversing through    *          the prefix in TST.    * @return suggest list of auto-completed keys for the given prefix query.    */
DECL|method|prefixCompletion
specifier|public
name|ArrayList
argument_list|<
name|TernaryTreeNode
argument_list|>
name|prefixCompletion
parameter_list|(
name|TernaryTreeNode
name|root
parameter_list|,
name|CharSequence
name|s
parameter_list|,
name|int
name|x
parameter_list|)
block|{
name|TernaryTreeNode
name|p
init|=
name|root
decl_stmt|;
name|ArrayList
argument_list|<
name|TernaryTreeNode
argument_list|>
name|suggest
init|=
operator|new
name|ArrayList
argument_list|<
name|TernaryTreeNode
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|p
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|x
argument_list|)
operator|<
name|p
operator|.
name|splitchar
condition|)
block|{
name|p
operator|=
name|p
operator|.
name|loKid
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|x
argument_list|)
operator|==
name|p
operator|.
name|splitchar
condition|)
block|{
if|if
condition|(
name|x
operator|==
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
break|break;
block|}
else|else
block|{
name|x
operator|++
expr_stmt|;
block|}
name|p
operator|=
name|p
operator|.
name|eqKid
expr_stmt|;
block|}
else|else
block|{
name|p
operator|=
name|p
operator|.
name|hiKid
expr_stmt|;
block|}
block|}
if|if
condition|(
name|p
operator|==
literal|null
condition|)
return|return
name|suggest
return|;
if|if
condition|(
name|p
operator|.
name|eqKid
operator|==
literal|null
operator|&&
name|p
operator|.
name|token
operator|==
literal|null
condition|)
return|return
name|suggest
return|;
if|if
condition|(
name|p
operator|.
name|eqKid
operator|==
literal|null
operator|&&
name|p
operator|.
name|token
operator|!=
literal|null
condition|)
block|{
name|suggest
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
return|return
name|suggest
return|;
block|}
if|if
condition|(
name|p
operator|.
name|token
operator|!=
literal|null
condition|)
block|{
name|suggest
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|p
operator|=
name|p
operator|.
name|eqKid
expr_stmt|;
name|Stack
argument_list|<
name|TernaryTreeNode
argument_list|>
name|st
init|=
operator|new
name|Stack
argument_list|<
name|TernaryTreeNode
argument_list|>
argument_list|()
decl_stmt|;
name|st
operator|.
name|push
argument_list|(
name|p
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|st
operator|.
name|empty
argument_list|()
condition|)
block|{
name|TernaryTreeNode
name|top
init|=
name|st
operator|.
name|peek
argument_list|()
decl_stmt|;
name|st
operator|.
name|pop
argument_list|()
expr_stmt|;
if|if
condition|(
name|top
operator|.
name|token
operator|!=
literal|null
condition|)
block|{
name|suggest
operator|.
name|add
argument_list|(
name|top
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|top
operator|.
name|eqKid
operator|!=
literal|null
condition|)
block|{
name|st
operator|.
name|push
argument_list|(
name|top
operator|.
name|eqKid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|top
operator|.
name|loKid
operator|!=
literal|null
condition|)
block|{
name|st
operator|.
name|push
argument_list|(
name|top
operator|.
name|loKid
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|top
operator|.
name|hiKid
operator|!=
literal|null
condition|)
block|{
name|st
operator|.
name|push
argument_list|(
name|top
operator|.
name|hiKid
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|suggest
return|;
block|}
block|}
end_class
end_unit
