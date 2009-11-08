begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.ru
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ru
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
begin_comment
comment|// for javadocs
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
name|LetterTokenizer
import|;
end_import
begin_comment
comment|// for javadocs
end_comment
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
begin_comment
comment|/**  * A RussianLetterTokenizer is a {@link Tokenizer} that extends {@link LetterTokenizer}  * by also allowing the basic latin digits 0-9.   */
end_comment
begin_class
DECL|class|RussianLetterTokenizer
specifier|public
class|class
name|RussianLetterTokenizer
extends|extends
name|CharTokenizer
block|{
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|AttributeSource
name|source
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
DECL|method|RussianLetterTokenizer
specifier|public
name|RussianLetterTokenizer
parameter_list|(
name|AttributeFactory
name|factory
parameter_list|,
name|Reader
name|in
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|/**      * Collects only characters which satisfy      * {@link Character#isLetter(char)}.      */
annotation|@
name|Override
DECL|method|isTokenChar
specifier|protected
name|boolean
name|isTokenChar
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|Character
operator|.
name|isLetter
argument_list|(
name|c
argument_list|)
operator|||
operator|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
operator|)
condition|)
return|return
literal|true
return|;
else|else
return|return
literal|false
return|;
block|}
block|}
end_class
end_unit
