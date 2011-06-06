begin_unit
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
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
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
name|LinkedHashMap
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
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
name|analysis
operator|.
name|Token
import|;
end_import
begin_comment
comment|/**  *<p>  * Given a list of possible Spelling Corrections for multiple mis-spelled words  * in a query, This iterator returns Possible Correction combinations ordered by  * reasonable probability that such a combination will return actual hits if  * re-queried. This implementation simply ranks the Possible Combinations by the  * sum of their component ranks.  *</p>  *   */
end_comment
begin_class
DECL|class|PossibilityIterator
specifier|public
class|class
name|PossibilityIterator
implements|implements
name|Iterator
argument_list|<
name|RankedSpellPossibility
argument_list|>
block|{
DECL|field|possibilityList
specifier|private
name|List
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
name|possibilityList
init|=
operator|new
name|ArrayList
argument_list|<
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rankedPossibilityIterator
specifier|private
name|Iterator
argument_list|<
name|RankedSpellPossibility
argument_list|>
name|rankedPossibilityIterator
init|=
literal|null
decl_stmt|;
DECL|field|correctionIndex
specifier|private
name|int
name|correctionIndex
index|[]
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|PossibilityIterator
specifier|private
name|PossibilityIterator
parameter_list|()
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"You shan't go here."
argument_list|)
throw|;
block|}
comment|/** 	 *<p> 	 * We assume here that the passed-in inner LinkedHashMaps are already sorted 	 * in order of "Best Possible Correction". 	 *</p> 	 *  	 * @param suggestions 	 */
DECL|method|PossibilityIterator
specifier|public
name|PossibilityIterator
parameter_list|(
name|Map
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|suggestions
parameter_list|,
name|int
name|maximumRequiredSuggestions
parameter_list|,
name|int
name|maxEvaluations
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|entry
range|:
name|suggestions
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Token
name|token
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|possibleCorrections
init|=
operator|new
name|ArrayList
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|entry1
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|SpellCheckCorrection
name|correction
init|=
operator|new
name|SpellCheckCorrection
argument_list|()
decl_stmt|;
name|correction
operator|.
name|setOriginal
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|correction
operator|.
name|setCorrection
argument_list|(
name|entry1
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|correction
operator|.
name|setNumberOfOccurences
argument_list|(
name|entry1
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|possibleCorrections
operator|.
name|add
argument_list|(
name|correction
argument_list|)
expr_stmt|;
block|}
name|possibilityList
operator|.
name|add
argument_list|(
name|possibleCorrections
argument_list|)
expr_stmt|;
block|}
name|int
name|wrapSize
init|=
name|possibilityList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|wrapSize
operator|==
literal|0
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|correctionIndex
operator|=
operator|new
name|int
index|[
name|wrapSize
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|wrapSize
condition|;
name|i
operator|++
control|)
block|{
name|int
name|suggestSize
init|=
name|possibilityList
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|suggestSize
operator|==
literal|0
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
break|break;
block|}
name|correctionIndex
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
name|long
name|count
init|=
literal|0
decl_stmt|;
name|PriorityQueue
argument_list|<
name|RankedSpellPossibility
argument_list|>
name|rankedPossibilities
init|=
operator|new
name|PriorityQueue
argument_list|<
name|RankedSpellPossibility
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|maxEvaluations
operator|&&
name|internalHasNext
argument_list|()
condition|)
block|{
name|RankedSpellPossibility
name|rsp
init|=
name|internalNext
argument_list|()
decl_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|rankedPossibilities
operator|.
name|size
argument_list|()
operator|>=
name|maximumRequiredSuggestions
operator|&&
name|rsp
operator|.
name|getRank
argument_list|()
operator|>=
name|rankedPossibilities
operator|.
name|peek
argument_list|()
operator|.
name|getRank
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|rankedPossibilities
operator|.
name|offer
argument_list|(
name|rsp
argument_list|)
expr_stmt|;
if|if
condition|(
name|rankedPossibilities
operator|.
name|size
argument_list|()
operator|>
name|maximumRequiredSuggestions
condition|)
block|{
name|rankedPossibilities
operator|.
name|poll
argument_list|()
expr_stmt|;
block|}
block|}
name|RankedSpellPossibility
index|[]
name|rpArr
init|=
operator|new
name|RankedSpellPossibility
index|[
name|rankedPossibilities
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|rankedPossibilities
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|rpArr
index|[
name|i
index|]
operator|=
name|rankedPossibilities
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|rankedPossibilityIterator
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|rpArr
argument_list|)
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
DECL|method|internalHasNext
specifier|private
name|boolean
name|internalHasNext
parameter_list|()
block|{
return|return
operator|!
name|done
return|;
block|}
comment|/** 	 *<p> 	 * This method is converting the independent LinkHashMaps containing various 	 * (silo'ed) suggestions for each mis-spelled word into individual 	 * "holistic query corrections", aka. "Spell Check Possibility" 	 *</p> 	 *<p> 	 * Rank here is the sum of each selected term's position in its respective 	 * LinkedHashMap. 	 *</p> 	 *  	 * @return 	 */
DECL|method|internalNext
specifier|private
name|RankedSpellPossibility
name|internalNext
parameter_list|()
block|{
if|if
condition|(
name|done
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|possibleCorrection
init|=
operator|new
name|ArrayList
argument_list|<
name|SpellCheckCorrection
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|rank
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|correctionIndex
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|SpellCheckCorrection
argument_list|>
name|singleWordPossibilities
init|=
name|possibilityList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SpellCheckCorrection
name|singleWordPossibility
init|=
name|singleWordPossibilities
operator|.
name|get
argument_list|(
name|correctionIndex
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|rank
operator|+=
name|correctionIndex
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|correctionIndex
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|correctionIndex
index|[
name|i
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|correctionIndex
index|[
name|i
index|]
operator|==
name|singleWordPossibilities
operator|.
name|size
argument_list|()
condition|)
block|{
name|correctionIndex
index|[
name|i
index|]
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|correctionIndex
operator|.
name|length
operator|==
literal|1
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
for|for
control|(
name|int
name|ii
init|=
name|i
operator|-
literal|1
init|;
name|ii
operator|>=
literal|0
condition|;
name|ii
operator|--
control|)
block|{
name|correctionIndex
index|[
name|ii
index|]
operator|++
expr_stmt|;
if|if
condition|(
name|correctionIndex
index|[
name|ii
index|]
operator|>=
name|possibilityList
operator|.
name|get
argument_list|(
name|ii
argument_list|)
operator|.
name|size
argument_list|()
operator|&&
name|ii
operator|>
literal|0
condition|)
block|{
name|correctionIndex
index|[
name|ii
index|]
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
block|}
name|possibleCorrection
operator|.
name|add
argument_list|(
name|singleWordPossibility
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|correctionIndex
index|[
literal|0
index|]
operator|==
name|possibilityList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|size
argument_list|()
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
block|}
name|RankedSpellPossibility
name|rsl
init|=
operator|new
name|RankedSpellPossibility
argument_list|()
decl_stmt|;
name|rsl
operator|.
name|setCorrections
argument_list|(
name|possibleCorrection
argument_list|)
expr_stmt|;
name|rsl
operator|.
name|setRank
argument_list|(
name|rank
argument_list|)
expr_stmt|;
return|return
name|rsl
return|;
block|}
DECL|method|hasNext
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|rankedPossibilityIterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
DECL|method|next
specifier|public
name|RankedSpellPossibility
name|next
parameter_list|()
block|{
return|return
name|rankedPossibilityIterator
operator|.
name|next
argument_list|()
return|;
block|}
DECL|method|remove
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class
end_unit
