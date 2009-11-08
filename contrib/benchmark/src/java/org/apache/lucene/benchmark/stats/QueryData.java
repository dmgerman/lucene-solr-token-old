begin_unit
begin_package
DECL|package|org.apache.lucene.benchmark.stats
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|stats
package|;
end_package
begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|Query
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
name|benchmark
operator|.
name|Constants
import|;
end_import
begin_comment
comment|/**  * This class holds parameters for a query benchmark.  *  */
end_comment
begin_class
DECL|class|QueryData
specifier|public
class|class
name|QueryData
block|{
comment|/** Benchmark id */
DECL|field|id
specifier|public
name|String
name|id
decl_stmt|;
comment|/** Lucene query */
DECL|field|q
specifier|public
name|Query
name|q
decl_stmt|;
comment|/** If true, re-open index reader before benchmark. */
DECL|field|reopen
specifier|public
name|boolean
name|reopen
decl_stmt|;
comment|/** If true, warm-up the index reader before searching by sequentially    * retrieving all documents from index.    */
DECL|field|warmup
specifier|public
name|boolean
name|warmup
decl_stmt|;
comment|/**    * If true, actually retrieve documents returned in Hits.    */
DECL|field|retrieve
specifier|public
name|boolean
name|retrieve
decl_stmt|;
comment|/**    * Prepare a list of benchmark data, using all possible combinations of    * benchmark parameters.    * @param queries source Lucene queries    * @return The QueryData    */
DECL|method|getAll
specifier|public
specifier|static
name|QueryData
index|[]
name|getAll
parameter_list|(
name|Query
index|[]
name|queries
parameter_list|)
block|{
name|Vector
argument_list|<
name|QueryData
argument_list|>
name|vqd
init|=
operator|new
name|Vector
argument_list|<
name|QueryData
argument_list|>
argument_list|()
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
name|queries
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|r
init|=
literal|1
init|;
name|r
operator|>=
literal|0
condition|;
name|r
operator|--
control|)
block|{
for|for
control|(
name|int
name|w
init|=
literal|1
init|;
name|w
operator|>=
literal|0
condition|;
name|w
operator|--
control|)
block|{
for|for
control|(
name|int
name|t
init|=
literal|0
init|;
name|t
operator|<
literal|2
condition|;
name|t
operator|++
control|)
block|{
name|QueryData
name|qd
init|=
operator|new
name|QueryData
argument_list|()
decl_stmt|;
name|qd
operator|.
name|id
operator|=
literal|"qd-"
operator|+
name|i
operator|+
name|r
operator|+
name|w
operator|+
name|t
expr_stmt|;
name|qd
operator|.
name|reopen
operator|=
name|Constants
operator|.
name|BOOLEANS
index|[
name|r
index|]
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
name|qd
operator|.
name|warmup
operator|=
name|Constants
operator|.
name|BOOLEANS
index|[
name|w
index|]
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
name|qd
operator|.
name|retrieve
operator|=
name|Constants
operator|.
name|BOOLEANS
index|[
name|t
index|]
operator|.
name|booleanValue
argument_list|()
expr_stmt|;
name|qd
operator|.
name|q
operator|=
name|queries
index|[
name|i
index|]
expr_stmt|;
name|vqd
operator|.
name|add
argument_list|(
name|qd
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
name|vqd
operator|.
name|toArray
argument_list|(
operator|new
name|QueryData
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/** Short legend for interpreting toString() output. */
DECL|method|getLabels
specifier|public
specifier|static
name|String
name|getLabels
parameter_list|()
block|{
return|return
literal|"# Query data: R-reopen, W-warmup, T-retrieve, N-no"
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|id
operator|+
literal|" "
operator|+
operator|(
name|reopen
condition|?
literal|"R"
else|:
literal|"NR"
operator|)
operator|+
literal|" "
operator|+
operator|(
name|warmup
condition|?
literal|"W"
else|:
literal|"NW"
operator|)
operator|+
literal|" "
operator|+
operator|(
name|retrieve
condition|?
literal|"T"
else|:
literal|"NT"
operator|)
operator|+
literal|" ["
operator|+
name|q
operator|.
name|toString
argument_list|()
operator|+
literal|"]"
return|;
block|}
block|}
end_class
end_unit
