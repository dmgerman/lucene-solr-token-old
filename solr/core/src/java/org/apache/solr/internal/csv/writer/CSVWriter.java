begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.internal.csv.writer
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|internal
operator|.
name|csv
operator|.
name|writer
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
begin_comment
comment|/**  * CSVWriter  *  */
end_comment
begin_class
DECL|class|CSVWriter
specifier|public
class|class
name|CSVWriter
block|{
comment|/** The CSV config **/
DECL|field|config
specifier|private
name|CSVConfig
name|config
decl_stmt|;
comment|/** The writer **/
DECL|field|writer
specifier|private
name|Writer
name|writer
decl_stmt|;
comment|/**      *       */
DECL|method|CSVWriter
specifier|public
name|CSVWriter
parameter_list|()
block|{     }
DECL|method|CSVWriter
specifier|public
name|CSVWriter
parameter_list|(
name|CSVConfig
name|config
parameter_list|)
block|{
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
DECL|method|writeRecord
specifier|public
name|void
name|writeRecord
parameter_list|(
name|Map
name|map
parameter_list|)
block|{
name|CSVField
index|[]
name|fields
init|=
name|config
operator|.
name|getFields
argument_list|()
decl_stmt|;
try|try
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Object
name|o
init|=
name|map
operator|.
name|get
argument_list|(
name|fields
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|!=
literal|null
condition|)
block|{
name|String
name|value
init|=
name|o
operator|.
name|toString
argument_list|()
decl_stmt|;
name|value
operator|=
name|writeValue
argument_list|(
name|fields
index|[
name|i
index|]
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|config
operator|.
name|isDelimiterIgnored
argument_list|()
operator|&&
name|fields
operator|.
name|length
operator|!=
operator|(
name|i
operator|+
literal|1
operator|)
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|config
operator|.
name|getDelimiter
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|config
operator|.
name|isEndTrimmed
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"i : "
operator|+
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|Character
operator|.
name|isWhitespace
argument_list|(
name|sb
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break;
block|}
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|String
name|line
init|=
name|sb
operator|.
name|toString
argument_list|()
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|writeValue
specifier|protected
name|String
name|writeValue
parameter_list|(
name|CSVField
name|field
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|config
operator|.
name|isFixedWidth
argument_list|()
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|<
name|field
operator|.
name|getSize
argument_list|()
condition|)
block|{
name|int
name|fillPattern
init|=
name|config
operator|.
name|getFill
argument_list|()
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|overrideFill
argument_list|()
condition|)
block|{
name|fillPattern
operator|=
name|field
operator|.
name|getFill
argument_list|()
expr_stmt|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|int
name|fillSize
init|=
operator|(
name|field
operator|.
name|getSize
argument_list|()
operator|-
name|value
operator|.
name|length
argument_list|()
operator|)
decl_stmt|;
name|char
index|[]
name|fill
init|=
operator|new
name|char
index|[
name|fillSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|fill
argument_list|,
name|config
operator|.
name|getFillChar
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fillPattern
operator|==
name|CSVConfig
operator|.
name|FILLLEFT
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|fill
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// defaults to fillpattern FILLRIGHT when fixedwidth is used
name|sb
operator|.
name|append
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|fill
argument_list|)
expr_stmt|;
name|value
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
name|field
operator|.
name|getSize
argument_list|()
condition|)
block|{
comment|// value to big..
name|value
operator|=
name|value
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|field
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|config
operator|.
name|isValueDelimiterIgnored
argument_list|()
condition|)
block|{
comment|// add the value delimiter..
name|value
operator|=
name|config
operator|.
name|getValueDelimiter
argument_list|()
operator|+
name|value
operator|+
name|config
operator|.
name|getValueDelimiter
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|value
return|;
block|}
comment|/**      * @return the CVSConfig or null if not present      */
DECL|method|getConfig
specifier|public
name|CSVConfig
name|getConfig
parameter_list|()
block|{
return|return
name|config
return|;
block|}
comment|/**      * Set the CSVConfig      * @param config the CVSConfig      */
DECL|method|setConfig
specifier|public
name|void
name|setConfig
parameter_list|(
name|CSVConfig
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
block|}
comment|/**      * Set the writer to write the CSV file to.      * @param writer the writer.      */
DECL|method|setWriter
specifier|public
name|void
name|setWriter
parameter_list|(
name|Writer
name|writer
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
block|}
block|}
end_class
end_unit
