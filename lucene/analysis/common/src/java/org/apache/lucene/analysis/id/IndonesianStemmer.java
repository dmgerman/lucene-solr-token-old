begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.id
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|id
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
comment|/**  * Stemmer for Indonesian.  *<p>  * Stems Indonesian words with the algorithm presented in:  *<i>A Study of Stemming Effects on Information Retrieval in   * Bahasa Indonesia</i>, Fadillah Z Tala.  * http://www.illc.uva.nl/Publications/ResearchReports/MoL-2003-02.text.pdf  */
end_comment
begin_class
DECL|class|IndonesianStemmer
specifier|public
class|class
name|IndonesianStemmer
block|{
DECL|field|numSyllables
specifier|private
name|int
name|numSyllables
decl_stmt|;
DECL|field|flags
specifier|private
name|int
name|flags
decl_stmt|;
DECL|field|REMOVED_KE
specifier|private
specifier|static
specifier|final
name|int
name|REMOVED_KE
init|=
literal|1
decl_stmt|;
DECL|field|REMOVED_PENG
specifier|private
specifier|static
specifier|final
name|int
name|REMOVED_PENG
init|=
literal|2
decl_stmt|;
DECL|field|REMOVED_DI
specifier|private
specifier|static
specifier|final
name|int
name|REMOVED_DI
init|=
literal|4
decl_stmt|;
DECL|field|REMOVED_MENG
specifier|private
specifier|static
specifier|final
name|int
name|REMOVED_MENG
init|=
literal|8
decl_stmt|;
DECL|field|REMOVED_TER
specifier|private
specifier|static
specifier|final
name|int
name|REMOVED_TER
init|=
literal|16
decl_stmt|;
DECL|field|REMOVED_BER
specifier|private
specifier|static
specifier|final
name|int
name|REMOVED_BER
init|=
literal|32
decl_stmt|;
DECL|field|REMOVED_PE
specifier|private
specifier|static
specifier|final
name|int
name|REMOVED_PE
init|=
literal|64
decl_stmt|;
comment|/**    * Stem a term (returning its new length).    *<p>    * Use<code>stemDerivational</code> to control whether full stemming    * or only light inflectional stemming is done.    */
DECL|method|stem
specifier|public
name|int
name|stem
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|,
name|boolean
name|stemDerivational
parameter_list|)
block|{
name|flags
operator|=
literal|0
expr_stmt|;
name|numSyllables
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|isVowel
argument_list|(
name|text
index|[
name|i
index|]
argument_list|)
condition|)
name|numSyllables
operator|++
expr_stmt|;
if|if
condition|(
name|numSyllables
operator|>
literal|2
condition|)
name|length
operator|=
name|removeParticle
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|numSyllables
operator|>
literal|2
condition|)
name|length
operator|=
name|removePossessivePronoun
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|stemDerivational
condition|)
name|length
operator|=
name|stemDerivational
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|length
return|;
block|}
DECL|method|stemDerivational
specifier|private
name|int
name|stemDerivational
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|int
name|oldLength
init|=
name|length
decl_stmt|;
if|if
condition|(
name|numSyllables
operator|>
literal|2
condition|)
name|length
operator|=
name|removeFirstOrderPrefix
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldLength
operator|!=
name|length
condition|)
block|{
comment|// a rule is fired
name|oldLength
operator|=
name|length
expr_stmt|;
if|if
condition|(
name|numSyllables
operator|>
literal|2
condition|)
name|length
operator|=
name|removeSuffix
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldLength
operator|!=
name|length
condition|)
comment|// a rule is fired
if|if
condition|(
name|numSyllables
operator|>
literal|2
condition|)
name|length
operator|=
name|removeSecondOrderPrefix
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// fail
if|if
condition|(
name|numSyllables
operator|>
literal|2
condition|)
name|length
operator|=
name|removeSecondOrderPrefix
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|numSyllables
operator|>
literal|2
condition|)
name|length
operator|=
name|removeSuffix
argument_list|(
name|text
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
return|return
name|length
return|;
block|}
DECL|method|isVowel
specifier|private
name|boolean
name|isVowel
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'a'
case|:
case|case
literal|'e'
case|:
case|case
literal|'i'
case|:
case|case
literal|'o'
case|:
case|case
literal|'u'
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
DECL|method|removeParticle
specifier|private
name|int
name|removeParticle
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"kah"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"lah"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"pun"
argument_list|)
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|length
operator|-
literal|3
return|;
block|}
return|return
name|length
return|;
block|}
DECL|method|removePossessivePronoun
specifier|private
name|int
name|removePossessivePronoun
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"ku"
argument_list|)
operator|||
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"mu"
argument_list|)
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|length
operator|-
literal|2
return|;
block|}
if|if
condition|(
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"nya"
argument_list|)
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|length
operator|-
literal|3
return|;
block|}
return|return
name|length
return|;
block|}
DECL|method|removeFirstOrderPrefix
specifier|private
name|int
name|removeFirstOrderPrefix
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"meng"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_MENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|4
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"meny"
argument_list|)
operator|&&
name|length
operator|>
literal|4
operator|&&
name|isVowel
argument_list|(
name|text
index|[
literal|4
index|]
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_MENG
expr_stmt|;
name|text
index|[
literal|3
index|]
operator|=
literal|'s'
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"men"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_MENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"mem"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_MENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"me"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_MENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|2
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"peng"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_PENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|4
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"peny"
argument_list|)
operator|&&
name|length
operator|>
literal|4
operator|&&
name|isVowel
argument_list|(
name|text
index|[
literal|4
index|]
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_PENG
expr_stmt|;
name|text
index|[
literal|3
index|]
operator|=
literal|'s'
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"peny"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_PENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|4
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"pen"
argument_list|)
operator|&&
name|length
operator|>
literal|3
operator|&&
name|isVowel
argument_list|(
name|text
index|[
literal|3
index|]
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_PENG
expr_stmt|;
name|text
index|[
literal|2
index|]
operator|=
literal|'t'
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|2
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"pen"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_PENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"pem"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_PENG
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"di"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_DI
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|2
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"ter"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_TER
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"ke"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_KE
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|2
argument_list|)
return|;
block|}
return|return
name|length
return|;
block|}
DECL|method|removeSecondOrderPrefix
specifier|private
name|int
name|removeSecondOrderPrefix
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"ber"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_BER
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|length
operator|==
literal|7
operator|&&
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"belajar"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_BER
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"be"
argument_list|)
operator|&&
name|length
operator|>
literal|4
operator|&&
operator|!
name|isVowel
argument_list|(
name|text
index|[
literal|2
index|]
argument_list|)
operator|&&
name|text
index|[
literal|3
index|]
operator|==
literal|'e'
operator|&&
name|text
index|[
literal|4
index|]
operator|==
literal|'r'
condition|)
block|{
name|flags
operator||=
name|REMOVED_BER
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|2
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"per"
argument_list|)
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|length
operator|==
literal|7
operator|&&
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"pelajar"
argument_list|)
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|3
argument_list|)
return|;
block|}
if|if
condition|(
name|startsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"pe"
argument_list|)
condition|)
block|{
name|flags
operator||=
name|REMOVED_PE
expr_stmt|;
name|numSyllables
operator|--
expr_stmt|;
return|return
name|deleteN
argument_list|(
name|text
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
literal|2
argument_list|)
return|;
block|}
return|return
name|length
return|;
block|}
DECL|method|removeSuffix
specifier|private
name|int
name|removeSuffix
parameter_list|(
name|char
name|text
index|[]
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"kan"
argument_list|)
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_KE
operator|)
operator|==
literal|0
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_PENG
operator|)
operator|==
literal|0
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_PE
operator|)
operator|==
literal|0
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|length
operator|-
literal|3
return|;
block|}
if|if
condition|(
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"an"
argument_list|)
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_DI
operator|)
operator|==
literal|0
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_MENG
operator|)
operator|==
literal|0
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_TER
operator|)
operator|==
literal|0
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|length
operator|-
literal|2
return|;
block|}
if|if
condition|(
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"i"
argument_list|)
operator|&&
operator|!
name|endsWith
argument_list|(
name|text
argument_list|,
name|length
argument_list|,
literal|"si"
argument_list|)
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_BER
operator|)
operator|==
literal|0
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_KE
operator|)
operator|==
literal|0
operator|&&
operator|(
name|flags
operator|&
name|REMOVED_PENG
operator|)
operator|==
literal|0
condition|)
block|{
name|numSyllables
operator|--
expr_stmt|;
return|return
name|length
operator|-
literal|1
return|;
block|}
return|return
name|length
return|;
block|}
block|}
end_class
end_unit
