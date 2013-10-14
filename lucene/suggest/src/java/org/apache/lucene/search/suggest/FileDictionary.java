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
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * Dictionary represented by a text file.  *   *<p/>Format allowed: 1 string per line, optionally with a tab-separated integer value:<br/>  * word1 TAB 100<br/>  * word2 word3 TAB 101<br/>  * word4 word5 TAB 102<br/>  */
end_comment
begin_class
DECL|class|FileDictionary
specifier|public
class|class
name|FileDictionary
implements|implements
name|Dictionary
block|{
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
comment|/**    * Creates a dictionary based on an inputstream.    *<p>    * NOTE: content is treated as UTF-8    */
DECL|method|FileDictionary
specifier|public
name|FileDictionary
parameter_list|(
name|InputStream
name|dictFile
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
name|IOUtils
operator|.
name|CHARSET_UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a dictionary based on a reader.    */
DECL|method|FileDictionary
specifier|public
name|FileDictionary
parameter_list|(
name|Reader
name|reader
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
block|}
annotation|@
name|Override
DECL|method|getWordsIterator
specifier|public
name|InputIterator
name|getWordsIterator
parameter_list|()
block|{
return|return
operator|new
name|FileIterator
argument_list|()
return|;
block|}
DECL|class|FileIterator
specifier|final
class|class
name|FileIterator
implements|implements
name|InputIterator
block|{
DECL|field|curFreq
specifier|private
name|long
name|curFreq
decl_stmt|;
DECL|field|spare
specifier|private
specifier|final
name|BytesRef
name|spare
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|weight
specifier|public
name|long
name|weight
parameter_list|()
block|{
return|return
name|curFreq
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
literal|"\t"
argument_list|)
decl_stmt|;
if|if
condition|(
name|fields
operator|.
name|length
operator|>
literal|1
condition|)
block|{
comment|// keep reading floats for bw compat
try|try
block|{
name|curFreq
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
name|curFreq
operator|=
operator|(
name|long
operator|)
name|Double
operator|.
name|parseDouble
argument_list|(
name|fields
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
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
block|}
else|else
block|{
name|spare
operator|.
name|copyChars
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|curFreq
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|spare
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
literal|false
return|;
block|}
block|}
block|}
end_class
end_unit
