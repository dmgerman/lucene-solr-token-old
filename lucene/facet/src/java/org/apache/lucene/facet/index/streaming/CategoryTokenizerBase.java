begin_unit
begin_package
DECL|package|org.apache.lucene.facet.index.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|index
operator|.
name|streaming
package|;
end_package
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
name|tokenattributes
operator|.
name|PayloadAttribute
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
name|tokenattributes
operator|.
name|CharTermAttribute
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
name|facet
operator|.
name|index
operator|.
name|CategoryDocumentBuilder
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryAttribute
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
name|facet
operator|.
name|index
operator|.
name|params
operator|.
name|FacetIndexingParams
import|;
end_import
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * A base class for all token filters which add term and payload attributes to  * tokens and are to be used in {@link CategoryDocumentBuilder}. Contains three  * attributes: {@link CategoryAttribute}, {@link CharTermAttribute} and  * {@link PayloadAttribute}.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CategoryTokenizerBase
specifier|public
specifier|abstract
class|class
name|CategoryTokenizerBase
extends|extends
name|TokenFilter
block|{
comment|/** The stream's category attributes. */
DECL|field|categoryAttribute
specifier|protected
name|CategoryAttribute
name|categoryAttribute
decl_stmt|;
comment|/** The stream's payload attribute. */
DECL|field|payloadAttribute
specifier|protected
name|PayloadAttribute
name|payloadAttribute
decl_stmt|;
comment|/** The stream's term attribute. */
DECL|field|termAttribute
specifier|protected
name|CharTermAttribute
name|termAttribute
decl_stmt|;
comment|/** The object used for constructing payloads. */
DECL|field|payload
specifier|protected
name|BytesRef
name|payload
init|=
operator|new
name|BytesRef
argument_list|()
decl_stmt|;
comment|/** Indexing params for creating term text **/
DECL|field|indexingParams
specifier|protected
name|FacetIndexingParams
name|indexingParams
decl_stmt|;
comment|/**    * Constructor.    *     * @param input    *            The input stream, either {@link CategoryParentsStream} or an    *            extension of {@link CategoryTokenizerBase}.    * @param indexingParams    *            The indexing params to use.    */
DECL|method|CategoryTokenizerBase
specifier|public
name|CategoryTokenizerBase
parameter_list|(
name|TokenStream
name|input
parameter_list|,
name|FacetIndexingParams
name|indexingParams
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|categoryAttribute
operator|=
name|this
operator|.
name|addAttribute
argument_list|(
name|CategoryAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|termAttribute
operator|=
name|this
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|payloadAttribute
operator|=
name|this
operator|.
name|addAttribute
argument_list|(
name|PayloadAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexingParams
operator|=
name|indexingParams
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|abstract
name|boolean
name|incrementToken
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class
end_unit
