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
name|io
operator|.
name|IOException
import|;
end_import
begin_comment
comment|/**  * @version $Id$  */
end_comment
begin_class
DECL|class|WordDelimiterFilterFactory
specifier|public
class|class
name|WordDelimiterFilterFactory
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
DECL|field|generateWordParts
name|int
name|generateWordParts
init|=
literal|0
decl_stmt|;
DECL|field|generateNumberParts
name|int
name|generateNumberParts
init|=
literal|0
decl_stmt|;
DECL|field|catenateWords
name|int
name|catenateWords
init|=
literal|0
decl_stmt|;
DECL|field|catenateNumbers
name|int
name|catenateNumbers
init|=
literal|0
decl_stmt|;
DECL|field|catenateAll
name|int
name|catenateAll
init|=
literal|0
decl_stmt|;
DECL|field|splitOnCaseChange
name|int
name|splitOnCaseChange
init|=
literal|0
decl_stmt|;
DECL|field|splitOnNumerics
name|int
name|splitOnNumerics
init|=
literal|0
decl_stmt|;
DECL|field|preserveOriginal
name|int
name|preserveOriginal
init|=
literal|0
decl_stmt|;
DECL|field|stemEnglishPossessive
name|int
name|stemEnglishPossessive
init|=
literal|0
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
name|generateWordParts
operator|=
name|getInt
argument_list|(
literal|"generateWordParts"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|generateNumberParts
operator|=
name|getInt
argument_list|(
literal|"generateNumberParts"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|catenateWords
operator|=
name|getInt
argument_list|(
literal|"catenateWords"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|catenateNumbers
operator|=
name|getInt
argument_list|(
literal|"catenateNumbers"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|catenateAll
operator|=
name|getInt
argument_list|(
literal|"catenateAll"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|splitOnCaseChange
operator|=
name|getInt
argument_list|(
literal|"splitOnCaseChange"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|splitOnNumerics
operator|=
name|getInt
argument_list|(
literal|"splitOnNumerics"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|preserveOriginal
operator|=
name|getInt
argument_list|(
literal|"preserveOriginal"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|stemEnglishPossessive
operator|=
name|getInt
argument_list|(
literal|"stemEnglishPossessive"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|create
specifier|public
name|WordDelimiterFilter
name|create
parameter_list|(
name|TokenStream
name|input
parameter_list|)
block|{
return|return
operator|new
name|WordDelimiterFilter
argument_list|(
name|input
argument_list|,
name|generateWordParts
argument_list|,
name|generateNumberParts
argument_list|,
name|catenateWords
argument_list|,
name|catenateNumbers
argument_list|,
name|catenateAll
argument_list|,
name|splitOnCaseChange
argument_list|,
name|preserveOriginal
argument_list|,
name|splitOnNumerics
argument_list|,
name|stemEnglishPossessive
argument_list|,
name|protectedWords
argument_list|)
return|;
block|}
block|}
end_class
end_unit
