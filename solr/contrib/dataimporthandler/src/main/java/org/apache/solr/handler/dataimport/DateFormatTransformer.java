begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package
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
name|SimpleDateFormat
import|;
end_import
begin_import
import|import
name|java
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
begin_comment
comment|/**  *<p>  * Transformer instance which creates Date instances out of Strings.  *</p>  *<p/>  *<p>  * Refer to<a  * href="http://wiki.apache.org/solr/DataImportHandler">http://wiki.apache.org/solr/DataImportHandler</a>  * for more details.  *</p>  *<p/>  *<b>This API is experimental and subject to change</b>  *  * @version $Id$  * @since solr 1.3  */
end_comment
begin_class
DECL|class|DateFormatTransformer
specifier|public
class|class
name|DateFormatTransformer
extends|extends
name|Transformer
block|{
DECL|field|fmtCache
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|SimpleDateFormat
argument_list|>
name|fmtCache
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SimpleDateFormat
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|DateFormatTransformer
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|transformRow
specifier|public
name|Object
name|transformRow
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|aRow
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
range|:
name|context
operator|.
name|getAllEntityFields
argument_list|()
control|)
block|{
name|Locale
name|locale
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|String
name|customLocale
init|=
name|map
operator|.
name|get
argument_list|(
literal|"locale"
argument_list|)
decl_stmt|;
if|if
condition|(
name|customLocale
operator|!=
literal|null
condition|)
block|{
name|locale
operator|=
operator|new
name|Locale
argument_list|(
name|customLocale
argument_list|)
expr_stmt|;
block|}
name|String
name|fmt
init|=
name|map
operator|.
name|get
argument_list|(
name|DATE_TIME_FMT
argument_list|)
decl_stmt|;
if|if
condition|(
name|fmt
operator|==
literal|null
condition|)
continue|continue;
name|String
name|column
init|=
name|map
operator|.
name|get
argument_list|(
name|DataImporter
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
name|String
name|srcCol
init|=
name|map
operator|.
name|get
argument_list|(
name|RegexTransformer
operator|.
name|SRC_COL_NAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcCol
operator|==
literal|null
condition|)
name|srcCol
operator|=
name|column
expr_stmt|;
try|try
block|{
name|Object
name|o
init|=
name|aRow
operator|.
name|get
argument_list|(
name|srcCol
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|List
name|inputs
init|=
operator|(
name|List
operator|)
name|o
decl_stmt|;
name|List
argument_list|<
name|Date
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|<
name|Date
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
name|input
range|:
name|inputs
control|)
block|{
name|results
operator|.
name|add
argument_list|(
name|process
argument_list|(
name|input
argument_list|,
name|fmt
argument_list|,
name|locale
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|aRow
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|results
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|aRow
operator|.
name|put
argument_list|(
name|column
argument_list|,
name|process
argument_list|(
name|o
argument_list|,
name|fmt
argument_list|,
name|locale
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not parse a Date field "
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|aRow
return|;
block|}
DECL|method|process
specifier|private
name|Date
name|process
parameter_list|(
name|Object
name|value
parameter_list|,
name|String
name|format
parameter_list|,
name|Locale
name|locale
parameter_list|)
throws|throws
name|ParseException
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|String
name|strVal
init|=
name|value
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|strVal
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|SimpleDateFormat
name|fmt
init|=
name|fmtCache
operator|.
name|get
argument_list|(
name|format
argument_list|)
decl_stmt|;
if|if
condition|(
name|fmt
operator|==
literal|null
condition|)
block|{
name|fmt
operator|=
operator|new
name|SimpleDateFormat
argument_list|(
name|format
argument_list|,
name|locale
argument_list|)
expr_stmt|;
name|fmtCache
operator|.
name|put
argument_list|(
name|format
argument_list|,
name|fmt
argument_list|)
expr_stmt|;
block|}
return|return
name|fmt
operator|.
name|parse
argument_list|(
name|strVal
argument_list|)
return|;
block|}
DECL|field|DATE_TIME_FMT
specifier|public
specifier|static
specifier|final
name|String
name|DATE_TIME_FMT
init|=
literal|"dateTimeFormat"
decl_stmt|;
block|}
end_class
end_unit
