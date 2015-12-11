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
name|NumericDocValues
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
name|SortedNumericDocValues
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
name|LegacyNumericUtils
import|;
end_import
begin_comment
comment|/**   * Selects a value from the document's list to use as the representative value   *<p>  * This provides a NumericDocValues view over the SortedNumeric, for use with sorting,  * expressions, function queries, etc.  */
end_comment
begin_class
DECL|class|SortedNumericSelector
specifier|public
class|class
name|SortedNumericSelector
block|{
comment|/**     * Type of selection to perform.    */
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
comment|// TODO: we could do MEDIAN in constant time (at most 2 lookups)
block|}
comment|/**     * Wraps a multi-valued SortedNumericDocValues as a single-valued view, using the specified selector     * and numericType.    */
DECL|method|wrap
specifier|public
specifier|static
name|NumericDocValues
name|wrap
parameter_list|(
name|SortedNumericDocValues
name|sortedNumeric
parameter_list|,
name|Type
name|selector
parameter_list|,
name|SortField
operator|.
name|Type
name|numericType
parameter_list|)
block|{
if|if
condition|(
name|numericType
operator|!=
name|SortField
operator|.
name|Type
operator|.
name|INT
operator|&&
name|numericType
operator|!=
name|SortField
operator|.
name|Type
operator|.
name|LONG
operator|&&
name|numericType
operator|!=
name|SortField
operator|.
name|Type
operator|.
name|FLOAT
operator|&&
name|numericType
operator|!=
name|SortField
operator|.
name|Type
operator|.
name|DOUBLE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"numericType must be a numeric type"
argument_list|)
throw|;
block|}
specifier|final
name|NumericDocValues
name|view
decl_stmt|;
name|NumericDocValues
name|singleton
init|=
name|DocValues
operator|.
name|unwrapSingleton
argument_list|(
name|sortedNumeric
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
name|view
operator|=
name|singleton
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|selector
condition|)
block|{
case|case
name|MIN
case|:
name|view
operator|=
operator|new
name|MinValue
argument_list|(
name|sortedNumeric
argument_list|)
expr_stmt|;
break|break;
case|case
name|MAX
case|:
name|view
operator|=
operator|new
name|MaxValue
argument_list|(
name|sortedNumeric
argument_list|)
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
comment|// undo the numericutils sortability
switch|switch
condition|(
name|numericType
condition|)
block|{
case|case
name|FLOAT
case|:
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|LegacyNumericUtils
operator|.
name|sortableFloatBits
argument_list|(
operator|(
name|int
operator|)
name|view
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
case|case
name|DOUBLE
case|:
return|return
operator|new
name|NumericDocValues
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|long
name|get
parameter_list|(
name|int
name|docID
parameter_list|)
block|{
return|return
name|LegacyNumericUtils
operator|.
name|sortableDoubleBits
argument_list|(
name|view
operator|.
name|get
argument_list|(
name|docID
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
default|default:
return|return
name|view
return|;
block|}
block|}
comment|/** Wraps a SortedNumericDocValues and returns the first value (min) */
DECL|class|MinValue
specifier|static
class|class
name|MinValue
extends|extends
name|NumericDocValues
block|{
DECL|field|in
specifier|final
name|SortedNumericDocValues
name|in
decl_stmt|;
DECL|method|MinValue
name|MinValue
parameter_list|(
name|SortedNumericDocValues
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
DECL|method|get
specifier|public
name|long
name|get
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
if|if
condition|(
name|in
operator|.
name|count
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|0
return|;
comment|// missing
block|}
else|else
block|{
return|return
name|in
operator|.
name|valueAt
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
block|}
comment|/** Wraps a SortedNumericDocValues and returns the last value (max) */
DECL|class|MaxValue
specifier|static
class|class
name|MaxValue
extends|extends
name|NumericDocValues
block|{
DECL|field|in
specifier|final
name|SortedNumericDocValues
name|in
decl_stmt|;
DECL|method|MaxValue
name|MaxValue
parameter_list|(
name|SortedNumericDocValues
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
DECL|method|get
specifier|public
name|long
name|get
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
name|count
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
literal|0
return|;
comment|// missing
block|}
else|else
block|{
return|return
name|in
operator|.
name|valueAt
argument_list|(
name|count
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
