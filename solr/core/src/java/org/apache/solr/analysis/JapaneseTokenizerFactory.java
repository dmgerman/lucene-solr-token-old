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
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CodingErrorAction
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|ja
operator|.
name|JapaneseTokenizer
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
name|ja
operator|.
name|JapaneseTokenizer
operator|.
name|Mode
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
name|ja
operator|.
name|dict
operator|.
name|UserDictionary
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
name|IOUtils
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
comment|/**  * Factory for {@link org.apache.lucene.analysis.ja.JapaneseTokenizer}.  *<pre class="prettyprint">  *&lt;fieldType name="text_ja" class="solr.TextField"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.JapaneseTokenizerFactory"  *       mode=NORMAL  *       userDictionary=user.txt  *       userDictionaryEncoding=UTF-8  *     /&gt;  *&lt;filter class="solr.JapaneseBaseFormFilterFactory"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;  *</pre>  */
end_comment
begin_class
DECL|class|JapaneseTokenizerFactory
specifier|public
class|class
name|JapaneseTokenizerFactory
extends|extends
name|TokenizerFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|MODE
specifier|private
specifier|static
specifier|final
name|String
name|MODE
init|=
literal|"mode"
decl_stmt|;
DECL|field|USER_DICT_PATH
specifier|private
specifier|static
specifier|final
name|String
name|USER_DICT_PATH
init|=
literal|"userDictionary"
decl_stmt|;
DECL|field|USER_DICT_ENCODING
specifier|private
specifier|static
specifier|final
name|String
name|USER_DICT_ENCODING
init|=
literal|"userDictionaryEncoding"
decl_stmt|;
DECL|field|userDictionary
specifier|private
name|UserDictionary
name|userDictionary
decl_stmt|;
DECL|field|mode
specifier|private
name|Mode
name|mode
decl_stmt|;
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
block|{
name|mode
operator|=
name|getMode
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|userDictionaryPath
init|=
name|args
operator|.
name|get
argument_list|(
name|USER_DICT_PATH
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|userDictionaryPath
operator|!=
literal|null
condition|)
block|{
name|InputStream
name|stream
init|=
name|loader
operator|.
name|openResource
argument_list|(
name|userDictionaryPath
argument_list|)
decl_stmt|;
name|String
name|encoding
init|=
name|args
operator|.
name|get
argument_list|(
name|USER_DICT_ENCODING
argument_list|)
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
name|encoding
operator|=
name|IOUtils
operator|.
name|UTF_8
expr_stmt|;
block|}
name|CharsetDecoder
name|decoder
init|=
name|Charset
operator|.
name|forName
argument_list|(
name|encoding
argument_list|)
operator|.
name|newDecoder
argument_list|()
operator|.
name|onMalformedInput
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
operator|.
name|onUnmappableCharacter
argument_list|(
name|CodingErrorAction
operator|.
name|REPORT
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|stream
argument_list|,
name|decoder
argument_list|)
decl_stmt|;
name|userDictionary
operator|=
operator|new
name|UserDictionary
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|userDictionary
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Exception thrown while loading dictionary"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|Tokenizer
name|create
parameter_list|(
name|Reader
name|input
parameter_list|)
block|{
return|return
operator|new
name|JapaneseTokenizer
argument_list|(
name|input
argument_list|,
name|userDictionary
argument_list|,
literal|true
argument_list|,
name|mode
argument_list|)
return|;
block|}
DECL|method|getMode
specifier|private
name|Mode
name|getMode
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
name|String
name|mode
init|=
name|args
operator|.
name|get
argument_list|(
name|MODE
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|!=
literal|null
condition|)
block|{
return|return
name|Mode
operator|.
name|valueOf
argument_list|(
name|mode
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|JapaneseTokenizer
operator|.
name|DEFAULT_MODE
return|;
block|}
block|}
block|}
end_class
end_unit
