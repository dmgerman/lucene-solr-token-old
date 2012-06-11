begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Iterator
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
name|FieldPhraseList
operator|.
name|WeightedPhraseInfo
import|;
end_import
begin_comment
comment|/**  * A abstract implementation of {@link FragListBuilder}.  */
end_comment
begin_class
DECL|class|BaseFragListBuilder
specifier|public
specifier|abstract
class|class
name|BaseFragListBuilder
implements|implements
name|FragListBuilder
block|{
DECL|field|MARGIN_DEFAULT
specifier|public
specifier|static
specifier|final
name|int
name|MARGIN_DEFAULT
init|=
literal|6
decl_stmt|;
DECL|field|MIN_FRAG_CHAR_SIZE_FACTOR
specifier|public
specifier|static
specifier|final
name|int
name|MIN_FRAG_CHAR_SIZE_FACTOR
init|=
literal|3
decl_stmt|;
DECL|field|margin
specifier|final
name|int
name|margin
decl_stmt|;
DECL|field|minFragCharSize
specifier|final
name|int
name|minFragCharSize
decl_stmt|;
DECL|method|BaseFragListBuilder
specifier|public
name|BaseFragListBuilder
parameter_list|(
name|int
name|margin
parameter_list|)
block|{
if|if
condition|(
name|margin
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"margin("
operator|+
name|margin
operator|+
literal|") is too small. It must be 0 or higher."
argument_list|)
throw|;
name|this
operator|.
name|margin
operator|=
name|margin
expr_stmt|;
name|this
operator|.
name|minFragCharSize
operator|=
name|Math
operator|.
name|max
argument_list|(
literal|1
argument_list|,
name|margin
operator|*
name|MIN_FRAG_CHAR_SIZE_FACTOR
argument_list|)
expr_stmt|;
block|}
DECL|method|BaseFragListBuilder
specifier|public
name|BaseFragListBuilder
parameter_list|()
block|{
name|this
argument_list|(
name|MARGIN_DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|createFieldFragList
specifier|protected
name|FieldFragList
name|createFieldFragList
parameter_list|(
name|FieldPhraseList
name|fieldPhraseList
parameter_list|,
name|FieldFragList
name|fieldFragList
parameter_list|,
name|int
name|fragCharSize
parameter_list|)
block|{
if|if
condition|(
name|fragCharSize
operator|<
name|minFragCharSize
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"fragCharSize("
operator|+
name|fragCharSize
operator|+
literal|") is too small. It must be "
operator|+
name|minFragCharSize
operator|+
literal|" or higher."
argument_list|)
throw|;
name|List
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|wpil
init|=
operator|new
name|ArrayList
argument_list|<
name|WeightedPhraseInfo
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|WeightedPhraseInfo
argument_list|>
name|ite
init|=
name|fieldPhraseList
operator|.
name|getPhraseList
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|WeightedPhraseInfo
name|phraseInfo
init|=
literal|null
decl_stmt|;
name|int
name|startOffset
init|=
literal|0
decl_stmt|;
name|boolean
name|taken
init|=
literal|false
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
operator|!
name|taken
condition|)
block|{
if|if
condition|(
operator|!
name|ite
operator|.
name|hasNext
argument_list|()
condition|)
break|break;
name|phraseInfo
operator|=
name|ite
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
name|taken
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|phraseInfo
operator|==
literal|null
condition|)
break|break;
comment|// if the phrase violates the border of previous fragment, discard it and try next phrase
if|if
condition|(
name|phraseInfo
operator|.
name|getStartOffset
argument_list|()
operator|<
name|startOffset
condition|)
continue|continue;
name|wpil
operator|.
name|clear
argument_list|()
expr_stmt|;
name|wpil
operator|.
name|add
argument_list|(
name|phraseInfo
argument_list|)
expr_stmt|;
name|int
name|st
init|=
name|phraseInfo
operator|.
name|getStartOffset
argument_list|()
operator|-
name|margin
operator|<
name|startOffset
condition|?
name|startOffset
else|:
name|phraseInfo
operator|.
name|getStartOffset
argument_list|()
operator|-
name|margin
decl_stmt|;
name|int
name|en
init|=
name|st
operator|+
name|fragCharSize
decl_stmt|;
if|if
condition|(
name|phraseInfo
operator|.
name|getEndOffset
argument_list|()
operator|>
name|en
condition|)
name|en
operator|=
name|phraseInfo
operator|.
name|getEndOffset
argument_list|()
expr_stmt|;
name|startOffset
operator|=
name|en
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|ite
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|phraseInfo
operator|=
name|ite
operator|.
name|next
argument_list|()
expr_stmt|;
name|taken
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|phraseInfo
operator|==
literal|null
condition|)
break|break;
block|}
else|else
break|break;
if|if
condition|(
name|phraseInfo
operator|.
name|getEndOffset
argument_list|()
operator|<=
name|en
condition|)
name|wpil
operator|.
name|add
argument_list|(
name|phraseInfo
argument_list|)
expr_stmt|;
else|else
break|break;
block|}
name|fieldFragList
operator|.
name|add
argument_list|(
name|st
argument_list|,
name|en
argument_list|,
name|wpil
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldFragList
return|;
block|}
block|}
end_class
end_unit
