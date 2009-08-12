begin_unit
begin_package
DECL|package|org.apache.lucene.search.payloads
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|payloads
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
name|Term
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
name|Scorer
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
name|Searcher
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
name|Weight
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
name|spans
operator|.
name|TermSpans
import|;
end_import
begin_comment
comment|/**  * Copyright 2004 The Apache Software Foundation  *<p/>  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p/>  * http://www.apache.org/licenses/LICENSE-2.0  *<p/>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * The BoostingTermQuery is very similar to the {@link org.apache.lucene.search.spans.SpanTermQuery} except  * that it factors in the value of the payload located at each of the positions where the  * {@link org.apache.lucene.index.Term} occurs.  *<p>  * In order to take advantage of this, you must override {@link org.apache.lucene.search.Similarity#scorePayload(String, byte[],int,int)}  * which returns 1 by default.  *<p>  * Payload scores are averaged across term occurrences in the document.    *   * @see org.apache.lucene.search.Similarity#scorePayload(String, byte[], int, int)  *  * @deprecated See {@link org.apache.lucene.search.payloads.BoostingFunctionTermQuery}  */
end_comment
begin_class
DECL|class|BoostingTermQuery
specifier|public
class|class
name|BoostingTermQuery
extends|extends
name|BoostingFunctionTermQuery
implements|implements
name|PayloadQuery
block|{
DECL|method|BoostingTermQuery
specifier|public
name|BoostingTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
name|this
argument_list|(
name|term
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|BoostingTermQuery
specifier|public
name|BoostingTermQuery
parameter_list|(
name|Term
name|term
parameter_list|,
name|boolean
name|includeSpanScore
parameter_list|)
block|{
name|super
argument_list|(
name|term
argument_list|,
operator|new
name|AveragePayloadFunction
argument_list|()
argument_list|,
name|includeSpanScore
argument_list|)
expr_stmt|;
block|}
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostingTermWeight
argument_list|(
name|this
argument_list|,
name|searcher
argument_list|)
return|;
block|}
DECL|class|BoostingTermWeight
specifier|protected
class|class
name|BoostingTermWeight
extends|extends
name|BoostingFunctionTermWeight
block|{
DECL|method|BoostingTermWeight
specifier|public
name|BoostingTermWeight
parameter_list|(
name|BoostingTermQuery
name|query
parameter_list|,
name|Searcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|query
argument_list|,
name|searcher
argument_list|)
expr_stmt|;
block|}
DECL|method|scorer
specifier|public
name|Scorer
name|scorer
parameter_list|(
name|IndexReader
name|reader
parameter_list|,
name|boolean
name|scoreDocsInOrder
parameter_list|,
name|boolean
name|topScorer
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|BoostingFunctionSpanScorer
argument_list|(
operator|(
name|TermSpans
operator|)
name|query
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
argument_list|,
name|this
argument_list|,
name|similarity
argument_list|,
name|reader
operator|.
name|norms
argument_list|(
name|query
operator|.
name|getField
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|BoostingTermQuery
operator|)
condition|)
return|return
literal|false
return|;
name|BoostingTermQuery
name|other
init|=
operator|(
name|BoostingTermQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getBoost
argument_list|()
operator|==
name|other
operator|.
name|getBoost
argument_list|()
operator|)
operator|&&
name|this
operator|.
name|term
operator|.
name|equals
argument_list|(
name|other
operator|.
name|term
argument_list|)
return|;
block|}
block|}
end_class
end_unit
