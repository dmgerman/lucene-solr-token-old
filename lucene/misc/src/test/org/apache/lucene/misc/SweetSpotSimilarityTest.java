begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.misc
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|misc
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
name|search
operator|.
name|similarities
operator|.
name|ClassicSimilarity
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
name|search
operator|.
name|similarities
operator|.
name|PerFieldSimilarityWrapper
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
name|search
operator|.
name|similarities
operator|.
name|Similarity
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
name|search
operator|.
name|similarities
operator|.
name|TFIDFSimilarity
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
name|index
operator|.
name|FieldInvertState
import|;
end_import
begin_comment
comment|/**  * Test of the SweetSpotSimilarity  */
end_comment
begin_class
DECL|class|SweetSpotSimilarityTest
specifier|public
class|class
name|SweetSpotSimilarityTest
extends|extends
name|LuceneTestCase
block|{
DECL|method|computeAndDecodeNorm
specifier|public
specifier|static
name|float
name|computeAndDecodeNorm
parameter_list|(
name|SweetSpotSimilarity
name|decode
parameter_list|,
name|Similarity
name|encode
parameter_list|,
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
name|decode
operator|.
name|decodeNormValue
argument_list|(
name|computeAndGetNorm
argument_list|(
name|encode
argument_list|,
name|state
argument_list|)
argument_list|)
return|;
block|}
DECL|method|computeAndGetNorm
specifier|public
specifier|static
name|byte
name|computeAndGetNorm
parameter_list|(
name|Similarity
name|s
parameter_list|,
name|FieldInvertState
name|state
parameter_list|)
block|{
return|return
operator|(
name|byte
operator|)
name|s
operator|.
name|computeNorm
argument_list|(
name|state
argument_list|)
return|;
block|}
DECL|method|testSweetSpotComputeNorm
specifier|public
name|void
name|testSweetSpotComputeNorm
parameter_list|()
block|{
specifier|final
name|SweetSpotSimilarity
name|ss
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|0.5f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Similarity
name|d
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
name|Similarity
name|s
init|=
name|ss
decl_stmt|;
comment|// base case, should degrade
name|FieldInvertState
name|invertState
init|=
operator|new
name|FieldInvertState
argument_list|(
literal|"bogus"
argument_list|)
decl_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"base case: i="
operator|+
name|i
argument_list|,
name|computeAndGetNorm
argument_list|(
name|d
argument_list|,
name|invertState
argument_list|)
argument_list|,
name|computeAndGetNorm
argument_list|(
name|s
argument_list|,
name|invertState
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// make a sweet spot
name|ss
operator|.
name|setLengthNormFactors
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|,
literal|0.5f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"3,10: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|computeAndDecodeNorm
argument_list|(
name|ss
argument_list|,
name|ss
argument_list|,
name|invertState
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
operator|-
literal|9
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normD
init|=
name|computeAndGetNorm
argument_list|(
name|d
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normS
init|=
name|computeAndGetNorm
argument_list|(
name|s
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"3,10: 10<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// separate sweet spot for certain fields
specifier|final
name|SweetSpotSimilarity
name|ssBar
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssBar
operator|.
name|setLengthNormFactors
argument_list|(
literal|8
argument_list|,
literal|13
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SweetSpotSimilarity
name|ssYak
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssYak
operator|.
name|setLengthNormFactors
argument_list|(
literal|6
argument_list|,
literal|9
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SweetSpotSimilarity
name|ssA
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssA
operator|.
name|setLengthNormFactors
argument_list|(
literal|5
argument_list|,
literal|8
argument_list|,
literal|0.5f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SweetSpotSimilarity
name|ssB
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|ssB
operator|.
name|setLengthNormFactors
argument_list|(
literal|5
argument_list|,
literal|8
argument_list|,
literal|0.1f
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|Similarity
name|sp
init|=
operator|new
name|PerFieldSimilarityWrapper
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Similarity
name|get
parameter_list|(
name|String
name|field
parameter_list|)
block|{
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"bar"
argument_list|)
condition|)
return|return
name|ssBar
return|;
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"yak"
argument_list|)
condition|)
return|return
name|ssYak
return|;
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"a"
argument_list|)
condition|)
return|return
name|ssA
return|;
elseif|else
if|if
condition|(
name|field
operator|.
name|equals
argument_list|(
literal|"b"
argument_list|)
condition|)
return|return
name|ssB
return|;
else|else
return|return
name|ss
return|;
block|}
block|}
decl_stmt|;
name|invertState
operator|=
operator|new
name|FieldInvertState
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|3
init|;
name|i
operator|<=
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f: 3,10: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|computeAndDecodeNorm
argument_list|(
name|ss
argument_list|,
name|sp
argument_list|,
name|invertState
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|10
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
operator|-
literal|9
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normD
init|=
name|computeAndGetNorm
argument_list|(
name|d
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normS
init|=
name|computeAndGetNorm
argument_list|(
name|sp
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f: 3,10: 10<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|invertState
operator|=
operator|new
name|FieldInvertState
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|8
init|;
name|i
operator|<=
literal|13
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f: 8,13: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|computeAndDecodeNorm
argument_list|(
name|ss
argument_list|,
name|sp
argument_list|,
name|invertState
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|invertState
operator|=
operator|new
name|FieldInvertState
argument_list|(
literal|"yak"
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|6
init|;
name|i
operator|<=
literal|9
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f: 6,9: spot i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|computeAndDecodeNorm
argument_list|(
name|ss
argument_list|,
name|sp
argument_list|,
name|invertState
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|invertState
operator|=
operator|new
name|FieldInvertState
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|13
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
operator|-
literal|12
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normD
init|=
name|computeAndGetNorm
argument_list|(
name|d
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normS
init|=
name|computeAndGetNorm
argument_list|(
name|sp
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f: 8,13: 13<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|invertState
operator|=
operator|new
name|FieldInvertState
argument_list|(
literal|"yak"
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|9
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|.
name|setLength
argument_list|(
name|i
operator|-
literal|8
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normD
init|=
name|computeAndGetNorm
argument_list|(
name|d
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normS
init|=
name|computeAndGetNorm
argument_list|(
name|sp
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"f: 6,9: 9<x : i="
operator|+
name|i
argument_list|,
name|normD
argument_list|,
name|normS
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// steepness
for|for
control|(
name|int
name|i
init|=
literal|9
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|invertState
operator|=
operator|new
name|FieldInvertState
argument_list|(
literal|"a"
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normSS
init|=
name|computeAndGetNorm
argument_list|(
name|sp
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|invertState
operator|=
operator|new
name|FieldInvertState
argument_list|(
literal|"b"
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setBoost
argument_list|(
literal|1.0f
argument_list|)
expr_stmt|;
name|invertState
operator|.
name|setLength
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|byte
name|normS
init|=
name|computeAndGetNorm
argument_list|(
name|sp
argument_list|,
name|invertState
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"s: i="
operator|+
name|i
operator|+
literal|" : a="
operator|+
name|normSS
operator|+
literal|"< b="
operator|+
name|normS
argument_list|,
name|normSS
operator|<
name|normS
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testSweetSpotTf
specifier|public
name|void
name|testSweetSpotTf
parameter_list|()
block|{
name|SweetSpotSimilarity
name|ss
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
decl_stmt|;
name|TFIDFSimilarity
name|d
init|=
operator|new
name|ClassicSimilarity
argument_list|()
decl_stmt|;
name|TFIDFSimilarity
name|s
init|=
name|ss
decl_stmt|;
comment|// tf equal
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|0.0f
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf: i="
operator|+
name|i
argument_list|,
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
comment|// tf higher
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|1.0f
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"tf: i="
operator|+
name|i
operator|+
literal|" : d="
operator|+
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|+
literal|"< s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// tf flat
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|1.0f
argument_list|,
literal|6.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf flat1: i="
operator|+
name|i
argument_list|,
literal|1.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
name|ss
operator|.
name|setBaselineTfFactors
argument_list|(
literal|2.0f
argument_list|,
literal|6.0f
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
literal|"tf flat2: i="
operator|+
name|i
argument_list|,
literal|2.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|6
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|+
literal|"< d="
operator|+
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<
name|d
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// stupidity
name|assertEquals
argument_list|(
literal|"tf zero"
argument_list|,
literal|0.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
DECL|method|testHyperbolicSweetSpot
specifier|public
name|void
name|testHyperbolicSweetSpot
parameter_list|()
block|{
name|SweetSpotSimilarity
name|ss
init|=
operator|new
name|SweetSpotSimilarity
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|float
name|tf
parameter_list|(
name|float
name|freq
parameter_list|)
block|{
return|return
name|hyperbolicTf
argument_list|(
name|freq
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|ss
operator|.
name|setHyperbolicTfFactors
argument_list|(
literal|3.3f
argument_list|,
literal|7.7f
argument_list|,
name|Math
operator|.
name|E
argument_list|,
literal|5.0f
argument_list|)
expr_stmt|;
name|TFIDFSimilarity
name|s
init|=
name|ss
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
literal|"MIN tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
literal|3.3f
operator|<=
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"MAX tf: i="
operator|+
name|i
operator|+
literal|" : s="
operator|+
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
argument_list|,
name|s
operator|.
name|tf
argument_list|(
name|i
argument_list|)
operator|<=
literal|7.7f
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"MID tf"
argument_list|,
literal|3.3f
operator|+
operator|(
literal|7.7f
operator|-
literal|3.3f
operator|)
operator|/
literal|2.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
literal|5
argument_list|)
argument_list|,
literal|0.00001f
argument_list|)
expr_stmt|;
comment|// stupidity
name|assertEquals
argument_list|(
literal|"tf zero"
argument_list|,
literal|0.0f
argument_list|,
name|s
operator|.
name|tf
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0.0f
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
