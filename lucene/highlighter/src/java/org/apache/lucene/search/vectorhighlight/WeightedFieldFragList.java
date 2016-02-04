begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package
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
name|HashSet
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
operator|.
name|FieldFragList
operator|.
name|WeightedFragInfo
operator|.
name|SubInfo
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
name|vectorhighlight
operator|.
name|FieldPhraseList
operator|.
name|WeightedPhraseInfo
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
name|vectorhighlight
operator|.
name|FieldTermStack
operator|.
name|TermInfo
import|;
end_import
begin_comment
comment|/**  * A weighted implementation of {@link FieldFragList}.  */
end_comment
begin_class
DECL|class|WeightedFieldFragList
specifier|public
class|class
name|WeightedFieldFragList
extends|extends
name|FieldFragList
block|{
comment|/**    * a constructor.    *     * @param fragCharSize the length (number of chars) of a fragment    */
DECL|method|WeightedFieldFragList
specifier|public
name|WeightedFieldFragList
parameter_list|(
name|int
name|fragCharSize
parameter_list|)
block|{
name|super
argument_list|(
name|fragCharSize
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.vectorhighlight.FieldFragList#add( int startOffset, int endOffset, List<WeightedPhraseInfo> phraseInfoList )    */
annotation|@
name|Override
DECL|method|add
specifier|public
name|void
name|add
parameter_list|(
name|int
name|startOffset
parameter_list|,
name|int
name|endOffset
parameter_list|,
name|List
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|phraseInfoList
parameter_list|)
block|{
name|List
argument_list|<
name|SubInfo
argument_list|>
name|tempSubInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SubInfo
argument_list|>
name|realSubInfos
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|HashSet
argument_list|<
name|String
argument_list|>
name|distinctTerms
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|length
init|=
literal|0
decl_stmt|;
for|for
control|(
name|WeightedPhraseInfo
name|phraseInfo
range|:
name|phraseInfoList
control|)
block|{
name|float
name|phraseTotalBoost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|TermInfo
name|ti
range|:
name|phraseInfo
operator|.
name|getTermsInfos
argument_list|()
control|)
block|{
if|if
condition|(
name|distinctTerms
operator|.
name|add
argument_list|(
name|ti
operator|.
name|getText
argument_list|()
argument_list|)
condition|)
name|phraseTotalBoost
operator|+=
name|ti
operator|.
name|getWeight
argument_list|()
operator|*
name|phraseInfo
operator|.
name|getBoost
argument_list|()
expr_stmt|;
name|length
operator|++
expr_stmt|;
block|}
name|tempSubInfos
operator|.
name|add
argument_list|(
operator|new
name|SubInfo
argument_list|(
name|phraseInfo
operator|.
name|getText
argument_list|()
argument_list|,
name|phraseInfo
operator|.
name|getTermsOffsets
argument_list|()
argument_list|,
name|phraseInfo
operator|.
name|getSeqnum
argument_list|()
argument_list|,
name|phraseTotalBoost
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// We want that terms per fragment (length) is included into the weight. Otherwise a one-word-query
comment|// would cause an equal weight for all fragments regardless of how much words they contain.
comment|// To avoid that fragments containing a high number of words possibly "outrank" more relevant fragments
comment|// we "bend" the length with a standard-normalization a little bit.
name|float
name|norm
init|=
name|length
operator|*
operator|(
literal|1
operator|/
operator|(
name|float
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|length
argument_list|)
operator|)
decl_stmt|;
name|float
name|totalBoost
init|=
literal|0
decl_stmt|;
for|for
control|(
name|SubInfo
name|tempSubInfo
range|:
name|tempSubInfos
control|)
block|{
name|float
name|subInfoBoost
init|=
name|tempSubInfo
operator|.
name|getBoost
argument_list|()
operator|*
name|norm
decl_stmt|;
name|realSubInfos
operator|.
name|add
argument_list|(
operator|new
name|SubInfo
argument_list|(
name|tempSubInfo
operator|.
name|getText
argument_list|()
argument_list|,
name|tempSubInfo
operator|.
name|getTermsOffsets
argument_list|()
argument_list|,
name|tempSubInfo
operator|.
name|getSeqnum
argument_list|()
argument_list|,
name|subInfoBoost
argument_list|)
argument_list|)
expr_stmt|;
name|totalBoost
operator|+=
name|subInfoBoost
expr_stmt|;
block|}
name|getFragInfos
argument_list|()
operator|.
name|add
argument_list|(
operator|new
name|WeightedFragInfo
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|,
name|realSubInfos
argument_list|,
name|totalBoost
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
