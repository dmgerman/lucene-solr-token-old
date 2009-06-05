begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|Token
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
begin_comment
comment|/**  * Converts the query string to a Collection of Lucene tokens using a regular expression.  * Boolean operators AND and OR are skipped.  *  * @since solr 1.3  **/
end_comment
begin_class
DECL|class|SpellingQueryConverter
specifier|public
class|class
name|SpellingQueryConverter
extends|extends
name|QueryConverter
block|{
DECL|field|QUERY_REGEX
specifier|protected
name|Pattern
name|QUERY_REGEX
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?:(?!(\\p{L}+:|\\d+)))\\p{L}+"
argument_list|)
decl_stmt|;
comment|/**    * Converts the original query string to a collection of Lucene Tokens.    * @param original the original query string    * @return a Collection of Lucene Tokens    */
DECL|method|convert
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|>
name|convert
parameter_list|(
name|String
name|original
parameter_list|)
block|{
if|if
condition|(
name|original
operator|==
literal|null
condition|)
block|{
comment|// this can happen with q.alt = and no query
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
name|Collection
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
comment|//TODO: Extract the words using a simple regex, but not query stuff, and then analyze them to produce the token stream
name|Matcher
name|matcher
init|=
name|QUERY_REGEX
operator|.
name|matcher
argument_list|(
name|original
argument_list|)
decl_stmt|;
name|TokenStream
name|stream
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
name|String
name|word
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|word
operator|.
name|equals
argument_list|(
literal|"AND"
argument_list|)
operator|==
literal|false
operator|&&
name|word
operator|.
name|equals
argument_list|(
literal|"OR"
argument_list|)
operator|==
literal|false
condition|)
block|{
try|try
block|{
name|stream
operator|=
name|analyzer
operator|.
name|reusableTokenStream
argument_list|(
literal|""
argument_list|,
operator|new
name|StringReader
argument_list|(
name|word
argument_list|)
argument_list|)
expr_stmt|;
name|Token
name|token
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|stream
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|token
operator|.
name|setStartOffset
argument_list|(
name|matcher
operator|.
name|start
argument_list|()
argument_list|)
expr_stmt|;
name|token
operator|.
name|setEndOffset
argument_list|(
name|matcher
operator|.
name|end
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
block|}
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
