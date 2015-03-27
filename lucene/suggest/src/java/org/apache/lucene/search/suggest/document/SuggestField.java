begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.document
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
operator|.
name|document
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
name|ByteArrayOutputStream
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
name|Analyzer
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|FieldType
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
name|index
operator|.
name|IndexOptions
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
name|store
operator|.
name|OutputStreamDataOutput
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
begin_comment
comment|/**  *<p>  * Field that indexes a string value and a weight as a weighted completion  * against a named suggester.  * Field is tokenized, not stored and stores documents, frequencies and positions.  * Field can be used to provide near real time document suggestions.  *</p>  *<p>  * Besides the usual {@link org.apache.lucene.analysis.Analyzer}s,  * {@link CompletionAnalyzer}  * can be used to tune suggest field only parameters  * (e.g. preserving token seperators, preserving position increments  * when converting the token stream to an automaton)  *</p>  *<p>  * Example indexing usage:  *<pre class="prettyprint">  * document.add(new SuggestField(name, "suggestion", 4));  *</pre>  * To perform document suggestions based on the this field, use  * {@link SuggestIndexSearcher#suggest(String, CharSequence, int, org.apache.lucene.search.Filter)}  *<p>  * Example query usage:  *<pre class="prettyprint">  * SuggestIndexSearcher indexSearcher = ..  * indexSearcher.suggest(name, "su", 2)  *</pre>  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|SuggestField
specifier|public
class|class
name|SuggestField
extends|extends
name|Field
block|{
DECL|field|FIELD_TYPE
specifier|private
specifier|static
specifier|final
name|FieldType
name|FIELD_TYPE
init|=
operator|new
name|FieldType
argument_list|()
decl_stmt|;
static|static
block|{
name|FIELD_TYPE
operator|.
name|setTokenized
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setStored
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setStoreTermVectors
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setOmitNorms
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|setIndexOptions
argument_list|(
name|IndexOptions
operator|.
name|DOCS_AND_FREQS_AND_POSITIONS
argument_list|)
expr_stmt|;
name|FIELD_TYPE
operator|.
name|freeze
argument_list|()
expr_stmt|;
block|}
DECL|field|surfaceForm
specifier|private
specifier|final
name|BytesRef
name|surfaceForm
decl_stmt|;
DECL|field|weight
specifier|private
specifier|final
name|long
name|weight
decl_stmt|;
comment|/**    * Creates a {@link SuggestField}    *    * @param name   of the field    * @param value  to get suggestions on    * @param weight weight of the suggestion    */
DECL|method|SuggestField
specifier|public
name|SuggestField
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|long
name|weight
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|FIELD_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|weight
operator|<
literal|0l
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"weight must be>= 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|surfaceForm
operator|=
operator|new
name|BytesRef
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|tokenStream
specifier|public
name|TokenStream
name|tokenStream
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|,
name|TokenStream
name|reuse
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|stream
init|=
name|super
operator|.
name|tokenStream
argument_list|(
name|analyzer
argument_list|,
name|reuse
argument_list|)
decl_stmt|;
name|CompletionTokenStream
name|completionStream
decl_stmt|;
if|if
condition|(
name|stream
operator|instanceof
name|CompletionTokenStream
condition|)
block|{
name|completionStream
operator|=
operator|(
name|CompletionTokenStream
operator|)
name|stream
expr_stmt|;
block|}
else|else
block|{
name|completionStream
operator|=
operator|new
name|CompletionTokenStream
argument_list|(
name|stream
argument_list|)
expr_stmt|;
block|}
name|BytesRef
name|suggestPayload
init|=
name|buildSuggestPayload
argument_list|(
name|surfaceForm
argument_list|,
name|weight
argument_list|,
operator|(
name|char
operator|)
name|completionStream
operator|.
name|sepLabel
argument_list|()
argument_list|)
decl_stmt|;
name|completionStream
operator|.
name|setPayload
argument_list|(
name|suggestPayload
argument_list|)
expr_stmt|;
return|return
name|completionStream
return|;
block|}
DECL|method|buildSuggestPayload
specifier|private
name|BytesRef
name|buildSuggestPayload
parameter_list|(
name|BytesRef
name|surfaceForm
parameter_list|,
name|long
name|weight
parameter_list|,
name|char
name|sepLabel
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|surfaceForm
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|surfaceForm
operator|.
name|bytes
index|[
name|i
index|]
operator|==
name|sepLabel
condition|)
block|{
assert|assert
name|sepLabel
operator|==
literal|'\u001f'
assert|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"surface form cannot contain unit separator character U+001F; this character is reserved"
argument_list|)
throw|;
block|}
block|}
name|ByteArrayOutputStream
name|byteArrayOutputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
init|(
name|OutputStreamDataOutput
name|output
init|=
operator|new
name|OutputStreamDataOutput
argument_list|(
name|byteArrayOutputStream
argument_list|)
init|)
block|{
name|output
operator|.
name|writeVInt
argument_list|(
name|surfaceForm
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeBytes
argument_list|(
name|surfaceForm
operator|.
name|bytes
argument_list|,
name|surfaceForm
operator|.
name|offset
argument_list|,
name|surfaceForm
operator|.
name|length
argument_list|)
expr_stmt|;
name|output
operator|.
name|writeVLong
argument_list|(
name|weight
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BytesRef
argument_list|(
name|byteArrayOutputStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
