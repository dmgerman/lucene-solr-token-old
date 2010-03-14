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
name|WhitespaceAnalyzer
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
name|HashSet
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  *  * @since solr 1.3  **/
end_comment
begin_class
DECL|class|SimpleQueryConverter
class|class
name|SimpleQueryConverter
extends|extends
name|SpellingQueryConverter
block|{
annotation|@
name|Override
DECL|method|convert
specifier|public
name|Collection
argument_list|<
name|Token
argument_list|>
name|convert
parameter_list|(
name|String
name|origQuery
parameter_list|)
block|{
name|Collection
argument_list|<
name|Token
argument_list|>
name|result
init|=
operator|new
name|HashSet
argument_list|<
name|Token
argument_list|>
argument_list|()
decl_stmt|;
name|WhitespaceAnalyzer
name|analyzer
init|=
operator|new
name|WhitespaceAnalyzer
argument_list|()
decl_stmt|;
name|TokenStream
name|ts
init|=
name|analyzer
operator|.
name|tokenStream
argument_list|(
literal|""
argument_list|,
operator|new
name|StringReader
argument_list|(
name|origQuery
argument_list|)
argument_list|)
decl_stmt|;
name|Token
name|tok
init|=
literal|null
decl_stmt|;
try|try
block|{
while|while
condition|(
operator|(
name|tok
operator|=
name|ts
operator|.
name|next
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|tok
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
