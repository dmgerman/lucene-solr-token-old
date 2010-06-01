begin_unit
begin_package
DECL|package|org.apache.lucene.collation
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|collation
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RawCollationKey
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
name|util
operator|.
name|IndexableBinaryStringTools
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
comment|/**  *<p>  *   Converts each token into its {@link com.ibm.icu.text.CollationKey}, and  *   then encodes the CollationKey with {@link IndexableBinaryStringTools}, to  *   allow it to be stored as an index term.  *</p>  *<p>  *<strong>WARNING:</strong> Make sure you use exactly the same Collator at  *   index and query time -- CollationKeys are only comparable when produced by  *   the same Collator.  {@link com.ibm.icu.text.RuleBasedCollator}s are   *   independently versioned, so it is safe to search against stored  *   CollationKeys if the following are exactly the same (best practice is  *   to store this information with the index and check that they remain the  *   same at query time):  *</p>  *<ol>  *<li>  *     Collator version - see {@link Collator#getVersion()}  *</li>  *<li>  *     The collation strength used - see {@link Collator#setStrength(int)}  *</li>  *</ol>   *<p>  *   CollationKeys generated by ICU Collators are not compatible with those  *   generated by java.text.Collators.  Specifically, if you use   *   ICUCollationKeyFilter to generate index terms, do not use   *   {@link CollationKeyFilter} on the query side, or vice versa.  *</p>  *<p>  *   ICUCollationKeyFilter is significantly faster and generates significantly  *   shorter keys than CollationKeyFilter.  See  *<a href="http://site.icu-project.org/charts/collation-icu4j-sun"  *>http://site.icu-project.org/charts/collation-icu4j-sun</a> for key  *   generation timing and key length comparisons between ICU4J and  *   java.text.Collator over several languages.  *</p>  */
end_comment
begin_class
DECL|class|ICUCollationKeyFilter
specifier|public
specifier|final
class|class
name|ICUCollationKeyFilter
extends|extends
name|TokenFilter
block|{
DECL|field|collator
specifier|private
name|Collator
name|collator
init|=
literal|null
decl_stmt|;
DECL|field|reusableKey
specifier|private
name|RawCollationKey
name|reusableKey
init|=
operator|new
name|RawCollationKey
argument_list|()
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
init|=
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    *     * @param input Source token stream    * @param collator CollationKey generator    */
DECL|method|ICUCollationKeyFilter
specifier|public
name|ICUCollationKeyFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|Collator
name|collator
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|collator
operator|=
name|collator
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
name|char
index|[]
name|termBuffer
init|=
name|termAtt
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|String
name|termText
init|=
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|termAtt
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|collator
operator|.
name|getRawCollationKey
argument_list|(
name|termText
argument_list|,
name|reusableKey
argument_list|)
expr_stmt|;
name|int
name|encodedLength
init|=
name|IndexableBinaryStringTools
operator|.
name|getEncodedLength
argument_list|(
name|reusableKey
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|reusableKey
operator|.
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|encodedLength
operator|>
name|termBuffer
operator|.
name|length
condition|)
block|{
name|termAtt
operator|.
name|resizeBuffer
argument_list|(
name|encodedLength
argument_list|)
expr_stmt|;
block|}
name|termAtt
operator|.
name|setLength
argument_list|(
name|encodedLength
argument_list|)
expr_stmt|;
name|IndexableBinaryStringTools
operator|.
name|encode
argument_list|(
name|reusableKey
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|reusableKey
operator|.
name|size
argument_list|,
name|termAtt
operator|.
name|buffer
argument_list|()
argument_list|,
literal|0
argument_list|,
name|encodedLength
argument_list|)
expr_stmt|;
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
