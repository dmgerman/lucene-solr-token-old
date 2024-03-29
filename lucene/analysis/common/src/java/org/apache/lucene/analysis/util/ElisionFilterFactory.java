begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|util
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
name|fr
operator|.
name|FrenchAnalyzer
import|;
end_import
begin_comment
comment|/**  * Factory for {@link ElisionFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_elsn" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.StandardTokenizerFactory"/&gt;  *&lt;filter class="solr.LowerCaseFilterFactory"/&gt;  *&lt;filter class="solr.ElisionFilterFactory"   *       articles="stopwordarticles.txt" ignoreCase="true"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  */
end_comment
begin_class
DECL|class|ElisionFilterFactory
specifier|public
class|class
name|ElisionFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
implements|,
name|MultiTermAwareComponent
block|{
DECL|field|articlesFile
specifier|private
specifier|final
name|String
name|articlesFile
decl_stmt|;
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|articles
specifier|private
name|CharArraySet
name|articles
decl_stmt|;
comment|/** Creates a new ElisionFilterFactory */
DECL|method|ElisionFilterFactory
specifier|public
name|ElisionFilterFactory
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
name|articlesFile
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"articles"
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
name|articlesFile
operator|==
literal|null
condition|)
block|{
name|articles
operator|=
name|FrenchAnalyzer
operator|.
name|DEFAULT_ARTICLES
expr_stmt|;
block|}
else|else
block|{
name|articles
operator|=
name|getWordSet
argument_list|(
name|loader
argument_list|,
name|articlesFile
argument_list|,
name|ignoreCase
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|create
specifier|public
name|ElisionFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|ElisionFilter
argument_list|(
name|input
argument_list|,
name|articles
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMultiTermComponent
specifier|public
name|AbstractAnalysisFactory
name|getMultiTermComponent
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class
end_unit
