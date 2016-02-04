begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.ngram
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ngram
package|;
end_package
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
name|Tokenizer
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
name|AttributeFactory
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Tokenizes the input from an edge into n-grams of given size(s).  *<p>  * This {@link Tokenizer} create n-grams from the beginning edge of a input token.  *<p><a name="match_version"></a>As of Lucene 4.4, this class supports  * {@link #isTokenChar(int) pre-tokenization} and correctly handles  * supplementary characters.  */
end_comment
begin_class
DECL|class|EdgeNGramTokenizer
specifier|public
class|class
name|EdgeNGramTokenizer
extends|extends
name|NGramTokenizer
block|{
DECL|field|DEFAULT_MAX_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_GRAM_SIZE
init|=
literal|1
decl_stmt|;
DECL|field|DEFAULT_MIN_GRAM_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_GRAM_SIZE
init|=
literal|1
decl_stmt|;
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|super
argument_list|(
name|minGram
argument_list|,
name|maxGram
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates EdgeNGramTokenizer that can generate n-grams in the sizes of the given range    *    * @param factory {@link org.apache.lucene.util.AttributeFactory} to use    * @param minGram the smallest n-gram to generate    * @param maxGram the largest n-gram to generate    */
DECL|method|EdgeNGramTokenizer
specifier|public
name|EdgeNGramTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|int
name|minGram
parameter_list|,
name|int
name|maxGram
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|minGram
argument_list|,
name|maxGram
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
