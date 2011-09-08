begin_unit
begin_package
DECL|package|org.apache.lucene.search.vectorhighlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|vectorhighlight
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
begin_class
DECL|class|SimpleBoundaryScanner
specifier|public
class|class
name|SimpleBoundaryScanner
implements|implements
name|BoundaryScanner
block|{
DECL|field|DEFAULT_MAX_SCAN
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_SCAN
init|=
literal|20
decl_stmt|;
DECL|field|DEFAULT_BOUNDARY_CHARS
specifier|public
specifier|static
specifier|final
name|Character
index|[]
name|DEFAULT_BOUNDARY_CHARS
init|=
block|{
literal|'.'
block|,
literal|','
block|,
literal|'!'
block|,
literal|'?'
block|,
literal|' '
block|,
literal|'\t'
block|,
literal|'\n'
block|}
decl_stmt|;
DECL|field|maxScan
specifier|protected
name|int
name|maxScan
decl_stmt|;
DECL|field|boundaryChars
specifier|protected
name|Set
argument_list|<
name|Character
argument_list|>
name|boundaryChars
decl_stmt|;
DECL|method|SimpleBoundaryScanner
specifier|public
name|SimpleBoundaryScanner
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_MAX_SCAN
argument_list|,
name|DEFAULT_BOUNDARY_CHARS
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleBoundaryScanner
specifier|public
name|SimpleBoundaryScanner
parameter_list|(
name|int
name|maxScan
parameter_list|)
block|{
name|this
argument_list|(
name|maxScan
argument_list|,
name|DEFAULT_BOUNDARY_CHARS
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleBoundaryScanner
specifier|public
name|SimpleBoundaryScanner
parameter_list|(
name|Character
index|[]
name|boundaryChars
parameter_list|)
block|{
name|this
argument_list|(
name|DEFAULT_MAX_SCAN
argument_list|,
name|boundaryChars
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleBoundaryScanner
specifier|public
name|SimpleBoundaryScanner
parameter_list|(
name|int
name|maxScan
parameter_list|,
name|Character
index|[]
name|boundaryChars
parameter_list|)
block|{
name|this
operator|.
name|maxScan
operator|=
name|maxScan
expr_stmt|;
name|this
operator|.
name|boundaryChars
operator|=
operator|new
name|HashSet
argument_list|<
name|Character
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|boundaryChars
operator|.
name|addAll
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|boundaryChars
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|SimpleBoundaryScanner
specifier|public
name|SimpleBoundaryScanner
parameter_list|(
name|int
name|maxScan
parameter_list|,
name|Set
argument_list|<
name|Character
argument_list|>
name|boundaryChars
parameter_list|)
block|{
name|this
operator|.
name|maxScan
operator|=
name|maxScan
expr_stmt|;
name|this
operator|.
name|boundaryChars
operator|=
name|boundaryChars
expr_stmt|;
block|}
DECL|method|findStartOffset
specifier|public
name|int
name|findStartOffset
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
name|start
parameter_list|)
block|{
comment|// avoid illegal start offset
if|if
condition|(
name|start
operator|>
name|buffer
operator|.
name|length
argument_list|()
operator|||
name|start
operator|<
literal|1
condition|)
return|return
name|start
return|;
name|int
name|offset
decl_stmt|,
name|count
init|=
name|maxScan
decl_stmt|;
for|for
control|(
name|offset
operator|=
name|start
init|;
name|offset
operator|>
literal|0
operator|&&
name|count
operator|>
literal|0
condition|;
name|count
operator|--
control|)
block|{
comment|// found?
if|if
condition|(
name|boundaryChars
operator|.
name|contains
argument_list|(
name|buffer
operator|.
name|charAt
argument_list|(
name|offset
operator|-
literal|1
argument_list|)
argument_list|)
condition|)
return|return
name|offset
return|;
name|offset
operator|--
expr_stmt|;
block|}
comment|// not found
return|return
name|start
return|;
block|}
DECL|method|findEndOffset
specifier|public
name|int
name|findEndOffset
parameter_list|(
name|StringBuilder
name|buffer
parameter_list|,
name|int
name|start
parameter_list|)
block|{
comment|// avoid illegal start offset
if|if
condition|(
name|start
operator|>
name|buffer
operator|.
name|length
argument_list|()
operator|||
name|start
operator|<
literal|0
condition|)
return|return
name|start
return|;
name|int
name|offset
decl_stmt|,
name|count
init|=
name|maxScan
decl_stmt|;
comment|//for( offset = start; offset<= buffer.length()&& count> 0; count-- ){
for|for
control|(
name|offset
operator|=
name|start
init|;
name|offset
argument_list|<
name|buffer
operator|.
name|length
operator|(
operator|)
operator|&&
name|count
argument_list|>
literal|0
condition|;
name|count
operator|--
control|)
block|{
comment|// found?
if|if
condition|(
name|boundaryChars
operator|.
name|contains
argument_list|(
name|buffer
operator|.
name|charAt
argument_list|(
name|offset
argument_list|)
argument_list|)
condition|)
return|return
name|offset
return|;
name|offset
operator|++
expr_stmt|;
block|}
comment|// not found
return|return
name|start
return|;
block|}
block|}
end_class
end_unit
