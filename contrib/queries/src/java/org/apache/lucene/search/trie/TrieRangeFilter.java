begin_unit
begin_package
DECL|package|org.apache.lucene.search.trie
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|trie
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
name|java
operator|.
name|util
operator|.
name|Date
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
name|TermDocs
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
name|TermEnum
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|OpenBitSet
import|;
end_import
begin_comment
comment|/**  * Implementation of a Lucene {@link Filter} that implements trie-based range filtering.  * This filter depends on a specific structure of terms in the index that can only be created  * by {@link TrieUtils} methods.  * For more information, how the algorithm works, see the package description {@link org.apache.lucene.search.trie}.  */
end_comment
begin_class
DECL|class|TrieRangeFilter
specifier|public
specifier|final
class|class
name|TrieRangeFilter
extends|extends
name|Filter
block|{
comment|/**    * Universal constructor (expert use only): Uses already trie-converted min/max values.    * You can set<code>min</code> or<code>max</code> (but not both) to<code>null</code> to leave one bound open.    * With<code>minInclusive</code> and<code>maxInclusive</code> can be choosen, if the corresponding    * bound should be included or excluded from the range.    */
DECL|method|TrieRangeFilter
specifier|public
name|TrieRangeFilter
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
name|String
name|min
parameter_list|,
name|String
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|,
specifier|final
name|TrieUtils
name|variant
parameter_list|)
block|{
if|if
condition|(
name|min
operator|==
literal|null
operator|&&
name|max
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The min and max values cannot be both null."
argument_list|)
throw|;
name|this
operator|.
name|trieVariant
operator|=
name|variant
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// just for toString()
name|this
operator|.
name|minUnconverted
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|maxUnconverted
operator|=
name|max
expr_stmt|;
name|this
operator|.
name|minInclusive
operator|=
name|minInclusive
expr_stmt|;
name|this
operator|.
name|maxInclusive
operator|=
name|maxInclusive
expr_stmt|;
comment|// encode bounds
name|this
operator|.
name|min
operator|=
operator|(
name|min
operator|==
literal|null
operator|)
condition|?
name|trieVariant
operator|.
name|TRIE_CODED_NUMERIC_MIN
else|:
operator|(
name|minInclusive
condition|?
name|min
else|:
name|variant
operator|.
name|incrementTrieCoded
argument_list|(
name|min
argument_list|)
operator|)
expr_stmt|;
name|this
operator|.
name|max
operator|=
operator|(
name|max
operator|==
literal|null
operator|)
condition|?
name|trieVariant
operator|.
name|TRIE_CODED_NUMERIC_MAX
else|:
operator|(
name|maxInclusive
condition|?
name|max
else|:
name|variant
operator|.
name|decrementTrieCoded
argument_list|(
name|max
argument_list|)
operator|)
expr_stmt|;
comment|// check encoded values
if|if
condition|(
name|this
operator|.
name|min
operator|.
name|length
argument_list|()
operator|!=
name|trieVariant
operator|.
name|TRIE_CODED_LENGTH
operator|||
name|this
operator|.
name|max
operator|.
name|length
argument_list|()
operator|!=
name|trieVariant
operator|.
name|TRIE_CODED_LENGTH
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Invalid trie encoded numerical value representation (incompatible length)."
argument_list|)
throw|;
block|}
comment|/**    * Generates a trie filter using the supplied field with range bounds in numeric form (double).    * You can set<code>min</code> or<code>max</code> (but not both) to<code>null</code> to leave one bound open.    * With<code>minInclusive</code> and<code>maxInclusive</code> can be choosen, if the corresponding    * bound should be included or excluded from the range.    */
DECL|method|TrieRangeFilter
specifier|public
name|TrieRangeFilter
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|Double
name|min
parameter_list|,
specifier|final
name|Double
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|,
specifier|final
name|TrieUtils
name|variant
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
operator|(
name|min
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|variant
operator|.
name|doubleToTrieCoded
argument_list|(
name|min
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|,
operator|(
name|max
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|variant
operator|.
name|doubleToTrieCoded
argument_list|(
name|max
operator|.
name|doubleValue
argument_list|()
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|this
operator|.
name|minUnconverted
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|maxUnconverted
operator|=
name|max
expr_stmt|;
block|}
comment|/**    * Generates a trie filter using the supplied field with range bounds in date/time form.    * You can set<code>min</code> or<code>max</code> (but not both) to<code>null</code> to leave one bound open.    * With<code>minInclusive</code> and<code>maxInclusive</code> can be choosen, if the corresponding    * bound should be included or excluded from the range.    */
DECL|method|TrieRangeFilter
specifier|public
name|TrieRangeFilter
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|Date
name|min
parameter_list|,
specifier|final
name|Date
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|,
specifier|final
name|TrieUtils
name|variant
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
operator|(
name|min
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|variant
operator|.
name|dateToTrieCoded
argument_list|(
name|min
argument_list|)
argument_list|,
operator|(
name|max
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|variant
operator|.
name|dateToTrieCoded
argument_list|(
name|max
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|this
operator|.
name|minUnconverted
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|maxUnconverted
operator|=
name|max
expr_stmt|;
block|}
comment|/**    * Generates a trie filter using the supplied field with range bounds in integer form (long).    * You can set<code>min</code> or<code>max</code> (but not both) to<code>null</code> to leave one bound open.    * With<code>minInclusive</code> and<code>maxInclusive</code> can be choosen, if the corresponding    * bound should be included or excluded from the range.    */
DECL|method|TrieRangeFilter
specifier|public
name|TrieRangeFilter
parameter_list|(
specifier|final
name|String
name|field
parameter_list|,
specifier|final
name|Long
name|min
parameter_list|,
specifier|final
name|Long
name|max
parameter_list|,
specifier|final
name|boolean
name|minInclusive
parameter_list|,
specifier|final
name|boolean
name|maxInclusive
parameter_list|,
specifier|final
name|TrieUtils
name|variant
parameter_list|)
block|{
name|this
argument_list|(
name|field
argument_list|,
operator|(
name|min
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|min
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|,
operator|(
name|max
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|variant
operator|.
name|longToTrieCoded
argument_list|(
name|max
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|,
name|minInclusive
argument_list|,
name|maxInclusive
argument_list|,
name|variant
argument_list|)
expr_stmt|;
name|this
operator|.
name|minUnconverted
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|maxUnconverted
operator|=
name|max
expr_stmt|;
block|}
comment|//@Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toString
argument_list|(
literal|null
argument_list|)
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
specifier|final
name|String
name|field
parameter_list|)
block|{
specifier|final
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|field
operator|.
name|equals
argument_list|(
name|field
argument_list|)
condition|)
name|sb
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|append
argument_list|(
name|minInclusive
condition|?
literal|'['
else|:
literal|'{'
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|minUnconverted
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|minUnconverted
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" TO "
argument_list|)
operator|.
name|append
argument_list|(
operator|(
name|maxUnconverted
operator|==
literal|null
operator|)
condition|?
literal|"*"
else|:
name|maxUnconverted
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|maxInclusive
condition|?
literal|']'
else|:
literal|'}'
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Two instances are equal if they have the same trie-encoded range bounds, same field, and same variant.    * If one of the instances uses an exclusive lower bound, it is equal to a range with inclusive bound,    * when the inclusive lower bound is equal to the incremented exclusive lower bound of the other one.    * The same applys for the upper bound in other direction.    */
comment|//@Override
DECL|method|equals
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|TrieRangeFilter
condition|)
block|{
name|TrieRangeFilter
name|q
init|=
operator|(
name|TrieRangeFilter
operator|)
name|o
decl_stmt|;
comment|// trieVariants are singleton per type, so no equals needed.
return|return
operator|(
name|field
operator|==
name|q
operator|.
name|field
operator|&&
name|min
operator|.
name|equals
argument_list|(
name|q
operator|.
name|min
argument_list|)
operator|&&
name|max
operator|.
name|equals
argument_list|(
name|q
operator|.
name|max
argument_list|)
operator|&&
name|trieVariant
operator|==
name|q
operator|.
name|trieVariant
operator|)
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|//@Override
DECL|method|hashCode
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
comment|// the hash code uses from the variant only the number of bits, as this is unique for the variant
return|return
name|field
operator|.
name|hashCode
argument_list|()
operator|+
operator|(
name|min
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x14fa55fb
operator|)
operator|+
operator|(
name|max
operator|.
name|hashCode
argument_list|()
operator|^
literal|0x733fa5fe
operator|)
operator|+
operator|(
name|trieVariant
operator|.
name|TRIE_BITS
operator|^
literal|0x64365465
operator|)
return|;
block|}
comment|/** prints the String in hexadecimal \\u notation (for debugging of<code>setBits()</code>) */
DECL|method|stringToHexDigits
specifier|private
name|String
name|stringToHexDigits
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|s
operator|.
name|length
argument_list|()
operator|*
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|c
init|=
name|s
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|c
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\\u"
argument_list|)
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
name|int
operator|)
name|ch
argument_list|)
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
comment|/** Marks documents in a specific range. Code borrowed from original RangeFilter and simplified (and returns number of terms) */
DECL|method|setBits
specifier|private
name|int
name|setBits
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|TermDocs
name|termDocs
parameter_list|,
specifier|final
name|OpenBitSet
name|bits
parameter_list|,
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|)
throws|throws
name|IOException
block|{
comment|//System.out.println(stringToHexDigits(lowerTerm)+" TO "+stringToHexDigits(upperTerm));
name|int
name|count
init|=
literal|0
decl_stmt|,
name|len
init|=
name|lowerTerm
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|String
name|field
decl_stmt|;
if|if
condition|(
name|len
operator|<
name|trieVariant
operator|.
name|TRIE_CODED_LENGTH
condition|)
block|{
comment|// lower precision value is in helper field
name|field
operator|=
operator|(
name|this
operator|.
name|field
operator|+
name|trieVariant
operator|.
name|LOWER_PRECISION_FIELD_NAME_SUFFIX
operator|)
operator|.
name|intern
argument_list|()
expr_stmt|;
comment|// add padding before lower precision values to group them
name|lowerTerm
operator|=
operator|new
name|StringBuffer
argument_list|(
name|len
operator|+
literal|1
argument_list|)
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
name|trieVariant
operator|.
name|TRIE_CODED_PADDING_START
operator|+
name|len
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|lowerTerm
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
name|upperTerm
operator|=
operator|new
name|StringBuffer
argument_list|(
name|len
operator|+
literal|1
argument_list|)
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
name|trieVariant
operator|.
name|TRIE_CODED_PADDING_START
operator|+
name|len
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|upperTerm
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|// length is longer by 1 char because of padding
name|len
operator|++
expr_stmt|;
block|}
else|else
block|{
comment|// full precision value is in original field
name|field
operator|=
name|this
operator|.
name|field
expr_stmt|;
block|}
specifier|final
name|TermEnum
name|enumerator
init|=
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|field
argument_list|,
name|lowerTerm
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
do|do
block|{
specifier|final
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|!=
literal|null
operator|&&
name|term
operator|.
name|field
argument_list|()
operator|==
name|field
condition|)
block|{
comment|// break out when upperTerm reached or length of term is different
specifier|final
name|String
name|t
init|=
name|term
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|!=
name|t
operator|.
name|length
argument_list|()
operator|||
name|t
operator|.
name|compareTo
argument_list|(
name|upperTerm
argument_list|)
operator|>
literal|0
condition|)
break|break;
comment|// we have a good term, find the docs
name|count
operator|++
expr_stmt|;
name|termDocs
operator|.
name|seek
argument_list|(
name|enumerator
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
name|bits
operator|.
name|set
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
break|break;
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/** Splits range recursively (and returns number of terms) */
DECL|method|splitRange
specifier|private
name|int
name|splitRange
parameter_list|(
specifier|final
name|IndexReader
name|reader
parameter_list|,
specifier|final
name|TermDocs
name|termDocs
parameter_list|,
specifier|final
name|OpenBitSet
name|bits
parameter_list|,
specifier|final
name|String
name|min
parameter_list|,
specifier|final
name|boolean
name|lowerBoundOpen
parameter_list|,
specifier|final
name|String
name|max
parameter_list|,
specifier|final
name|boolean
name|upperBoundOpen
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|length
init|=
name|min
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|String
name|minShort
init|=
name|lowerBoundOpen
condition|?
name|min
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|length
operator|-
literal|1
argument_list|)
else|:
name|trieVariant
operator|.
name|incrementTrieCoded
argument_list|(
name|min
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|length
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|String
name|maxShort
init|=
name|upperBoundOpen
condition|?
name|max
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|length
operator|-
literal|1
argument_list|)
else|:
name|trieVariant
operator|.
name|decrementTrieCoded
argument_list|(
name|max
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|length
operator|-
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
operator|==
literal|1
operator|||
name|minShort
operator|.
name|compareTo
argument_list|(
name|maxShort
argument_list|)
operator|>=
literal|0
condition|)
block|{
comment|// we are in the lowest precision or the current precision is not existent
name|count
operator|+=
name|setBits
argument_list|(
name|reader
argument_list|,
name|termDocs
argument_list|,
name|bits
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Avoid too much seeking: first go deeper into lower precision
comment|// (in IndexReader's TermEnum these terms are earlier).
comment|// Do this only, if the current length is not trieVariant.TRIE_CODED_LENGTH (not full precision),
comment|// because terms from the highest prec come before all lower prec terms
comment|// (because the field name is ordered before the suffixed one).
if|if
condition|(
name|length
operator|!=
name|trieVariant
operator|.
name|TRIE_CODED_LENGTH
condition|)
name|count
operator|+=
name|splitRange
argument_list|(
name|reader
argument_list|,
name|termDocs
argument_list|,
name|bits
argument_list|,
name|minShort
argument_list|,
name|lowerBoundOpen
argument_list|,
name|maxShort
argument_list|,
name|upperBoundOpen
argument_list|)
expr_stmt|;
comment|// Avoid too much seeking: set bits for lower part of current (higher) precision.
comment|// These terms come later in IndexReader's TermEnum.
if|if
condition|(
operator|!
name|lowerBoundOpen
condition|)
block|{
name|count
operator|+=
name|setBits
argument_list|(
name|reader
argument_list|,
name|termDocs
argument_list|,
name|bits
argument_list|,
name|min
argument_list|,
name|trieVariant
operator|.
name|decrementTrieCoded
argument_list|(
name|minShort
operator|+
name|trieVariant
operator|.
name|TRIE_CODED_SYMBOL_MIN
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Avoid too much seeking: set bits for upper part of current precision.
comment|// These terms come later in IndexReader's TermEnum.
if|if
condition|(
operator|!
name|upperBoundOpen
condition|)
block|{
name|count
operator|+=
name|setBits
argument_list|(
name|reader
argument_list|,
name|termDocs
argument_list|,
name|bits
argument_list|,
name|trieVariant
operator|.
name|incrementTrieCoded
argument_list|(
name|maxShort
operator|+
name|trieVariant
operator|.
name|TRIE_CODED_SYMBOL_MAX
argument_list|)
argument_list|,
name|max
argument_list|)
expr_stmt|;
block|}
comment|// If the first step (see above) was not done (because length==trieVariant.TRIE_CODED_LENGTH) we do it now.
if|if
condition|(
name|length
operator|==
name|trieVariant
operator|.
name|TRIE_CODED_LENGTH
condition|)
name|count
operator|+=
name|splitRange
argument_list|(
name|reader
argument_list|,
name|termDocs
argument_list|,
name|bits
argument_list|,
name|minShort
argument_list|,
name|lowerBoundOpen
argument_list|,
name|maxShort
argument_list|,
name|upperBoundOpen
argument_list|)
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
comment|/**    * Returns a DocIdSet that provides the documents which should be permitted or prohibited in search results.    */
comment|//@Override
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
if|if
condition|(
name|min
operator|.
name|compareTo
argument_list|(
name|max
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// shortcut: if min>max, no docs will match!
name|lastNumberOfTerms
operator|=
literal|0
expr_stmt|;
return|return
name|DocIdSet
operator|.
name|EMPTY_DOCIDSET
return|;
block|}
else|else
block|{
specifier|final
name|OpenBitSet
name|bits
init|=
operator|new
name|OpenBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
try|try
block|{
name|lastNumberOfTerms
operator|=
name|splitRange
argument_list|(
name|reader
argument_list|,
name|termDocs
argument_list|,
name|bits
argument_list|,
name|min
argument_list|,
name|trieVariant
operator|.
name|TRIE_CODED_NUMERIC_MIN
operator|.
name|equals
argument_list|(
name|min
argument_list|)
argument_list|,
name|max
argument_list|,
name|trieVariant
operator|.
name|TRIE_CODED_NUMERIC_MAX
operator|.
name|equals
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|bits
return|;
block|}
block|}
comment|/**    * EXPERT: Return the number of terms visited during the last execution of {@link #getDocIdSet}.    * This may be used for performance comparisons of different trie variants and their effectiveness.    * This method is not thread safe, be sure to only call it when no query is running!    * @throws IllegalStateException if {@link #getDocIdSet} was not yet executed.    */
DECL|method|getLastNumberOfTerms
specifier|public
name|int
name|getLastNumberOfTerms
parameter_list|()
block|{
if|if
condition|(
name|lastNumberOfTerms
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
return|return
name|lastNumberOfTerms
return|;
block|}
comment|// members
DECL|field|field
DECL|field|min
DECL|field|max
specifier|private
specifier|final
name|String
name|field
decl_stmt|,
name|min
decl_stmt|,
name|max
decl_stmt|;
DECL|field|trieVariant
specifier|private
specifier|final
name|TrieUtils
name|trieVariant
decl_stmt|;
DECL|field|minInclusive
DECL|field|maxInclusive
specifier|private
specifier|final
name|boolean
name|minInclusive
decl_stmt|,
name|maxInclusive
decl_stmt|;
DECL|field|minUnconverted
DECL|field|maxUnconverted
specifier|private
name|Object
name|minUnconverted
decl_stmt|,
name|maxUnconverted
decl_stmt|;
DECL|field|lastNumberOfTerms
specifier|private
name|int
name|lastNumberOfTerms
init|=
operator|-
literal|1
decl_stmt|;
block|}
end_class
end_unit
