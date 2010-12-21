begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.core
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|core
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|CharTokenizer
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
name|AttributeSource
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
comment|/**  * LowerCaseTokenizer performs the function of LetterTokenizer  * and LowerCaseFilter together.  It divides text at non-letters and converts  * them to lower case.  While it is functionally equivalent to the combination  * of LetterTokenizer and LowerCaseFilter, there is a performance advantage  * to doing the two tasks at once, hence this (redundant) implementation.  *<P>  * Note: this does a decent job for most European languages, but does a terrible  * job for some Asian languages, where words are not separated by spaces.  *</p>  *<p>  *<a name="version"/>  * You must specify the required {@link Version} compatibility when creating  * {@link LowerCaseTokenizer}:  *<ul>  *<li>As of 3.1, {@link CharTokenizer} uses an int based API to normalize and  * detect token characters. See {@link CharTokenizer#isTokenChar(int)} and  * {@link CharTokenizer#normalize(int)} for details.</li>  *</ul>  *</p>  */
end_comment
begin_class
DECL|class|LowerCaseTokenizer
specifier|public
specifier|final
class|class
name|LowerCaseTokenizer
extends|extends
name|LetterTokenizer
block|{
comment|/**    * Construct a new LowerCaseTokenizer.    *     * @param matchVersion    *          Lucene version to match See {@link<a href="#version">above</a>}    *     * @param in    *          the input to split up into tokens    */
DECL|method|LowerCaseTokenizer
specifier|public
name|LowerCaseTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**     * Construct a new LowerCaseTokenizer using a given {@link AttributeSource}.    *    * @param matchVersion    *          Lucene version to match See {@link<a href="#version">above</a>}    * @param source    *          the attribute source to use for this {@link Tokenizer}    * @param in    *          the input to split up into tokens    */
DECL|method|LowerCaseTokenizer
specifier|public
name|LowerCaseTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|source
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct a new LowerCaseTokenizer using a given    * {@link org.apache.lucene.util.AttributeSource.AttributeFactory}.    *    * @param matchVersion    *          Lucene version to match See {@link<a href="#version">above</a>}    * @param factory    *          the attribute factory to use for this {@link Tokenizer}    * @param in    *          the input to split up into tokens    */
DECL|method|LowerCaseTokenizer
specifier|public
name|LowerCaseTokenizer
parameter_list|(
name|Version
name|matchVersion
parameter_list|,
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|matchVersion
argument_list|,
name|factory
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/** Converts char to lower case    * {@link Character#toLowerCase(int)}.*/
annotation|@
name|Override
DECL|method|normalize
specifier|protected
name|int
name|normalize
parameter_list|(
name|int
name|c
parameter_list|)
block|{
return|return
name|Character
operator|.
name|toLowerCase
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
end_class
end_unit
