begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis.hunspell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|hunspell
package|;
end_package
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|CharBuffer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CoderResult
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import
begin_comment
comment|// many hunspell dictionaries use this encoding, yet java does not have it?!?!
end_comment
begin_class
DECL|class|ISO8859_14Decoder
specifier|final
class|class
name|ISO8859_14Decoder
extends|extends
name|CharsetDecoder
block|{
DECL|field|TABLE
specifier|static
specifier|final
name|char
name|TABLE
index|[]
init|=
operator|new
name|char
index|[]
block|{
literal|0x00A0
block|,
literal|0x1E02
block|,
literal|0x1E03
block|,
literal|0x00A3
block|,
literal|0x010A
block|,
literal|0x010B
block|,
literal|0x1E0A
block|,
literal|0x00A7
block|,
literal|0x1E80
block|,
literal|0x00A9
block|,
literal|0x1E82
block|,
literal|0x1E0B
block|,
literal|0x1EF2
block|,
literal|0x00AD
block|,
literal|0x00AE
block|,
literal|0x0178
block|,
literal|0x1E1E
block|,
literal|0x1E1F
block|,
literal|0x0120
block|,
literal|0x0121
block|,
literal|0x1E40
block|,
literal|0x1E41
block|,
literal|0x00B6
block|,
literal|0x1E56
block|,
literal|0x1E81
block|,
literal|0x1E57
block|,
literal|0x1E83
block|,
literal|0x1E60
block|,
literal|0x1EF3
block|,
literal|0x1E84
block|,
literal|0x1E85
block|,
literal|0x1E61
block|,
literal|0x00C0
block|,
literal|0x00C1
block|,
literal|0x00C2
block|,
literal|0x00C3
block|,
literal|0x00C4
block|,
literal|0x00C5
block|,
literal|0x00C6
block|,
literal|0x00C7
block|,
literal|0x00C8
block|,
literal|0x00C9
block|,
literal|0x00CA
block|,
literal|0x00CB
block|,
literal|0x00CC
block|,
literal|0x00CD
block|,
literal|0x00CE
block|,
literal|0x00CF
block|,
literal|0x0174
block|,
literal|0x00D1
block|,
literal|0x00D2
block|,
literal|0x00D3
block|,
literal|0x00D4
block|,
literal|0x00D5
block|,
literal|0x00D6
block|,
literal|0x1E6A
block|,
literal|0x00D8
block|,
literal|0x00D9
block|,
literal|0x00DA
block|,
literal|0x00DB
block|,
literal|0x00DC
block|,
literal|0x00DD
block|,
literal|0x0176
block|,
literal|0x00DF
block|,
literal|0x00E0
block|,
literal|0x00E1
block|,
literal|0x00E2
block|,
literal|0x00E3
block|,
literal|0x00E4
block|,
literal|0x00E5
block|,
literal|0x00E6
block|,
literal|0x00E7
block|,
literal|0x00E8
block|,
literal|0x00E9
block|,
literal|0x00EA
block|,
literal|0x00EB
block|,
literal|0x00EC
block|,
literal|0x00ED
block|,
literal|0x00EE
block|,
literal|0x00EF
block|,
literal|0x0175
block|,
literal|0x00F1
block|,
literal|0x00F2
block|,
literal|0x00F3
block|,
literal|0x00F4
block|,
literal|0x00F5
block|,
literal|0x00F6
block|,
literal|0x1E6B
block|,
literal|0x00F8
block|,
literal|0x00F9
block|,
literal|0x00FA
block|,
literal|0x00FB
block|,
literal|0x00FC
block|,
literal|0x00FD
block|,
literal|0x0177
block|,
literal|0x00FF
block|}
decl_stmt|;
DECL|method|ISO8859_14Decoder
name|ISO8859_14Decoder
parameter_list|()
block|{
name|super
argument_list|(
name|StandardCharsets
operator|.
name|ISO_8859_1
comment|/* fake with similar properties */
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|decodeLoop
specifier|protected
name|CoderResult
name|decodeLoop
parameter_list|(
name|ByteBuffer
name|in
parameter_list|,
name|CharBuffer
name|out
parameter_list|)
block|{
while|while
condition|(
name|in
operator|.
name|hasRemaining
argument_list|()
operator|&&
name|out
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
name|char
name|ch
init|=
call|(
name|char
call|)
argument_list|(
name|in
operator|.
name|get
argument_list|()
operator|&
literal|0xff
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|>=
literal|0xA0
condition|)
block|{
name|ch
operator|=
name|TABLE
index|[
name|ch
operator|-
literal|0xA0
index|]
expr_stmt|;
block|}
name|out
operator|.
name|put
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
return|return
name|in
operator|.
name|hasRemaining
argument_list|()
condition|?
name|CoderResult
operator|.
name|OVERFLOW
else|:
name|CoderResult
operator|.
name|UNDERFLOW
return|;
block|}
block|}
end_class
end_unit
