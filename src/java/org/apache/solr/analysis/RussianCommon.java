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
name|ru
operator|.
name|*
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
name|solr
operator|.
name|core
operator|.
name|SolrConfig
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
name|slf4j
operator|.
name|Logger
import|;
end_import
begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import
begin_class
annotation|@
name|Deprecated
DECL|class|RussianCommon
specifier|public
class|class
name|RussianCommon
block|{
DECL|field|logger
specifier|private
specifier|static
name|Logger
name|logger
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RussianCommon
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|CHARSETS
specifier|private
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|char
index|[]
argument_list|>
name|CHARSETS
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|char
index|[]
argument_list|>
argument_list|()
decl_stmt|;
static|static
block|{
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"UnicodeRussian"
argument_list|,
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
expr_stmt|;
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"KOI8"
argument_list|,
name|RussianCharsets
operator|.
name|KOI8
argument_list|)
expr_stmt|;
name|CHARSETS
operator|.
name|put
argument_list|(
literal|"CP1251"
argument_list|,
name|RussianCharsets
operator|.
name|CP1251
argument_list|)
expr_stmt|;
block|}
DECL|method|getCharset
specifier|public
specifier|static
name|char
index|[]
name|getCharset
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|name
condition|)
return|return
name|RussianCharsets
operator|.
name|UnicodeRussian
return|;
name|char
index|[]
name|charset
init|=
name|CHARSETS
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|charset
operator|.
name|equals
argument_list|(
name|RussianCharsets
operator|.
name|UnicodeRussian
argument_list|)
condition|)
name|logger
operator|.
name|warn
argument_list|(
literal|"Specifying UnicodeRussian is no longer required (default).  "
operator|+
literal|"Use of the charset parameter will cause an error in Solr 1.5"
argument_list|)
expr_stmt|;
else|else
name|logger
operator|.
name|warn
argument_list|(
literal|"Support for this custom encoding is deprecated.  "
operator|+
literal|"Use of the charset parameter will cause an error in Solr 1.5"
argument_list|)
expr_stmt|;
if|if
condition|(
literal|null
operator|==
name|charset
condition|)
block|{
throw|throw
operator|new
name|SolrException
argument_list|(
name|ErrorCode
operator|.
name|SERVER_ERROR
argument_list|,
literal|"Don't understand charset: "
operator|+
name|name
argument_list|)
throw|;
block|}
return|return
name|charset
return|;
block|}
block|}
end_class
end_unit
