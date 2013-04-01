begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.commongrams
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|commongrams
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|TokenFilter
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
name|*
import|;
end_import
begin_comment
comment|/**  * Constructs a {@link CommonGramsFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_cmmngrms" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.CommonGramsFilterFactory" words="commongramsstopwords.txt" ignoreCase="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment
begin_class
DECL|class|CommonGramsFilterFactory
specifier|public
class|class
name|CommonGramsFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
comment|// TODO: shared base class for Stop/Keep/CommonGrams?
DECL|field|commonWords
specifier|private
name|CharArraySet
name|commonWords
decl_stmt|;
DECL|field|commonWordFiles
specifier|private
specifier|final
name|String
name|commonWordFiles
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|String
name|format
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
comment|/** Creates a new CommonGramsFilterFactory */
DECL|method|CommonGramsFilterFactory
specifier|public
name|CommonGramsFilterFactory
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
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|commonWordFiles
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"words"
argument_list|)
expr_stmt|;
name|format
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"format"
argument_list|)
expr_stmt|;
name|ignoreCase
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
literal|"ignoreCase"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|args
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown parameters: "
operator|+
name|args
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|inform
specifier|public
name|void
name|inform
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|commonWordFiles
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|"snowball"
operator|.
name|equalsIgnoreCase
argument_list|(
name|format
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
annotation|@
name|Override
DECL|method|create
specifier|public
name|TokenFilter
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
return|return
name|commonGrams
return|;
block|}
block|}
end_class
end_unit
