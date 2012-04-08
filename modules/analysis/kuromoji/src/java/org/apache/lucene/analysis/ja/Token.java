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
name|analysis
operator|.
name|ja
operator|.
name|JapaneseTokenizer
operator|.
name|Type
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
name|dict
operator|.
name|Dictionary
import|;
end_import
begin_comment
comment|/**  * Analyzed token with morphological data from its dictionary.  */
end_comment
begin_class
DECL|class|Token
specifier|public
class|class
name|Token
block|{
DECL|field|dictionary
specifier|private
specifier|final
name|Dictionary
name|dictionary
decl_stmt|;
DECL|field|wordId
specifier|private
specifier|final
name|int
name|wordId
decl_stmt|;
DECL|field|surfaceForm
specifier|private
specifier|final
name|char
index|[]
name|surfaceForm
decl_stmt|;
DECL|field|offset
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
DECL|field|position
specifier|private
specifier|final
name|int
name|position
decl_stmt|;
DECL|field|positionLength
specifier|private
name|int
name|positionLength
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
DECL|method|Token
specifier|public
name|Token
parameter_list|(
name|int
name|wordId
parameter_list|,
name|char
index|[]
name|surfaceForm
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|,
name|Type
name|type
parameter_list|,
name|int
name|position
parameter_list|,
name|Dictionary
name|dictionary
parameter_list|)
block|{
name|this
operator|.
name|wordId
operator|=
name|wordId
expr_stmt|;
name|this
operator|.
name|surfaceForm
operator|=
name|surfaceForm
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|position
operator|=
name|position
expr_stmt|;
name|this
operator|.
name|positionLength
operator|=
name|positionLength
expr_stmt|;
name|this
operator|.
name|dictionary
operator|=
name|dictionary
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Token(\""
operator|+
operator|new
name|String
argument_list|(
name|surfaceForm
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
operator|+
literal|"\" pos="
operator|+
name|position
operator|+
literal|" length="
operator|+
name|length
operator|+
literal|" posLen="
operator|+
name|positionLength
operator|+
literal|" type="
operator|+
name|type
operator|+
literal|" wordId="
operator|+
name|wordId
operator|+
literal|" leftID="
operator|+
name|dictionary
operator|.
name|getLeftId
argument_list|(
name|wordId
argument_list|)
operator|+
literal|")"
return|;
block|}
comment|/**    * @return surfaceForm    */
DECL|method|getSurfaceForm
specifier|public
name|char
index|[]
name|getSurfaceForm
parameter_list|()
block|{
return|return
name|surfaceForm
return|;
block|}
comment|/**    * @return offset into surfaceForm    */
DECL|method|getOffset
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
comment|/**    * @return length of surfaceForm    */
DECL|method|getLength
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/**    * @return surfaceForm as a String    */
DECL|method|getSurfaceFormString
specifier|public
name|String
name|getSurfaceFormString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|surfaceForm
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * @return reading. null if token doesn't have reading.    */
DECL|method|getReading
specifier|public
name|String
name|getReading
parameter_list|()
block|{
return|return
name|dictionary
operator|.
name|getReading
argument_list|(
name|wordId
argument_list|,
name|surfaceForm
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * @return pronunciation. null if token doesn't have pronunciation.    */
DECL|method|getPronunciation
specifier|public
name|String
name|getPronunciation
parameter_list|()
block|{
return|return
name|dictionary
operator|.
name|getPronunciation
argument_list|(
name|wordId
argument_list|,
name|surfaceForm
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * @return part of speech.    */
DECL|method|getPartOfSpeech
specifier|public
name|String
name|getPartOfSpeech
parameter_list|()
block|{
return|return
name|dictionary
operator|.
name|getPartOfSpeech
argument_list|(
name|wordId
argument_list|)
return|;
block|}
comment|/**    * @return inflection type or null    */
DECL|method|getInflectionType
specifier|public
name|String
name|getInflectionType
parameter_list|()
block|{
return|return
name|dictionary
operator|.
name|getInflectionType
argument_list|(
name|wordId
argument_list|)
return|;
block|}
comment|/**    * @return inflection form or null    */
DECL|method|getInflectionForm
specifier|public
name|String
name|getInflectionForm
parameter_list|()
block|{
return|return
name|dictionary
operator|.
name|getInflectionForm
argument_list|(
name|wordId
argument_list|)
return|;
block|}
comment|/**    * @return base form or null if token is not inflected    */
DECL|method|getBaseForm
specifier|public
name|String
name|getBaseForm
parameter_list|()
block|{
return|return
name|dictionary
operator|.
name|getBaseForm
argument_list|(
name|wordId
argument_list|,
name|surfaceForm
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Returns true if this token is known word    * @return true if this token is in standard dictionary. false if not.    */
DECL|method|isKnown
specifier|public
name|boolean
name|isKnown
parameter_list|()
block|{
return|return
name|type
operator|==
name|Type
operator|.
name|KNOWN
return|;
block|}
comment|/**    * Returns true if this token is unknown word    * @return true if this token is unknown word. false if not.    */
DECL|method|isUnknown
specifier|public
name|boolean
name|isUnknown
parameter_list|()
block|{
return|return
name|type
operator|==
name|Type
operator|.
name|UNKNOWN
return|;
block|}
comment|/**    * Returns true if this token is defined in user dictionary    * @return true if this token is in user dictionary. false if not.    */
DECL|method|isUser
specifier|public
name|boolean
name|isUser
parameter_list|()
block|{
return|return
name|type
operator|==
name|Type
operator|.
name|USER
return|;
block|}
comment|/**    * Get index of this token in input text    * @return position of token    */
DECL|method|getPosition
specifier|public
name|int
name|getPosition
parameter_list|()
block|{
return|return
name|position
return|;
block|}
comment|/**    * Set the position length (in tokens) of this token.  For normal    * tokens this is 1; for compound tokens it's> 1.    */
DECL|method|setPositionLength
specifier|public
name|void
name|setPositionLength
parameter_list|(
name|int
name|positionLength
parameter_list|)
block|{
name|this
operator|.
name|positionLength
operator|=
name|positionLength
expr_stmt|;
block|}
comment|/**    * Get the length (in tokens) of this token.  For normal    * tokens this is 1; for compound tokens it's> 1.    * @return position length of token    */
DECL|method|getPositionLength
specifier|public
name|int
name|getPositionLength
parameter_list|()
block|{
return|return
name|positionLength
return|;
block|}
block|}
end_class
end_unit
