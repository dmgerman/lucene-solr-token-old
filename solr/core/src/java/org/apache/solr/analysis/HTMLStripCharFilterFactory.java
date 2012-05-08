begin_unit
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
name|charfilter
operator|.
name|HTMLStripCharFilter
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
name|Set
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
begin_comment
comment|/** * Factory for {@link HTMLStripCharFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_html" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;charFilter class="solr.HTMLStripCharFilterFactory" escapedTags="a, title" /&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment
begin_class
DECL|class|HTMLStripCharFilterFactory
specifier|public
class|class
name|HTMLStripCharFilterFactory
extends|extends
name|CharFilterFactory
block|{
DECL|field|escapedTags
name|Set
argument_list|<
name|String
argument_list|>
name|escapedTags
init|=
literal|null
decl_stmt|;
DECL|field|TAG_NAME_PATTERN
name|Pattern
name|TAG_NAME_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"[^\\s,]+"
argument_list|)
decl_stmt|;
DECL|method|create
specifier|public
name|HTMLStripCharFilter
name|create
parameter_list|(
name|CharStream
name|input
parameter_list|)
block|{
name|HTMLStripCharFilter
name|charFilter
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|escapedTags
condition|)
block|{
name|charFilter
operator|=
operator|new
name|HTMLStripCharFilter
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|charFilter
operator|=
operator|new
name|HTMLStripCharFilter
argument_list|(
name|input
argument_list|,
name|escapedTags
argument_list|)
expr_stmt|;
block|}
return|return
name|charFilter
return|;
block|}
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
name|String
name|escapedTagsArg
init|=
name|args
operator|.
name|get
argument_list|(
literal|"escapedTags"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|escapedTagsArg
condition|)
block|{
name|Matcher
name|matcher
init|=
name|TAG_NAME_PATTERN
operator|.
name|matcher
argument_list|(
name|escapedTagsArg
argument_list|)
decl_stmt|;
while|while
condition|(
name|matcher
operator|.
name|find
argument_list|()
condition|)
block|{
if|if
condition|(
literal|null
operator|==
name|escapedTags
condition|)
block|{
name|escapedTags
operator|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|escapedTags
operator|.
name|add
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class
end_unit
