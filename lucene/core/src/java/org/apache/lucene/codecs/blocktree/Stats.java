begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.codecs.blocktree
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|codecs
operator|.
name|blocktree
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|PostingsReaderBase
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
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import
begin_comment
comment|/**  * BlockTree statistics for a single field   * returned by {@link FieldReader#getStats()}.  * @lucene.internal  */
end_comment
begin_class
DECL|class|Stats
specifier|public
class|class
name|Stats
block|{
comment|/** Byte size of the index. */
DECL|field|indexNumBytes
specifier|public
name|long
name|indexNumBytes
decl_stmt|;
comment|/** Total number of terms in the field. */
DECL|field|totalTermCount
specifier|public
name|long
name|totalTermCount
decl_stmt|;
comment|/** Total number of bytes (sum of term lengths) across all terms in the field. */
DECL|field|totalTermBytes
specifier|public
name|long
name|totalTermBytes
decl_stmt|;
comment|// TODO: add total auto-prefix term count
comment|/** The number of normal (non-floor) blocks in the terms file. */
DECL|field|nonFloorBlockCount
specifier|public
name|int
name|nonFloorBlockCount
decl_stmt|;
comment|/** The number of floor blocks (meta-blocks larger than the    *  allowed {@code maxItemsPerBlock}) in the terms file. */
DECL|field|floorBlockCount
specifier|public
name|int
name|floorBlockCount
decl_stmt|;
comment|/** The number of sub-blocks within the floor blocks. */
DECL|field|floorSubBlockCount
specifier|public
name|int
name|floorSubBlockCount
decl_stmt|;
comment|/** The number of "internal" blocks (that have both    *  terms and sub-blocks). */
DECL|field|mixedBlockCount
specifier|public
name|int
name|mixedBlockCount
decl_stmt|;
comment|/** The number of "leaf" blocks (blocks that have only    *  terms). */
DECL|field|termsOnlyBlockCount
specifier|public
name|int
name|termsOnlyBlockCount
decl_stmt|;
comment|/** The number of "internal" blocks that do not contain    *  terms (have only sub-blocks). */
DECL|field|subBlocksOnlyBlockCount
specifier|public
name|int
name|subBlocksOnlyBlockCount
decl_stmt|;
comment|/** Total number of blocks. */
DECL|field|totalBlockCount
specifier|public
name|int
name|totalBlockCount
decl_stmt|;
comment|/** Number of blocks at each prefix depth. */
DECL|field|blockCountByPrefixLen
specifier|public
name|int
index|[]
name|blockCountByPrefixLen
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
DECL|field|startBlockCount
specifier|private
name|int
name|startBlockCount
decl_stmt|;
DECL|field|endBlockCount
specifier|private
name|int
name|endBlockCount
decl_stmt|;
comment|/** Total number of bytes used to store term suffixes. */
DECL|field|totalBlockSuffixBytes
specifier|public
name|long
name|totalBlockSuffixBytes
decl_stmt|;
comment|/** Total number of bytes used to store term stats (not    *  including what the {@link PostingsReaderBase}    *  stores. */
DECL|field|totalBlockStatsBytes
specifier|public
name|long
name|totalBlockStatsBytes
decl_stmt|;
comment|/** Total bytes stored by the {@link PostingsReaderBase},    *  plus the other few vInts stored in the frame. */
DECL|field|totalBlockOtherBytes
specifier|public
name|long
name|totalBlockOtherBytes
decl_stmt|;
comment|/** Segment name. */
DECL|field|segment
specifier|public
specifier|final
name|String
name|segment
decl_stmt|;
comment|/** Field name. */
DECL|field|field
specifier|public
specifier|final
name|String
name|field
decl_stmt|;
DECL|method|Stats
name|Stats
parameter_list|(
name|String
name|segment
parameter_list|,
name|String
name|field
parameter_list|)
block|{
name|this
operator|.
name|segment
operator|=
name|segment
expr_stmt|;
name|this
operator|.
name|field
operator|=
name|field
expr_stmt|;
block|}
DECL|method|startBlock
name|void
name|startBlock
parameter_list|(
name|SegmentTermsEnumFrame
name|frame
parameter_list|,
name|boolean
name|isFloor
parameter_list|)
block|{
name|totalBlockCount
operator|++
expr_stmt|;
if|if
condition|(
name|isFloor
condition|)
block|{
if|if
condition|(
name|frame
operator|.
name|fp
operator|==
name|frame
operator|.
name|fpOrig
condition|)
block|{
name|floorBlockCount
operator|++
expr_stmt|;
block|}
name|floorSubBlockCount
operator|++
expr_stmt|;
block|}
else|else
block|{
name|nonFloorBlockCount
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|blockCountByPrefixLen
operator|.
name|length
operator|<=
name|frame
operator|.
name|prefix
condition|)
block|{
name|blockCountByPrefixLen
operator|=
name|ArrayUtil
operator|.
name|grow
argument_list|(
name|blockCountByPrefixLen
argument_list|,
literal|1
operator|+
name|frame
operator|.
name|prefix
argument_list|)
expr_stmt|;
block|}
name|blockCountByPrefixLen
index|[
name|frame
operator|.
name|prefix
index|]
operator|++
expr_stmt|;
name|startBlockCount
operator|++
expr_stmt|;
name|totalBlockSuffixBytes
operator|+=
name|frame
operator|.
name|suffixesReader
operator|.
name|length
argument_list|()
expr_stmt|;
name|totalBlockStatsBytes
operator|+=
name|frame
operator|.
name|statsReader
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
DECL|method|endBlock
name|void
name|endBlock
parameter_list|(
name|SegmentTermsEnumFrame
name|frame
parameter_list|)
block|{
specifier|final
name|int
name|termCount
init|=
name|frame
operator|.
name|isLeafBlock
condition|?
name|frame
operator|.
name|entCount
else|:
name|frame
operator|.
name|state
operator|.
name|termBlockOrd
decl_stmt|;
specifier|final
name|int
name|subBlockCount
init|=
name|frame
operator|.
name|entCount
operator|-
name|termCount
decl_stmt|;
name|totalTermCount
operator|+=
name|termCount
expr_stmt|;
if|if
condition|(
name|termCount
operator|!=
literal|0
operator|&&
name|subBlockCount
operator|!=
literal|0
condition|)
block|{
name|mixedBlockCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|termCount
operator|!=
literal|0
condition|)
block|{
name|termsOnlyBlockCount
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|subBlockCount
operator|!=
literal|0
condition|)
block|{
name|subBlocksOnlyBlockCount
operator|++
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
name|endBlockCount
operator|++
expr_stmt|;
specifier|final
name|long
name|otherBytes
init|=
name|frame
operator|.
name|fpEnd
operator|-
name|frame
operator|.
name|fp
operator|-
name|frame
operator|.
name|suffixesReader
operator|.
name|length
argument_list|()
operator|-
name|frame
operator|.
name|statsReader
operator|.
name|length
argument_list|()
decl_stmt|;
assert|assert
name|otherBytes
operator|>
literal|0
operator|:
literal|"otherBytes="
operator|+
name|otherBytes
operator|+
literal|" frame.fp="
operator|+
name|frame
operator|.
name|fp
operator|+
literal|" frame.fpEnd="
operator|+
name|frame
operator|.
name|fpEnd
assert|;
name|totalBlockOtherBytes
operator|+=
name|otherBytes
expr_stmt|;
block|}
DECL|method|term
name|void
name|term
parameter_list|(
name|BytesRef
name|term
parameter_list|)
block|{
name|totalTermBytes
operator|+=
name|term
operator|.
name|length
expr_stmt|;
block|}
DECL|method|finish
name|void
name|finish
parameter_list|()
block|{
assert|assert
name|startBlockCount
operator|==
name|endBlockCount
operator|:
literal|"startBlockCount="
operator|+
name|startBlockCount
operator|+
literal|" endBlockCount="
operator|+
name|endBlockCount
assert|;
assert|assert
name|totalBlockCount
operator|==
name|floorSubBlockCount
operator|+
name|nonFloorBlockCount
operator|:
literal|"floorSubBlockCount="
operator|+
name|floorSubBlockCount
operator|+
literal|" nonFloorBlockCount="
operator|+
name|nonFloorBlockCount
operator|+
literal|" totalBlockCount="
operator|+
name|totalBlockCount
assert|;
assert|assert
name|totalBlockCount
operator|==
name|mixedBlockCount
operator|+
name|termsOnlyBlockCount
operator|+
name|subBlocksOnlyBlockCount
operator|:
literal|"totalBlockCount="
operator|+
name|totalBlockCount
operator|+
literal|" mixedBlockCount="
operator|+
name|mixedBlockCount
operator|+
literal|" subBlocksOnlyBlockCount="
operator|+
name|subBlocksOnlyBlockCount
operator|+
literal|" termsOnlyBlockCount="
operator|+
name|termsOnlyBlockCount
assert|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
literal|1024
argument_list|)
decl_stmt|;
name|PrintStream
name|out
decl_stmt|;
try|try
block|{
name|out
operator|=
operator|new
name|PrintStream
argument_list|(
name|bos
argument_list|,
literal|false
argument_list|,
name|IOUtils
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"  index FST:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|indexNumBytes
operator|+
literal|" bytes"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  terms:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|totalTermCount
operator|+
literal|" terms"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|totalTermBytes
operator|+
literal|" bytes"
operator|+
operator|(
name|totalTermCount
operator|!=
literal|0
condition|?
literal|" ("
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.1f"
argument_list|,
operator|(
operator|(
name|double
operator|)
name|totalTermBytes
operator|)
operator|/
name|totalTermCount
argument_list|)
operator|+
literal|" bytes/term)"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  blocks:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|totalBlockCount
operator|+
literal|" blocks"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|termsOnlyBlockCount
operator|+
literal|" terms-only blocks"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|subBlocksOnlyBlockCount
operator|+
literal|" sub-block-only blocks"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|mixedBlockCount
operator|+
literal|" mixed blocks"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|floorBlockCount
operator|+
literal|" floor blocks"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
operator|(
name|totalBlockCount
operator|-
name|floorSubBlockCount
operator|)
operator|+
literal|" non-floor blocks"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|floorSubBlockCount
operator|+
literal|" floor sub-blocks"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|totalBlockSuffixBytes
operator|+
literal|" term suffix bytes"
operator|+
operator|(
name|totalBlockCount
operator|!=
literal|0
condition|?
literal|" ("
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.1f"
argument_list|,
operator|(
operator|(
name|double
operator|)
name|totalBlockSuffixBytes
operator|)
operator|/
name|totalBlockCount
argument_list|)
operator|+
literal|" suffix-bytes/block)"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|totalBlockStatsBytes
operator|+
literal|" term stats bytes"
operator|+
operator|(
name|totalBlockCount
operator|!=
literal|0
condition|?
literal|" ("
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.1f"
argument_list|,
operator|(
operator|(
name|double
operator|)
name|totalBlockStatsBytes
operator|)
operator|/
name|totalBlockCount
argument_list|)
operator|+
literal|" stats-bytes/block)"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"    "
operator|+
name|totalBlockOtherBytes
operator|+
literal|" other bytes"
operator|+
operator|(
name|totalBlockCount
operator|!=
literal|0
condition|?
literal|" ("
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.1f"
argument_list|,
operator|(
operator|(
name|double
operator|)
name|totalBlockOtherBytes
operator|)
operator|/
name|totalBlockCount
argument_list|)
operator|+
literal|" other-bytes/block)"
else|:
literal|""
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalBlockCount
operator|!=
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"    by prefix length:"
argument_list|)
expr_stmt|;
name|int
name|total
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|prefix
init|=
literal|0
init|;
name|prefix
operator|<
name|blockCountByPrefixLen
operator|.
name|length
condition|;
name|prefix
operator|++
control|)
block|{
specifier|final
name|int
name|blockCount
init|=
name|blockCountByPrefixLen
index|[
name|prefix
index|]
decl_stmt|;
name|total
operator|+=
name|blockCount
expr_stmt|;
if|if
condition|(
name|blockCount
operator|!=
literal|0
condition|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"      "
operator|+
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%2d"
argument_list|,
name|prefix
argument_list|)
operator|+
literal|": "
operator|+
name|blockCount
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|totalBlockCount
operator|==
name|total
assert|;
block|}
try|try
block|{
return|return
name|bos
operator|.
name|toString
argument_list|(
name|IOUtils
operator|.
name|UTF_8
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|bogus
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|bogus
argument_list|)
throw|;
block|}
block|}
block|}
end_class
end_unit
