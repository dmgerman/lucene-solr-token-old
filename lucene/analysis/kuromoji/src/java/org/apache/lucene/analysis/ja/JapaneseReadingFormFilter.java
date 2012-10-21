begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ja
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ja
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
name|analysis
operator|.
name|TokenFilter
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
name|analysis
operator|.
name|TokenStream
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|analysis
operator|.
name|ja
operator|.
name|tokenattributes
operator|.
name|ReadingAttribute
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
name|analysis
operator|.
name|ja
operator|.
name|util
operator|.
name|ToStringUtil
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
comment|/**  * A {@link org.apache.lucene.analysis.TokenFilter} that replaces the term  * attribute with the reading of a token in either katakana or romaji form.  * The default reading form is katakana.  */
end_comment
begin_class
DECL|class|JapaneseReadingFormFilter
specifier|public
specifier|final
class|class
name|JapaneseReadingFormFilter
extends|extends
name|TokenFilter
block|{
DECL|field|termAttr
specifier|private
specifier|final
name|CharTermAttribute
name|termAttr
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|readingAttr
specifier|private
specifier|final
name|ReadingAttribute
name|readingAttr
init|=
name|addAttribute
argument_list|(
name|ReadingAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|buffer
specifier|private
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
DECL|field|useRomaji
specifier|private
name|boolean
name|useRomaji
decl_stmt|;
DECL|method|JapaneseReadingFormFilter
specifier|public
name|JapaneseReadingFormFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|boolean
name|useRomaji
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|useRomaji
operator|=
name|useRomaji
expr_stmt|;
block|}
DECL|method|JapaneseReadingFormFilter
specifier|public
name|JapaneseReadingFormFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|input
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
name|String
name|reading
init|=
name|readingAttr
operator|.
name|getReading
argument_list|()
decl_stmt|;
if|if
condition|(
name|useRomaji
condition|)
block|{
if|if
condition|(
name|reading
operator|==
literal|null
condition|)
block|{
comment|// if its an OOV term, just try the term text
name|buffer
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|ToStringUtil
operator|.
name|getRomanization
argument_list|(
name|buffer
argument_list|,
name|termAttr
argument_list|)
expr_stmt|;
name|termAttr
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ToStringUtil
operator|.
name|getRomanization
argument_list|(
name|termAttr
operator|.
name|setEmpty
argument_list|()
argument_list|,
name|reading
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// just replace the term text with the reading, if it exists
if|if
condition|(
name|reading
operator|!=
literal|null
condition|)
block|{
name|termAttr
operator|.
name|setEmpty
argument_list|()
operator|.
name|append
argument_list|(
name|reading
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class
end_unit
