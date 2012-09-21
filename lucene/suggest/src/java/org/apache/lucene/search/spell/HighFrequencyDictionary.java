begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package
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
name|index
operator|.
name|Terms
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
name|MultiFields
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
name|BytesRefIterator
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
comment|/**  * HighFrequencyDictionary: terms taken from the given field  * of a Lucene index, which appear in a number of documents  * above a given threshold.  *  * Threshold is a value in [0..1] representing the minimum  * number of documents (of the total) where a term should appear.  *   * Based on LuceneDictionary.  */
end_comment
begin_class
DECL|class|HighFrequencyDictionary
specifier|public
class|class
name|HighFrequencyDictionary
implements|implements
name|Dictionary
block|{
DECL|field|reader
specifier|private
name|IndexReader
name|reader
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|field|thresh
specifier|private
name|float
name|thresh
decl_stmt|;
comment|/**    * Creates a new Dictionary, pulling source terms from    * the specified<code>field</code> in the provided<code>reader</code>.    *<p>    * Terms appearing in less than<code>thresh</code> percentage of documents    * will be excluded.    */
DECL|method|HighFrequencyDictionary
specifier|public
name|HighFrequencyDictionary
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|String
name|field
parameter_list|,
name|float
name|thresh
parameter_list|)
block|{
name|this
operator|.
name|reader
operator|=
name|reader
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
name|this
operator|.
name|thresh
operator|=
name|thresh
expr_stmt|;
block|}
DECL|method|getWordsIterator
specifier|public
specifier|final
name|BytesRefIterator
name|getWordsIterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|HighFrequencyIterator
argument_list|()
return|;
block|}
DECL|class|HighFrequencyIterator
specifier|final
class|class
name|HighFrequencyIterator
implements|implements
name|TermFreqIterator
block|{
DECL|field|spare
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
DECL|field|termsEnum
specifier|private
specifier|final
name|TermsEnum
name|termsEnum
decl_stmt|;
DECL|field|minNumDocs
specifier|private
name|int
name|minNumDocs
decl_stmt|;
DECL|field|freq
specifier|private
name|long
name|freq
decl_stmt|;
DECL|method|HighFrequencyIterator
name|HighFrequencyIterator
parameter_list|()
throws|throws
name|IOException
block|{
name|Terms
name|terms
init|=
name|MultiFields
operator|.
name|getTerms
argument_list|(
name|reader
argument_list|,
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|!=
literal|null
condition|)
block|{
name|termsEnum
operator|=
name|terms
operator|.
name|iterator
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|termsEnum
operator|=
literal|null
expr_stmt|;
block|}
name|minNumDocs
operator|=
call|(
name|int
call|)
argument_list|(
name|thresh
operator|*
operator|(
name|float
operator|)
name|reader
operator|.
name|numDocs
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|isFrequent
specifier|private
name|boolean
name|isFrequent
parameter_list|(
name|int
name|freq
parameter_list|)
block|{
return|return
name|freq
operator|>=
name|minNumDocs
return|;
block|}
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|freq
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|termsEnum
operator|!=
literal|null
condition|)
block|{
name|BytesRef
name|next
decl_stmt|;
while|while
condition|(
operator|(
name|next
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isFrequent
argument_list|(
name|termsEnum
operator|.
name|docFreq
argument_list|()
argument_list|)
condition|)
block|{
name|freq
operator|=
name|termsEnum
operator|.
name|docFreq
argument_list|()
expr_stmt|;
name|spare
operator|.
name|copyBytes
argument_list|(
name|next
argument_list|)
expr_stmt|;
return|return
name|spare
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
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
if|if
condition|(
name|termsEnum
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|termsEnum
operator|.
name|getComparator
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class
end_unit
