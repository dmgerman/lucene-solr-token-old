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
name|Reader
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
name|analysis
operator|.
name|pattern
operator|.
name|PatternTokenizer
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
name|InitializationException
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
name|TokenizerFactory
import|;
end_import
begin_comment
comment|/**  * Factory for {@link PatternTokenizer}.  * This tokenizer uses regex pattern matching to construct distinct tokens  * for the input stream.  It takes two arguments:  "pattern" and "group".  *<p/>  *<ul>  *<li>"pattern" is the regular expression.</li>  *<li>"group" says which group to extract into tokens.</li>  *</ul>  *<p>  * group=-1 (the default) is equivalent to "split".  In this case, the tokens will  * be equivalent to the output from (without empty tokens):  * {@link String#split(java.lang.String)}  *</p>  *<p>  * Using group>= 0 selects the matching group as the token.  For example, if you have:<br/>  *<pre>  *  pattern = \'([^\']+)\'  *  group = 0  *  input = aaa 'bbb' 'ccc'  *</pre>  * the output will be two tokens: 'bbb' and 'ccc' (including the ' marks).  With the same input  * but using group=1, the output would be: bbb and ccc (no ' marks)  *</p>  *<p>NOTE: This Tokenizer does not output tokens that are of zero length.</p>  *  *<pre class="prettyprint">  *&lt;fieldType name="text_ptn" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.PatternTokenizerFactory" pattern="\'([^\']+)\'" group="1"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>   *   * @see PatternTokenizer  * @since solr1.2  *  */
end_comment
begin_class
DECL|class|PatternTokenizerFactory
specifier|public
class|class
name|PatternTokenizerFactory
extends|extends
name|TokenizerFactory
block|{
DECL|field|PATTERN
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN
init|=
literal|"pattern"
decl_stmt|;
DECL|field|GROUP
specifier|public
specifier|static
specifier|final
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
DECL|field|pattern
specifier|protected
name|Pattern
name|pattern
decl_stmt|;
DECL|field|group
specifier|protected
name|int
name|group
decl_stmt|;
comment|/**    * Require a configured pattern    */
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
name|pattern
operator|=
name|getPattern
argument_list|(
name|PATTERN
argument_list|)
expr_stmt|;
name|group
operator|=
operator|-
literal|1
expr_stmt|;
comment|// use 'split'
name|String
name|g
init|=
name|args
operator|.
name|get
argument_list|(
name|GROUP
argument_list|)
decl_stmt|;
if|if
condition|(
name|g
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|group
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|g
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"invalid group argument: "
operator|+
name|g
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**    * Split the input using configured pattern    */
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
specifier|final
name|Reader
name|in
parameter_list|)
block|{
try|try
block|{
return|return
operator|new
name|PatternTokenizer
argument_list|(
name|in
argument_list|,
name|pattern
argument_list|,
name|group
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"IOException thrown creating PatternTokenizer instance"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
