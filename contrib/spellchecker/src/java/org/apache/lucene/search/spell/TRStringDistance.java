begin_unit
begin_package
DECL|package|org.apache.lucene.search.spell
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|spell
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Levenshtein edit distance class.  */
end_comment
begin_class
DECL|class|TRStringDistance
specifier|final
class|class
name|TRStringDistance
implements|implements
name|StringDistance
block|{
comment|/**      * Optimized to run a bit faster than the static getDistance().      * In one benchmark times were 5.3sec using ctr vs 8.5sec w/ static method, thus 37% faster.      */
DECL|method|TRStringDistance
specifier|public
name|TRStringDistance
parameter_list|()
block|{     }
comment|//*****************************
comment|// Compute Levenshtein distance: see org.apache.commons.lang.StringUtils#getLevenshteinDistance(String, String)
comment|//*****************************
DECL|method|getDistance
specifier|public
name|float
name|getDistance
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|other
parameter_list|)
block|{
name|char
index|[]
name|sa
decl_stmt|;
name|int
name|n
decl_stmt|;
name|int
name|p
index|[]
decl_stmt|;
comment|//'previous' cost array, horizontally
name|int
name|d
index|[]
decl_stmt|;
comment|// cost array, horizontally
name|int
name|_d
index|[]
decl_stmt|;
comment|//placeholder to assist in swapping p and d
comment|/*            The difference between this impl. and the previous is that, rather            than creating and retaining a matrix of size s.length()+1 by t.length()+1,            we maintain two single-dimensional arrays of length s.length()+1.  The first, d,            is the 'current working' distance array that maintains the newest distance cost            counts as we iterate through the characters of String s.  Each time we increment            the index of String t we are comparing, d is copied to p, the second int[].  Doing so            allows us to retain the previous cost counts as required by the algorithm (taking            the minimum of the cost count to the left, up one, and diagonally up and to the left            of the current cost count being calculated).  (Note that the arrays aren't really            copied anymore, just switched...this is clearly much better than cloning an array            or doing a System.arraycopy() each time  through the outer loop.)             Effectively, the difference between the two implementations is this one does not            cause an out of memory condition when calculating the LD over two very large strings.          */
name|sa
operator|=
name|target
operator|.
name|toCharArray
argument_list|()
expr_stmt|;
name|n
operator|=
name|sa
operator|.
name|length
expr_stmt|;
name|p
operator|=
operator|new
name|int
index|[
name|n
operator|+
literal|1
index|]
expr_stmt|;
name|d
operator|=
operator|new
name|int
index|[
name|n
operator|+
literal|1
index|]
expr_stmt|;
specifier|final
name|int
name|m
init|=
name|other
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|m
operator|==
literal|0
condition|)
block|{
return|return
literal|1
return|;
block|}
comment|// indexes into strings s and t
name|int
name|i
decl_stmt|;
comment|// iterates through s
name|int
name|j
decl_stmt|;
comment|// iterates through t
name|char
name|t_j
decl_stmt|;
comment|// jth character of t
name|int
name|cost
decl_stmt|;
comment|// cost
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|p
index|[
name|i
index|]
operator|=
name|i
expr_stmt|;
block|}
for|for
control|(
name|j
operator|=
literal|1
init|;
name|j
operator|<=
name|m
condition|;
name|j
operator|++
control|)
block|{
name|t_j
operator|=
name|other
operator|.
name|charAt
argument_list|(
name|j
operator|-
literal|1
argument_list|)
expr_stmt|;
name|d
index|[
literal|0
index|]
operator|=
name|j
expr_stmt|;
for|for
control|(
name|i
operator|=
literal|1
init|;
name|i
operator|<=
name|n
condition|;
name|i
operator|++
control|)
block|{
name|cost
operator|=
name|sa
index|[
name|i
operator|-
literal|1
index|]
operator|==
name|t_j
condition|?
literal|0
else|:
literal|1
expr_stmt|;
comment|// minimum of cell to the left+1, to the top+1, diagonally left and up +cost
name|d
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|min
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|d
index|[
name|i
operator|-
literal|1
index|]
operator|+
literal|1
argument_list|,
name|p
index|[
name|i
index|]
operator|+
literal|1
argument_list|)
argument_list|,
name|p
index|[
name|i
operator|-
literal|1
index|]
operator|+
name|cost
argument_list|)
expr_stmt|;
block|}
comment|// copy current distance counts to 'previous row' distance counts
name|_d
operator|=
name|p
expr_stmt|;
name|p
operator|=
name|d
expr_stmt|;
name|d
operator|=
name|_d
expr_stmt|;
block|}
comment|// our last action in the above loop was to switch d and p, so p now
comment|// actually has the most recent cost counts
return|return
literal|1.0f
operator|-
operator|(
operator|(
name|float
operator|)
name|p
index|[
name|n
index|]
operator|/
name|Math
operator|.
name|min
argument_list|(
name|other
operator|.
name|length
argument_list|()
argument_list|,
name|sa
operator|.
name|length
argument_list|)
operator|)
return|;
block|}
block|}
end_class
end_unit
