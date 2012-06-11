begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|CharStream
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
name|pattern
operator|.
name|PatternReplaceCharFilter
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
name|util
operator|.
name|CharFilterFactory
import|;
end_import
begin_comment
comment|/**  * Factory for {@link PatternReplaceCharFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_ptnreplace" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;charFilter class="solr.PatternReplaceCharFilterFactory"   *                    pattern="([^a-z])" replacement=""/&gt;  *&lt;tokenizer class="solr.KeywordTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   *  * @since Solr 3.1  */
end_comment
begin_class
DECL|class|PatternReplaceCharFilterFactory
specifier|public
class|class
name|PatternReplaceCharFilterFactory
extends|extends
name|CharFilterFactory
block|{
DECL|field|p
specifier|private
name|Pattern
name|p
decl_stmt|;
DECL|field|replacement
specifier|private
name|String
name|replacement
decl_stmt|;
annotation|@
name|Override
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|p
operator|=
name|getPattern
argument_list|(
literal|"pattern"
argument_list|)
expr_stmt|;
name|replacement
operator|=
name|args
operator|.
name|get
argument_list|(
literal|"replacement"
argument_list|)
expr_stmt|;
if|if
condition|(
name|replacement
operator|==
literal|null
condition|)
name|replacement
operator|=
literal|""
expr_stmt|;
comment|// TODO: throw exception if you set maxBlockChars or blockDelimiters ?
block|}
DECL|method|create
specifier|public
name|CharStream
name|create
parameter_list|(
name|CharStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|PatternReplaceCharFilter
argument_list|(
name|p
argument_list|,
name|replacement
argument_list|,
name|input
argument_list|)
return|;
block|}
block|}
end_class
end_unit
