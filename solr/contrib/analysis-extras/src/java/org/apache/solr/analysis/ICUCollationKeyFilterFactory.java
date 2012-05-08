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
name|InputStream
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
name|collation
operator|.
name|ICUCollationKeyFilter
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
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|RuleBasedCollator
import|;
end_import
begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|util
operator|.
name|ULocale
import|;
end_import
begin_comment
comment|/**  *<!-- see LUCENE-4015 for why we cannot link -->  * Factory for<code>ICUCollationKeyFilter</code>.  *<p>  * This factory can be created in two ways:   *<ul>  *<li>Based upon a system collator associated with a Locale.  *<li>Based upon a tailored ruleset.  *</ul>  *<p>  * Using a System collator:  *<ul>  *<li>locale: RFC 3066 locale ID (mandatory)  *<li>strength: 'primary','secondary','tertiary', 'quaternary', or 'identical' (optional)  *<li>decomposition: 'no', or 'canonical' (optional)  *</ul>  *<p>  * Using a Tailored ruleset:  *<ul>  *<li>custom: UTF-8 text file containing rules supported by RuleBasedCollator (mandatory)  *<li>strength: 'primary','secondary','tertiary', 'quaternary', or 'identical' (optional)  *<li>decomposition: 'no' or 'canonical' (optional)  *</ul>  *<p>  * Expert options:  *<ul>  *<li>alternate: 'shifted' or 'non-ignorable'. Can be used to ignore punctuation/whitespace.  *<li>caseLevel: 'true' or 'false'. Useful with strength=primary to ignore accents but not case.  *<li>caseFirst: 'lower' or 'upper'. Useful to control which is sorted first when case is not ignored.  *<li>numeric: 'true' or 'false'. Digits are sorted according to numeric value, e.g. foobar-9 sorts before foobar-10  *<li>variableTop: single character or contraction. Controls what is variable for 'alternate'  *</ul>  *  * @see Collator  * @see ULocale  * @see RuleBasedCollator  * @deprecated use {@link org.apache.solr.schema.ICUCollationField} instead.  */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|ICUCollationKeyFilterFactory
specifier|public
class|class
name|ICUCollationKeyFilterFactory
extends|extends
name|TokenFilterFactory
implements|implements
name|MultiTermAwareComponent
implements|,
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
name|localeID
init|=
name|args
operator|.
name|get
argument_list|(
literal|"locale"
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
name|String
name|alternate
init|=
name|args
operator|.
name|get
argument_list|(
literal|"alternate"
argument_list|)
decl_stmt|;
name|String
name|caseLevel
init|=
name|args
operator|.
name|get
argument_list|(
literal|"caseLevel"
argument_list|)
decl_stmt|;
name|String
name|caseFirst
init|=
name|args
operator|.
name|get
argument_list|(
literal|"caseFirst"
argument_list|)
decl_stmt|;
name|String
name|numeric
init|=
name|args
operator|.
name|get
argument_list|(
literal|"numeric"
argument_list|)
decl_stmt|;
name|String
name|variableTop
init|=
name|args
operator|.
name|get
argument_list|(
literal|"variableTop"
argument_list|)
decl_stmt|;
if|if
condition|(
name|custom
operator|==
literal|null
operator|&&
name|localeID
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
literal|"Either custom or locale is required."
argument_list|)
throw|;
if|if
condition|(
name|custom
operator|!=
literal|null
operator|&&
name|localeID
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
literal|"Cannot specify both locale and custom. "
operator|+
literal|"To tailor rules for a built-in language, see the javadocs for RuleBasedCollator. "
operator|+
literal|"Then save the entire customized ruleset to a file, and use with the custom parameter"
argument_list|)
throw|;
if|if
condition|(
name|localeID
operator|!=
literal|null
condition|)
block|{
comment|// create from a system collator, based on Locale.
name|collator
operator|=
name|createFromLocale
argument_list|(
name|localeID
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
literal|"quaternary"
argument_list|)
condition|)
name|collator
operator|.
name|setStrength
argument_list|(
name|Collator
operator|.
name|QUATERNARY
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
comment|// expert options: concrete subclasses are always a RuleBasedCollator
name|RuleBasedCollator
name|rbc
init|=
operator|(
name|RuleBasedCollator
operator|)
name|collator
decl_stmt|;
if|if
condition|(
name|alternate
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|alternate
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"shifted"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setAlternateHandlingShifted
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|alternate
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"non-ignorable"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setAlternateHandlingShifted
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid alternate: "
operator|+
name|alternate
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|caseLevel
operator|!=
literal|null
condition|)
block|{
name|rbc
operator|.
name|setCaseLevel
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|caseLevel
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|caseFirst
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|caseFirst
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"lower"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setLowerCaseFirst
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|caseFirst
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"upper"
argument_list|)
condition|)
block|{
name|rbc
operator|.
name|setUpperCaseFirst
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Invalid caseFirst: "
operator|+
name|caseFirst
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|numeric
operator|!=
literal|null
condition|)
block|{
name|rbc
operator|.
name|setNumericCollation
argument_list|(
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|numeric
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|variableTop
operator|!=
literal|null
condition|)
block|{
name|rbc
operator|.
name|setVariableTop
argument_list|(
name|variableTop
argument_list|)
expr_stmt|;
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
name|ICUCollationKeyFilter
argument_list|(
name|input
argument_list|,
name|collator
argument_list|)
return|;
block|}
comment|/*    * Create a locale from localeID.    * Then return the appropriate collator for the locale.    */
DECL|method|createFromLocale
specifier|private
name|Collator
name|createFromLocale
parameter_list|(
name|String
name|localeID
parameter_list|)
block|{
return|return
name|Collator
operator|.
name|getInstance
argument_list|(
operator|new
name|ULocale
argument_list|(
name|localeID
argument_list|)
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
name|Exception
name|e
parameter_list|)
block|{
comment|// io error or invalid rules
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
annotation|@
name|Override
DECL|method|getMultiTermComponent
specifier|public
name|Object
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
