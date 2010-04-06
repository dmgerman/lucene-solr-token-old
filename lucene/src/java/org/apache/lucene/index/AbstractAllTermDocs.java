begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
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
begin_comment
comment|/** Base class for enumerating all but deleted docs.  *   *<p>NOTE: this class is meant only to be used internally  * by Lucene; it's only public so it can be shared across  * packages.  This means the API is freely subject to  * change, and, the class could be removed entirely, in any  * Lucene release.  Use directly at your own risk! */
end_comment
begin_class
annotation|@
name|Deprecated
DECL|class|AbstractAllTermDocs
specifier|public
specifier|abstract
class|class
name|AbstractAllTermDocs
implements|implements
name|TermDocs
block|{
DECL|field|maxDoc
specifier|protected
name|int
name|maxDoc
decl_stmt|;
DECL|field|doc
specifier|protected
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|AbstractAllTermDocs
specifier|protected
name|AbstractAllTermDocs
parameter_list|(
name|int
name|maxDoc
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|Term
name|term
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|term
operator|==
literal|null
condition|)
block|{
name|doc
operator|=
operator|-
literal|1
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
DECL|method|seek
specifier|public
name|void
name|seek
parameter_list|(
name|TermEnum
name|termEnum
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
DECL|method|doc
specifier|public
name|int
name|doc
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
literal|1
return|;
block|}
DECL|method|next
specifier|public
name|boolean
name|next
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|skipTo
argument_list|(
name|doc
operator|+
literal|1
argument_list|)
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|int
index|[]
name|docs
parameter_list|,
name|int
index|[]
name|freqs
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|length
init|=
name|docs
operator|.
name|length
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|length
operator|&&
name|doc
operator|<
name|maxDoc
condition|)
block|{
if|if
condition|(
operator|!
name|isDeleted
argument_list|(
name|doc
argument_list|)
condition|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|doc
expr_stmt|;
name|freqs
index|[
name|i
index|]
operator|=
literal|1
expr_stmt|;
operator|++
name|i
expr_stmt|;
block|}
name|doc
operator|++
expr_stmt|;
block|}
return|return
name|i
return|;
block|}
DECL|method|skipTo
specifier|public
name|boolean
name|skipTo
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|doc
operator|=
name|target
expr_stmt|;
while|while
condition|(
name|doc
operator|<
name|maxDoc
condition|)
block|{
if|if
condition|(
operator|!
name|isDeleted
argument_list|(
name|doc
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|doc
operator|++
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{   }
DECL|method|isDeleted
specifier|public
specifier|abstract
name|boolean
name|isDeleted
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
block|}
end_class
end_unit
