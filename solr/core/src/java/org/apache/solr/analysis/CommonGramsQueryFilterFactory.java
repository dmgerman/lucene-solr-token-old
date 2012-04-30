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
name|util
operator|.
name|Map
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
name|commongrams
operator|.
name|CommonGramsFilter
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
name|commongrams
operator|.
name|CommonGramsQueryFilter
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
name|core
operator|.
name|StopAnalyzer
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
name|CharArraySet
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
name|ResourceLoader
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
name|ResourceLoaderAware
import|;
end_import
begin_comment
comment|/**  * Construct {@link CommonGramsQueryFilter}.  *   * This is pretty close to a straight copy from {@link StopFilterFactory}.  *   *<pre class="prettyprint">  *&lt;fieldType name="text_cmmngrmsqry" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.CommonGramsQueryFilterFactory" words="commongramsquerystopwords.txt" ignoreCase="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment
begin_class
DECL|class|CommonGramsQueryFilterFactory
specifier|public
class|class
name|CommonGramsQueryFilterFactory
extends|extends
name|BaseTokenFilterFactory
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
name|commonWordFiles
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
if|if
condition|(
name|commonWordFiles
operator|!=
literal|null
condition|)
block|{
try|try
block|{
if|if
condition|(
literal|"snowball"
operator|.
name|equalsIgnoreCase
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|"format"
argument_list|)
argument_list|)
condition|)
block|{
name|commonWords
operator|=
name|getSnowballWordSet
argument_list|(
name|loader
argument_list|,
name|commonWordFiles
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|commonWords
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|commonWordFiles
argument_list|,
name|ignoreCase
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
name|InitializationException
argument_list|(
literal|"IOException thrown while loading common word file"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|commonWords
operator|=
name|StopAnalyzer
operator|.
name|ENGLISH_STOP_WORDS_SET
expr_stmt|;
block|}
block|}
comment|// Force the use of a char array set, as it is the most performant, although
comment|// this may break things if Lucene ever goes away from it. See SOLR-1095
DECL|field|commonWords
specifier|private
name|CharArraySet
name|commonWords
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
decl_stmt|;
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
DECL|method|getCommonWords
specifier|public
name|CharArraySet
name|getCommonWords
parameter_list|()
block|{
return|return
name|commonWords
return|;
block|}
comment|/**    * Create a CommonGramsFilter and wrap it with a CommonGramsQueryFilter    */
DECL|method|create
specifier|public
name|CommonGramsQueryFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|CommonGramsFilter
name|commonGrams
init|=
operator|new
name|CommonGramsFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|,
name|commonWords
argument_list|)
decl_stmt|;
name|CommonGramsQueryFilter
name|commonGramsQuery
init|=
operator|new
name|CommonGramsQueryFilter
argument_list|(
name|commonGrams
argument_list|)
decl_stmt|;
return|return
name|commonGramsQuery
return|;
block|}
block|}
end_class
end_unit
