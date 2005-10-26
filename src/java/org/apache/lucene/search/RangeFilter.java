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
name|java
operator|.
name|util
operator|.
name|BitSet
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
name|IndexReader
import|;
end_import
begin_comment
comment|/**  * A Filter that restricts search results to a range of values in a given  * field.  *   *<p>  * This code borrows heavily from {@link RangeQuery}, but is implemented as a Filter  * (much like {@link DateFilter}).  *</p>  */
end_comment
begin_class
DECL|class|RangeFilter
specifier|public
class|class
name|RangeFilter
extends|extends
name|Filter
block|{
DECL|field|fieldName
specifier|private
name|String
name|fieldName
decl_stmt|;
DECL|field|lowerTerm
specifier|private
name|String
name|lowerTerm
decl_stmt|;
DECL|field|upperTerm
specifier|private
name|String
name|upperTerm
decl_stmt|;
DECL|field|includeLower
specifier|private
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|private
name|boolean
name|includeUpper
decl_stmt|;
comment|/**      * @param fieldName The field this range applies to      * @param lowerTerm The lower bound on this range      * @param upperTerm The upper bound on this range      * @param includeLower Does this range include the lower bound?      * @param includeUpper Does this range include the upper bound?      * @throws IllegalArgumentException if both terms are null or if      *  lowerTerm is null and includeLower is true (similar for upperTerm      *  and includeUpper)      */
DECL|method|RangeFilter
specifier|public
name|RangeFilter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|lowerTerm
operator|=
name|lowerTerm
expr_stmt|;
name|this
operator|.
name|upperTerm
operator|=
name|upperTerm
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|lowerTerm
operator|&&
literal|null
operator|==
name|upperTerm
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"At least one value must be non-null"
argument_list|)
throw|;
block|}
if|if
condition|(
name|includeLower
operator|&&
literal|null
operator|==
name|lowerTerm
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The lower bound must be non-null to be inclusive"
argument_list|)
throw|;
block|}
if|if
condition|(
name|includeUpper
operator|&&
literal|null
operator|==
name|upperTerm
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The upper bound must be non-null to be inclusive"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Constructs a filter for field<code>fieldName</code> matching      * less than or equal to<code>upperTerm</code>.      */
DECL|method|Less
specifier|public
specifier|static
name|RangeFilter
name|Less
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|upperTerm
parameter_list|)
block|{
return|return
operator|new
name|RangeFilter
argument_list|(
name|fieldName
argument_list|,
literal|null
argument_list|,
name|upperTerm
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Constructs a filter for field<code>fieldName</code> matching      * greater than or equal to<code>lowerTerm</code>.      */
DECL|method|More
specifier|public
specifier|static
name|RangeFilter
name|More
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|lowerTerm
parameter_list|)
block|{
return|return
operator|new
name|RangeFilter
argument_list|(
name|fieldName
argument_list|,
name|lowerTerm
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Returns a BitSet with true for documents which should be      * permitted in search results, and false for those that should      * not.      */
DECL|method|bits
specifier|public
name|BitSet
name|bits
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|BitSet
name|bits
init|=
operator|new
name|BitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
name|TermEnum
name|enumerator
init|=
operator|(
literal|null
operator|!=
name|lowerTerm
condition|?
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|lowerTerm
argument_list|)
argument_list|)
else|:
name|reader
operator|.
name|terms
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
literal|""
argument_list|)
argument_list|)
operator|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|enumerator
operator|.
name|term
argument_list|()
operator|==
literal|null
condition|)
block|{
return|return
name|bits
return|;
block|}
name|boolean
name|checkLower
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|includeLower
condition|)
comment|// make adjustments to set to exclusive
name|checkLower
operator|=
literal|true
expr_stmt|;
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
do|do
block|{
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
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|checkLower
operator|||
literal|null
operator|==
name|lowerTerm
operator|||
name|term
operator|.
name|text
argument_list|()
operator|.
name|compareTo
argument_list|(
name|lowerTerm
argument_list|)
operator|>
literal|0
condition|)
block|{
name|checkLower
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|upperTerm
operator|!=
literal|null
condition|)
block|{
name|int
name|compare
init|=
name|upperTerm
operator|.
name|compareTo
argument_list|(
name|term
operator|.
name|text
argument_list|()
argument_list|)
decl_stmt|;
comment|/* if beyond the upper term, or is exclusive and                                  * this is equal to the upper term, break out */
if|if
condition|(
operator|(
name|compare
operator|<
literal|0
operator|)
operator|||
operator|(
operator|!
name|includeUpper
operator|&&
name|compare
operator|==
literal|0
operator|)
condition|)
block|{
break|break;
block|}
block|}
comment|/* we have a good term, find the docs */
name|termDocs
operator|.
name|seek
argument_list|(
name|enumerator
operator|.
name|term
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
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
block|}
block|}
else|else
block|{
break|break;
block|}
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
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
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
name|bits
return|;
block|}
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|includeLower
condition|?
literal|"["
else|:
literal|"{"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|lowerTerm
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|lowerTerm
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
literal|"-"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|!=
name|upperTerm
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
name|upperTerm
argument_list|)
expr_stmt|;
block|}
name|buffer
operator|.
name|append
argument_list|(
name|includeUpper
condition|?
literal|"]"
else|:
literal|"}"
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true if<code>o</code> is equal to this. */
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|RangeFilter
operator|)
condition|)
return|return
literal|false
return|;
name|RangeFilter
name|other
init|=
operator|(
name|RangeFilter
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|fieldName
operator|.
name|equals
argument_list|(
name|other
operator|.
name|fieldName
argument_list|)
operator|||
name|this
operator|.
name|includeLower
operator|!=
name|other
operator|.
name|includeLower
operator|||
name|this
operator|.
name|includeUpper
operator|!=
name|other
operator|.
name|includeUpper
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|this
operator|.
name|lowerTerm
operator|!=
literal|null
condition|?
operator|!
name|this
operator|.
name|lowerTerm
operator|.
name|equals
argument_list|(
name|other
operator|.
name|lowerTerm
argument_list|)
else|:
name|other
operator|.
name|lowerTerm
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|this
operator|.
name|upperTerm
operator|!=
literal|null
condition|?
operator|!
name|this
operator|.
name|upperTerm
operator|.
name|equals
argument_list|(
name|other
operator|.
name|upperTerm
argument_list|)
else|:
name|other
operator|.
name|upperTerm
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/** Returns a hash code value for this object.*/
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|h
init|=
name|fieldName
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|h
operator|^=
name|lowerTerm
operator|!=
literal|null
condition|?
name|lowerTerm
operator|.
name|hashCode
argument_list|()
else|:
literal|0xB6ECE882
expr_stmt|;
name|h
operator|=
operator|(
name|h
operator|<<
literal|1
operator|)
operator||
operator|(
name|h
operator|>>>
literal|31
operator|)
expr_stmt|;
comment|// rotate to distinguish lower from upper
name|h
operator|^=
operator|(
name|upperTerm
operator|!=
literal|null
condition|?
operator|(
name|upperTerm
operator|.
name|hashCode
argument_list|()
operator|)
else|:
literal|0x91BEC2C2
operator|)
expr_stmt|;
name|h
operator|^=
operator|(
name|includeLower
condition|?
literal|0xD484B933
else|:
literal|0
operator|)
operator|^
operator|(
name|includeUpper
condition|?
literal|0x6AE423AC
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|h
return|;
block|}
block|}
end_class
end_unit
