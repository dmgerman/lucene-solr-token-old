begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.ar
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
package|;
end_package
begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
operator|.
name|StemmerUtil
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  *  Stemmer for Arabic.  *<p>  *  Stemming  is done in-place for efficiency, operating on a termbuffer.  *<p>  *  Stemming is defined as:  *<ul>  *<li> Removal of attached definite article, conjunction, and prepositions.  *<li> Stemming of common suffixes.  *</ul>  *  */
end_comment
begin_class
DECL|class|ArabicStemmer
specifier|public
class|class
name|ArabicStemmer
block|{
DECL|field|ALEF
specifier|public
specifier|static
specifier|final
name|char
name|ALEF
init|=
literal|'\u0627'
decl_stmt|;
DECL|field|BEH
specifier|public
specifier|static
specifier|final
name|char
name|BEH
init|=
literal|'\u0628'
decl_stmt|;
DECL|field|TEH_MARBUTA
specifier|public
specifier|static
specifier|final
name|char
name|TEH_MARBUTA
init|=
literal|'\u0629'
decl_stmt|;
DECL|field|TEH
specifier|public
specifier|static
specifier|final
name|char
name|TEH
init|=
literal|'\u062A'
decl_stmt|;
DECL|field|FEH
specifier|public
specifier|static
specifier|final
name|char
name|FEH
init|=
literal|'\u0641'
decl_stmt|;
DECL|field|KAF
specifier|public
specifier|static
specifier|final
name|char
name|KAF
init|=
literal|'\u0643'
decl_stmt|;
DECL|field|LAM
specifier|public
specifier|static
specifier|final
name|char
name|LAM
init|=
literal|'\u0644'
decl_stmt|;
DECL|field|NOON
specifier|public
specifier|static
specifier|final
name|char
name|NOON
init|=
literal|'\u0646'
decl_stmt|;
DECL|field|HEH
specifier|public
specifier|static
specifier|final
name|char
name|HEH
init|=
literal|'\u0647'
decl_stmt|;
DECL|field|WAW
specifier|public
specifier|static
specifier|final
name|char
name|WAW
init|=
literal|'\u0648'
decl_stmt|;
DECL|field|YEH
specifier|public
specifier|static
specifier|final
name|char
name|YEH
init|=
literal|'\u064A'
decl_stmt|;
DECL|field|prefixes
specifier|public
specifier|static
specifier|final
name|char
name|prefixes
index|[]
index|[]
init|=
block|{
operator|(
literal|""
operator|+
name|ALEF
operator|+
name|LAM
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|WAW
operator|+
name|ALEF
operator|+
name|LAM
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|BEH
operator|+
name|ALEF
operator|+
name|LAM
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|KAF
operator|+
name|ALEF
operator|+
name|LAM
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|FEH
operator|+
name|ALEF
operator|+
name|LAM
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|LAM
operator|+
name|LAM
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|WAW
operator|)
operator|.
name|toCharArray
argument_list|()
block|,   }
decl_stmt|;
DECL|field|suffixes
specifier|public
specifier|static
specifier|final
name|char
name|suffixes
index|[]
index|[]
init|=
block|{
operator|(
literal|""
operator|+
name|HEH
operator|+
name|ALEF
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|ALEF
operator|+
name|NOON
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|ALEF
operator|+
name|TEH
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|WAW
operator|+
name|NOON
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|YEH
operator|+
name|NOON
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|YEH
operator|+
name|HEH
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|YEH
operator|+
name|TEH_MARBUTA
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|HEH
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|TEH_MARBUTA
operator|)
operator|.
name|toCharArray
argument_list|()
block|,
operator|(
literal|""
operator|+
name|YEH
operator|)
operator|.
name|toCharArray
argument_list|()
block|, }
decl_stmt|;
comment|/**    * Stem an input buffer of Arabic text.    *     * @param s input buffer    * @param len length of input buffer    * @return length of input buffer after normalization    */
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|len
operator|=
name|stemPrefix
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
name|stemSuffix
argument_list|(
name|s
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
comment|/**    * Stem a prefix off an Arabic word.    * @param s input buffer    * @param len length of input buffer    * @return new length of input buffer after stemming.    */
DECL|method|stemPrefix
specifier|public
name|int
name|stemPrefix
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|startsWithCheckLength
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
name|prefixes
index|[
name|i
index|]
argument_list|)
condition|)
return|return
name|deleteN
argument_list|(
name|s
argument_list|,
literal|0
argument_list|,
name|len
argument_list|,
name|prefixes
index|[
name|i
index|]
operator|.
name|length
argument_list|)
return|;
return|return
name|len
return|;
block|}
comment|/**    * Stem suffix(es) off an Arabic word.    * @param s input buffer    * @param len length of input buffer    * @return new length of input buffer after stemming    */
DECL|method|stemSuffix
specifier|public
name|int
name|stemSuffix
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|suffixes
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|endsWithCheckLength
argument_list|(
name|s
argument_list|,
name|len
argument_list|,
name|suffixes
index|[
name|i
index|]
argument_list|)
condition|)
name|len
operator|=
name|deleteN
argument_list|(
name|s
argument_list|,
name|len
operator|-
name|suffixes
index|[
name|i
index|]
operator|.
name|length
argument_list|,
name|len
argument_list|,
name|suffixes
index|[
name|i
index|]
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
comment|/**    * Returns true if the prefix matches and can be stemmed    * @param s input buffer    * @param len length of input buffer    * @param prefix prefix to check    * @return true if the prefix matches and can be stemmed    */
DECL|method|startsWithCheckLength
name|boolean
name|startsWithCheckLength
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|,
name|char
name|prefix
index|[]
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|.
name|length
operator|==
literal|1
operator|&&
name|len
operator|<
literal|4
condition|)
block|{
comment|// wa- prefix requires at least 3 characters
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|len
operator|<
name|prefix
operator|.
name|length
operator|+
literal|2
condition|)
block|{
comment|// other prefixes require only 2.
return|return
literal|false
return|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|prefix
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|s
index|[
name|i
index|]
operator|!=
name|prefix
index|[
name|i
index|]
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
comment|/**    * Returns true if the suffix matches and can be stemmed    * @param s input buffer    * @param len length of input buffer    * @param suffix suffix to check    * @return true if the suffix matches and can be stemmed    */
DECL|method|endsWithCheckLength
name|boolean
name|endsWithCheckLength
parameter_list|(
name|char
name|s
index|[]
parameter_list|,
name|int
name|len
parameter_list|,
name|char
name|suffix
index|[]
parameter_list|)
block|{
if|if
condition|(
name|len
operator|<
name|suffix
operator|.
name|length
operator|+
literal|2
condition|)
block|{
comment|// all suffixes require at least 2 characters after stemming
return|return
literal|false
return|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|suffix
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|s
index|[
name|len
operator|-
name|suffix
operator|.
name|length
operator|+
name|i
index|]
operator|!=
name|suffix
index|[
name|i
index|]
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
block|}
end_class
end_unit
