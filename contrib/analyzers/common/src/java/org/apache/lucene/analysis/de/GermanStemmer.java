begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.de
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|de
package|;
end_package
begin_comment
comment|// This file is encoded in UTF-8
end_comment
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A stemmer for German words.   *<p>  * The algorithm is based on the report  * "A Fast and Simple Stemming Algorithm for German Words" by J&ouml;rg  * Caumanns (joerg.caumanns at isst.fhg.de).  *</p>  *  * @version   $Id$  */
end_comment
begin_class
DECL|class|GermanStemmer
specifier|public
class|class
name|GermanStemmer
block|{
comment|/**      * Buffer for the terms while stemming them.      */
DECL|field|sb
specifier|private
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|/**      * Amount of characters that are removed with<tt>substitute()</tt> while stemming.      */
DECL|field|substCount
specifier|private
name|int
name|substCount
init|=
literal|0
decl_stmt|;
comment|/**      * Stemms the given term to an unique<tt>discriminator</tt>.      *      * @param term  The term that should be stemmed.      * @return      Discriminator for<tt>term</tt>      */
DECL|method|stem
specifier|protected
name|String
name|stem
parameter_list|(
name|String
name|term
parameter_list|)
block|{
comment|// Use lowercase for medium stemming.
name|term
operator|=
name|term
operator|.
name|toLowerCase
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|isStemmable
argument_list|(
name|term
argument_list|)
condition|)
return|return
name|term
return|;
comment|// Reset the StringBuilder.
name|sb
operator|.
name|delete
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
name|term
argument_list|)
expr_stmt|;
comment|// Stemming starts here...
name|substitute
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|strip
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|optimize
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|resubstitute
argument_list|(
name|sb
argument_list|)
expr_stmt|;
name|removeParticleDenotion
argument_list|(
name|sb
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Checks if a term could be stemmed.      *      * @return  true if, and only if, the given term consists in letters.      */
DECL|method|isStemmable
specifier|private
name|boolean
name|isStemmable
parameter_list|(
name|String
name|term
parameter_list|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|term
operator|.
name|length
argument_list|()
condition|;
name|c
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|Character
operator|.
name|isLetter
argument_list|(
name|term
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/**      * suffix stripping (stemming) on the current term. The stripping is reduced      * to the seven "base" suffixes "e", "s", "n", "t", "em", "er" and * "nd",      * from which all regular suffixes are build of. The simplification causes      * some overstemming, and way more irregular stems, but still provides unique.      * discriminators in the most of those cases.      * The algorithm is context free, except of the length restrictions.      */
DECL|method|strip
specifier|private
name|void
name|strip
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
name|boolean
name|doMore
init|=
literal|true
decl_stmt|;
while|while
condition|(
name|doMore
operator|&&
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|3
condition|)
block|{
if|if
condition|(
operator|(
name|buffer
operator|.
name|length
argument_list|()
operator|+
name|substCount
operator|>
literal|5
operator|)
operator|&&
name|buffer
operator|.
name|substring
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"nd"
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|delete
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|buffer
operator|.
name|length
argument_list|()
operator|+
name|substCount
operator|>
literal|4
operator|)
operator|&&
name|buffer
operator|.
name|substring
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"em"
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|delete
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|buffer
operator|.
name|length
argument_list|()
operator|+
name|substCount
operator|>
literal|4
operator|)
operator|&&
name|buffer
operator|.
name|substring
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"er"
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|delete
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'e'
condition|)
block|{
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'s'
condition|)
block|{
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'n'
condition|)
block|{
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// "t" occurs only as suffix of verbs.
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'t'
condition|)
block|{
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doMore
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Does some optimizations on the term. This optimisations are      * contextual.      */
DECL|method|optimize
specifier|private
name|void
name|optimize
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
comment|// Additional step for female plurals of professions and inhabitants.
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|5
operator|&&
name|buffer
operator|.
name|substring
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|5
argument_list|,
name|buffer
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
literal|"erin*"
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|strip
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
comment|// Additional step for irregular plural nouns like "Matrizen -> Matrix".
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
operator|(
literal|'z'
operator|)
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|,
literal|'x'
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Removes a particle denotion ("ge") from a term.      */
DECL|method|removeParticleDenotion
specifier|private
name|void
name|removeParticleDenotion
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
if|if
condition|(
name|buffer
operator|.
name|length
argument_list|()
operator|>
literal|4
condition|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|3
condition|;
name|c
operator|++
control|)
block|{
if|if
condition|(
name|buffer
operator|.
name|substring
argument_list|(
name|c
argument_list|,
name|c
operator|+
literal|4
argument_list|)
operator|.
name|equals
argument_list|(
literal|"gege"
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|delete
argument_list|(
name|c
argument_list|,
name|c
operator|+
literal|2
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
block|}
comment|/**      * Do some substitutions for the term to reduce overstemming:      *      * - Substitute Umlauts with their corresponding vowel: Ã¤Ã¶Ã¼ -> aou,      *   "Ã" is substituted by "ss"      * - Substitute a second char of a pair of equal characters with      *   an asterisk: ?? -> ?*      * - Substitute some common character combinations with a token:      *   sch/ch/ei/ie/ig/st -> $/Â§/%/&/#/!      */
DECL|method|substitute
specifier|private
name|void
name|substitute
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
name|substCount
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|buffer
operator|.
name|length
argument_list|()
condition|;
name|c
operator|++
control|)
block|{
comment|// Replace the second char of a pair of the equal characters with an asterisk
if|if
condition|(
name|c
operator|>
literal|0
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|-
literal|1
argument_list|)
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'*'
argument_list|)
expr_stmt|;
block|}
comment|// Substitute Umlauts.
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'Ã¤'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'a'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'Ã¶'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'o'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'Ã¼'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'u'
argument_list|)
expr_stmt|;
block|}
comment|// Fix bug so that 'Ã' at the end of a word is replaced.
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'Ã'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'s'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|insert
argument_list|(
name|c
operator|+
literal|1
argument_list|,
literal|'s'
argument_list|)
expr_stmt|;
name|substCount
operator|++
expr_stmt|;
block|}
comment|// Take care that at least one character is left left side from the current one
if|if
condition|(
name|c
operator|<
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
comment|// Masking several common character combinations with an token
if|if
condition|(
operator|(
name|c
operator|<
name|buffer
operator|.
name|length
argument_list|()
operator|-
literal|2
operator|)
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'s'
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
operator|==
literal|'c'
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|+
literal|2
argument_list|)
operator|==
literal|'h'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'$'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|delete
argument_list|(
name|c
operator|+
literal|1
argument_list|,
name|c
operator|+
literal|3
argument_list|)
expr_stmt|;
name|substCount
operator|=
operator|+
literal|2
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'c'
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
operator|==
literal|'h'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'Â§'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
expr_stmt|;
name|substCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'e'
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
operator|==
literal|'i'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'%'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
expr_stmt|;
name|substCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'i'
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
operator|==
literal|'e'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'&'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
expr_stmt|;
name|substCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'i'
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
operator|==
literal|'g'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'#'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
expr_stmt|;
name|substCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'s'
operator|&&
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
operator|==
literal|'t'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'!'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|deleteCharAt
argument_list|(
name|c
operator|+
literal|1
argument_list|)
expr_stmt|;
name|substCount
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**      * Undoes the changes made by substitute(). That are character pairs and      * character combinations. Umlauts will remain as their corresponding vowel,      * as "Ã" remains as "ss".      */
DECL|method|resubstitute
specifier|private
name|void
name|resubstitute
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|)
block|{
for|for
control|(
name|int
name|c
init|=
literal|0
init|;
name|c
operator|<
name|buffer
operator|.
name|length
argument_list|()
condition|;
name|c
operator|++
control|)
block|{
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'*'
condition|)
block|{
name|char
name|x
init|=
name|buffer
operator|.
name|charAt
argument_list|(
name|c
operator|-
literal|1
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
name|x
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'$'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'s'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|insert
argument_list|(
name|c
operator|+
literal|1
argument_list|,
operator|new
name|char
index|[]
block|{
literal|'c'
block|,
literal|'h'
block|}
argument_list|,
literal|0
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'Â§'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'c'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|insert
argument_list|(
name|c
operator|+
literal|1
argument_list|,
literal|'h'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'%'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'e'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|insert
argument_list|(
name|c
operator|+
literal|1
argument_list|,
literal|'i'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'&'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'i'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|insert
argument_list|(
name|c
operator|+
literal|1
argument_list|,
literal|'e'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'#'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'i'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|insert
argument_list|(
name|c
operator|+
literal|1
argument_list|,
literal|'g'
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|buffer
operator|.
name|charAt
argument_list|(
name|c
argument_list|)
operator|==
literal|'!'
condition|)
block|{
name|buffer
operator|.
name|setCharAt
argument_list|(
name|c
argument_list|,
literal|'s'
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|insert
argument_list|(
name|c
operator|+
literal|1
argument_list|,
literal|'t'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
