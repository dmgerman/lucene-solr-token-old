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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DocValues
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
name|RandomAccessOrds
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
name|SortedDocValues
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
name|SortedSetDocValues
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
name|BytesRef
import|;
end_import
begin_comment
comment|/** Selects a value from the document's set to use as the representative value */
end_comment
begin_class
DECL|class|SortedSetSelector
specifier|public
class|class
name|SortedSetSelector
block|{
comment|/**     * Type of selection to perform.    *<p>    * Limitations:    *<ul>    *<li>Fields containing {@link Integer#MAX_VALUE} or more unique values    *       are unsupported.    *<li>Selectors other than ({@link Type#MIN}) require     *       optional codec support. However several codecs provided by Lucene,     *       including the current default codec, support this.    *</ul>    */
DECL|enum|Type
specifier|public
enum|enum
name|Type
block|{
comment|/**       * Selects the minimum value in the set       */
DECL|enum constant|MIN
name|MIN
block|,
comment|/**       * Selects the maximum value in the set       */
DECL|enum constant|MAX
name|MAX
block|,
comment|/**       * Selects the middle value in the set.      *<p>      * If the set has an even number of values, the lower of the middle two is chosen.      */
DECL|enum constant|MIDDLE_MIN
name|MIDDLE_MIN
block|,
comment|/**       * Selects the middle value in the set.      *<p>      * If the set has an even number of values, the higher of the middle two is chosen      */
DECL|enum constant|MIDDLE_MAX
name|MIDDLE_MAX
block|}
comment|/** Wraps a multi-valued SortedSetDocValues as a single-valued view, using the specified selector */
DECL|method|wrap
specifier|public
specifier|static
name|SortedDocValues
name|wrap
parameter_list|(
name|SortedSetDocValues
name|sortedSet
parameter_list|,
name|Type
name|selector
parameter_list|)
block|{
if|if
condition|(
name|sortedSet
operator|.
name|getValueCount
argument_list|()
operator|>=
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"fields containing more than "
operator|+
operator|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
operator|)
operator|+
literal|" unique terms are unsupported"
argument_list|)
throw|;
block|}
name|SortedDocValues
name|singleton
init|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|sortedSet
argument_list|)
decl_stmt|;
if|if
condition|(
name|singleton
operator|!=
literal|null
condition|)
block|{
comment|// it's actually single-valued in practice, but indexed as multi-valued,
comment|// so just sort on the underlying single-valued dv directly.
comment|// regardless of selector type, this optimization is safe!
return|return
name|singleton
return|;
block|}
elseif|else
if|if
condition|(
name|selector
operator|==
name|Type
operator|.
name|MIN
condition|)
block|{
return|return
operator|new
name|MinValue
argument_list|(
name|sortedSet
argument_list|)
return|;
block|}
else|else
block|{
if|if
condition|(
name|sortedSet
operator|instanceof
name|RandomAccessOrds
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"codec does not support random access ordinals, cannot use selector: "
operator|+
name|selector
argument_list|)
throw|;
block|}
name|RandomAccessOrds
name|randomOrds
init|=
operator|(
name|RandomAccessOrds
operator|)
name|sortedSet
decl_stmt|;
switch|switch
condition|(
name|selector
condition|)
block|{
case|case
name|MAX
case|:
return|return
operator|new
name|MaxValue
argument_list|(
name|randomOrds
argument_list|)
return|;
case|case
name|MIDDLE_MIN
case|:
return|return
operator|new
name|MiddleMinValue
argument_list|(
name|randomOrds
argument_list|)
return|;
case|case
name|MIDDLE_MAX
case|:
return|return
operator|new
name|MiddleMaxValue
argument_list|(
name|randomOrds
argument_list|)
return|;
case|case
name|MIN
case|:
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
block|}
comment|/** Wraps a SortedSetDocValues and returns the first ordinal (min) */
DECL|class|MinValue
specifier|static
class|class
name|MinValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|SortedSetDocValues
name|in
decl_stmt|;
DECL|method|MinValue
name|MinValue
parameter_list|(
name|SortedSetDocValues
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|nextOrd
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/** Wraps a SortedSetDocValues and returns the last ordinal (max) */
DECL|class|MaxValue
specifier|static
class|class
name|MaxValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|RandomAccessOrds
name|in
decl_stmt|;
DECL|method|MaxValue
name|MaxValue
parameter_list|(
name|RandomAccessOrds
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|in
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|ordAt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/** Wraps a SortedSetDocValues and returns the middle ordinal (or min of the two) */
DECL|class|MiddleMinValue
specifier|static
class|class
name|MiddleMinValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|RandomAccessOrds
name|in
decl_stmt|;
DECL|method|MiddleMinValue
name|MiddleMinValue
parameter_list|(
name|RandomAccessOrds
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|in
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|ordAt
argument_list|(
operator|(
name|count
operator|-
literal|1
operator|)
operator|>>>
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
comment|/** Wraps a SortedSetDocValues and returns the middle ordinal (or max of the two) */
DECL|class|MiddleMaxValue
specifier|static
class|class
name|MiddleMaxValue
extends|extends
name|SortedDocValues
block|{
DECL|field|in
specifier|final
name|RandomAccessOrds
name|in
decl_stmt|;
DECL|method|MiddleMaxValue
name|MiddleMaxValue
parameter_list|(
name|RandomAccessOrds
name|in
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
name|in
operator|.
name|setDocument
argument_list|(
name|docID
argument_list|)
expr_stmt|;
specifier|final
name|int
name|count
init|=
name|in
operator|.
name|cardinality
argument_list|()
decl_stmt|;
if|if
condition|(
name|count
operator|==
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|ordAt
argument_list|(
name|count
operator|>>>
literal|1
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|lookupOrd
specifier|public
name|void
name|lookupOrd
parameter_list|(
name|int
name|ord
parameter_list|,
name|BytesRef
name|result
parameter_list|)
block|{
name|in
operator|.
name|lookupOrd
argument_list|(
name|ord
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getValueCount
specifier|public
name|int
name|getValueCount
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|getValueCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|lookupTerm
specifier|public
name|int
name|lookupTerm
parameter_list|(
name|BytesRef
name|key
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|in
operator|.
name|lookupTerm
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
block|}
end_class
end_unit