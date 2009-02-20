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
name|util
operator|.
name|Map
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
name|io
operator|.
name|File
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
name|Token
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
name|util
operator|.
name|StrUtils
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
name|org
operator|.
name|tartarus
operator|.
name|snowball
operator|.
name|SnowballProgram
import|;
end_import
begin_comment
comment|/**  * Factory for SnowballFilters, with configurable language  *   * Browsing the code, SnowballFilter uses reflection to adapt to Lucene... don't  * use this if you are concerned about speed. Use EnglishPorterFilterFactory.  *   * @version $Id$  */
end_comment
begin_class
DECL|class|SnowballPorterFilterFactory
specifier|public
class|class
name|SnowballPorterFilterFactory
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
DECL|field|language
specifier|private
name|String
name|language
init|=
literal|"English"
decl_stmt|;
DECL|field|stemClass
specifier|private
name|Class
name|stemClass
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
name|File
name|protectedWordFiles
init|=
operator|new
name|File
argument_list|(
name|wordFiles
argument_list|)
decl_stmt|;
if|if
condition|(
name|protectedWordFiles
operator|.
name|exists
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|wlist
init|=
name|loader
operator|.
name|getLines
argument_list|(
name|wordFiles
argument_list|)
decl_stmt|;
comment|//This cast is safe in Lucene
name|protectedWords
operator|=
operator|new
name|CharArraySet
argument_list|(
name|wlist
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//No need to go through StopFilter as before, since it just uses a List internally
block|}
else|else
block|{
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
name|wordFiles
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
name|List
argument_list|<
name|String
argument_list|>
name|wlist
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
if|if
condition|(
name|protectedWords
operator|==
literal|null
condition|)
name|protectedWords
operator|=
operator|new
name|CharArraySet
argument_list|(
name|wlist
argument_list|,
literal|false
argument_list|)
expr_stmt|;
else|else
name|protectedWords
operator|.
name|addAll
argument_list|(
name|wlist
argument_list|)
expr_stmt|;
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
specifier|final
name|String
name|cfgLanguage
init|=
name|args
operator|.
name|get
argument_list|(
literal|"language"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cfgLanguage
operator|!=
literal|null
condition|)
name|language
operator|=
name|cfgLanguage
expr_stmt|;
try|try
block|{
name|stemClass
operator|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.tartarus.snowball.ext."
operator|+
name|language
operator|+
literal|"Stemmer"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Can't find class for stemmer language "
operator|+
name|language
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|create
specifier|public
name|SnowballPorterFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
name|SnowballProgram
name|program
decl_stmt|;
try|try
block|{
name|program
operator|=
operator|(
name|SnowballProgram
operator|)
name|stemClass
operator|.
name|newInstance
argument_list|()
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
literal|"Error instantiating stemmer for language "
operator|+
name|language
operator|+
literal|"from class "
operator|+
name|stemClass
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|SnowballPorterFilter
argument_list|(
name|input
argument_list|,
name|program
argument_list|,
name|protectedWords
argument_list|)
return|;
block|}
block|}
end_class
begin_class
DECL|class|SnowballPorterFilter
class|class
name|SnowballPorterFilter
extends|extends
name|TokenFilter
block|{
DECL|field|protWords
specifier|private
specifier|final
name|CharArraySet
name|protWords
decl_stmt|;
DECL|field|stemmer
specifier|private
name|SnowballProgram
name|stemmer
decl_stmt|;
DECL|method|SnowballPorterFilter
specifier|public
name|SnowballPorterFilter
parameter_list|(
name|TokenStream
name|source
parameter_list|,
name|SnowballProgram
name|stemmer
parameter_list|,
name|CharArraySet
name|protWords
parameter_list|)
block|{
name|super
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|this
operator|.
name|protWords
operator|=
name|protWords
expr_stmt|;
name|this
operator|.
name|stemmer
operator|=
name|stemmer
expr_stmt|;
block|}
comment|/**    * the original code from lucene sandbox    * public final Token next() throws IOException {    * Token token = input.next();    * if (token == null)    * return null;    * stemmer.setCurrent(token.termText());    * try {    * stemMethod.invoke(stemmer, EMPTY_ARGS);    * } catch (Exception e) {    * throw new RuntimeException(e.toString());    * }    * return new Token(stemmer.getCurrent(),    * token.startOffset(), token.endOffset(), token.type());    * }    */
annotation|@
name|Override
DECL|method|next
specifier|public
name|Token
name|next
parameter_list|(
name|Token
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|Token
name|result
init|=
name|input
operator|.
name|next
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|char
index|[]
name|termBuffer
init|=
name|result
operator|.
name|termBuffer
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|result
operator|.
name|termLength
argument_list|()
decl_stmt|;
comment|// if protected, don't stem.  use this to avoid stemming collisions.
if|if
condition|(
name|protWords
operator|!=
literal|null
operator|&&
name|protWords
operator|.
name|contains
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
condition|)
block|{
return|return
name|result
return|;
block|}
name|stemmer
operator|.
name|setCurrent
argument_list|(
operator|new
name|String
argument_list|(
name|termBuffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
argument_list|)
expr_stmt|;
comment|//ugh, wish the Stemmer took a char array
name|stemmer
operator|.
name|stem
argument_list|()
expr_stmt|;
name|String
name|newstr
init|=
name|stemmer
operator|.
name|getCurrent
argument_list|()
decl_stmt|;
name|result
operator|.
name|setTermBuffer
argument_list|(
name|newstr
operator|.
name|toCharArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|newstr
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class
end_unit
