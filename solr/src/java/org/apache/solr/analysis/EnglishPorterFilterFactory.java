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
name|miscellaneous
operator|.
name|KeywordMarkerFilter
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
name|snowball
operator|.
name|SnowballFilter
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
name|solr
operator|.
name|common
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
name|solr
operator|.
name|util
operator|.
name|plugin
operator|.
name|ResourceLoaderAware
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
comment|/**  * @version $Id$  *  * @deprecated Use SnowballPorterFilterFactory with language="English" instead  */
end_comment
begin_class
DECL|class|EnglishPorterFilterFactory
specifier|public
class|class
name|EnglishPorterFilterFactory
extends|extends
name|BaseTokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|PROTECTED_TOKENS
specifier|public
specifier|static
specifier|final
name|String
name|PROTECTED_TOKENS
init|=
literal|"protected"
decl_stmt|;
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
name|PROTECTED_TOKENS
argument_list|)
decl_stmt|;
if|if
condition|(
name|wordFiles
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|protectedWords
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|wordFiles
argument_list|,
literal|false
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
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|protectedWords
specifier|private
name|CharArraySet
name|protectedWords
init|=
literal|null
decl_stmt|;
DECL|method|create
specifier|public
name|TokenFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
if|if
condition|(
name|protectedWords
operator|!=
literal|null
condition|)
name|input
operator|=
operator|new
name|KeywordMarkerFilter
argument_list|(
name|input
argument_list|,
name|protectedWords
argument_list|)
expr_stmt|;
return|return
operator|new
name|SnowballFilter
argument_list|(
name|input
argument_list|,
operator|new
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
