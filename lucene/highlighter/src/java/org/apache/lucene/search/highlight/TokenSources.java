begin_unit
begin_comment
comment|/*  * Created on 28-Oct-2004  */
end_comment
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
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
name|index
operator|.
name|Fields
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
name|IndexReader
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
name|StoredDocument
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
name|Terms
import|;
end_import
begin_comment
comment|/**  * Hides implementation issues associated with obtaining a TokenStream for use  * with the higlighter - can obtain from TermFreqVectors with offsets and  * (optionally) positions or from Analyzer class reparsing the stored content.  */
end_comment
begin_class
DECL|class|TokenSources
specifier|public
class|class
name|TokenSources
block|{
comment|/**    * A convenience method that tries to first get a {@link TokenStreamFromTermVector} for the    * specified docId, then, falls back to using the passed in    * {@link org.apache.lucene.document.Document} to retrieve the TokenStream.    * This is useful when you already have the document, but would prefer to use    * the vector first.    *     * @param reader The {@link org.apache.lucene.index.IndexReader} to use to try    *        and get the vector from    * @param docId The docId to retrieve.    * @param field The field to retrieve on the document    * @param document The document to fall back on    * @param analyzer The analyzer to use for creating the TokenStream if the    *        vector doesn't exist    * @return The {@link org.apache.lucene.analysis.TokenStream} for the    *         {@link org.apache.lucene.index.IndexableField} on the    *         {@link org.apache.lucene.document.Document}    * @throws IOException if there was an error loading    */
DECL|method|getAnyTokenStream
specifier|public
specifier|static
name|TokenStream
name|getAnyTokenStream
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|StoredDocument
name|document
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
literal|null
decl_stmt|;
name|Fields
name|vectors
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectors
operator|!=
literal|null
condition|)
block|{
name|Terms
name|vector
init|=
name|vectors
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|!=
literal|null
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
name|vector
argument_list|)
expr_stmt|;
block|}
block|}
comment|// No token info stored so fall back to analyzing raw content
if|if
condition|(
name|ts
operator|==
literal|null
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
name|document
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
comment|/**    * A convenience method that tries a number of approaches to getting a token    * stream. The cost of finding there are no termVectors in the index is    * minimal (1000 invocations still registers 0 ms). So this "lazy" (flexible?)    * approach to coding is probably acceptable    *     * @return null if field not stored correctly    * @throws IOException If there is a low-level I/O error    */
DECL|method|getAnyTokenStream
specifier|public
specifier|static
name|TokenStream
name|getAnyTokenStream
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|TokenStream
name|ts
init|=
literal|null
decl_stmt|;
name|Fields
name|vectors
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectors
operator|!=
literal|null
condition|)
block|{
name|Terms
name|vector
init|=
name|vectors
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|!=
literal|null
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
name|vector
argument_list|)
expr_stmt|;
block|}
block|}
comment|// No token info stored so fall back to analyzing raw content
if|if
condition|(
name|ts
operator|==
literal|null
condition|)
block|{
name|ts
operator|=
name|getTokenStream
argument_list|(
name|reader
argument_list|,
name|docId
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
return|return
name|ts
return|;
block|}
comment|/** Simply calls {@link #getTokenStream(org.apache.lucene.index.Terms)} now. */
annotation|@
name|Deprecated
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|Terms
name|vector
parameter_list|,
name|boolean
name|tokenPositionsGuaranteedContiguous
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getTokenStream
argument_list|(
name|vector
argument_list|)
return|;
block|}
comment|/**    * Returns a token stream generated from a {@link Terms}. This    * can be used to feed the highlighter with a pre-parsed token    * stream.  The {@link Terms} must have offsets available. If there are no positions available,    * all tokens will have position increments reflecting adjacent tokens, or coincident when terms    * share a start offset. If there are stopwords filtered from the index, you probably want to ensure    * term vectors have positions so that phrase queries won't match across stopwords.    *    * @throws IllegalArgumentException if no offsets are available    */
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
specifier|final
name|Terms
name|tpv
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|tpv
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Highlighting requires offsets from the TokenStream."
argument_list|)
throw|;
comment|//TokenStreamFromTermVector can handle a lack of offsets if there are positions. But
comment|// highlighters require offsets, so we insist here.
block|}
return|return
operator|new
name|TokenStreamFromTermVector
argument_list|(
name|tpv
argument_list|,
operator|-
literal|1
argument_list|)
return|;
comment|// TODO propagate maxStartOffset; see LUCENE-6445
block|}
comment|/**    * Returns a {@link TokenStream} with positions and offsets constructed from    * field termvectors.  If the field has no termvectors or offsets    * are not included in the termvector, return null.  See {@link #getTokenStream(org.apache.lucene.index.Terms)}    * for an explanation of what happens when positions aren't present.    *    * @param reader the {@link IndexReader} to retrieve term vectors from    * @param docId the document to retrieve termvectors for    * @param field the field to retrieve termvectors for    * @return a {@link TokenStream}, or null if offsets are not available    * @throws IOException If there is a low-level I/O error    *    * @see #getTokenStream(org.apache.lucene.index.Terms)    */
DECL|method|getTokenStreamWithOffsets
specifier|public
specifier|static
name|TokenStream
name|getTokenStreamWithOffsets
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|)
throws|throws
name|IOException
block|{
name|Fields
name|vectors
init|=
name|reader
operator|.
name|getTermVectors
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|vectors
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Terms
name|vector
init|=
name|vectors
operator|.
name|terms
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|vector
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|vector
operator|.
name|hasOffsets
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|getTokenStream
argument_list|(
name|vector
argument_list|)
return|;
block|}
comment|// convenience method
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|int
name|docId
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
throws|throws
name|IOException
block|{
name|StoredDocument
name|doc
init|=
name|reader
operator|.
name|document
argument_list|(
name|docId
argument_list|)
decl_stmt|;
return|return
name|getTokenStream
argument_list|(
name|doc
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|StoredDocument
name|doc
parameter_list|,
name|String
name|field
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|String
name|contents
init|=
name|doc
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
if|if
condition|(
name|contents
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Field "
operator|+
name|field
operator|+
literal|" in document is not stored and cannot be analyzed"
argument_list|)
throw|;
block|}
return|return
name|getTokenStream
argument_list|(
name|field
argument_list|,
name|contents
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
comment|// convenience method
DECL|method|getTokenStream
specifier|public
specifier|static
name|TokenStream
name|getTokenStream
parameter_list|(
name|String
name|field
parameter_list|,
name|String
name|contents
parameter_list|,
name|Analyzer
name|analyzer
parameter_list|)
block|{
try|try
block|{
return|return
name|analyzer
operator|.
name|tokenStream
argument_list|(
name|field
argument_list|,
name|contents
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
