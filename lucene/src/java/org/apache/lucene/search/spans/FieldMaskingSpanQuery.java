begin_unit
begin_package
DECL|package|org.apache.lucene.search.spans
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spans
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Query
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
name|IndexSearcher
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
name|Similarity
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
name|ToStringUtils
import|;
end_import
begin_comment
comment|/**  *<p>Wrapper to allow {@link SpanQuery} objects participate in composite   * single-field SpanQueries by 'lying' about their search field. That is,   * the masked SpanQuery will function as normal,   * but {@link SpanQuery#getField()} simply hands back the value supplied   * in this class's constructor.</p>  *   *<p>This can be used to support Queries like {@link SpanNearQuery} or   * {@link SpanOrQuery} across different fields, which is not ordinarily   * permitted.</p>  *   *<p>This can be useful for denormalized relational data: for example, when   * indexing a document with conceptually many 'children':</p>  *   *<pre>  *  teacherid: 1  *  studentfirstname: james  *  studentsurname: jones  *    *  teacherid: 2  *  studenfirstname: james  *  studentsurname: smith  *  studentfirstname: sally  *  studentsurname: jones  *</pre>  *   *<p>a SpanNearQuery with a slop of 0 can be applied across two   * {@link SpanTermQuery} objects as follows:  *<pre>  *    SpanQuery q1  = new SpanTermQuery(new Term("studentfirstname", "james"));  *    SpanQuery q2  = new SpanTermQuery(new Term("studentsurname", "jones"));  *    SpanQuery q2m = new FieldMaskingSpanQuery(q2, "studentfirstname");  *    Query q = new SpanNearQuery(new SpanQuery[]{q1, q2m}, -1, false);  *</pre>  * to search for 'studentfirstname:james studentsurname:jones' and find   * teacherid 1 without matching teacherid 2 (which has a 'james' in position 0   * and 'jones' in position 1).</p>  *   *<p>Note: as {@link #getField()} returns the masked field, scoring will be   * done using the norms of the field name supplied. This may lead to unexpected  * scoring behaviour.</p>  */
end_comment
begin_class
DECL|class|FieldMaskingSpanQuery
specifier|public
class|class
name|FieldMaskingSpanQuery
extends|extends
name|SpanQuery
block|{
DECL|field|maskedQuery
specifier|private
name|SpanQuery
name|maskedQuery
decl_stmt|;
DECL|field|field
specifier|private
name|String
name|field
decl_stmt|;
DECL|method|FieldMaskingSpanQuery
specifier|public
name|FieldMaskingSpanQuery
parameter_list|(
name|SpanQuery
name|maskedQuery
parameter_list|,
name|String
name|maskedField
parameter_list|)
block|{
name|this
operator|.
name|maskedQuery
operator|=
name|maskedQuery
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|maskedField
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getField
specifier|public
name|String
name|getField
parameter_list|()
block|{
return|return
name|field
return|;
block|}
DECL|method|getMaskedQuery
specifier|public
name|SpanQuery
name|getMaskedQuery
parameter_list|()
block|{
return|return
name|maskedQuery
return|;
block|}
comment|// :NOTE: getBoost and setBoost are not proxied to the maskedQuery
comment|// ...this is done to be more consistent with things like SpanFirstQuery
annotation|@
name|Override
DECL|method|getSpans
specifier|public
name|Spans
name|getSpans
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|maskedQuery
operator|.
name|getSpans
argument_list|(
name|reader
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|extractTerms
specifier|public
name|void
name|extractTerms
parameter_list|(
name|Set
argument_list|<
name|Term
argument_list|>
name|terms
parameter_list|)
block|{
name|maskedQuery
operator|.
name|extractTerms
argument_list|(
name|terms
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createWeight
specifier|public
name|Weight
name|createWeight
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|maskedQuery
operator|.
name|createWeight
argument_list|(
name|searcher
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldMaskingSpanQuery
name|clone
init|=
literal|null
decl_stmt|;
name|SpanQuery
name|rewritten
init|=
operator|(
name|SpanQuery
operator|)
name|maskedQuery
operator|.
name|rewrite
argument_list|(
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewritten
operator|!=
name|maskedQuery
condition|)
block|{
name|clone
operator|=
operator|(
name|FieldMaskingSpanQuery
operator|)
name|this
operator|.
name|clone
argument_list|()
expr_stmt|;
name|clone
operator|.
name|maskedQuery
operator|=
name|rewritten
expr_stmt|;
block|}
if|if
condition|(
name|clone
operator|!=
literal|null
condition|)
block|{
return|return
name|clone
return|;
block|}
else|else
block|{
return|return
name|this
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|field
parameter_list|)
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|"mask("
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|maskedQuery
operator|.
name|toString
argument_list|(
name|field
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|ToStringUtils
operator|.
name|boost
argument_list|(
name|getBoost
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|this
operator|.
name|field
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
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
name|FieldMaskingSpanQuery
operator|)
condition|)
return|return
literal|false
return|;
name|FieldMaskingSpanQuery
name|other
init|=
operator|(
name|FieldMaskingSpanQuery
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|getField
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getField
argument_list|()
argument_list|)
operator|&&
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
name|getMaskedQuery
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getMaskedQuery
argument_list|()
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getMaskedQuery
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|getField
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|^
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|getBoost
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
