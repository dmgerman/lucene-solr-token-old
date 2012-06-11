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
name|Comparator
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
name|FilteredTermsEnum
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
name|TermsEnum
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
comment|/**  * Subclass of FilteredTermEnum for enumerating all terms that match the  * specified range parameters.  *<p>Term enumerations are always ordered by  * {@link #getComparator}.  Each term in the enumeration is  * greater than all that precede it.</p>  */
end_comment
begin_class
DECL|class|TermRangeTermsEnum
specifier|public
class|class
name|TermRangeTermsEnum
extends|extends
name|FilteredTermsEnum
block|{
DECL|field|includeLower
specifier|final
specifier|private
name|boolean
name|includeLower
decl_stmt|;
DECL|field|includeUpper
specifier|final
specifier|private
name|boolean
name|includeUpper
decl_stmt|;
DECL|field|lowerBytesRef
specifier|final
specifier|private
name|BytesRef
name|lowerBytesRef
decl_stmt|;
DECL|field|upperBytesRef
specifier|final
specifier|private
name|BytesRef
name|upperBytesRef
decl_stmt|;
DECL|field|termComp
specifier|private
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|termComp
decl_stmt|;
comment|/**    * Enumerates all terms greater/equal than<code>lowerTerm</code>    * but less/equal than<code>upperTerm</code>.     *     * If an endpoint is null, it is said to be "open". Either or both     * endpoints may be open.  Open endpoints may not be exclusive     * (you can't select all but the first or last term without     * explicitly specifying the term to exclude.)    *     * @param tenum    *          TermsEnum to filter    * @param lowerTerm    *          The term text at the lower end of the range    * @param upperTerm    *          The term text at the upper end of the range    * @param includeLower    *          If true, the<code>lowerTerm</code> is included in the range.    * @param includeUpper    *          If true, the<code>upperTerm</code> is included in the range.    *     * @throws IOException    */
DECL|method|TermRangeTermsEnum
specifier|public
name|TermRangeTermsEnum
parameter_list|(
name|TermsEnum
name|tenum
parameter_list|,
name|BytesRef
name|lowerTerm
parameter_list|,
name|BytesRef
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|tenum
argument_list|)
expr_stmt|;
comment|// do a little bit of normalization...
comment|// open ended range queries should always be inclusive.
if|if
condition|(
name|lowerTerm
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|lowerBytesRef
operator|=
operator|new
name|BytesRef
argument_list|()
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|lowerBytesRef
operator|=
name|lowerTerm
expr_stmt|;
name|this
operator|.
name|includeLower
operator|=
name|includeLower
expr_stmt|;
block|}
if|if
condition|(
name|upperTerm
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|includeUpper
operator|=
literal|true
expr_stmt|;
name|upperBytesRef
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|includeUpper
operator|=
name|includeUpper
expr_stmt|;
name|upperBytesRef
operator|=
name|upperTerm
expr_stmt|;
block|}
name|setInitialSeekTerm
argument_list|(
name|lowerBytesRef
argument_list|)
expr_stmt|;
name|termComp
operator|=
name|getComparator
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|accept
specifier|protected
name|AcceptStatus
name|accept
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|includeLower
operator|&&
name|term
operator|.
name|equals
argument_list|(
name|lowerBytesRef
argument_list|)
condition|)
return|return
name|AcceptStatus
operator|.
name|NO
return|;
comment|// Use this field's default sort ordering
if|if
condition|(
name|upperBytesRef
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|termComp
operator|.
name|compare
argument_list|(
name|upperBytesRef
argument_list|,
name|term
argument_list|)
decl_stmt|;
comment|/*        * if beyond the upper term, or is exclusive and this is equal to        * the upper term, break out        */
if|if
condition|(
operator|(
name|cmp
operator|<
literal|0
operator|)
operator|||
operator|(
operator|!
name|includeUpper
operator|&&
name|cmp
operator|==
literal|0
operator|)
condition|)
block|{
return|return
name|AcceptStatus
operator|.
name|END
return|;
block|}
block|}
return|return
name|AcceptStatus
operator|.
name|YES
return|;
block|}
block|}
end_class
end_unit
