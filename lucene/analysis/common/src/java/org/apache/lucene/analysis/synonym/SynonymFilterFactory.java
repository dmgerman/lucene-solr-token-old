begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.synonym
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|synonym
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Analyzer
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
name|core
operator|.
name|LowerCaseFilter
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
name|WhitespaceTokenizer
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
name|TokenFilterFactory
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
name|Version
import|;
end_import
begin_comment
comment|/**  * Factory for {@link SynonymFilter}.  *<pre class="prettyprint">  *&lt;fieldType name="text_synonym" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.WhitespaceTokenizerFactory"/&gt;  *&lt;filter class="solr.SynonymFilterFactory" synonyms="synonyms.txt"   *             format="solr" ignoreCase="false" expand="true"   *             tokenizerFactory="solr.WhitespaceTokenizerFactory"  *             [optional tokenizer factory parameters]/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   *<p>  * An optional param name prefix of "tokenizerFactory." may be used for any   * init params that the SynonymFilterFactory needs to pass to the specified   * TokenizerFactory.  If the TokenizerFactory expects an init parameters with   * the same name as an init param used by the SynonymFilterFactory, the prefix   * is mandatory.  *</p>  *   *<p>  * The optional {@code format} parameter controls how the synonyms will be parsed:  * It supports the short names of {@code solr} for {@link SolrSynonymParser}   * and {@code wordnet} for and {@link WordnetSynonymParser}, or your own   * {@code SynonymMap.Parser} class name. The default is {@code solr}.  * A custom {@link SynonymMap.Parser} is expected to have a constructor taking:  *<ul>  *<li><code>boolean dedup</code> - true if duplicates should be ignored, false otherwise</li>  *<li><code>boolean expand</code> - true if conflation groups should be expanded, false if they are one-directional</li>  *<li><code>{@link Analyzer} analyzer</code> - an analyzer used for each raw synonym</li>  *</ul>  * @see SolrSynonymParser SolrSynonymParser: default format  */
end_comment
begin_class
DECL|class|SynonymFilterFactory
specifier|public
class|class
name|SynonymFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|ignoreCase
specifier|private
specifier|final
name|boolean
name|ignoreCase
decl_stmt|;
DECL|field|tokenizerFactory
specifier|private
specifier|final
name|String
name|tokenizerFactory
decl_stmt|;
DECL|field|synonyms
specifier|private
specifier|final
name|String
name|synonyms
decl_stmt|;
DECL|field|format
specifier|private
specifier|final
name|String
name|format
decl_stmt|;
DECL|field|expand
specifier|private
specifier|final
name|boolean
name|expand
decl_stmt|;
DECL|field|analyzerName
specifier|private
specifier|final
name|String
name|analyzerName
decl_stmt|;
DECL|field|tokArgs
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|tokArgs
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|map
specifier|private
name|SynonymMap
name|map
decl_stmt|;
DECL|method|SynonymFilterFactory
specifier|public
name|SynonymFilterFactory
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
name|synonyms
operator|=
name|require
argument_list|(
name|args
argument_list|,
literal|"synonyms"
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
name|expand
operator|=
name|getBoolean
argument_list|(
name|args
argument_list|,
literal|"expand"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|analyzerName
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"analyzer"
argument_list|)
expr_stmt|;
name|tokenizerFactory
operator|=
name|get
argument_list|(
name|args
argument_list|,
literal|"tokenizerFactory"
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzerName
operator|!=
literal|null
operator|&&
name|tokenizerFactory
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Analyzer and TokenizerFactory can't be specified both: "
operator|+
name|analyzerName
operator|+
literal|" and "
operator|+
name|tokenizerFactory
argument_list|)
throw|;
block|}
if|if
condition|(
name|tokenizerFactory
operator|!=
literal|null
condition|)
block|{
name|tokArgs
operator|.
name|put
argument_list|(
literal|"luceneMatchVersion"
argument_list|,
name|getLuceneMatchVersion
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|String
argument_list|>
name|itr
init|=
name|args
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|itr
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
name|itr
operator|.
name|next
argument_list|()
decl_stmt|;
name|tokArgs
operator|.
name|put
argument_list|(
name|key
operator|.
name|replaceAll
argument_list|(
literal|"^tokenizerFactory\\."
argument_list|,
literal|""
argument_list|)
argument_list|,
name|args
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|itr
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
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
DECL|method|create
specifier|public
name|TokenStream
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
comment|// if the fst is null, it means there's actually no synonyms... just return the original stream
comment|// as there is nothing to do here.
return|return
name|map
operator|.
name|fst
operator|==
literal|null
condition|?
name|input
else|:
operator|new
name|SynonymFilter
argument_list|(
name|input
argument_list|,
name|map
argument_list|,
name|ignoreCase
argument_list|)
return|;
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
specifier|final
name|TokenizerFactory
name|factory
init|=
name|tokenizerFactory
operator|==
literal|null
condition|?
literal|null
else|:
name|loadTokenizerFactory
argument_list|(
name|loader
argument_list|,
name|tokenizerFactory
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
decl_stmt|;
if|if
condition|(
name|analyzerName
operator|!=
literal|null
condition|)
block|{
name|analyzer
operator|=
name|loadAnalyzer
argument_list|(
name|loader
argument_list|,
name|analyzerName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|analyzer
operator|=
operator|new
name|Analyzer
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|Tokenizer
name|tokenizer
init|=
name|factory
operator|==
literal|null
condition|?
operator|new
name|WhitespaceTokenizer
argument_list|()
else|:
name|factory
operator|.
name|create
argument_list|()
decl_stmt|;
name|TokenStream
name|stream
init|=
name|ignoreCase
condition|?
operator|new
name|LowerCaseFilter
argument_list|(
name|tokenizer
argument_list|)
else|:
name|tokenizer
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|stream
argument_list|)
return|;
block|}
block|}
expr_stmt|;
block|}
try|try
init|(
name|Analyzer
name|a
init|=
name|analyzer
init|)
block|{
name|String
name|formatClass
init|=
name|format
decl_stmt|;
if|if
condition|(
name|format
operator|==
literal|null
operator|||
name|format
operator|.
name|equals
argument_list|(
literal|"solr"
argument_list|)
condition|)
block|{
name|formatClass
operator|=
name|SolrSynonymParser
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|format
operator|.
name|equals
argument_list|(
literal|"wordnet"
argument_list|)
condition|)
block|{
name|formatClass
operator|=
name|WordnetSynonymParser
operator|.
name|class
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
comment|// TODO: expose dedup as a parameter?
name|map
operator|=
name|loadSynonyms
argument_list|(
name|loader
argument_list|,
name|formatClass
argument_list|,
literal|true
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Error parsing synonyms file:"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Load synonyms with the given {@link SynonymMap.Parser} class.    */
DECL|method|loadSynonyms
specifier|protected
name|SynonymMap
name|loadSynonyms
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|cname
parameter_list|,
name|boolean
name|dedup
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
block|{
name|CharsetDecoder
name|decoder
init|=
name|StandardCharsets
operator|.
name|UTF_8
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
name|SynonymMap
operator|.
name|Parser
name|parser
decl_stmt|;
name|Class
argument_list|<
name|?
extends|extends
name|SynonymMap
operator|.
name|Parser
argument_list|>
name|clazz
init|=
name|loader
operator|.
name|findClass
argument_list|(
name|cname
argument_list|,
name|SynonymMap
operator|.
name|Parser
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|boolean
operator|.
name|class
argument_list|,
name|Analyzer
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|dedup
argument_list|,
name|expand
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
name|List
argument_list|<
name|String
argument_list|>
name|files
init|=
name|splitFileNames
argument_list|(
name|synonyms
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|decoder
operator|.
name|reset
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|Reader
name|isr
init|=
operator|new
name|InputStreamReader
argument_list|(
name|loader
operator|.
name|openResource
argument_list|(
name|file
argument_list|)
argument_list|,
name|decoder
argument_list|)
init|)
block|{
name|parser
operator|.
name|parse
argument_list|(
name|isr
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parser
operator|.
name|build
argument_list|()
return|;
block|}
comment|// (there are no tests for this functionality)
DECL|method|loadTokenizerFactory
specifier|private
name|TokenizerFactory
name|loadTokenizerFactory
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|cname
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|TokenizerFactory
argument_list|>
name|clazz
init|=
name|loader
operator|.
name|findClass
argument_list|(
name|cname
argument_list|,
name|TokenizerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|TokenizerFactory
name|tokFactory
init|=
name|clazz
operator|.
name|getConstructor
argument_list|(
name|Map
operator|.
name|class
argument_list|)
operator|.
name|newInstance
argument_list|(
name|tokArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokFactory
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|tokFactory
operator|)
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
return|return
name|tokFactory
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
DECL|method|loadAnalyzer
specifier|private
name|Analyzer
name|loadAnalyzer
parameter_list|(
name|ResourceLoader
name|loader
parameter_list|,
name|String
name|cname
parameter_list|)
throws|throws
name|IOException
block|{
name|Class
argument_list|<
name|?
extends|extends
name|Analyzer
argument_list|>
name|clazz
init|=
name|loader
operator|.
name|findClass
argument_list|(
name|cname
argument_list|,
name|Analyzer
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|Analyzer
name|analyzer
init|=
name|clazz
operator|.
name|getConstructor
argument_list|()
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|analyzer
operator|instanceof
name|ResourceLoaderAware
condition|)
block|{
operator|(
operator|(
name|ResourceLoaderAware
operator|)
name|analyzer
operator|)
operator|.
name|inform
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
return|return
name|analyzer
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
end_class
end_unit
