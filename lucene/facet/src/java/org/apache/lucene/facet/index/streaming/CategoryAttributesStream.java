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
name|util
operator|.
name|Iterator
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
name|facet
operator|.
name|index
operator|.
name|attributes
operator|.
name|CategoryAttribute
import|;
end_import
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * An attribute stream built from an {@link Iterable} of  * {@link CategoryAttribute}. This stream should then be passed through several  * filters (see {@link CategoryParentsStream}, {@link CategoryListTokenizer} and  * {@link CategoryTokenizer}) until a token stream is produced that can be  * indexed by Lucene.  *<P>  * A CategoryAttributesStream object can be reused for producing more than one  * stream. To do that, the user should cause the underlying  * Iterable&lt;CategoryAttribute&gt; object to return a new set of categories, and  * then call {@link #reset()} to allow this stream to be used again.  *   * @lucene.experimental  */
end_comment
begin_class
DECL|class|CategoryAttributesStream
specifier|public
class|class
name|CategoryAttributesStream
extends|extends
name|TokenStream
block|{
DECL|field|categoryAttribute
specifier|protected
name|CategoryAttribute
name|categoryAttribute
decl_stmt|;
DECL|field|iterable
specifier|private
name|Iterable
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterable
decl_stmt|;
DECL|field|iterator
specifier|private
name|Iterator
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterator
decl_stmt|;
comment|/**    * Constructor    *     * @param iterable    *            {@link Iterable} of {@link CategoryAttribute}, from which    *            categories are taken.    */
DECL|method|CategoryAttributesStream
specifier|public
name|CategoryAttributesStream
parameter_list|(
name|Iterable
argument_list|<
name|CategoryAttribute
argument_list|>
name|iterable
parameter_list|)
block|{
name|this
operator|.
name|iterable
operator|=
name|iterable
expr_stmt|;
name|this
operator|.
name|iterator
operator|=
literal|null
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
block|}
annotation|@
name|Override
DECL|method|incrementToken
specifier|public
specifier|final
name|boolean
name|incrementToken
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|iterable
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|iterator
operator|=
name|iterable
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|categoryAttribute
operator|.
name|set
argument_list|(
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|reset
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|this
operator|.
name|iterator
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class
end_unit
