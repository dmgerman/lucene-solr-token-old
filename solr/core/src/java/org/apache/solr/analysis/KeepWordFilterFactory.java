begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
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
name|miscellaneous
operator|.
name|KeepWordFilter
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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * Factory for {@link KeepWordFilter}.   *<pre class="prettyprint">  *&lt;fieldType name="text_keepword" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.KeepWordFilterFactory" words="keepwords.txt" ignoreCase="false" enablePositionIncrements="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>   *  */
end_comment
begin_class
DECL|class|KeepWordFilterFactory
specifier|public
class|class
name|KeepWordFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
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
name|assureMatchVersion
argument_list|()
expr_stmt|;
block|}
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|String
name|wordFiles
init|=
name|args
operator|.
name|get
argument_list|(
literal|"words"
argument_list|)
decl_stmt|;
name|ignoreCase
operator|=
name|getBoolean
argument_list|(
literal|"ignoreCase"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|enablePositionIncrements
operator|=
name|getBoolean
argument_list|(
literal|"enablePositionIncrements"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|wordFiles
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|words
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|wordFiles
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"IOException thrown while loading words"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|words
specifier|private
name|CharArraySet
name|words
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|enablePositionIncrements
specifier|private
name|boolean
name|enablePositionIncrements
decl_stmt|;
comment|/**    * Set the keep word list.    * NOTE: if ignoreCase==true, the words are expected to be lowercase    */
DECL|method|setWords
specifier|public
name|void
name|setWords
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|words
parameter_list|)
block|{
name|this
operator|.
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|words
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
DECL|method|setIgnoreCase
specifier|public
name|void
name|setIgnoreCase
parameter_list|(
name|boolean
name|ignoreCase
parameter_list|)
block|{
if|if
condition|(
name|words
operator|!=
literal|null
operator|&&
name|this
operator|.
name|ignoreCase
operator|!=
name|ignoreCase
condition|)
block|{
name|words
operator|=
operator|new
name|CharArraySet
argument_list|(
name|luceneMatchVersion
argument_list|,
name|words
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|ignoreCase
operator|=
name|ignoreCase
expr_stmt|;
block|}
DECL|method|isEnablePositionIncrements
specifier|public
name|boolean
name|isEnablePositionIncrements
parameter_list|()
block|{
return|return
name|enablePositionIncrements
return|;
block|}
DECL|method|isIgnoreCase
specifier|public
name|boolean
name|isIgnoreCase
parameter_list|()
block|{
return|return
name|ignoreCase
return|;
block|}
DECL|method|getWords
specifier|public
name|CharArraySet
name|getWords
parameter_list|()
block|{
return|return
name|words
return|;
block|}
DECL|method|create
specifier|public
name|KeepWordFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|KeepWordFilter
argument_list|(
name|enablePositionIncrements
argument_list|,
name|input
argument_list|,
name|words
argument_list|)
return|;
block|}
block|}
end_class
end_unit
