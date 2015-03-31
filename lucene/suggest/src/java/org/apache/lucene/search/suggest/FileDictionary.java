begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|util
operator|.
name|Set
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
name|search
operator|.
name|spell
operator|.
name|Dictionary
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
name|BytesRef
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
name|BytesRefBuilder
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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Dictionary represented by a text file.  *   *<p>Format allowed: 1 entry per line:<br>  * An entry can be:<br>  *<ul>  *<li>suggestion</li>  *<li>suggestion<code>fieldDelimiter</code> weight</li>  *<li>suggestion<code>fieldDelimiter</code> weight<code>fieldDelimiter</code> payload</li>  *</ul>  * where the default<code>fieldDelimiter</code> is {@value #DEFAULT_FIELD_DELIMITER}<br>  *<p>  *<b>NOTE:</b>   *<ul>  *<li>In order to have payload enabled, the first entry has to have a payload</li>  *<li>If the weight for an entry is not specified then a value of 1 is used</li>  *<li>A payload cannot be specified without having the weight specified for an entry</li>  *<li>If the payload for an entry is not specified (assuming payload is enabled)   *  then an empty payload is returned</li>  *<li>An entry cannot have more than two<code>fieldDelimiter</code></li>  *</ul>  *<p>  *<b>Example:</b><br>  * word1 word2 TAB 100 TAB payload1<br>  * word3 TAB 101<br>  * word4 word3 TAB 102<br>  */
end_comment
begin_class
DECL|class|FileDictionary
specifier|public
class|class
name|FileDictionary
implements|implements
name|Dictionary
block|{
comment|/**    * Tab-delimited fields are most common thus the default, but one can override this via the constructor    */
DECL|field|DEFAULT_FIELD_DELIMITER
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_FIELD_DELIMITER
init|=
literal|"\t"
decl_stmt|;
DECL|field|in
specifier|private
name|BufferedReader
name|in
decl_stmt|;
DECL|field|line
specifier|private
name|String
name|line
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|fieldDelimiter
specifier|private
specifier|final
name|String
name|fieldDelimiter
decl_stmt|;
comment|/**    * Creates a dictionary based on an inputstream.    * Using {@link #DEFAULT_FIELD_DELIMITER} as the     * field seperator in a line.    *<p>    * NOTE: content is treated as UTF-8    */
DECL|method|FileDictionary
specifier|public
name|FileDictionary
parameter_list|(
name|InputStream
name|dictFile
parameter_list|)
block|{
name|this
argument_list|(
name|dictFile
argument_list|,
name|DEFAULT_FIELD_DELIMITER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a dictionary based on a reader.    * Using {@link #DEFAULT_FIELD_DELIMITER} as the     * field seperator in a line.    */
DECL|method|FileDictionary
specifier|public
name|FileDictionary
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
name|this
argument_list|(
name|reader
argument_list|,
name|DEFAULT_FIELD_DELIMITER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a dictionary based on a reader.     * Using<code>fieldDelimiter</code> to seperate out the    * fields in a line.    */
DECL|method|FileDictionary
specifier|public
name|FileDictionary
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|String
name|fieldDelimiter
parameter_list|)
block|{
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldDelimiter
operator|=
name|fieldDelimiter
expr_stmt|;
block|}
comment|/**    * Creates a dictionary based on an inputstream.    * Using<code>fieldDelimiter</code> to seperate out the    * fields in a line.    *<p>    * NOTE: content is treated as UTF-8    */
DECL|method|FileDictionary
specifier|public
name|FileDictionary
parameter_list|(
name|InputStream
name|dictFile
parameter_list|,
name|String
name|fieldDelimiter
parameter_list|)
block|{
name|in
operator|=
operator|new
name|BufferedReader
argument_list|(
name|IOUtils
operator|.
name|getDecodingReader
argument_list|(
name|dictFile
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|fieldDelimiter
operator|=
name|fieldDelimiter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEntryIterator
specifier|public
name|InputIterator
name|getEntryIterator
parameter_list|()
block|{
try|try
block|{
return|return
operator|new
name|FileIterator
argument_list|()
return|;
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
DECL|class|FileIterator
specifier|final
class|class
name|FileIterator
implements|implements
name|InputIterator
block|{
DECL|field|curWeight
specifier|private
name|long
name|curWeight
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRefBuilder
name|spare
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|curPayload
specifier|private
name|BytesRefBuilder
name|curPayload
init|=
operator|new
name|BytesRefBuilder
argument_list|()
decl_stmt|;
DECL|field|isFirstLine
specifier|private
name|boolean
name|isFirstLine
init|=
literal|true
decl_stmt|;
DECL|field|hasPayloads
specifier|private
name|boolean
name|hasPayloads
init|=
literal|false
decl_stmt|;
DECL|method|FileIterator
specifier|private
name|FileIterator
parameter_list|()
throws|throws
name|IOException
block|{
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|split
argument_list|(
name|fieldDelimiter
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|3
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"More than 3 fields in one line"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|3
condition|)
block|{
comment|// term, weight, payload
name|hasPayloads
operator|=
literal|true
expr_stmt|;
name|spare
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|readWeight
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|curPayload
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|2
condition|)
block|{
comment|// term, weight
name|spare
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|readWeight
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// only term
name|spare
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|curWeight
operator|=
literal|1
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|curWeight
return|;
block|}
annotation|@
name|Override
DECL|method|next
specifier|public
name|BytesRef
name|next
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|done
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|isFirstLine
condition|)
block|{
name|isFirstLine
operator|=
literal|false
expr_stmt|;
return|return
name|spare
operator|.
name|get
argument_list|()
return|;
block|}
name|line
operator|=
name|in
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|String
index|[]
name|fields
init|=
name|line
operator|.
name|split
argument_list|(
name|fieldDelimiter
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|3
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"More than 3 fields in one line"
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|3
condition|)
block|{
comment|// term, weight and payload
name|spare
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|readWeight
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|curPayload
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fields
operator|.
name|length
operator|==
literal|2
condition|)
block|{
comment|// term, weight
name|spare
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|readWeight
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
comment|// have an empty payload
name|curPayload
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// only term
name|spare
operator|.
name|copyChars
argument_list|(
name|fields
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|curWeight
operator|=
literal|1
expr_stmt|;
if|if
condition|(
name|hasPayloads
condition|)
block|{
name|curPayload
operator|=
operator|new
name|BytesRefBuilder
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|spare
operator|.
name|get
argument_list|()
return|;
block|}
else|else
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|IOUtils
operator|.
name|close
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|payload
specifier|public
name|BytesRef
name|payload
parameter_list|()
block|{
return|return
operator|(
name|hasPayloads
operator|)
condition|?
name|curPayload
operator|.
name|get
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasPayloads
specifier|public
name|boolean
name|hasPayloads
parameter_list|()
block|{
return|return
name|hasPayloads
return|;
block|}
DECL|method|readWeight
specifier|private
name|void
name|readWeight
parameter_list|(
name|String
name|weight
parameter_list|)
block|{
comment|// keep reading floats for bw compat
try|try
block|{
name|curWeight
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|curWeight
operator|=
operator|(
name|long
operator|)
name|Double
operator|.
name|parseDouble
argument_list|(
name|weight
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|contexts
specifier|public
name|Set
argument_list|<
name|BytesRef
argument_list|>
name|contexts
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|hasContexts
specifier|public
name|boolean
name|hasContexts
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
end_class
end_unit
