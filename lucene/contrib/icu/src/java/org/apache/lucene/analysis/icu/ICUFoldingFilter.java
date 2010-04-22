begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.icu
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|icu
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
name|TokenStream
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
name|Normalizer2
import|;
end_import
begin_comment
comment|/**  * A TokenFilter that applies search term folding to Unicode text,  * applying foldings from UTR#30 Character Foldings.  *<p>  * This filter applies the following foldings from the report to unicode text:  *<ul>  *<li>Accent removal  *<li>Case folding  *<li>Canonical duplicates folding  *<li>Dashes folding  *<li>Diacritic removal (including stroke, hook, descender)  *<li>Greek letterforms folding  *<li>Han Radical folding  *<li>Hebrew Alternates folding  *<li>Jamo folding  *<li>Letterforms folding  *<li>Math symbol folding  *<li>Multigraph Expansions: All  *<li>Native digit folding  *<li>No-break folding  *<li>Overline folding  *<li>Positional forms folding  *<li>Small forms folding  *<li>Space folding  *<li>Spacing Accents folding  *<li>Subscript folding  *<li>Superscript folding  *<li>Suzhou Numeral folding  *<li>Symbol folding  *<li>Underline folding  *<li>Vertical forms folding  *<li>Width folding  *</ul>  *<p>  * Additionally, Default Ignorables are removed, and text is normalized to NFKC.  * All foldings, case folding, and normalization mappings are applied recursively  * to ensure a fully folded and normalized result.  *</p>  */
end_comment
begin_class
DECL|class|ICUFoldingFilter
specifier|public
specifier|final
class|class
name|ICUFoldingFilter
extends|extends
name|ICUNormalizer2Filter
block|{
DECL|field|normalizer
specifier|private
specifier|static
specifier|final
name|Normalizer2
name|normalizer
init|=
name|Normalizer2
operator|.
name|getInstance
argument_list|(
name|ICUFoldingFilter
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"utr30.nrm"
argument_list|)
argument_list|,
literal|"utr30"
argument_list|,
name|Normalizer2
operator|.
name|Mode
operator|.
name|COMPOSE
argument_list|)
decl_stmt|;
comment|/**    * Create a new ICUFoldingFilter on the specified input    */
DECL|method|ICUFoldingFilter
specifier|public
name|ICUFoldingFilter
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|,
name|normalizer
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
