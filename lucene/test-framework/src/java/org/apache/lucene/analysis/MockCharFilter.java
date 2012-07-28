begin_unit
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import
begin_comment
comment|/** the purpose of this charfilter is to send offsets out of bounds   if the analyzer doesn't use correctOffset or does incorrect offset math. */
end_comment
begin_class
DECL|class|MockCharFilter
specifier|public
class|class
name|MockCharFilter
extends|extends
name|CharFilter
block|{
DECL|field|remainder
specifier|final
name|int
name|remainder
decl_stmt|;
comment|// for testing only
DECL|method|MockCharFilter
specifier|public
name|MockCharFilter
parameter_list|(
name|Reader
name|in
parameter_list|,
name|int
name|remainder
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// TODO: instead of fixed remainder... maybe a fixed
comment|// random seed?
name|this
operator|.
name|remainder
operator|=
name|remainder
expr_stmt|;
if|if
condition|(
name|remainder
operator|<
literal|0
operator|||
name|remainder
operator|>=
literal|10
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"invalid remainder parameter (must be 0..10): "
operator|+
name|remainder
argument_list|)
throw|;
block|}
block|}
comment|// for testing only, uses a remainder of 0
DECL|method|MockCharFilter
specifier|public
name|MockCharFilter
parameter_list|(
name|Reader
name|in
parameter_list|)
block|{
name|this
argument_list|(
name|in
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|field|currentOffset
name|int
name|currentOffset
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|delta
name|int
name|delta
init|=
literal|0
decl_stmt|;
DECL|field|bufferedCh
name|int
name|bufferedCh
init|=
operator|-
literal|1
decl_stmt|;
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
comment|// we have a buffered character, add an offset correction and return it
if|if
condition|(
name|bufferedCh
operator|>=
literal|0
condition|)
block|{
name|int
name|ch
init|=
name|bufferedCh
decl_stmt|;
name|bufferedCh
operator|=
operator|-
literal|1
expr_stmt|;
name|currentOffset
operator|++
expr_stmt|;
name|addOffCorrectMap
argument_list|(
name|currentOffset
argument_list|,
name|delta
operator|-
literal|1
argument_list|)
expr_stmt|;
name|delta
operator|--
expr_stmt|;
return|return
name|ch
return|;
block|}
comment|// otherwise actually read one
name|int
name|ch
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|ch
operator|<
literal|0
condition|)
return|return
name|ch
return|;
name|currentOffset
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|ch
operator|%
literal|10
operator|)
operator|!=
name|remainder
operator|||
name|Character
operator|.
name|isHighSurrogate
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
operator|||
name|Character
operator|.
name|isLowSurrogate
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
condition|)
block|{
return|return
name|ch
return|;
block|}
comment|// we will double this character, so buffer it.
name|bufferedCh
operator|=
name|ch
expr_stmt|;
return|return
name|ch
return|;
block|}
annotation|@
name|Override
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|char
index|[]
name|cbuf
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|numRead
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|;
name|i
operator|<
name|off
operator|+
name|len
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|c
operator|==
operator|-
literal|1
condition|)
break|break;
name|cbuf
index|[
name|i
index|]
operator|=
operator|(
name|char
operator|)
name|c
expr_stmt|;
name|numRead
operator|++
expr_stmt|;
block|}
return|return
name|numRead
operator|==
literal|0
condition|?
operator|-
literal|1
else|:
name|numRead
return|;
block|}
annotation|@
name|Override
DECL|method|correct
specifier|public
name|int
name|correct
parameter_list|(
name|int
name|currentOff
parameter_list|)
block|{
name|SortedMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|subMap
init|=
name|corrections
operator|.
name|subMap
argument_list|(
literal|0
argument_list|,
name|currentOff
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
name|subMap
operator|.
name|isEmpty
argument_list|()
condition|?
name|currentOff
else|:
name|currentOff
operator|+
name|subMap
operator|.
name|get
argument_list|(
name|subMap
operator|.
name|lastKey
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|ret
operator|>=
literal|0
operator|:
literal|"currentOff="
operator|+
name|currentOff
operator|+
literal|",diff="
operator|+
operator|(
name|ret
operator|-
name|currentOff
operator|)
assert|;
return|return
name|ret
return|;
block|}
DECL|method|addOffCorrectMap
specifier|protected
name|void
name|addOffCorrectMap
parameter_list|(
name|int
name|off
parameter_list|,
name|int
name|cumulativeDiff
parameter_list|)
block|{
name|corrections
operator|.
name|put
argument_list|(
name|off
argument_list|,
name|cumulativeDiff
argument_list|)
expr_stmt|;
block|}
DECL|field|corrections
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|corrections
init|=
operator|new
name|TreeMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
block|}
end_class
end_unit
