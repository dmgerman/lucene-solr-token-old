begin_unit
begin_package
DECL|package|org.apache.lucene.util.packed
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|packed
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
name|Random
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
name|NumericDocValues
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
name|ByteArrayDataInput
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
name|Directory
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
name|IOContext
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
name|IndexInput
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
name|IndexOutput
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
name|packed
operator|.
name|DirectReader
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
name|packed
operator|.
name|DirectWriter
import|;
end_import
begin_class
DECL|class|TestDirectPacked
specifier|public
class|class
name|TestDirectPacked
extends|extends
name|LuceneTestCase
block|{
comment|/** simple encode/decode */
DECL|method|testSimple
specifier|public
name|void
name|testSimple
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|int
name|bitsPerValue
init|=
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|DirectWriter
name|writer
init|=
name|DirectWriter
operator|.
name|getInstance
argument_list|(
name|output
argument_list|,
literal|5
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|dir
operator|.
name|openInput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|NumericDocValues
name|reader
init|=
name|DirectReader
operator|.
name|getInstance
argument_list|(
name|input
operator|.
name|randomAccessSlice
argument_list|(
literal|0
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|reader
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|reader
operator|.
name|get
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|reader
operator|.
name|get
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** test exception is delivered if you add the wrong number of values */
DECL|method|testNotEnoughValues
specifier|public
name|void
name|testNotEnoughValues
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
name|int
name|bitsPerValue
init|=
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|IndexOutput
name|output
init|=
name|dir
operator|.
name|createOutput
argument_list|(
literal|"foo"
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|DirectWriter
name|writer
init|=
name|DirectWriter
operator|.
name|getInstance
argument_list|(
name|output
argument_list|,
literal|5
argument_list|,
name|bitsPerValue
argument_list|)
decl_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|writer
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"didn't get expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|expected
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Wrong number of values added"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|testRandom
specifier|public
name|void
name|testRandom
parameter_list|()
throws|throws
name|Exception
block|{
name|Directory
name|dir
init|=
name|newDirectory
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|bpv
init|=
literal|1
init|;
name|bpv
operator|<=
literal|64
condition|;
name|bpv
operator|++
control|)
block|{
name|doTestBpv
argument_list|(
name|dir
argument_list|,
name|bpv
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|doTestBpv
specifier|private
name|void
name|doTestBpv
parameter_list|(
name|Directory
name|directory
parameter_list|,
name|int
name|bpv
parameter_list|)
throws|throws
name|Exception
block|{
name|MyRandom
name|random
init|=
operator|new
name|MyRandom
argument_list|(
name|random
argument_list|()
operator|.
name|nextLong
argument_list|()
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
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|long
name|original
index|[]
init|=
name|randomLongs
argument_list|(
name|random
argument_list|,
name|bpv
argument_list|)
decl_stmt|;
name|int
name|bitsRequired
init|=
name|bpv
operator|==
literal|64
condition|?
literal|64
else|:
name|DirectWriter
operator|.
name|bitsRequired
argument_list|(
literal|1L
operator|<<
operator|(
name|bpv
operator|-
literal|1
operator|)
argument_list|)
decl_stmt|;
name|String
name|name
init|=
literal|"bpv"
operator|+
name|bpv
operator|+
literal|"_"
operator|+
name|i
decl_stmt|;
name|IndexOutput
name|output
init|=
name|directory
operator|.
name|createOutput
argument_list|(
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|DirectWriter
name|writer
init|=
name|DirectWriter
operator|.
name|getInstance
argument_list|(
name|output
argument_list|,
name|original
operator|.
name|length
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|original
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|writer
operator|.
name|add
argument_list|(
name|original
index|[
name|j
index|]
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|finish
argument_list|()
expr_stmt|;
name|output
operator|.
name|close
argument_list|()
expr_stmt|;
name|IndexInput
name|input
init|=
name|directory
operator|.
name|openInput
argument_list|(
name|name
argument_list|,
name|IOContext
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|NumericDocValues
name|reader
init|=
name|DirectReader
operator|.
name|getInstance
argument_list|(
name|input
operator|.
name|randomAccessSlice
argument_list|(
literal|0
argument_list|,
name|input
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|bitsRequired
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|original
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"bpv="
operator|+
name|bpv
argument_list|,
name|original
index|[
name|j
index|]
argument_list|,
name|reader
operator|.
name|get
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|input
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|randomLongs
specifier|private
name|long
index|[]
name|randomLongs
parameter_list|(
name|MyRandom
name|random
parameter_list|,
name|int
name|bpv
parameter_list|)
block|{
name|int
name|amount
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|5000
argument_list|)
decl_stmt|;
name|long
name|longs
index|[]
init|=
operator|new
name|long
index|[
name|amount
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
operator|<
name|longs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|longs
index|[
name|i
index|]
operator|=
name|random
operator|.
name|nextLong
argument_list|(
name|bpv
argument_list|)
expr_stmt|;
block|}
return|return
name|longs
return|;
block|}
comment|// java.util.Random only returns 48bits of randomness in nextLong...
DECL|class|MyRandom
specifier|static
class|class
name|MyRandom
extends|extends
name|Random
block|{
DECL|field|buffer
name|byte
name|buffer
index|[]
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
DECL|field|input
name|ByteArrayDataInput
name|input
init|=
operator|new
name|ByteArrayDataInput
argument_list|()
decl_stmt|;
DECL|method|MyRandom
name|MyRandom
parameter_list|(
name|long
name|seed
parameter_list|)
block|{
name|super
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
DECL|method|nextLong
specifier|public
specifier|synchronized
name|long
name|nextLong
parameter_list|(
name|int
name|bpv
parameter_list|)
block|{
name|nextBytes
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|input
operator|.
name|reset
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|long
name|bits
init|=
name|input
operator|.
name|readLong
argument_list|()
decl_stmt|;
return|return
name|bits
operator|>>>
operator|(
literal|64
operator|-
name|bpv
operator|)
return|;
block|}
block|}
block|}
end_class
end_unit