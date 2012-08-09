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
comment|/**  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|ByteBlockPool
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
name|RecyclingByteBlockAllocator
import|;
end_import
begin_class
DECL|class|TestByteSlices
specifier|public
class|class
name|TestByteSlices
extends|extends
name|LuceneTestCase
block|{
DECL|method|testBasic
specifier|public
name|void
name|testBasic
parameter_list|()
throws|throws
name|Throwable
block|{
name|ByteBlockPool
name|pool
init|=
operator|new
name|ByteBlockPool
argument_list|(
operator|new
name|RecyclingByteBlockAllocator
argument_list|(
name|ByteBlockPool
operator|.
name|BYTE_BLOCK_SIZE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|int
name|NUM_STREAM
init|=
name|atLeast
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|ByteSliceWriter
name|writer
init|=
operator|new
name|ByteSliceWriter
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|int
index|[]
name|starts
init|=
operator|new
name|int
index|[
name|NUM_STREAM
index|]
decl_stmt|;
name|int
index|[]
name|uptos
init|=
operator|new
name|int
index|[
name|NUM_STREAM
index|]
decl_stmt|;
name|int
index|[]
name|counters
init|=
operator|new
name|int
index|[
name|NUM_STREAM
index|]
decl_stmt|;
name|ByteSliceReader
name|reader
init|=
operator|new
name|ByteSliceReader
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ti
init|=
literal|0
init|;
name|ti
operator|<
literal|100
condition|;
name|ti
operator|++
control|)
block|{
for|for
control|(
name|int
name|stream
init|=
literal|0
init|;
name|stream
operator|<
name|NUM_STREAM
condition|;
name|stream
operator|++
control|)
block|{
name|starts
index|[
name|stream
index|]
operator|=
operator|-
literal|1
expr_stmt|;
name|counters
index|[
name|stream
index|]
operator|=
literal|0
expr_stmt|;
block|}
name|int
name|num
init|=
name|atLeast
argument_list|(
literal|3000
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|iter
init|=
literal|0
init|;
name|iter
operator|<
name|num
condition|;
name|iter
operator|++
control|)
block|{
name|int
name|stream
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|stream
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|stream
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
name|NUM_STREAM
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"write stream="
operator|+
name|stream
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|starts
index|[
name|stream
index|]
operator|==
operator|-
literal|1
condition|)
block|{
specifier|final
name|int
name|spot
init|=
name|pool
operator|.
name|newSlice
argument_list|(
name|ByteBlockPool
operator|.
name|FIRST_LEVEL_SIZE
argument_list|)
decl_stmt|;
name|starts
index|[
name|stream
index|]
operator|=
name|uptos
index|[
name|stream
index|]
operator|=
name|spot
operator|+
name|pool
operator|.
name|byteOffset
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  init to "
operator|+
name|starts
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|writer
operator|.
name|init
argument_list|(
name|uptos
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
name|int
name|numValue
decl_stmt|;
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|==
literal|3
condition|)
block|{
name|numValue
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|5
argument_list|)
operator|==
literal|3
condition|)
block|{
name|numValue
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|numValue
operator|=
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numValue
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    write "
operator|+
operator|(
name|counters
index|[
name|stream
index|]
operator|+
name|j
operator|)
argument_list|)
expr_stmt|;
block|}
comment|// write some large (incl. negative) ints:
name|writer
operator|.
name|writeVInt
argument_list|(
name|random
argument_list|()
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|writeVInt
argument_list|(
name|counters
index|[
name|stream
index|]
operator|+
name|j
argument_list|)
expr_stmt|;
block|}
name|counters
index|[
name|stream
index|]
operator|+=
name|numValue
expr_stmt|;
name|uptos
index|[
name|stream
index|]
operator|=
name|writer
operator|.
name|getAddress
argument_list|()
expr_stmt|;
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"    addr now "
operator|+
name|uptos
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|stream
init|=
literal|0
init|;
name|stream
operator|<
name|NUM_STREAM
condition|;
name|stream
operator|++
control|)
block|{
if|if
condition|(
name|VERBOSE
condition|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"  stream="
operator|+
name|stream
operator|+
literal|" count="
operator|+
name|counters
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|starts
index|[
name|stream
index|]
operator|!=
operator|-
literal|1
operator|&&
name|starts
index|[
name|stream
index|]
operator|!=
name|uptos
index|[
name|stream
index|]
condition|)
block|{
name|reader
operator|.
name|init
argument_list|(
name|pool
argument_list|,
name|starts
index|[
name|stream
index|]
argument_list|,
name|uptos
index|[
name|stream
index|]
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|counters
index|[
name|stream
index|]
condition|;
name|j
operator|++
control|)
block|{
name|reader
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|j
argument_list|,
name|reader
operator|.
name|readVInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|pool
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
