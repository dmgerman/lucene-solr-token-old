begin_unit
begin_package
DECL|package|org.apache.lucene.util.automaton.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|fst
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
comment|/** Can next() and advance() through the terms in an FST   * @lucene.experimental */
end_comment
begin_class
DECL|class|BytesRefFSTEnum
specifier|public
specifier|final
class|class
name|BytesRefFSTEnum
parameter_list|<
name|T
parameter_list|>
extends|extends
name|FSTEnum
argument_list|<
name|T
argument_list|>
block|{
DECL|field|current
specifier|private
specifier|final
name|BytesRef
name|current
init|=
operator|new
name|BytesRef
argument_list|(
literal|10
argument_list|)
decl_stmt|;
DECL|field|result
specifier|private
specifier|final
name|InputOutput
argument_list|<
name|T
argument_list|>
name|result
init|=
operator|new
name|InputOutput
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|target
specifier|private
name|BytesRef
name|target
decl_stmt|;
DECL|class|InputOutput
specifier|public
specifier|static
class|class
name|InputOutput
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|input
specifier|public
name|BytesRef
name|input
decl_stmt|;
DECL|field|output
specifier|public
name|T
name|output
decl_stmt|;
block|}
comment|/** doFloor controls the behavior of advance: if it's true    *  doFloor is true, advance positions to the biggest    *  term before target.  */
DECL|method|BytesRefFSTEnum
specifier|public
name|BytesRefFSTEnum
parameter_list|(
name|FST
argument_list|<
name|T
argument_list|>
name|fst
parameter_list|)
block|{
name|super
argument_list|(
name|fst
argument_list|)
expr_stmt|;
name|result
operator|.
name|input
operator|=
name|current
expr_stmt|;
name|current
operator|.
name|offset
operator|=
literal|1
expr_stmt|;
block|}
DECL|method|current
specifier|public
name|InputOutput
argument_list|<
name|T
argument_list|>
name|current
parameter_list|()
block|{
return|return
name|result
return|;
block|}
DECL|method|next
specifier|public
name|InputOutput
argument_list|<
name|T
argument_list|>
name|next
parameter_list|()
throws|throws
name|IOException
block|{
comment|//System.out.println("  enum.next");
name|doNext
argument_list|()
expr_stmt|;
return|return
name|setResult
argument_list|()
return|;
block|}
comment|/** Seeks to smallest term that's>= target. */
DECL|method|seekCeil
specifier|public
name|InputOutput
argument_list|<
name|T
argument_list|>
name|seekCeil
parameter_list|(
name|BytesRef
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|targetLength
operator|=
name|target
operator|.
name|length
expr_stmt|;
name|super
operator|.
name|doSeekCeil
argument_list|()
expr_stmt|;
return|return
name|setResult
argument_list|()
return|;
block|}
comment|/** Seeks to biggest term that's<= target. */
DECL|method|seekFloor
specifier|public
name|InputOutput
argument_list|<
name|T
argument_list|>
name|seekFloor
parameter_list|(
name|BytesRef
name|target
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|targetLength
operator|=
name|target
operator|.
name|length
expr_stmt|;
name|super
operator|.
name|doSeekFloor
argument_list|()
expr_stmt|;
return|return
name|setResult
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTargetLabel
specifier|protected
name|int
name|getTargetLabel
parameter_list|()
block|{
if|if
condition|(
name|upto
operator|-
literal|1
operator|==
name|target
operator|.
name|length
condition|)
block|{
return|return
name|FST
operator|.
name|END_LABEL
return|;
block|}
else|else
block|{
return|return
name|target
operator|.
name|bytes
index|[
name|target
operator|.
name|offset
operator|+
name|upto
operator|-
literal|1
index|]
operator|&
literal|0xFF
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getCurrentLabel
specifier|protected
name|int
name|getCurrentLabel
parameter_list|()
block|{
comment|// current.offset fixed at 1
return|return
name|current
operator|.
name|bytes
index|[
name|upto
index|]
operator|&
literal|0xFF
return|;
block|}
annotation|@
name|Override
DECL|method|setCurrentLabel
specifier|protected
name|void
name|setCurrentLabel
parameter_list|(
name|int
name|label
parameter_list|)
block|{
name|current
operator|.
name|bytes
index|[
name|upto
index|]
operator|=
operator|(
name|byte
operator|)
name|label
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|grow
specifier|protected
name|void
name|grow
parameter_list|()
block|{
name|current
operator|.
name|grow
argument_list|(
name|upto
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
DECL|method|setResult
specifier|private
name|InputOutput
argument_list|<
name|T
argument_list|>
name|setResult
parameter_list|()
block|{
if|if
condition|(
name|upto
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
name|current
operator|.
name|length
operator|=
name|upto
operator|-
literal|1
expr_stmt|;
name|result
operator|.
name|output
operator|=
name|output
index|[
name|upto
index|]
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_class
end_unit
