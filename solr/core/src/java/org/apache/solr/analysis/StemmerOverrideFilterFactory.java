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
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|StemmerOverrideFilter
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
name|util
operator|.
name|StrUtils
import|;
end_import
begin_comment
comment|/**  * Factory for {@link StemmerOverrideFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_dicstem" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.StemmerOverrideFilterFactory" dictionary="dictionary.txt" ignoreCase="false"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *  */
end_comment
begin_class
DECL|class|StemmerOverrideFilterFactory
specifier|public
class|class
name|StemmerOverrideFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|dictionary
specifier|private
name|CharArrayMap
argument_list|<
name|String
argument_list|>
name|dictionary
init|=
literal|null
decl_stmt|;
DECL|field|ignoreCase
specifier|private
name|boolean
name|ignoreCase
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
name|dictionaryFiles
init|=
name|args
operator|.
name|get
argument_list|(
literal|"dictionary"
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
name|dictionaryFiles
operator|!=
literal|null
condition|)
block|{
name|assureMatchVersion
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|StrUtils
operator|.
name|splitFileNames
argument_list|(
name|dictionaryFiles
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|files
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|dictionary
operator|=
operator|new
name|CharArrayMap
argument_list|<
name|String
argument_list|>
argument_list|(
name|luceneMatchVersion
argument_list|,
name|files
operator|.
name|size
argument_list|()
operator|*
literal|10
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|file
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|list
control|)
block|{
name|String
index|[]
name|mapping
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|dictionary
operator|.
name|put
argument_list|(
name|mapping
index|[
literal|0
index|]
argument_list|,
name|mapping
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"IOException thrown while loading dictionary"
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
name|dictionary
operator|==
literal|null
condition|?
name|input
else|:
operator|new
name|StemmerOverrideFilter
argument_list|(
name|luceneMatchVersion
argument_list|,
name|input
argument_list|,
name|dictionary
argument_list|)
return|;
block|}
block|}
end_class
end_unit
