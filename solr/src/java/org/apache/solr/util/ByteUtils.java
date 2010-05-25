begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
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
name|util
operator|.
name|ArrayUtil
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
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|noggit
operator|.
name|CharArr
import|;
end_import
begin_class
DECL|class|ByteUtils
specifier|public
class|class
name|ByteUtils
block|{
comment|/** Converts utf8 to utf16 and returns the number of 16 bit Java chars written.    * Full characters are read, even if this reads past the length passed (and can result in    * an exception if invalid UTF8 is passed).    * The char[] out should probably have enough room to hold the worst case of each byte becoming a Java char. */
DECL|method|UTF8toUTF16
specifier|public
specifier|static
name|int
name|UTF8toUTF16
parameter_list|(
name|byte
index|[]
name|utf8
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|,
name|char
index|[]
name|out
parameter_list|,
name|int
name|out_offset
parameter_list|)
block|{
name|int
name|out_start
init|=
name|out_offset
decl_stmt|;
while|while
condition|(
name|offset
operator|<
name|len
condition|)
block|{
name|int
name|b
init|=
name|utf8
index|[
name|offset
operator|++
index|]
operator|&
literal|0xff
decl_stmt|;
if|if
condition|(
name|b
operator|<
literal|0xc0
condition|)
block|{
assert|assert
name|b
operator|<
literal|0x80
assert|;
name|out
index|[
name|out_offset
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|b
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|<
literal|0xe0
condition|)
block|{
name|out
index|[
name|out_offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x1f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|utf8
index|[
name|offset
operator|++
index|]
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|b
operator|<
literal|0xf0
condition|)
block|{
name|out
index|[
name|out_offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0xf
operator|)
operator|<<
literal|12
operator|)
operator|+
operator|(
operator|(
name|utf8
index|[
name|offset
index|]
operator|&
literal|0x3f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|utf8
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0x3f
operator|)
argument_list|)
expr_stmt|;
name|offset
operator|+=
literal|2
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|b
operator|<
literal|0xf8
assert|;
name|int
name|ch
init|=
operator|(
operator|(
name|b
operator|&
literal|0x7
operator|)
operator|<<
literal|18
operator|)
operator|+
operator|(
operator|(
name|utf8
index|[
name|offset
index|]
operator|&
literal|0x3f
operator|)
operator|<<
literal|12
operator|)
operator|+
operator|(
operator|(
name|utf8
index|[
name|offset
operator|+
literal|1
index|]
operator|&
literal|0x3f
operator|)
operator|<<
literal|6
operator|)
operator|+
operator|(
name|utf8
index|[
name|offset
operator|+
literal|2
index|]
operator|&
literal|0x3f
operator|)
decl_stmt|;
name|offset
operator|+=
literal|3
expr_stmt|;
if|if
condition|(
name|ch
operator|<
literal|0xffff
condition|)
block|{
name|out
index|[
name|out_offset
operator|++
index|]
operator|=
operator|(
name|char
operator|)
name|ch
expr_stmt|;
block|}
else|else
block|{
name|int
name|chHalf
init|=
name|ch
operator|-
literal|0x0010000
decl_stmt|;
name|out
index|[
name|out_offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|chHalf
operator|>>
literal|10
operator|)
operator|+
literal|0xD800
argument_list|)
expr_stmt|;
name|out
index|[
name|out_offset
operator|++
index|]
operator|=
call|(
name|char
call|)
argument_list|(
operator|(
name|chHalf
operator|&
literal|0x3FFL
operator|)
operator|+
literal|0xDC00
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|out_offset
operator|-
name|out_start
return|;
block|}
comment|/** Convert UTF8 bytes into UTF16 characters. */
DECL|method|UTF8toUTF16
specifier|public
specifier|static
name|void
name|UTF8toUTF16
parameter_list|(
name|BytesRef
name|utf8
parameter_list|,
name|CharArr
name|out
parameter_list|)
block|{
comment|// TODO: do in chunks if the input is large
name|out
operator|.
name|reserve
argument_list|(
name|utf8
operator|.
name|length
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|UTF8toUTF16
argument_list|(
name|utf8
operator|.
name|bytes
argument_list|,
name|utf8
operator|.
name|offset
argument_list|,
name|utf8
operator|.
name|length
argument_list|,
name|out
operator|.
name|getArray
argument_list|()
argument_list|,
name|out
operator|.
name|getEnd
argument_list|()
argument_list|)
decl_stmt|;
name|out
operator|.
name|setEnd
argument_list|(
name|out
operator|.
name|getEnd
argument_list|()
operator|+
name|n
argument_list|)
expr_stmt|;
block|}
comment|/** Convert UTF8 bytes into a String */
DECL|method|UTF8toUTF16
specifier|public
specifier|static
name|String
name|UTF8toUTF16
parameter_list|(
name|BytesRef
name|utf8
parameter_list|)
block|{
name|char
index|[]
name|out
init|=
operator|new
name|char
index|[
name|utf8
operator|.
name|length
index|]
decl_stmt|;
name|int
name|n
init|=
name|UTF8toUTF16
argument_list|(
name|utf8
operator|.
name|bytes
argument_list|,
name|utf8
operator|.
name|offset
argument_list|,
name|utf8
operator|.
name|length
argument_list|,
name|out
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|out
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
block|}
end_class
end_unit
