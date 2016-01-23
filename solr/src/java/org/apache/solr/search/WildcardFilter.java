begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
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
name|Filter
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
name|DocIdSet
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
name|WildcardTermEnum
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
name|TermEnum
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
name|TermDocs
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
name|OpenBitSet
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
begin_comment
comment|/**  *  * @version $Id$  */
end_comment
begin_class
DECL|class|WildcardFilter
specifier|public
class|class
name|WildcardFilter
extends|extends
name|Filter
block|{
DECL|field|term
specifier|protected
specifier|final
name|Term
name|term
decl_stmt|;
DECL|method|WildcardFilter
specifier|public
name|WildcardFilter
parameter_list|(
name|Term
name|wildcardTerm
parameter_list|)
block|{
name|this
operator|.
name|term
operator|=
name|wildcardTerm
expr_stmt|;
block|}
DECL|method|getTerm
specifier|public
name|Term
name|getTerm
parameter_list|()
block|{
return|return
name|term
return|;
block|}
annotation|@
name|Override
DECL|method|getDocIdSet
specifier|public
name|DocIdSet
name|getDocIdSet
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|OpenBitSet
name|bitSet
init|=
operator|new
name|OpenBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
decl_stmt|;
operator|new
name|WildcardGenerator
argument_list|(
name|term
argument_list|)
block|{
specifier|public
name|void
name|handleDoc
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
block|}
operator|.
name|generate
argument_list|(
name|reader
argument_list|)
expr_stmt|;
return|return
name|bitSet
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
return|return
name|o
operator|instanceof
name|WildcardFilter
operator|&&
operator|(
operator|(
name|WildcardFilter
operator|)
name|o
operator|)
operator|.
name|term
operator|.
name|equals
argument_list|(
name|this
operator|.
name|term
argument_list|)
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
name|term
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"WildcardFilter("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|term
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
begin_class
DECL|class|WildcardGenerator
specifier|abstract
class|class
name|WildcardGenerator
implements|implements
name|IdGenerator
block|{
DECL|field|wildcard
specifier|protected
specifier|final
name|Term
name|wildcard
decl_stmt|;
DECL|method|WildcardGenerator
name|WildcardGenerator
parameter_list|(
name|Term
name|wildcard
parameter_list|)
block|{
name|this
operator|.
name|wildcard
operator|=
name|wildcard
expr_stmt|;
block|}
DECL|method|generate
specifier|public
name|void
name|generate
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|TermEnum
name|enumerator
init|=
operator|new
name|WildcardTermEnum
argument_list|(
name|reader
argument_list|,
name|wildcard
argument_list|)
decl_stmt|;
name|TermDocs
name|termDocs
init|=
name|reader
operator|.
name|termDocs
argument_list|()
decl_stmt|;
try|try
block|{
do|do
block|{
name|Term
name|term
init|=
name|enumerator
operator|.
name|term
argument_list|()
decl_stmt|;
if|if
condition|(
name|term
operator|==
literal|null
condition|)
break|break;
name|termDocs
operator|.
name|seek
argument_list|(
name|term
argument_list|)
expr_stmt|;
while|while
condition|(
name|termDocs
operator|.
name|next
argument_list|()
condition|)
block|{
name|handleDoc
argument_list|(
name|termDocs
operator|.
name|doc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
do|while
condition|(
name|enumerator
operator|.
name|next
argument_list|()
condition|)
do|;
block|}
finally|finally
block|{
name|termDocs
operator|.
name|close
argument_list|()
expr_stmt|;
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
