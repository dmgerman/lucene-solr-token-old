begin_unit
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
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
name|Bits
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
name|ReaderUtil
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
comment|/**  * Exposes flex API, merged from flex API of sub-segments.  *  * @lucene.experimental  */
end_comment
begin_class
DECL|class|MultiDocsEnum
specifier|public
specifier|final
class|class
name|MultiDocsEnum
extends|extends
name|DocsEnum
block|{
DECL|field|subs
specifier|private
name|EnumWithSlice
index|[]
name|subs
decl_stmt|;
DECL|field|numSubs
name|int
name|numSubs
decl_stmt|;
DECL|field|upto
name|int
name|upto
decl_stmt|;
DECL|field|current
name|DocsEnum
name|current
decl_stmt|;
DECL|field|currentBase
name|int
name|currentBase
decl_stmt|;
DECL|field|skipDocs
name|Bits
name|skipDocs
decl_stmt|;
DECL|field|doc
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
DECL|method|reset
name|MultiDocsEnum
name|reset
parameter_list|(
specifier|final
name|EnumWithSlice
index|[]
name|subs
parameter_list|,
specifier|final
name|int
name|numSubs
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|numSubs
operator|=
name|numSubs
expr_stmt|;
name|this
operator|.
name|subs
operator|=
name|subs
expr_stmt|;
name|upto
operator|=
operator|-
literal|1
expr_stmt|;
name|current
operator|=
literal|null
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getNumSubs
specifier|public
name|int
name|getNumSubs
parameter_list|()
block|{
return|return
name|numSubs
return|;
block|}
DECL|method|getSubs
specifier|public
name|EnumWithSlice
index|[]
name|getSubs
parameter_list|()
block|{
return|return
name|subs
return|;
block|}
annotation|@
name|Override
DECL|method|freq
specifier|public
name|int
name|freq
parameter_list|()
block|{
return|return
name|current
operator|.
name|freq
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|docID
specifier|public
name|int
name|docID
parameter_list|()
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
DECL|method|advance
specifier|public
name|int
name|advance
parameter_list|(
name|int
name|target
parameter_list|)
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|doc
init|=
name|current
operator|.
name|advance
argument_list|(
name|target
operator|-
name|currentBase
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|==
name|NO_MORE_DOCS
condition|)
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
return|return
name|this
operator|.
name|doc
operator|=
name|doc
operator|+
name|currentBase
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|upto
operator|==
name|numSubs
operator|-
literal|1
condition|)
block|{
return|return
name|this
operator|.
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|upto
operator|++
expr_stmt|;
name|current
operator|=
name|subs
index|[
name|upto
index|]
operator|.
name|docsEnum
expr_stmt|;
name|currentBase
operator|=
name|subs
index|[
name|upto
index|]
operator|.
name|slice
operator|.
name|start
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|nextDoc
specifier|public
name|int
name|nextDoc
parameter_list|()
throws|throws
name|IOException
block|{
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|upto
operator|==
name|numSubs
operator|-
literal|1
condition|)
block|{
return|return
name|this
operator|.
name|doc
operator|=
name|NO_MORE_DOCS
return|;
block|}
else|else
block|{
name|upto
operator|++
expr_stmt|;
name|current
operator|=
name|subs
index|[
name|upto
index|]
operator|.
name|docsEnum
expr_stmt|;
name|currentBase
operator|=
name|subs
index|[
name|upto
index|]
operator|.
name|slice
operator|.
name|start
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|doc
init|=
name|current
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
name|NO_MORE_DOCS
condition|)
block|{
return|return
name|this
operator|.
name|doc
operator|=
name|currentBase
operator|+
name|doc
return|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
comment|// TODO: implement bulk read more efficiently than super
DECL|class|EnumWithSlice
specifier|public
specifier|final
specifier|static
class|class
name|EnumWithSlice
block|{
DECL|field|docsEnum
specifier|public
name|DocsEnum
name|docsEnum
decl_stmt|;
DECL|field|slice
specifier|public
name|ReaderUtil
operator|.
name|Slice
name|slice
decl_stmt|;
block|}
block|}
end_class
end_unit
