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
name|InputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|text
operator|.
name|RuleBasedCollator
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
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
name|collation
operator|.
name|CollationKeyFilter
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
name|common
operator|.
name|SolrException
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
name|SolrException
operator|.
name|ErrorCode
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
begin_comment
comment|/**  * Factory for {@link CollationKeyFilter}.  *<p>  * This factory can be created in two ways:   *<ul>  *<li>Based upon a system collator associated with a Locale.  *<li>Based upon a tailored ruleset.  *</ul>  *<p>  * Using a System collator:  *<ul>  *<li>language: ISO-639 language code (mandatory)  *<li>country: ISO-3166 country code (optional)  *<li>variant: vendor or browser-specific code (optional)  *<li>strength: 'primary','secondary','tertiary', or 'identical' (optional)  *<li>decomposition: 'no','canonical', or 'full' (optional)  *</ul>  *<p>  * Using a Tailored ruleset:  *<ul>  *<li>custom: UTF-8 text file containing rules supported by RuleBasedCollator (mandatory)  *<li>strength: 'primary','secondary','tertiary', or 'identical' (optional)  *<li>decomposition: 'no','canonical', or 'full' (optional)  *</ul>  *   *<pre class="prettyprint">  *&lt;fieldType name="text_clltnky" class="solr.TextField" positionIncrementGap="100"&gt;  *&lt;analyzer&gt;  *&lt;tokenizer class="solr.KeywordTokenizerFactory"/&gt;  *&lt;filter class="solr.CollationKeyFilterFactory" language="ja" country="JP"/&gt;  *&lt;/analyzer&gt;  *&lt;/fieldType&gt;</pre>  *   * @see Collator  * @see Locale  * @see RuleBasedCollator  * @since solr 3.1  */
end_comment
begin_class
DECL|class|CollationKeyFilterFactory
specifier|public
class|class
name|CollationKeyFilterFactory
extends|extends
name|BaseTokenFilterFactory
implements|implements
name|ResourceLoaderAware
block|{
DECL|field|collator
specifier|private
name|Collator
name|collator
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
name|custom
init|=
name|args
operator|.
name|get
argument_list|(
literal|"custom"
argument_list|)
decl_stmt|;
name|String
name|language
init|=
name|args
operator|.
name|get
argument_list|(
literal|"language"
argument_list|)
decl_stmt|;
name|String
name|country
init|=
name|args
operator|.
name|get
argument_list|(
literal|"country"
argument_list|)
decl_stmt|;
name|String
name|variant
init|=
name|args
operator|.
name|get
argument_list|(
literal|"variant"
argument_list|)
decl_stmt|;
name|String
name|strength
init|=
name|args
operator|.
name|get
argument_list|(
literal|"strength"
argument_list|)
decl_stmt|;
name|String
name|decomposition
init|=
name|args
operator|.
name|get
argument_list|(
literal|"decomposition"
argument_list|)
decl_stmt|;
if|if
condition|(
name|custom
operator|==
literal|null
operator|&&
name|language
operator|==
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Either custom or language is required."
argument_list|)
throw|;
if|if
condition|(
name|custom
operator|!=
literal|null
operator|&&
operator|(
name|language
operator|!=
literal|null
operator|||
name|country
operator|!=
literal|null
operator|||
name|variant
operator|!=
literal|null
operator|)
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Cannot specify both language and custom. "
operator|+
literal|"To tailor rules for a built-in language, see the javadocs for RuleBasedCollator. "
operator|+
literal|"Then save the entire customized ruleset to a file, and use with the custom parameter"
argument_list|)
throw|;
if|if
condition|(
name|language
operator|!=
literal|null
condition|)
block|{
comment|// create from a system collator, based on Locale.
name|collator
operator|=
name|createFromLocale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// create from a custom ruleset
name|collator
operator|=
name|createFromRules
argument_list|(
name|custom
argument_list|,
name|loader
argument_list|)
expr_stmt|;
block|}
comment|// set the strength flag, otherwise it will be the default.
if|if
condition|(
name|strength
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"primary"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|PRIMARY
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"secondary"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|SECONDARY
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"tertiary"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|TERTIARY
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|strength
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"identical"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|IDENTICAL
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid strength: "
operator|+
name|strength
argument_list|)
throw|;
block|}
comment|// set the decomposition flag, otherwise it will be the default.
if|if
condition|(
name|decomposition
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|NO_DECOMPOSITION
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"canonical"
argument_list|)
condition|)
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|CANONICAL_DECOMPOSITION
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|decomposition
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"full"
argument_list|)
condition|)
name|collator
operator|.
name|setDecomposition
argument_list|(
name|Collator
operator|.
name|FULL_DECOMPOSITION
argument_list|)
expr_stmt|;
else|else
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid decomposition: "
operator|+
name|decomposition
argument_list|)
throw|;
block|}
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
operator|new
name|CollationKeyFilter
argument_list|(
name|input
argument_list|,
name|collator
argument_list|)
return|;
block|}
comment|/*    * Create a locale from language, with optional country and variant.    * Then return the appropriate collator for the locale.    */
DECL|method|createFromLocale
specifier|private
name|Collator
name|createFromLocale
parameter_list|(
name|String
name|language
parameter_list|,
name|String
name|country
parameter_list|,
name|String
name|variant
parameter_list|)
block|{
name|Locale
name|locale
decl_stmt|;
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|==
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"To specify variant, country is required"
argument_list|)
throw|;
elseif|else
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|!=
literal|null
operator|&&
name|variant
operator|!=
literal|null
condition|)
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|,
name|variant
argument_list|)
expr_stmt|;
elseif|else
if|if
condition|(
name|language
operator|!=
literal|null
operator|&&
name|country
operator|!=
literal|null
condition|)
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|language
argument_list|,
name|country
argument_list|)
expr_stmt|;
else|else
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|language
argument_list|)
expr_stmt|;
return|return
name|Collator
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
return|;
block|}
comment|/*    * Read custom rules from a file, and create a RuleBasedCollator    * The file cannot support comments, as # might be in the rules!    */
DECL|method|createFromRules
specifier|private
name|Collator
name|createFromRules
parameter_list|(
name|String
name|fileName
parameter_list|,
name|ResourceLoader
name|loader
parameter_list|)
block|{
name|InputStream
name|input
init|=
literal|null
decl_stmt|;
try|try
block|{
name|input
operator|=
name|loader
operator|.
name|openResource
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
name|String
name|rules
init|=
name|IOUtils
operator|.
name|toString
argument_list|(
name|input
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
return|return
operator|new
name|RuleBasedCollator
argument_list|(
name|rules
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// io error
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
comment|// invalid rules
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
