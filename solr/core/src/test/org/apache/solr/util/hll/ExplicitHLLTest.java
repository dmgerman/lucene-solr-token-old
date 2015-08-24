begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.util.hll
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|hll
package|;
end_package
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
name|junit
operator|.
name|Test
import|;
end_import
begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|hppc
operator|.
name|LongHashSet
import|;
end_import
begin_import
import|import static
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|RandomizedTest
operator|.
name|*
import|;
end_import
begin_comment
comment|/**  * Tests {@link HLL} of type {@link HLLType#EXPLICIT}.  */
end_comment
begin_class
DECL|class|ExplicitHLLTest
specifier|public
class|class
name|ExplicitHLLTest
extends|extends
name|LuceneTestCase
block|{
comment|/**      * Tests basic set semantics of {@link HLL#addRaw(long)}.      */
annotation|@
name|Test
DECL|method|addBasicTest
specifier|public
name|void
name|addBasicTest
parameter_list|()
block|{
block|{
comment|// Adding a single positive value to an empty set should work.
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
name|hll
operator|.
name|addRaw
argument_list|(
literal|1L
comment|/*positive*/
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Adding a single negative value to an empty set should work.
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
name|hll
operator|.
name|addRaw
argument_list|(
operator|-
literal|1L
comment|/*negative*/
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Adding a duplicate value to a set should be a no-op.
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
name|hll
operator|.
name|addRaw
argument_list|(
literal|1L
comment|/*positive*/
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|1L
comment|/*arbitrary*/
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|1L
comment|/*dupe*/
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ------------------------------------------------------------------------
comment|/**      * Tests {@link HLL#union(HLL)}.      */
annotation|@
name|Test
DECL|method|unionTest
specifier|public
name|void
name|unionTest
parameter_list|()
block|{
block|{
comment|// Unioning two distinct sets should work
specifier|final
name|HLL
name|hllA
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
specifier|final
name|HLL
name|hllB
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
name|hllA
operator|.
name|addRaw
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|hllA
operator|.
name|addRaw
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|hllB
operator|.
name|addRaw
argument_list|(
literal|3L
argument_list|)
expr_stmt|;
name|hllA
operator|.
name|union
argument_list|(
name|hllB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hllA
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Unioning two sets whose union doesn't exceed the cardinality cap should not promote
specifier|final
name|HLL
name|hllA
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
specifier|final
name|HLL
name|hllB
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
name|hllA
operator|.
name|addRaw
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|hllA
operator|.
name|addRaw
argument_list|(
literal|2L
argument_list|)
expr_stmt|;
name|hllB
operator|.
name|addRaw
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|hllA
operator|.
name|union
argument_list|(
name|hllB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hllA
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
block|{
comment|// unioning two sets whose union exceeds the cardinality cap should promote
specifier|final
name|HLL
name|hllA
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
specifier|final
name|HLL
name|hllB
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
comment|// fill up sets to explicitThreshold
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|128
comment|/*explicitThreshold*/
condition|;
name|i
operator|++
control|)
block|{
name|hllA
operator|.
name|addRaw
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|hllB
operator|.
name|addRaw
argument_list|(
name|i
operator|+
literal|128
argument_list|)
expr_stmt|;
block|}
name|hllA
operator|.
name|union
argument_list|(
name|hllB
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hllA
operator|.
name|getType
argument_list|()
argument_list|,
name|HLLType
operator|.
name|SPARSE
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ------------------------------------------------------------------------
comment|/**      * Tests {@link HLL#clear()}      */
annotation|@
name|Test
DECL|method|clearTest
specifier|public
name|void
name|clearTest
parameter_list|()
block|{
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
name|hll
operator|.
name|addRaw
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|1L
argument_list|)
expr_stmt|;
name|hll
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|// ------------------------------------------------------------------------
comment|/**      */
annotation|@
name|Test
DECL|method|toFromBytesTest
specifier|public
name|void
name|toFromBytesTest
parameter_list|()
block|{
specifier|final
name|ISchemaVersion
name|schemaVersion
init|=
name|SerializationUtil
operator|.
name|DEFAULT_SCHEMA_VERSION
decl_stmt|;
specifier|final
name|HLLType
name|type
init|=
name|HLLType
operator|.
name|EXPLICIT
decl_stmt|;
specifier|final
name|int
name|padding
init|=
name|schemaVersion
operator|.
name|paddingBytes
argument_list|(
name|type
argument_list|)
decl_stmt|;
specifier|final
name|int
name|bytesPerWord
init|=
literal|8
decl_stmt|;
block|{
comment|// Should work on an empty set
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|bytes
init|=
name|hll
operator|.
name|toBytes
argument_list|(
name|schemaVersion
argument_list|)
decl_stmt|;
comment|// assert output has correct byte length
name|assertEquals
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|padding
comment|/*no elements, just padding*/
argument_list|)
expr_stmt|;
specifier|final
name|HLL
name|inHLL
init|=
name|HLL
operator|.
name|fromBytes
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|assertElementsEqual
argument_list|(
name|hll
argument_list|,
name|inHLL
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Should work on a partially filled set
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
literal|128
comment|/*arbitrary*/
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
literal|3
condition|;
name|i
operator|++
control|)
block|{
name|hll
operator|.
name|addRaw
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|bytes
init|=
name|hll
operator|.
name|toBytes
argument_list|(
name|schemaVersion
argument_list|)
decl_stmt|;
comment|// assert output has correct byte length
name|assertEquals
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|padding
operator|+
operator|(
name|bytesPerWord
operator|*
literal|3
comment|/*elements*/
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|HLL
name|inHLL
init|=
name|HLL
operator|.
name|fromBytes
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|assertElementsEqual
argument_list|(
name|hll
argument_list|,
name|inHLL
argument_list|)
expr_stmt|;
block|}
block|{
comment|// Should work on a full set
specifier|final
name|int
name|explicitThreshold
init|=
literal|128
decl_stmt|;
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
name|explicitThreshold
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
name|explicitThreshold
condition|;
name|i
operator|++
control|)
block|{
name|hll
operator|.
name|addRaw
argument_list|(
literal|27
operator|+
name|i
comment|/*arbitrary*/
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|bytes
init|=
name|hll
operator|.
name|toBytes
argument_list|(
name|schemaVersion
argument_list|)
decl_stmt|;
comment|// assert output has correct byte length
name|assertEquals
argument_list|(
name|bytes
operator|.
name|length
argument_list|,
name|padding
operator|+
operator|(
name|bytesPerWord
operator|*
name|explicitThreshold
comment|/*elements*/
operator|)
argument_list|)
expr_stmt|;
specifier|final
name|HLL
name|inHLL
init|=
name|HLL
operator|.
name|fromBytes
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|assertElementsEqual
argument_list|(
name|hll
argument_list|,
name|inHLL
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ------------------------------------------------------------------------
comment|/**      * Tests correctness against {@link java.util.HashSet}.      */
annotation|@
name|Test
DECL|method|randomValuesTest
specifier|public
name|void
name|randomValuesTest
parameter_list|()
block|{
specifier|final
name|int
name|explicitThreshold
init|=
literal|4096
decl_stmt|;
specifier|final
name|HashSet
argument_list|<
name|Long
argument_list|>
name|canonical
init|=
operator|new
name|HashSet
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|HLL
name|hll
init|=
name|newHLL
argument_list|(
name|explicitThreshold
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
name|explicitThreshold
condition|;
name|i
operator|++
control|)
block|{
name|long
name|randomLong
init|=
name|randomLong
argument_list|()
decl_stmt|;
name|canonical
operator|.
name|add
argument_list|(
name|randomLong
argument_list|)
expr_stmt|;
name|hll
operator|.
name|addRaw
argument_list|(
name|randomLong
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|canonicalCardinality
init|=
name|canonical
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|hll
operator|.
name|cardinality
argument_list|()
argument_list|,
name|canonicalCardinality
argument_list|)
expr_stmt|;
block|}
comment|// ------------------------------------------------------------------------
comment|/**      * Tests promotion to {@link HLLType#SPARSE} and {@link HLLType#FULL}.      */
annotation|@
name|Test
DECL|method|promotionTest
specifier|public
name|void
name|promotionTest
parameter_list|()
block|{
block|{
comment|// locally scoped for sanity
specifier|final
name|int
name|explicitThreshold
init|=
literal|128
decl_stmt|;
specifier|final
name|HLL
name|hll
init|=
operator|new
name|HLL
argument_list|(
literal|11
comment|/*log2m, unused*/
argument_list|,
literal|5
comment|/*regwidth, unused*/
argument_list|,
name|explicitThreshold
argument_list|,
literal|256
comment|/*sparseThreshold*/
argument_list|,
name|HLLType
operator|.
name|EXPLICIT
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
name|explicitThreshold
operator|+
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|hll
operator|.
name|addRaw
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|hll
operator|.
name|getType
argument_list|()
argument_list|,
name|HLLType
operator|.
name|SPARSE
argument_list|)
expr_stmt|;
block|}
block|{
comment|// locally scoped for sanity
specifier|final
name|HLL
name|hll
init|=
operator|new
name|HLL
argument_list|(
literal|11
comment|/*log2m, unused*/
argument_list|,
literal|5
comment|/*regwidth, unused*/
argument_list|,
literal|4
comment|/*expthresh => explicitThreshold = 8*/
argument_list|,
literal|false
comment|/*sparseon*/
argument_list|,
name|HLLType
operator|.
name|EXPLICIT
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
literal|9
comment|/*> explicitThreshold */
condition|;
name|i
operator|++
control|)
block|{
name|hll
operator|.
name|addRaw
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|hll
operator|.
name|getType
argument_list|()
argument_list|,
name|HLLType
operator|.
name|FULL
argument_list|)
expr_stmt|;
block|}
block|}
comment|// ************************************************************************
comment|// assertion helpers
comment|/**      * Asserts that values in both sets are exactly equal.      */
DECL|method|assertElementsEqual
specifier|private
specifier|static
name|void
name|assertElementsEqual
parameter_list|(
specifier|final
name|HLL
name|hllA
parameter_list|,
specifier|final
name|HLL
name|hllB
parameter_list|)
block|{
specifier|final
name|LongHashSet
name|internalSetA
init|=
name|hllA
operator|.
name|explicitStorage
decl_stmt|;
specifier|final
name|LongHashSet
name|internalSetB
init|=
name|hllB
operator|.
name|explicitStorage
decl_stmt|;
name|assertTrue
argument_list|(
name|internalSetA
operator|.
name|equals
argument_list|(
name|internalSetB
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Builds a {@link HLLType#EXPLICIT} {@link HLL} instance with the specified      * explicit threshold.      *      * @param  explicitThreshold explicit threshold to use for the constructed      *         {@link HLL}. This must be greater than zero.      * @return a default-sized {@link HLLType#EXPLICIT} empty {@link HLL} instance.      *         This will never be<code>null</code>.      */
DECL|method|newHLL
specifier|private
specifier|static
name|HLL
name|newHLL
parameter_list|(
specifier|final
name|int
name|explicitThreshold
parameter_list|)
block|{
return|return
operator|new
name|HLL
argument_list|(
literal|11
comment|/*log2m, unused*/
argument_list|,
literal|5
comment|/*regwidth, unused*/
argument_list|,
name|explicitThreshold
argument_list|,
literal|256
comment|/*sparseThreshold, arbitrary, unused*/
argument_list|,
name|HLLType
operator|.
name|EXPLICIT
argument_list|)
return|;
block|}
block|}
end_class
end_unit
