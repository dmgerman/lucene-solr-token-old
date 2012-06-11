begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.search
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
package|;
end_package
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|BitDocSet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|HashDocSet
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|DocSet
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
name|OpenBitSet
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
name|util
operator|.
name|BitSet
import|;
end_import
begin_comment
comment|/**  */
end_comment
begin_class
DECL|class|DocSetPerf
specifier|public
class|class
name|DocSetPerf
block|{
comment|// use test instead of assert since asserts may be turned off
DECL|method|test
specifier|public
specifier|static
name|void
name|test
parameter_list|(
name|boolean
name|condition
parameter_list|)
block|{
if|if
condition|(
operator|!
name|condition
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"test requestHandler: assertion failed!"
argument_list|)
throw|;
block|}
block|}
DECL|field|rand
specifier|static
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|bs
specifier|static
name|OpenBitSet
name|bs
decl_stmt|;
DECL|field|bds
specifier|static
name|BitDocSet
name|bds
decl_stmt|;
DECL|field|hds
specifier|static
name|HashDocSet
name|hds
decl_stmt|;
DECL|field|ids
specifier|static
name|int
index|[]
name|ids
decl_stmt|;
comment|// not unique
DECL|method|generate
specifier|static
name|void
name|generate
parameter_list|(
name|int
name|maxSize
parameter_list|,
name|int
name|bitsToSet
parameter_list|)
block|{
name|bs
operator|=
operator|new
name|OpenBitSet
argument_list|(
name|maxSize
argument_list|)
expr_stmt|;
name|ids
operator|=
operator|new
name|int
index|[
name|bitsToSet
index|]
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|maxSize
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bitsToSet
condition|;
name|i
operator|++
control|)
block|{
name|int
name|id
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|bs
operator|.
name|get
argument_list|(
name|id
argument_list|)
condition|)
block|{
name|bs
operator|.
name|fastSet
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|ids
index|[
name|count
operator|++
index|]
operator|=
name|id
expr_stmt|;
block|}
block|}
block|}
name|bds
operator|=
operator|new
name|BitDocSet
argument_list|(
name|bs
argument_list|,
name|bitsToSet
argument_list|)
expr_stmt|;
name|hds
operator|=
operator|new
name|HashDocSet
argument_list|(
name|ids
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|bsSize
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
name|boolean
name|randSize
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|bsSize
operator|.
name|endsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|bsSize
operator|=
name|bsSize
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|bsSize
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|randSize
operator|=
literal|true
expr_stmt|;
block|}
name|int
name|bitSetSize
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|bsSize
argument_list|)
decl_stmt|;
name|int
name|numSets
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|int
name|numBitsSet
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|String
name|test
init|=
name|args
index|[
literal|3
index|]
operator|.
name|intern
argument_list|()
decl_stmt|;
name|int
name|iter
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|4
index|]
argument_list|)
decl_stmt|;
name|int
name|ret
init|=
literal|0
decl_stmt|;
name|OpenBitSet
index|[]
name|sets
init|=
operator|new
name|OpenBitSet
index|[
name|numSets
index|]
decl_stmt|;
name|DocSet
index|[]
name|bset
init|=
operator|new
name|DocSet
index|[
name|numSets
index|]
decl_stmt|;
name|DocSet
index|[]
name|hset
init|=
operator|new
name|DocSet
index|[
name|numSets
index|]
decl_stmt|;
name|BitSet
name|scratch
init|=
operator|new
name|BitSet
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
name|numSets
condition|;
name|i
operator|++
control|)
block|{
name|generate
argument_list|(
name|randSize
condition|?
name|rand
operator|.
name|nextInt
argument_list|(
name|bitSetSize
argument_list|)
else|:
name|bitSetSize
argument_list|,
name|numBitsSet
argument_list|)
expr_stmt|;
name|sets
index|[
name|i
index|]
operator|=
name|bs
expr_stmt|;
name|bset
index|[
name|i
index|]
operator|=
name|bds
expr_stmt|;
name|hset
index|[
name|i
index|]
operator|=
name|hds
expr_stmt|;
block|}
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"test"
operator|.
name|equals
argument_list|(
name|test
argument_list|)
condition|)
block|{
for|for
control|(
name|int
name|it
init|=
literal|0
init|;
name|it
operator|<
name|iter
condition|;
name|it
operator|++
control|)
block|{
name|generate
argument_list|(
name|randSize
condition|?
name|rand
operator|.
name|nextInt
argument_list|(
name|bitSetSize
argument_list|)
else|:
name|bitSetSize
argument_list|,
name|numBitsSet
argument_list|)
expr_stmt|;
name|OpenBitSet
name|bs1
init|=
name|bs
decl_stmt|;
name|BitDocSet
name|bds1
init|=
name|bds
decl_stmt|;
name|HashDocSet
name|hds1
init|=
name|hds
decl_stmt|;
name|generate
argument_list|(
name|randSize
condition|?
name|rand
operator|.
name|nextInt
argument_list|(
name|bitSetSize
argument_list|)
else|:
name|bitSetSize
argument_list|,
name|numBitsSet
argument_list|)
expr_stmt|;
name|OpenBitSet
name|res
init|=
operator|(
operator|(
name|OpenBitSet
operator|)
name|bs1
operator|.
name|clone
argument_list|()
operator|)
decl_stmt|;
name|res
operator|.
name|and
argument_list|(
name|bs
argument_list|)
expr_stmt|;
name|int
name|icount
init|=
operator|(
name|int
operator|)
name|res
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|test
argument_list|(
name|bds1
operator|.
name|intersection
argument_list|(
name|bds
argument_list|)
operator|.
name|size
argument_list|()
operator|==
name|icount
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|bds1
operator|.
name|intersectionSize
argument_list|(
name|bds
argument_list|)
operator|==
name|icount
argument_list|)
expr_stmt|;
if|if
condition|(
name|bds1
operator|.
name|intersection
argument_list|(
name|hds
argument_list|)
operator|.
name|size
argument_list|()
operator|!=
name|icount
condition|)
block|{
name|DocSet
name|ds
init|=
name|bds1
operator|.
name|intersection
argument_list|(
name|hds
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"STOP"
argument_list|)
expr_stmt|;
block|}
name|test
argument_list|(
name|bds1
operator|.
name|intersection
argument_list|(
name|hds
argument_list|)
operator|.
name|size
argument_list|()
operator|==
name|icount
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|bds1
operator|.
name|intersectionSize
argument_list|(
name|hds
argument_list|)
operator|==
name|icount
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|hds1
operator|.
name|intersection
argument_list|(
name|bds
argument_list|)
operator|.
name|size
argument_list|()
operator|==
name|icount
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|hds1
operator|.
name|intersectionSize
argument_list|(
name|bds
argument_list|)
operator|==
name|icount
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|hds1
operator|.
name|intersection
argument_list|(
name|hds
argument_list|)
operator|.
name|size
argument_list|()
operator|==
name|icount
argument_list|)
expr_stmt|;
name|test
argument_list|(
name|hds1
operator|.
name|intersectionSize
argument_list|(
name|hds
argument_list|)
operator|==
name|icount
argument_list|)
expr_stmt|;
name|ret
operator|+=
name|icount
expr_stmt|;
block|}
block|}
name|String
name|type
init|=
literal|null
decl_stmt|;
name|String
name|oper
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|endsWith
argument_list|(
literal|"B"
argument_list|)
condition|)
block|{
name|type
operator|=
literal|"B"
expr_stmt|;
block|}
if|if
condition|(
name|test
operator|.
name|endsWith
argument_list|(
literal|"H"
argument_list|)
condition|)
block|{
name|type
operator|=
literal|"H"
expr_stmt|;
block|}
if|if
condition|(
name|test
operator|.
name|endsWith
argument_list|(
literal|"M"
argument_list|)
condition|)
block|{
name|type
operator|=
literal|"M"
expr_stmt|;
block|}
if|if
condition|(
name|test
operator|.
name|startsWith
argument_list|(
literal|"intersect"
argument_list|)
condition|)
name|oper
operator|=
literal|"intersect"
expr_stmt|;
if|if
condition|(
name|test
operator|.
name|startsWith
argument_list|(
literal|"intersectSize"
argument_list|)
condition|)
name|oper
operator|=
literal|"intersectSize"
expr_stmt|;
if|if
condition|(
name|test
operator|.
name|startsWith
argument_list|(
literal|"intersectAndSize"
argument_list|)
condition|)
name|oper
operator|=
literal|"intersectSize"
expr_stmt|;
if|if
condition|(
name|oper
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|it
init|=
literal|0
init|;
name|it
operator|<
name|iter
condition|;
name|it
operator|++
control|)
block|{
name|int
name|idx1
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|numSets
argument_list|)
decl_stmt|;
name|int
name|idx2
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|numSets
argument_list|)
decl_stmt|;
name|DocSet
name|a
init|=
literal|null
decl_stmt|,
name|b
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|"B"
condition|)
block|{
name|a
operator|=
name|bset
index|[
name|idx1
index|]
expr_stmt|;
name|b
operator|=
name|bset
index|[
name|idx2
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|"H"
condition|)
block|{
name|a
operator|=
name|hset
index|[
name|idx1
index|]
expr_stmt|;
name|b
operator|=
name|bset
index|[
name|idx2
index|]
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|==
literal|"M"
condition|)
block|{
if|if
condition|(
name|idx1
operator|<
name|idx2
condition|)
block|{
name|a
operator|=
name|bset
index|[
name|idx1
index|]
expr_stmt|;
name|b
operator|=
name|hset
index|[
name|idx2
index|]
expr_stmt|;
block|}
else|else
block|{
name|a
operator|=
name|hset
index|[
name|idx1
index|]
expr_stmt|;
name|b
operator|=
name|bset
index|[
name|idx2
index|]
expr_stmt|;
block|}
block|}
if|if
condition|(
name|oper
operator|==
literal|"intersect"
condition|)
block|{
name|DocSet
name|res
init|=
name|a
operator|.
name|intersection
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|ret
operator|+=
name|res
operator|.
name|memSize
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oper
operator|==
literal|"intersectSize"
condition|)
block|{
name|ret
operator|+=
name|a
operator|.
name|intersectionSize
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|oper
operator|==
literal|"intersectAndSize"
condition|)
block|{
name|DocSet
name|res
init|=
name|a
operator|.
name|intersection
argument_list|(
name|b
argument_list|)
decl_stmt|;
name|ret
operator|+=
name|res
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|long
name|end
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"TIME="
operator|+
operator|(
name|end
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
comment|// System.out.println("ret="+ret + " scratchsize="+scratch.size());
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"ret="
operator|+
name|ret
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
