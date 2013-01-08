begin_unit
begin_package
DECL|package|org.apache.solr.handler.clustering.carrot2
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|clustering
operator|.
name|carrot2
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
operator|.
name|ArabicNormalizer
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
name|ar
operator|.
name|ArabicStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|core
operator|.
name|LanguageCode
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|linguistic
operator|.
name|IStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|text
operator|.
name|linguistic
operator|.
name|IStemmerFactory
import|;
end_import
begin_import
import|import
name|org
operator|.
name|carrot2
operator|.
name|util
operator|.
name|ReflectionUtils
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|SnowballProgram
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|DanishStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|DutchStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|EnglishStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|FinnishStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|FrenchStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|GermanStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|HungarianStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|ItalianStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|NorwegianStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|PortugueseStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|RomanianStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|RussianStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|SpanishStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|SwedishStemmer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|ext
operator|.
name|TurkishStemmer
import|;
end_import
begin_comment
comment|/**  * An implementation of Carrot2's {@link IStemmerFactory} based on Lucene's  * APIs. Should the relevant Lucene APIs need to change, the changes can be made  * in this class.  */
end_comment
begin_class
DECL|class|LuceneCarrot2StemmerFactory
specifier|public
class|class
name|LuceneCarrot2StemmerFactory
implements|implements
name|IStemmerFactory
block|{
DECL|field|logger
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneCarrot2StemmerFactory
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
DECL|method|getStemmer
specifier|public
name|IStemmer
name|getStemmer
parameter_list|(
name|LanguageCode
name|language
parameter_list|)
block|{
switch|switch
condition|(
name|language
condition|)
block|{
case|case
name|ARABIC
case|:
return|return
name|ArabicStemmerFactory
operator|.
name|createStemmer
argument_list|()
return|;
case|case
name|CHINESE_SIMPLIFIED
case|:
return|return
name|IdentityStemmer
operator|.
name|INSTANCE
return|;
default|default:
comment|/*        * For other languages, try to use snowball's stemming.        */
return|return
name|SnowballStemmerFactory
operator|.
name|createStemmer
argument_list|(
name|language
argument_list|)
return|;
block|}
block|}
comment|/**    * Factory of {@link IStemmer} implementations from the<code>snowball</code>    * project.    */
DECL|class|SnowballStemmerFactory
specifier|private
specifier|final
specifier|static
class|class
name|SnowballStemmerFactory
block|{
comment|/**      * Static hard mapping from language codes to stemmer classes in Snowball.      * This mapping is not dynamic because we want to keep the possibility to      * obfuscate these classes.      */
DECL|field|snowballStemmerClasses
specifier|private
specifier|static
name|HashMap
argument_list|<
name|LanguageCode
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|SnowballProgram
argument_list|>
argument_list|>
name|snowballStemmerClasses
decl_stmt|;
static|static
block|{
name|snowballStemmerClasses
operator|=
operator|new
name|HashMap
argument_list|<
name|LanguageCode
argument_list|,
name|Class
argument_list|<
name|?
extends|extends
name|SnowballProgram
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|DANISH
argument_list|,
name|DanishStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|DUTCH
argument_list|,
name|DutchStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|ENGLISH
argument_list|,
name|EnglishStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|FINNISH
argument_list|,
name|FinnishStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|FRENCH
argument_list|,
name|FrenchStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|GERMAN
argument_list|,
name|GermanStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|HUNGARIAN
argument_list|,
name|HungarianStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|ITALIAN
argument_list|,
name|ItalianStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|NORWEGIAN
argument_list|,
name|NorwegianStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|PORTUGUESE
argument_list|,
name|PortugueseStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|ROMANIAN
argument_list|,
name|RomanianStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|RUSSIAN
argument_list|,
name|RussianStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|SPANISH
argument_list|,
name|SpanishStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|SWEDISH
argument_list|,
name|SwedishStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
name|snowballStemmerClasses
operator|.
name|put
argument_list|(
name|LanguageCode
operator|.
name|TURKISH
argument_list|,
name|TurkishStemmer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**      * An adapter converting Snowball programs into {@link IStemmer} interface.      */
DECL|class|SnowballStemmerAdapter
specifier|private
specifier|static
class|class
name|SnowballStemmerAdapter
implements|implements
name|IStemmer
block|{
DECL|field|snowballStemmer
specifier|private
specifier|final
name|SnowballProgram
name|snowballStemmer
decl_stmt|;
DECL|method|SnowballStemmerAdapter
specifier|public
name|SnowballStemmerAdapter
parameter_list|(
name|SnowballProgram
name|snowballStemmer
parameter_list|)
block|{
name|this
operator|.
name|snowballStemmer
operator|=
name|snowballStemmer
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stem
specifier|public
name|CharSequence
name|stem
parameter_list|(
name|CharSequence
name|word
parameter_list|)
block|{
name|snowballStemmer
operator|.
name|setCurrent
argument_list|(
name|word
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|snowballStemmer
operator|.
name|stem
argument_list|()
condition|)
block|{
return|return
name|snowballStemmer
operator|.
name|getCurrent
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|/**      * Create and return an {@link IStemmer} adapter for a      * {@link SnowballProgram} for a given language code. An identity stemmer is      * returned for unknown languages.      */
DECL|method|createStemmer
specifier|public
specifier|static
name|IStemmer
name|createStemmer
parameter_list|(
name|LanguageCode
name|language
parameter_list|)
block|{
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|SnowballProgram
argument_list|>
name|stemmerClazz
init|=
name|snowballStemmerClasses
operator|.
name|get
argument_list|(
name|language
argument_list|)
decl_stmt|;
if|if
condition|(
name|stemmerClazz
operator|==
literal|null
condition|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"No Snowball stemmer class for: "
operator|+
name|language
operator|.
name|name
argument_list|()
operator|+
literal|". Quality of clustering may be degraded."
argument_list|)
expr_stmt|;
return|return
name|IdentityStemmer
operator|.
name|INSTANCE
return|;
block|}
try|try
block|{
return|return
operator|new
name|SnowballStemmerAdapter
argument_list|(
name|stemmerClazz
operator|.
name|newInstance
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Could not instantiate snowball stemmer"
operator|+
literal|" for language: "
operator|+
name|language
operator|.
name|name
argument_list|()
operator|+
literal|". Quality of clustering may be degraded."
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|IdentityStemmer
operator|.
name|INSTANCE
return|;
block|}
block|}
block|}
comment|/**    * Factory of {@link IStemmer} implementations for the    * {@link LanguageCode#ARABIC} language. Requires<code>lucene-contrib</code>    * to be present in classpath, otherwise an empty (identity) stemmer is    * returned.    */
DECL|class|ArabicStemmerFactory
specifier|private
specifier|static
class|class
name|ArabicStemmerFactory
block|{
static|static
block|{
try|try
block|{
name|ReflectionUtils
operator|.
name|classForName
argument_list|(
name|ArabicStemmer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ReflectionUtils
operator|.
name|classForName
argument_list|(
name|ArabicNormalizer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
literal|"Could not instantiate Lucene stemmer for Arabic, clustering quality "
operator|+
literal|"of Arabic content may be degraded. For best quality clusters, "
operator|+
literal|"make sure Lucene's Arabic analyzer JAR is in the classpath"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Adapter to lucene-contrib Arabic analyzers.      */
DECL|class|LuceneStemmerAdapter
specifier|private
specifier|static
class|class
name|LuceneStemmerAdapter
implements|implements
name|IStemmer
block|{
DECL|field|delegate
specifier|private
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
operator|.
name|ArabicStemmer
name|delegate
decl_stmt|;
DECL|field|normalizer
specifier|private
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
operator|.
name|ArabicNormalizer
name|normalizer
decl_stmt|;
DECL|field|buffer
specifier|private
name|char
index|[]
name|buffer
init|=
operator|new
name|char
index|[
literal|0
index|]
decl_stmt|;
DECL|method|LuceneStemmerAdapter
specifier|private
name|LuceneStemmerAdapter
parameter_list|()
block|{
name|delegate
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
operator|.
name|ArabicStemmer
argument_list|()
expr_stmt|;
name|normalizer
operator|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|ar
operator|.
name|ArabicNormalizer
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stem
specifier|public
name|CharSequence
name|stem
parameter_list|(
name|CharSequence
name|word
parameter_list|)
block|{
if|if
condition|(
name|word
operator|.
name|length
argument_list|()
operator|>
name|buffer
operator|.
name|length
condition|)
block|{
name|buffer
operator|=
operator|new
name|char
index|[
name|word
operator|.
name|length
argument_list|()
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|word
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buffer
index|[
name|i
index|]
operator|=
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|int
name|newLen
init|=
name|normalizer
operator|.
name|normalize
argument_list|(
name|buffer
argument_list|,
name|word
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|newLen
operator|=
name|delegate
operator|.
name|stem
argument_list|(
name|buffer
argument_list|,
name|newLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|newLen
operator|!=
name|word
operator|.
name|length
argument_list|()
operator|||
operator|!
name|equals
argument_list|(
name|buffer
argument_list|,
name|newLen
argument_list|,
name|word
argument_list|)
condition|)
block|{
return|return
name|CharBuffer
operator|.
name|wrap
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|newLen
argument_list|)
return|;
block|}
comment|// Same-same.
return|return
literal|null
return|;
block|}
DECL|method|equals
specifier|private
name|boolean
name|equals
parameter_list|(
name|char
index|[]
name|buffer
parameter_list|,
name|int
name|len
parameter_list|,
name|CharSequence
name|word
parameter_list|)
block|{
assert|assert
name|len
operator|==
name|word
operator|.
name|length
argument_list|()
assert|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|buffer
index|[
name|i
index|]
operator|!=
name|word
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
DECL|method|createStemmer
specifier|public
specifier|static
name|IStemmer
name|createStemmer
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|LuceneStemmerAdapter
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
return|return
name|IdentityStemmer
operator|.
name|INSTANCE
return|;
block|}
block|}
block|}
comment|/**    * An implementation of {@link IStemmer} that always returns<code>null</code>    * which means no stemming.    */
DECL|class|IdentityStemmer
specifier|private
specifier|static
class|class
name|IdentityStemmer
implements|implements
name|IStemmer
block|{
DECL|field|INSTANCE
specifier|private
specifier|final
specifier|static
name|IdentityStemmer
name|INSTANCE
init|=
operator|new
name|IdentityStemmer
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|stem
specifier|public
name|CharSequence
name|stem
parameter_list|(
name|CharSequence
name|word
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class
end_unit
