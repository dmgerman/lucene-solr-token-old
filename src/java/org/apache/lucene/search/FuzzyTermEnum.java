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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|Term
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
comment|/** Subclass of FilteredTermEnum for enumerating all terms that are similiar  * to the specified filter term.  *  *<p>Term enumerations are always ordered by Term.compareTo().  Each term in  * the enumeration is greater than all that precede it.  */
end_comment
begin_class
DECL|class|FuzzyTermEnum
specifier|public
specifier|final
class|class
name|FuzzyTermEnum
extends|extends
name|FilteredTermEnum
block|{
comment|/* This should be somewhere around the average long word.      * If it is longer, we waste time and space. If it is shorter, we waste a      * little bit of time growing the array as we encounter longer words.      */
DECL|field|TYPICAL_LONGEST_WORD_IN_INDEX
specifier|private
specifier|static
specifier|final
name|int
name|TYPICAL_LONGEST_WORD_IN_INDEX
init|=
literal|19
decl_stmt|;
comment|/* Allows us save time required to create a new array      * everytime similarity is called.      */
DECL|field|d
specifier|private
name|int
index|[]
index|[]
name|d
decl_stmt|;
DECL|field|similarity
specifier|private
name|float
name|similarity
decl_stmt|;
DECL|field|endEnum
specifier|private
name|boolean
name|endEnum
init|=
literal|false
decl_stmt|;
DECL|field|searchTerm
specifier|private
name|Term
name|searchTerm
init|=
literal|null
decl_stmt|;
DECL|field|field
specifier|private
specifier|final
name|String
name|field
decl_stmt|;
DECL|field|text
specifier|private
specifier|final
name|String
name|text
decl_stmt|;
DECL|field|prefix
specifier|private
specifier|final
name|String
name|prefix
decl_stmt|;
DECL|field|minimumSimilarity
specifier|private
specifier|final
name|float
name|minimumSimilarity
decl_stmt|;
DECL|field|scale_factor
specifier|private
specifier|final
name|float
name|scale_factor
decl_stmt|;
DECL|field|maxDistances
specifier|private
specifier|final
name|int
index|[]
name|maxDistances
init|=
operator|new
name|int
index|[
name|TYPICAL_LONGEST_WORD_IN_INDEX
index|]
decl_stmt|;
comment|/**      * Creates a FuzzyTermEnum with an empty prefix and a minSimilarity of 0.5f.      *       * @param reader      * @param term      * @throws IOException      * @see #FuzzyTermEnum(IndexReader, Term, float, int)      */
DECL|method|FuzzyTermEnum
specifier|public
name|FuzzyTermEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|term
argument_list|,
name|FuzzyQuery
operator|.
name|defaultMinSimilarity
argument_list|,
name|FuzzyQuery
operator|.
name|defaultPrefixLength
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a FuzzyTermEnum with an empty prefix.      *       * @param reader      * @param term      * @param minSimilarity      * @throws IOException      * @see #FuzzyTermEnum(IndexReader, Term, float, int)      */
DECL|method|FuzzyTermEnum
specifier|public
name|FuzzyTermEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|,
name|float
name|minSimilarity
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|term
argument_list|,
name|minSimilarity
argument_list|,
name|FuzzyQuery
operator|.
name|defaultPrefixLength
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructor for enumeration of all terms from specified<code>reader</code> which share a prefix of      * length<code>prefixLength</code> with<code>term</code> and which have a fuzzy similarity&gt;      *<code>minSimilarity</code>.       *       * @param reader Delivers terms.      * @param term Pattern term.      * @param minSimilarity Minimum required similarity for terms from the reader. Default value is 0.5f.      * @param prefixLength Length of required common prefix. Default value is 0.      * @throws IOException      */
DECL|method|FuzzyTermEnum
specifier|public
name|FuzzyTermEnum
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|Term
name|term
parameter_list|,
specifier|final
name|float
name|minSimilarity
parameter_list|,
specifier|final
name|int
name|prefixLength
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|()
expr_stmt|;
if|if
condition|(
name|minSimilarity
operator|>=
literal|1.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity cannot be greater than or equal to 1"
argument_list|)
throw|;
elseif|else
if|if
condition|(
name|minSimilarity
operator|<
literal|0.0f
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minimumSimilarity cannot be less than 0"
argument_list|)
throw|;
if|if
condition|(
name|prefixLength
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"prefixLength cannot be less than 0"
argument_list|)
throw|;
name|this
operator|.
name|minimumSimilarity
operator|=
name|minSimilarity
expr_stmt|;
name|this
operator|.
name|scale_factor
operator|=
literal|1.0f
operator|/
operator|(
literal|1.0f
operator|-
name|minimumSimilarity
operator|)
expr_stmt|;
name|this
operator|.
name|searchTerm
operator|=
name|term
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|searchTerm
operator|.
name|field
argument_list|()
expr_stmt|;
comment|//The prefix could be longer than the word.
comment|//It's kind of silly though.  It means we must match the entire word.
specifier|final
name|int
name|fullSearchTermLength
init|=
name|searchTerm
operator|.
name|text
argument_list|()
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|realPrefixLength
init|=
name|prefixLength
operator|>
name|fullSearchTermLength
condition|?
name|fullSearchTermLength
else|:
name|prefixLength
decl_stmt|;
name|this
operator|.
name|text
operator|=
name|searchTerm
operator|.
name|text
argument_list|()
operator|.
name|substring
argument_list|(
name|realPrefixLength
argument_list|)
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|searchTerm
operator|.
name|text
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|realPrefixLength
argument_list|)
expr_stmt|;
name|initializeMaxDistances
argument_list|()
expr_stmt|;
name|this
operator|.
name|d
operator|=
name|initDistanceArray
argument_list|()
expr_stmt|;
name|setEnum
argument_list|(
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|searchTerm
operator|.
name|field
argument_list|()
argument_list|,
name|prefix
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * The termCompare method in FuzzyTermEnum uses Levenshtein distance to       * calculate the distance between the given term and the comparing term.       */
DECL|method|termCompare
specifier|protected
specifier|final
name|boolean
name|termCompare
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
if|if
condition|(
name|field
operator|==
name|term
operator|.
name|field
argument_list|()
operator|&&
name|term
operator|.
name|text
argument_list|()
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
specifier|final
name|String
name|target
init|=
name|term
operator|.
name|text
argument_list|()
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|similarity
operator|=
name|similarity
argument_list|(
name|target
argument_list|)
expr_stmt|;
return|return
operator|(
name|similarity
operator|>
name|minimumSimilarity
operator|)
return|;
block|}
name|endEnum
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
DECL|method|difference
specifier|public
specifier|final
name|float
name|difference
parameter_list|()
block|{
return|return
call|(
name|float
call|)
argument_list|(
operator|(
name|similarity
operator|-
name|minimumSimilarity
operator|)
operator|*
name|scale_factor
argument_list|)
return|;
block|}
DECL|method|endEnum
specifier|public
specifier|final
name|boolean
name|endEnum
parameter_list|()
block|{
return|return
name|endEnum
return|;
block|}
comment|/******************************      * Compute Levenshtein distance      ******************************/
comment|/**      * Finds and returns the smallest of three integers       */
DECL|method|min
specifier|private
specifier|static
specifier|final
name|int
name|min
parameter_list|(
name|int
name|a
parameter_list|,
name|int
name|b
parameter_list|,
name|int
name|c
parameter_list|)
block|{
specifier|final
name|int
name|t
init|=
operator|(
name|a
operator|<
name|b
operator|)
condition|?
name|a
else|:
name|b
decl_stmt|;
return|return
operator|(
name|t
operator|<
name|c
operator|)
condition|?
name|t
else|:
name|c
return|;
block|}
DECL|method|initDistanceArray
specifier|private
specifier|final
name|int
index|[]
index|[]
name|initDistanceArray
parameter_list|()
block|{
return|return
operator|new
name|int
index|[
name|this
operator|.
name|text
operator|.
name|length
argument_list|()
operator|+
literal|1
index|]
index|[
name|TYPICAL_LONGEST_WORD_IN_INDEX
index|]
return|;
block|}
comment|/**    *<p>Similarity returns a number that is 1.0f or less (including negative numbers)    * based on how similar the Term is compared to a target term.  It returns    * exactly 0.0f when    *<pre>    *    editDistance&lt; maximumEditDistance</pre>    * Otherwise it returns:    *<pre>    *    1 - (editDistance / length)</pre>    * where length is the length of the shortest term (text or target) including a    * prefix that are identical and editDistance is the Levenshtein distance for    * the two words.</p>    *    *<p>Embedded within this algorithm is a fail-fast Levenshtein distance    * algorithm.  The fail-fast algorithm differs from the standard Levenshtein    * distance algorithm in that it is aborted if it is discovered that the    * mimimum distance between the words is greater than some threshold.    *    *<p>To calculate the maximum distance threshold we use the following formula:    *<pre>    *     (1 - minimumSimilarity) / length</pre>    * where length is the shortest term including any prefix that is not part of the    * similarity comparision.  This formula was derived by solving for what maximum value    * of distance returns false for the following statements:    *<pre>    *   similarity = 1 - ((float)distance / (float) (prefixLength + Math.min(textlen, targetlen)));    *   return (similarity> minimumSimilarity);</pre>    * where distance is the Levenshtein distance for the two words.    *</p>    *<p>Levenshtein distance (also known as edit distance) is a measure of similiarity    * between two strings where the distance is measured as the number of character    * deletions, insertions or substitutions required to transform one string to    * the other string.    * @param target the target word or phrase    * @return the similarity,  0.0 or less indicates that it matches less than the required    * threshold and 1.0 indicates that the text and target are identical    */
DECL|method|similarity
specifier|private
specifier|synchronized
specifier|final
name|float
name|similarity
parameter_list|(
specifier|final
name|String
name|target
parameter_list|)
block|{
specifier|final
name|int
name|m
init|=
name|target
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|text
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
comment|//we don't have antyhing to compare.  That means if we just add
comment|//the letters for m we get the new word
return|return
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|0.0f
else|:
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|m
operator|/
name|prefix
operator|.
name|length
argument_list|()
operator|)
return|;
block|}
if|if
condition|(
name|m
operator|==
literal|0
condition|)
block|{
return|return
name|prefix
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|0.0f
else|:
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|n
operator|/
name|prefix
operator|.
name|length
argument_list|()
operator|)
return|;
block|}
specifier|final
name|int
name|maxDistance
init|=
name|getMaxDistance
argument_list|(
name|m
argument_list|)
decl_stmt|;
if|if
condition|(
name|maxDistance
operator|<
name|Math
operator|.
name|abs
argument_list|(
name|m
operator|-
name|n
argument_list|)
condition|)
block|{
comment|//just adding the characters of m to n or vice-versa results in
comment|//too many edits
comment|//for example "pre" length is 3 and "prefixes" length is 8.  We can see that
comment|//given this optimal circumstance, the edit distance cannot be less than 5.
comment|//which is 8-3 or more precisesly Math.abs(3-8).
comment|//if our maximum edit distance is 4, than we can discard this word
comment|//without looking at it.
return|return
literal|0.0f
return|;
block|}
comment|//let's make sure we have enough room in our array to do the distance calculations.
if|if
condition|(
name|d
index|[
literal|0
index|]
operator|.
name|length
operator|<=
name|m
condition|)
block|{
name|growDistanceArray
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
comment|// init matrix d
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
name|d
index|[
name|i
index|]
index|[
literal|0
index|]
operator|=
name|i
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
name|d
index|[
literal|0
index|]
index|[
name|j
index|]
operator|=
name|j
expr_stmt|;
comment|// start computing edit distance
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|int
name|bestPossibleEditDistance
init|=
name|m
decl_stmt|;
specifier|final
name|char
name|s_i
init|=
name|text
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|s_i
operator|!=
name|target
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|1
argument_list|)
condition|)
block|{
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
index|]
argument_list|,
name|d
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|,
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|d
index|[
name|i
index|]
index|[
name|j
index|]
operator|=
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
index|]
operator|+
literal|1
argument_list|,
name|d
index|[
name|i
index|]
index|[
name|j
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|,
name|d
index|[
name|i
operator|-
literal|1
index|]
index|[
name|j
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|bestPossibleEditDistance
operator|=
name|Math
operator|.
name|min
argument_list|(
name|bestPossibleEditDistance
argument_list|,
name|d
index|[
name|i
index|]
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
comment|//After calculating row i, the best possible edit distance
comment|//can be found by found by finding the smallest value in a given column.
comment|//If the bestPossibleEditDistance is greater than the max distance, abort.
if|if
condition|(
name|i
operator|>
name|maxDistance
operator|&&
name|bestPossibleEditDistance
operator|>
name|maxDistance
condition|)
block|{
comment|//equal is okay, but not greater
comment|//the closest the target can be to the text is just too far away.
comment|//this target is leaving the party early.
return|return
literal|0.0f
return|;
block|}
block|}
comment|// this will return less than 0.0 when the edit distance is
comment|// greater than the number of characters in the shorter word.
comment|// but this was the formula that was previously used in FuzzyTermEnum,
comment|// so it has not been changed (even though minimumSimilarity must be
comment|// greater than 0.0)
return|return
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|d
index|[
name|n
index|]
index|[
name|m
index|]
operator|/
call|(
name|float
call|)
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|+
name|Math
operator|.
name|min
argument_list|(
name|n
argument_list|,
name|m
argument_list|)
argument_list|)
operator|)
return|;
block|}
comment|/**    * Grow the second dimension of the array, so that we can calculate the    * Levenshtein difference.    */
DECL|method|growDistanceArray
specifier|private
name|void
name|growDistanceArray
parameter_list|(
name|int
name|m
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|d
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|d
index|[
name|i
index|]
operator|=
operator|new
name|int
index|[
name|m
operator|+
literal|1
index|]
expr_stmt|;
block|}
block|}
comment|/**    * The max Distance is the maximum Levenshtein distance for the text    * compared to some other value that results in score that is    * better than the minimum similarity.    * @param m the length of the "other value"    * @return the maximum levenshtein distance that we care about    */
DECL|method|getMaxDistance
specifier|private
specifier|final
name|int
name|getMaxDistance
parameter_list|(
name|int
name|m
parameter_list|)
block|{
return|return
operator|(
name|m
operator|<
name|maxDistances
operator|.
name|length
operator|)
condition|?
name|maxDistances
index|[
name|m
index|]
else|:
name|calculateMaxDistance
argument_list|(
name|m
argument_list|)
return|;
block|}
DECL|method|initializeMaxDistances
specifier|private
name|void
name|initializeMaxDistances
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|maxDistances
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|maxDistances
index|[
name|i
index|]
operator|=
name|calculateMaxDistance
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|calculateMaxDistance
specifier|private
name|int
name|calculateMaxDistance
parameter_list|(
name|int
name|m
parameter_list|)
block|{
return|return
call|(
name|int
call|)
argument_list|(
operator|(
literal|1
operator|-
name|minimumSimilarity
operator|)
operator|*
operator|(
name|Math
operator|.
name|min
argument_list|(
name|text
operator|.
name|length
argument_list|()
argument_list|,
name|m
argument_list|)
operator|+
name|prefix
operator|.
name|length
argument_list|()
operator|)
argument_list|)
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//call super.close() and let the garbage collector do its work.
block|}
block|}
end_class
end_unit
