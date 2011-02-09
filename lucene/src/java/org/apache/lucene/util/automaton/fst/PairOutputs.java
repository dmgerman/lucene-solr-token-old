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
name|store
operator|.
name|DataInput
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
name|DataOutput
import|;
end_import
begin_comment
comment|/**  * Pairs up two outputs into one.  * @lucene.experimental  */
end_comment
begin_class
DECL|class|PairOutputs
specifier|public
class|class
name|PairOutputs
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
extends|extends
name|Outputs
argument_list|<
name|PairOutputs
operator|.
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
argument_list|>
block|{
DECL|field|NO_OUTPUT
specifier|private
specifier|final
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|NO_OUTPUT
decl_stmt|;
DECL|field|outputs1
specifier|private
specifier|final
name|Outputs
argument_list|<
name|A
argument_list|>
name|outputs1
decl_stmt|;
DECL|field|outputs2
specifier|private
specifier|final
name|Outputs
argument_list|<
name|B
argument_list|>
name|outputs2
decl_stmt|;
DECL|class|Pair
specifier|public
specifier|static
class|class
name|Pair
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
block|{
DECL|field|output1
specifier|public
specifier|final
name|A
name|output1
decl_stmt|;
DECL|field|output2
specifier|public
specifier|final
name|B
name|output2
decl_stmt|;
DECL|method|Pair
specifier|public
name|Pair
parameter_list|(
name|A
name|output1
parameter_list|,
name|B
name|output2
parameter_list|)
block|{
name|this
operator|.
name|output1
operator|=
name|output1
expr_stmt|;
name|this
operator|.
name|output2
operator|=
name|output2
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|instanceof
name|Pair
condition|)
block|{
name|Pair
name|pair
init|=
operator|(
name|Pair
operator|)
name|other
decl_stmt|;
return|return
name|output1
operator|.
name|equals
argument_list|(
name|pair
operator|.
name|output1
argument_list|)
operator|&&
name|output2
operator|.
name|equals
argument_list|(
name|pair
operator|.
name|output2
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
name|output1
operator|.
name|hashCode
argument_list|()
operator|+
name|output2
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
empty_stmt|;
DECL|method|PairOutputs
specifier|public
name|PairOutputs
parameter_list|(
name|Outputs
argument_list|<
name|A
argument_list|>
name|outputs1
parameter_list|,
name|Outputs
argument_list|<
name|B
argument_list|>
name|outputs2
parameter_list|)
block|{
name|this
operator|.
name|outputs1
operator|=
name|outputs1
expr_stmt|;
name|this
operator|.
name|outputs2
operator|=
name|outputs2
expr_stmt|;
name|NO_OUTPUT
operator|=
operator|new
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
argument_list|(
name|outputs1
operator|.
name|getNoOutput
argument_list|()
argument_list|,
name|outputs2
operator|.
name|getNoOutput
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|get
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|get
parameter_list|(
name|A
name|output1
parameter_list|,
name|B
name|output2
parameter_list|)
block|{
if|if
condition|(
name|output1
operator|==
name|outputs1
operator|.
name|getNoOutput
argument_list|()
operator|&&
name|output2
operator|==
name|outputs2
operator|.
name|getNoOutput
argument_list|()
condition|)
block|{
return|return
name|NO_OUTPUT
return|;
block|}
else|else
block|{
return|return
operator|new
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
argument_list|(
name|output1
argument_list|,
name|output2
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|common
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|common
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|pair1
parameter_list|,
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|pair2
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|outputs1
operator|.
name|common
argument_list|(
name|pair1
operator|.
name|output1
argument_list|,
name|pair2
operator|.
name|output1
argument_list|)
argument_list|,
name|outputs2
operator|.
name|common
argument_list|(
name|pair1
operator|.
name|output2
argument_list|,
name|pair2
operator|.
name|output2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|subtract
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|subtract
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|,
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|inc
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|outputs1
operator|.
name|subtract
argument_list|(
name|output
operator|.
name|output1
argument_list|,
name|inc
operator|.
name|output1
argument_list|)
argument_list|,
name|outputs2
operator|.
name|subtract
argument_list|(
name|output
operator|.
name|output2
argument_list|,
name|inc
operator|.
name|output2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|add
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|add
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|prefix
parameter_list|,
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|outputs1
operator|.
name|add
argument_list|(
name|prefix
operator|.
name|output1
argument_list|,
name|output
operator|.
name|output1
argument_list|)
argument_list|,
name|outputs2
operator|.
name|add
argument_list|(
name|prefix
operator|.
name|output2
argument_list|,
name|output
operator|.
name|output2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|write
specifier|public
name|void
name|write
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|,
name|DataOutput
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
name|outputs1
operator|.
name|write
argument_list|(
name|output
operator|.
name|output1
argument_list|,
name|writer
argument_list|)
expr_stmt|;
name|outputs2
operator|.
name|write
argument_list|(
name|output
operator|.
name|output2
argument_list|,
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|A
name|output1
init|=
name|outputs1
operator|.
name|read
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|B
name|output2
init|=
name|outputs2
operator|.
name|read
argument_list|(
name|in
argument_list|)
decl_stmt|;
return|return
name|get
argument_list|(
name|output1
argument_list|,
name|output2
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getNoOutput
specifier|public
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|getNoOutput
parameter_list|()
block|{
return|return
name|NO_OUTPUT
return|;
block|}
annotation|@
name|Override
DECL|method|outputToString
specifier|public
name|String
name|outputToString
parameter_list|(
name|Pair
argument_list|<
name|A
argument_list|,
name|B
argument_list|>
name|output
parameter_list|)
block|{
return|return
literal|"<pair:"
operator|+
name|outputs1
operator|.
name|outputToString
argument_list|(
name|output
operator|.
name|output1
argument_list|)
operator|+
literal|","
operator|+
name|outputs2
operator|.
name|outputToString
argument_list|(
name|output
operator|.
name|output2
argument_list|)
operator|+
literal|">"
return|;
block|}
block|}
end_class
end_unit
