begin_unit
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|ReaderUtil
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
name|ArrayList
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
begin_comment
comment|/**  * Exposes flex API, merged from flex API of  * sub-segments.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiTerms
specifier|public
specifier|final
class|class
name|MultiTerms
extends|extends
name|Terms
block|{
DECL|field|subs
specifier|private
specifier|final
name|Terms
index|[]
name|subs
decl_stmt|;
DECL|field|subSlices
specifier|private
specifier|final
name|ReaderUtil
operator|.
name|Slice
index|[]
name|subSlices
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
DECL|method|MultiTerms
specifier|public
name|MultiTerms
parameter_list|(
name|Terms
index|[]
name|subs
parameter_list|,
name|ReaderUtil
operator|.
name|Slice
index|[]
name|subSlices
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|this
operator|.
name|subSlices
operator|=
name|subSlices
expr_stmt|;
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|_termComp
init|=
literal|null
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|_termComp
operator|==
literal|null
condition|)
block|{
name|_termComp
operator|=
name|subs
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// We cannot merge sub-readers that have
comment|// different TermComps
specifier|final
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|subTermComp
init|=
name|subs
index|[
name|i
index|]
operator|.
name|getComparator
argument_list|()
decl_stmt|;
if|if
condition|(
name|subTermComp
operator|!=
literal|null
operator|&&
operator|!
name|subTermComp
operator|.
name|equals
argument_list|(
name|_termComp
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"sub-readers have different BytesRef.Comparators; cannot merge"
argument_list|)
throw|;
block|}
block|}
block|}
name|termComp
operator|=
name|_termComp
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|TermsEnum
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
argument_list|>
name|termsEnums
init|=
operator|new
name|ArrayList
argument_list|<
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
argument_list|>
argument_list|()
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|TermsEnum
name|termsEnum
init|=
name|subs
index|[
name|i
index|]
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|termsEnum
operator|!=
literal|null
condition|)
block|{
name|termsEnums
operator|.
name|add
argument_list|(
operator|new
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
argument_list|(
name|termsEnum
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|termsEnums
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
operator|new
name|MultiTermsEnum
argument_list|(
name|subSlices
argument_list|)
operator|.
name|reset
argument_list|(
name|termsEnums
operator|.
name|toArray
argument_list|(
name|MultiTermsEnum
operator|.
name|TermsEnumIndex
operator|.
name|EMPTY_ARRAY
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|TermsEnum
operator|.
name|EMPTY
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getComparator
specifier|public
name|Comparator
argument_list|<
name|BytesRef
argument_list|>
name|getComparator
parameter_list|()
block|{
return|return
name|termComp
return|;
block|}
block|}
end_class
end_unit
