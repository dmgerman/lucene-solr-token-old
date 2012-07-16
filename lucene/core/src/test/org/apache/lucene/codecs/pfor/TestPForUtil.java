begin_unit
begin_package
DECL|package|org.apache.lucene.codecs.pfor
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pfor
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import
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
name|Random
import|;
end_import
begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|IntBuffer
import|;
end_import
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|pfor
operator|.
name|PForUtil
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
name|codecs
operator|.
name|pfor
operator|.
name|ForPostingsFormat
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
name|LuceneTestCase
import|;
end_import
begin_comment
comment|/**  * Test the core utility for PFor compress and decompress  * We don't specially provide test case for For encoder/decoder, since  * PFor is a extended version of For, and most methods will be reused   * here.  */
end_comment
begin_class
DECL|class|TestPForUtil
specifier|public
class|class
name|TestPForUtil
extends|extends
name|LuceneTestCase
block|{
DECL|field|MASK
specifier|static
specifier|final
name|int
index|[]
name|MASK
init|=
block|{
literal|0x00000000
block|,
literal|0x00000001
block|,
literal|0x00000003
block|,
literal|0x00000007
block|,
literal|0x0000000f
block|,
literal|0x0000001f
block|,
literal|0x0000003f
block|,
literal|0x0000007f
block|,
literal|0x000000ff
block|,
literal|0x000001ff
block|,
literal|0x000003ff
block|,
literal|0x000007ff
block|,
literal|0x00000fff
block|,
literal|0x00001fff
block|,
literal|0x00003fff
block|,
literal|0x00007fff
block|,
literal|0x0000ffff
block|,
literal|0x0001ffff
block|,
literal|0x0003ffff
block|,
literal|0x0007ffff
block|,
literal|0x000fffff
block|,
literal|0x001fffff
block|,
literal|0x003fffff
block|,
literal|0x007fffff
block|,
literal|0x00ffffff
block|,
literal|0x01ffffff
block|,
literal|0x03ffffff
block|,
literal|0x07ffffff
block|,
literal|0x0fffffff
block|,
literal|0x1fffffff
block|,
literal|0x3fffffff
block|,
literal|0x7fffffff
block|,
literal|0xffffffff
block|}
decl_stmt|;
DECL|field|gen
name|Random
name|gen
decl_stmt|;
DECL|method|initRandom
specifier|public
name|void
name|initRandom
parameter_list|()
block|{
name|this
operator|.
name|gen
operator|=
name|random
argument_list|()
expr_stmt|;
block|}
comment|/**    * Should not encode extra information other than single int    */
DECL|method|testAllEqual
specifier|public
name|void
name|testAllEqual
parameter_list|()
throws|throws
name|Exception
block|{
name|initRandom
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|ForPostingsFormat
operator|.
name|DEFAULT_BLOCK_SIZE
decl_stmt|;
name|int
index|[]
name|data
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|byte
index|[]
name|res
init|=
operator|new
name|byte
index|[
name|sz
operator|*
literal|8
index|]
decl_stmt|;
name|int
index|[]
name|copy
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|IntBuffer
name|resBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|res
argument_list|)
operator|.
name|asIntBuffer
argument_list|()
decl_stmt|;
name|int
name|ensz
decl_stmt|;
name|int
name|header
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
name|gen
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|=
name|ForUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|resBuffer
argument_list|)
expr_stmt|;
comment|// test For
name|ensz
operator|=
name|ForUtil
operator|.
name|getEncodedSize
argument_list|(
name|header
argument_list|)
expr_stmt|;
assert|assert
name|ensz
operator|==
literal|4
assert|;
name|ForUtil
operator|.
name|decompress
argument_list|(
name|resBuffer
argument_list|,
name|copy
argument_list|,
name|header
argument_list|)
expr_stmt|;
assert|assert
name|cmp
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
name|copy
argument_list|,
name|sz
argument_list|)
operator|==
literal|true
assert|;
name|Arrays
operator|.
name|fill
argument_list|(
name|data
argument_list|,
name|gen
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|header
operator|=
name|PForUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|resBuffer
argument_list|)
expr_stmt|;
comment|// test PFor
name|ensz
operator|=
name|PForUtil
operator|.
name|getEncodedSize
argument_list|(
name|header
argument_list|)
expr_stmt|;
assert|assert
name|ensz
operator|==
literal|4
assert|;
name|PForUtil
operator|.
name|decompress
argument_list|(
name|resBuffer
argument_list|,
name|copy
argument_list|,
name|header
argument_list|)
expr_stmt|;
assert|assert
name|cmp
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
name|copy
argument_list|,
name|sz
argument_list|)
operator|==
literal|true
assert|;
block|}
comment|/**    * Test correctness of forced exception.    * the forced ones should exactly fit max chain     */
DECL|method|testForcedExceptionDistance
specifier|public
name|void
name|testForcedExceptionDistance
parameter_list|()
throws|throws
name|Exception
block|{
name|initRandom
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|ForPostingsFormat
operator|.
name|DEFAULT_BLOCK_SIZE
decl_stmt|;
name|int
index|[]
name|data
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|byte
index|[]
name|res
init|=
operator|new
name|byte
index|[
name|sz
operator|*
literal|8
index|]
decl_stmt|;
name|int
index|[]
name|copy
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|IntBuffer
name|resBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|res
argument_list|)
operator|.
name|asIntBuffer
argument_list|()
decl_stmt|;
name|int
name|numBits
init|=
name|gen
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|+
literal|1
decl_stmt|;
name|int
name|i
decl_stmt|,
name|j
decl_stmt|;
name|int
name|pace
decl_stmt|,
name|ensz
decl_stmt|,
name|header
decl_stmt|;
name|int
name|expect
decl_stmt|,
name|got
decl_stmt|;
comment|// fill exception value with same pace, there should
comment|// be no forced exceptions.
name|createDistribution
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
literal|1
argument_list|,
name|MASK
index|[
name|numBits
index|]
argument_list|,
name|MASK
index|[
name|numBits
index|]
argument_list|)
expr_stmt|;
name|pace
operator|=
literal|1
operator|<<
name|numBits
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
operator|,
name|j
operator|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|+=
name|pace
control|)
block|{
name|int
name|exc
init|=
name|gen
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|data
index|[
name|i
index|]
operator|=
operator|(
name|exc
operator|&
literal|0xffff0000
operator|)
operator|==
literal|0
condition|?
name|exc
operator||
literal|0xffff0000
else|:
name|exc
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
name|header
operator|=
name|PForUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|resBuffer
argument_list|)
expr_stmt|;
name|ensz
operator|=
name|PForUtil
operator|.
name|getEncodedSize
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|expect
operator|=
name|j
expr_stmt|;
name|got
operator|=
name|PForUtil
operator|.
name|getExcNum
argument_list|(
name|header
argument_list|)
expr_stmt|;
assert|assert
name|expect
operator|==
name|got
operator|:
name|expect
operator|+
literal|" expected but got "
operator|+
name|got
assert|;
comment|// there should exactly one forced exception before each
comment|// exception when i>0
name|createDistribution
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
literal|1
argument_list|,
name|MASK
index|[
name|numBits
index|]
argument_list|,
name|MASK
index|[
name|numBits
index|]
argument_list|)
expr_stmt|;
name|pace
operator|=
operator|(
literal|1
operator|<<
name|numBits
operator|)
operator|+
literal|1
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
operator|,
name|j
operator|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|+=
name|pace
control|)
block|{
name|int
name|exc
init|=
name|gen
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|data
index|[
name|i
index|]
operator|=
operator|(
name|exc
operator|&
literal|0xffff0000
operator|)
operator|==
literal|0
condition|?
name|exc
operator||
literal|0xffff0000
else|:
name|exc
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
name|header
operator|=
name|PForUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|resBuffer
argument_list|)
expr_stmt|;
name|ensz
operator|=
name|PForUtil
operator|.
name|getEncodedSize
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|expect
operator|=
literal|2
operator|*
operator|(
name|j
operator|-
literal|1
operator|)
operator|+
literal|1
expr_stmt|;
name|got
operator|=
name|PForUtil
operator|.
name|getExcNum
argument_list|(
name|header
argument_list|)
expr_stmt|;
assert|assert
name|expect
operator|==
name|got
operator|:
name|expect
operator|+
literal|" expected but got "
operator|+
name|got
assert|;
comment|// two forced exception
name|createDistribution
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
literal|1
argument_list|,
name|MASK
index|[
name|numBits
index|]
argument_list|,
name|MASK
index|[
name|numBits
index|]
argument_list|)
expr_stmt|;
name|pace
operator|=
operator|(
literal|1
operator|<<
name|numBits
operator|)
operator|*
literal|2
operator|+
literal|1
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
operator|,
name|j
operator|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|+=
name|pace
control|)
block|{
name|int
name|exc
init|=
name|gen
operator|.
name|nextInt
argument_list|()
decl_stmt|;
name|data
index|[
name|i
index|]
operator|=
operator|(
name|exc
operator|&
literal|0xffff0000
operator|)
operator|==
literal|0
condition|?
name|exc
operator||
literal|0xffff0000
else|:
name|exc
expr_stmt|;
name|j
operator|++
expr_stmt|;
block|}
name|header
operator|=
name|PForUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|resBuffer
argument_list|)
expr_stmt|;
name|ensz
operator|=
name|PForUtil
operator|.
name|getEncodedSize
argument_list|(
name|header
argument_list|)
expr_stmt|;
name|expect
operator|=
literal|3
operator|*
operator|(
name|j
operator|-
literal|1
operator|)
operator|+
literal|1
expr_stmt|;
name|got
operator|=
name|PForUtil
operator|.
name|getExcNum
argument_list|(
name|header
argument_list|)
expr_stmt|;
assert|assert
name|expect
operator|==
name|got
operator|:
name|expect
operator|+
literal|" expected but got "
operator|+
name|got
assert|;
block|}
comment|/**    * Test correctness of ignored forced exception.    * The trailing forced exceptions should always be reverted    * since they're not necessary.     */
DECL|method|testTrailingForcedException
specifier|public
name|void
name|testTrailingForcedException
parameter_list|()
throws|throws
name|Exception
block|{
name|initRandom
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|ForPostingsFormat
operator|.
name|DEFAULT_BLOCK_SIZE
decl_stmt|;
assert|assert
name|sz
operator|%
literal|32
operator|==
literal|0
assert|;
name|Integer
index|[]
name|buff
init|=
operator|new
name|Integer
index|[
name|sz
index|]
decl_stmt|;
name|int
index|[]
name|data
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|int
index|[]
name|copy
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|byte
index|[]
name|res
init|=
operator|new
name|byte
index|[
name|sz
operator|*
literal|8
index|]
decl_stmt|;
name|IntBuffer
name|resBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|res
argument_list|)
operator|.
name|asIntBuffer
argument_list|()
decl_stmt|;
name|int
name|excIndex
init|=
name|gen
operator|.
name|nextInt
argument_list|(
name|sz
operator|/
literal|2
argument_list|)
decl_stmt|;
name|int
name|excValue
init|=
name|gen
operator|.
name|nextInt
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|excValue
operator|&
literal|0xffff0000
operator|)
operator|==
literal|0
condition|)
block|{
name|excValue
operator||=
literal|0xffff0000
expr_stmt|;
comment|// always prepare a 4 bytes exception
block|}
comment|// make value of numFrameBits to be small,
comment|// thus easy to get forced exceptions
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
operator|++
name|i
control|)
block|{
name|buff
index|[
name|i
index|]
operator|=
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
literal|1
expr_stmt|;
block|}
comment|// create only one value exception
name|buff
index|[
name|excIndex
index|]
operator|=
name|excValue
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
operator|++
name|i
control|)
name|data
index|[
name|i
index|]
operator|=
name|buff
index|[
name|i
index|]
expr_stmt|;
name|int
name|header
init|=
name|PForUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|resBuffer
argument_list|)
decl_stmt|;
name|int
name|ensz
init|=
name|PForUtil
operator|.
name|getEncodedSize
argument_list|(
name|header
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|ensz
operator|<=
name|sz
operator|*
literal|8
operator|)
operator|:
name|ensz
operator|+
literal|"> "
operator|+
name|sz
operator|*
literal|8
assert|;
comment|// must not exceed the loose upperbound
assert|assert
operator|(
name|ensz
operator|>=
literal|4
operator|)
assert|;
comment|// at least we have an exception, right?
name|PForUtil
operator|.
name|decompress
argument_list|(
name|resBuffer
argument_list|,
name|copy
argument_list|,
name|header
argument_list|)
expr_stmt|;
comment|//    println(getHex(data,sz)+"\n");
comment|//    println(getHex(res,ensz)+"\n");
comment|//    println(getHex(copy,sz)+"\n");
comment|// fetch the last int, i.e. last exception.
name|int
name|lastExc
init|=
operator|(
name|res
index|[
name|ensz
operator|-
literal|4
index|]
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
literal|0xff
operator|&
name|res
index|[
name|ensz
operator|-
literal|3
index|]
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
literal|0xff
operator|&
name|res
index|[
name|ensz
operator|-
literal|2
index|]
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
literal|0xff
operator|&
name|res
index|[
name|ensz
operator|-
literal|1
index|]
operator|)
decl_stmt|;
comment|// trailing forced exceptions are suppressed,
comment|// so the last exception should be what we assigned.
assert|assert
name|lastExc
operator|==
name|excValue
assert|;
assert|assert
name|cmp
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
name|copy
argument_list|,
name|sz
argument_list|)
operator|==
literal|true
assert|;
block|}
comment|/**    * Test correctness of compressing and decompressing.    * Here we randomly assign a rate of exception (i.e. 1-alpha),     * and test different scale of normal/exception values.    */
DECL|method|testAllDistribution
specifier|public
name|void
name|testAllDistribution
parameter_list|()
throws|throws
name|Exception
block|{
name|initRandom
argument_list|()
expr_stmt|;
name|int
name|sz
init|=
name|ForPostingsFormat
operator|.
name|DEFAULT_BLOCK_SIZE
decl_stmt|;
name|int
index|[]
name|data
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
literal|32
condition|;
operator|++
name|i
control|)
block|{
comment|// try to test every kinds of distribution
name|double
name|alpha
init|=
name|gen
operator|.
name|nextDouble
argument_list|()
decl_stmt|;
comment|// rate of normal value
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<=
literal|32
condition|;
operator|++
name|j
control|)
block|{
name|createDistribution
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
name|alpha
argument_list|,
name|MASK
index|[
name|i
index|]
argument_list|,
name|MASK
index|[
name|j
index|]
argument_list|)
expr_stmt|;
name|tryCompressAndDecompress
argument_list|(
name|data
argument_list|,
name|sz
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|createDistribution
specifier|public
name|void
name|createDistribution
parameter_list|(
name|int
index|[]
name|data
parameter_list|,
name|int
name|sz
parameter_list|,
name|double
name|alpha
parameter_list|,
name|int
name|masknorm
parameter_list|,
name|int
name|maskexc
parameter_list|)
block|{
name|Integer
index|[]
name|buff
init|=
operator|new
name|Integer
index|[
name|sz
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|sz
operator|*
name|alpha
condition|;
operator|++
name|i
control|)
name|buff
index|[
name|i
index|]
operator|=
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
name|masknorm
expr_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|sz
condition|;
operator|++
name|i
control|)
name|buff
index|[
name|i
index|]
operator|=
name|gen
operator|.
name|nextInt
argument_list|()
operator|&
name|maskexc
expr_stmt|;
name|Collections
operator|.
name|shuffle
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|buff
argument_list|)
argument_list|,
name|gen
argument_list|)
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
operator|++
name|i
control|)
name|data
index|[
name|i
index|]
operator|=
name|buff
index|[
name|i
index|]
expr_stmt|;
block|}
DECL|method|tryCompressAndDecompress
specifier|public
name|void
name|tryCompressAndDecompress
parameter_list|(
specifier|final
name|int
index|[]
name|data
parameter_list|,
name|int
name|sz
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
index|[]
name|res
init|=
operator|new
name|byte
index|[
name|sz
operator|*
literal|8
index|]
decl_stmt|;
comment|// loosely upperbound
name|IntBuffer
name|resBuffer
init|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|res
argument_list|)
operator|.
name|asIntBuffer
argument_list|()
decl_stmt|;
name|int
name|header
init|=
name|PForUtil
operator|.
name|compress
argument_list|(
name|data
argument_list|,
name|resBuffer
argument_list|)
decl_stmt|;
name|int
name|ensz
init|=
name|PForUtil
operator|.
name|getEncodedSize
argument_list|(
name|header
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|ensz
operator|<=
name|sz
operator|*
literal|8
operator|)
assert|;
comment|// must not exceed the loose upperbound
name|int
index|[]
name|copy
init|=
operator|new
name|int
index|[
name|sz
index|]
decl_stmt|;
name|PForUtil
operator|.
name|decompress
argument_list|(
name|resBuffer
argument_list|,
name|copy
argument_list|,
name|header
argument_list|)
expr_stmt|;
comment|//    println(getHex(data,sz)+"\n");
comment|//    println(getHex(res,ensz)+"\n");
comment|//    println(getHex(copy,sz)+"\n");
assert|assert
name|cmp
argument_list|(
name|data
argument_list|,
name|sz
argument_list|,
name|copy
argument_list|,
name|sz
argument_list|)
operator|==
literal|true
assert|;
block|}
DECL|method|cmp
specifier|public
name|boolean
name|cmp
parameter_list|(
name|int
index|[]
name|a
parameter_list|,
name|int
name|sza
parameter_list|,
name|int
index|[]
name|b
parameter_list|,
name|int
name|szb
parameter_list|)
block|{
if|if
condition|(
name|sza
operator|!=
name|szb
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sza
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|a
index|[
name|i
index|]
operator|!=
name|b
index|[
name|i
index|]
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"! %08x != %08x in %d"
argument_list|,
name|a
index|[
name|i
index|]
argument_list|,
name|b
index|[
name|i
index|]
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
DECL|method|getHex
specifier|public
specifier|static
name|String
name|getHex
parameter_list|(
name|byte
index|[]
name|raw
parameter_list|,
name|int
name|sz
parameter_list|)
block|{
specifier|final
name|String
name|HEXES
init|=
literal|"0123456789ABCDEF"
decl_stmt|;
if|if
condition|(
name|raw
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|StringBuilder
name|hex
init|=
operator|new
name|StringBuilder
argument_list|(
literal|2
operator|*
name|raw
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
operator|(
name|i
operator|)
operator|%
literal|16
operator|==
literal|0
condition|)
name|hex
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|byte
name|b
init|=
name|raw
index|[
name|i
index|]
decl_stmt|;
name|hex
operator|.
name|append
argument_list|(
name|HEXES
operator|.
name|charAt
argument_list|(
operator|(
name|b
operator|&
literal|0xF0
operator|)
operator|>>
literal|4
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
name|HEXES
operator|.
name|charAt
argument_list|(
operator|(
name|b
operator|&
literal|0x0F
operator|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
return|return
name|hex
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getHex
specifier|public
specifier|static
name|String
name|getHex
parameter_list|(
name|int
index|[]
name|raw
parameter_list|,
name|int
name|sz
parameter_list|)
block|{
if|if
condition|(
name|raw
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|StringBuilder
name|hex
init|=
operator|new
name|StringBuilder
argument_list|(
literal|4
operator|*
name|raw
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sz
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|%
literal|8
operator|==
literal|0
condition|)
name|hex
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
name|hex
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%08x "
argument_list|,
name|raw
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|hex
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|eprintln
specifier|static
name|void
name|eprintln
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|println
specifier|static
name|void
name|println
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|print
specifier|static
name|void
name|print
parameter_list|(
name|String
name|format
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|format
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
