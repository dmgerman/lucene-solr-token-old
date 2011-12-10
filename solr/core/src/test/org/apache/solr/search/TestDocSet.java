begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|OpenBitSet
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
name|OpenBitSetIterator
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
name|ReaderUtil
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
name|IndexReader
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
name|FilterIndexReader
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
name|IndexReader
operator|.
name|AtomicReaderContext
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
name|IndexReader
operator|.
name|ReaderContext
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
name|MultiReader
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
name|Filter
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
name|DocIdSet
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
name|DocIdSetIterator
import|;
end_import
begin_comment
comment|/**  *  */
end_comment
begin_class
DECL|class|TestDocSet
specifier|public
class|class
name|TestDocSet
extends|extends
name|LuceneTestCase
block|{
DECL|field|rand
name|Random
name|rand
init|=
name|random
decl_stmt|;
DECL|field|loadfactor
name|float
name|loadfactor
decl_stmt|;
DECL|method|getRandomSet
specifier|public
name|OpenBitSet
name|getRandomSet
parameter_list|(
name|int
name|sz
parameter_list|,
name|int
name|bitsToSet
parameter_list|)
block|{
name|OpenBitSet
name|bs
init|=
operator|new
name|OpenBitSet
argument_list|(
name|sz
argument_list|)
decl_stmt|;
if|if
condition|(
name|sz
operator|==
literal|0
condition|)
return|return
name|bs
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
name|bitsToSet
condition|;
name|i
operator|++
control|)
block|{
name|bs
operator|.
name|fastSet
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|bs
return|;
block|}
DECL|method|getHashDocSet
specifier|public
name|DocSet
name|getHashDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|bs
operator|.
name|cardinality
argument_list|()
index|]
decl_stmt|;
name|OpenBitSetIterator
name|iter
init|=
operator|new
name|OpenBitSetIterator
argument_list|(
name|bs
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|HashDocSet
argument_list|(
name|docs
argument_list|,
literal|0
argument_list|,
name|docs
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|getIntDocSet
specifier|public
name|DocSet
name|getIntDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
name|int
index|[]
name|docs
init|=
operator|new
name|int
index|[
operator|(
name|int
operator|)
name|bs
operator|.
name|cardinality
argument_list|()
index|]
decl_stmt|;
name|OpenBitSetIterator
name|iter
init|=
operator|new
name|OpenBitSetIterator
argument_list|(
name|bs
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
name|docs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|docs
index|[
name|i
index|]
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|docs
argument_list|)
return|;
block|}
DECL|method|getBitDocSet
specifier|public
name|DocSet
name|getBitDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
return|return
operator|new
name|BitDocSet
argument_list|(
name|bs
argument_list|)
return|;
block|}
DECL|method|getDocSlice
specifier|public
name|DocSet
name|getDocSlice
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
name|int
name|len
init|=
operator|(
name|int
operator|)
name|bs
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|int
index|[]
name|arr
init|=
operator|new
name|int
index|[
name|len
operator|+
literal|5
index|]
decl_stmt|;
name|arr
index|[
literal|0
index|]
operator|=
literal|10
expr_stmt|;
name|arr
index|[
literal|1
index|]
operator|=
literal|20
expr_stmt|;
name|arr
index|[
literal|2
index|]
operator|=
literal|30
expr_stmt|;
name|arr
index|[
name|arr
operator|.
name|length
operator|-
literal|1
index|]
operator|=
literal|1
expr_stmt|;
name|arr
index|[
name|arr
operator|.
name|length
operator|-
literal|2
index|]
operator|=
literal|2
expr_stmt|;
name|int
name|offset
init|=
literal|3
decl_stmt|;
name|int
name|end
init|=
name|offset
operator|+
name|len
decl_stmt|;
name|OpenBitSetIterator
name|iter
init|=
operator|new
name|OpenBitSetIterator
argument_list|(
name|bs
argument_list|)
decl_stmt|;
comment|// put in opposite order... DocLists are not ordered.
for|for
control|(
name|int
name|i
init|=
name|end
operator|-
literal|1
init|;
name|i
operator|>=
name|offset
condition|;
name|i
operator|--
control|)
block|{
name|arr
index|[
name|i
index|]
operator|=
name|iter
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|DocSlice
argument_list|(
name|offset
argument_list|,
name|len
argument_list|,
name|arr
argument_list|,
literal|null
argument_list|,
name|len
operator|*
literal|2
argument_list|,
literal|100.0f
argument_list|)
return|;
block|}
DECL|method|getDocSet
specifier|public
name|DocSet
name|getDocSet
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|)
block|{
switch|switch
condition|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
return|return
name|getHashDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|getBitDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|2
case|:
return|return
name|getBitDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|3
case|:
return|return
name|getBitDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|4
case|:
return|return
name|getIntDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|5
case|:
return|return
name|getIntDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|6
case|:
return|return
name|getIntDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|7
case|:
return|return
name|getIntDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|8
case|:
return|return
name|getIntDocSet
argument_list|(
name|bs
argument_list|)
return|;
case|case
literal|9
case|:
return|return
name|getDocSlice
argument_list|(
name|bs
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|checkEqual
specifier|public
name|void
name|checkEqual
parameter_list|(
name|OpenBitSet
name|bs
parameter_list|,
name|DocSet
name|set
parameter_list|)
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
name|set
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|bs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|set
operator|.
name|exists
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|bs
operator|.
name|cardinality
argument_list|()
argument_list|,
name|set
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|iter
specifier|public
name|void
name|iter
parameter_list|(
name|DocSet
name|d1
parameter_list|,
name|DocSet
name|d2
parameter_list|)
block|{
comment|// HashDocSet and DocList doesn't iterate in order.
if|if
condition|(
name|d1
operator|instanceof
name|HashDocSet
operator|||
name|d2
operator|instanceof
name|HashDocSet
operator|||
name|d1
operator|instanceof
name|DocList
operator|||
name|d2
operator|instanceof
name|DocList
condition|)
return|return;
name|DocIterator
name|i1
init|=
name|d1
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocIterator
name|i2
init|=
name|d2
operator|.
name|iterator
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|i1
operator|.
name|hasNext
argument_list|()
operator|==
name|i2
operator|.
name|hasNext
argument_list|()
operator|)
assert|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|boolean
name|b1
init|=
name|i1
operator|.
name|hasNext
argument_list|()
decl_stmt|;
name|boolean
name|b2
init|=
name|i2
operator|.
name|hasNext
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|b1
argument_list|,
name|b2
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|b1
condition|)
break|break;
name|assertEquals
argument_list|(
name|i1
operator|.
name|nextDoc
argument_list|()
argument_list|,
name|i2
operator|.
name|nextDoc
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doSingle
specifier|protected
name|void
name|doSingle
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|int
name|sz
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|sz2
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSize
argument_list|)
decl_stmt|;
name|OpenBitSet
name|bs1
init|=
name|getRandomSet
argument_list|(
name|sz
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|sz
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|OpenBitSet
name|bs2
init|=
name|getRandomSet
argument_list|(
name|sz
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|sz2
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DocSet
name|a1
init|=
operator|new
name|BitDocSet
argument_list|(
name|bs1
argument_list|)
decl_stmt|;
name|DocSet
name|a2
init|=
operator|new
name|BitDocSet
argument_list|(
name|bs2
argument_list|)
decl_stmt|;
name|DocSet
name|b1
init|=
name|getDocSet
argument_list|(
name|bs1
argument_list|)
decl_stmt|;
name|DocSet
name|b2
init|=
name|getDocSet
argument_list|(
name|bs2
argument_list|)
decl_stmt|;
name|checkEqual
argument_list|(
name|bs1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|bs2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|iter
argument_list|(
name|a1
argument_list|,
name|b1
argument_list|)
expr_stmt|;
name|iter
argument_list|(
name|a2
argument_list|,
name|b2
argument_list|)
expr_stmt|;
name|OpenBitSet
name|a_and
init|=
operator|(
name|OpenBitSet
operator|)
name|bs1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_and
operator|.
name|and
argument_list|(
name|bs2
argument_list|)
expr_stmt|;
name|OpenBitSet
name|a_or
init|=
operator|(
name|OpenBitSet
operator|)
name|bs1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_or
operator|.
name|or
argument_list|(
name|bs2
argument_list|)
expr_stmt|;
comment|// OpenBitSet a_xor = (OpenBitSet)bs1.clone(); a_xor.xor(bs2);
name|OpenBitSet
name|a_andn
init|=
operator|(
name|OpenBitSet
operator|)
name|bs1
operator|.
name|clone
argument_list|()
decl_stmt|;
name|a_andn
operator|.
name|andNot
argument_list|(
name|bs2
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_and
argument_list|,
name|b1
operator|.
name|intersection
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_or
argument_list|,
name|b1
operator|.
name|union
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|checkEqual
argument_list|(
name|a_andn
argument_list|,
name|b1
operator|.
name|andNot
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_and
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|intersectionSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_or
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|unionSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|a_andn
operator|.
name|cardinality
argument_list|()
argument_list|,
name|b1
operator|.
name|andNotSize
argument_list|(
name|b2
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doMany
specifier|public
name|void
name|doMany
parameter_list|(
name|int
name|maxSz
parameter_list|,
name|int
name|iter
parameter_list|)
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
name|iter
condition|;
name|i
operator|++
control|)
block|{
name|doSingle
argument_list|(
name|maxSz
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testRandomDocSets
specifier|public
name|void
name|testRandomDocSets
parameter_list|()
block|{
comment|// Make the size big enough to go over certain limits (such as one set
comment|// being 8 times the size of another in the int set, or going over 2 times
comment|// 64 bits for the bit doc set.  Smaller sets can hit more boundary conditions though.
name|doMany
argument_list|(
literal|130
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
comment|// doMany(130, 1000000);
block|}
DECL|method|getRandomDocSet
specifier|public
name|DocSet
name|getRandomDocSet
parameter_list|(
name|int
name|n
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|OpenBitSet
name|obs
init|=
operator|new
name|OpenBitSet
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
name|int
index|[]
name|a
init|=
operator|new
name|int
index|[
name|n
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
name|n
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|idx
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
decl_stmt|;
if|if
condition|(
name|obs
operator|.
name|getAndSet
argument_list|(
name|idx
argument_list|)
condition|)
continue|continue;
name|a
index|[
name|i
index|]
operator|=
name|idx
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|n
operator|<=
name|smallSetCuttoff
condition|)
block|{
if|if
condition|(
name|smallSetType
operator|==
literal|0
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
operator|new
name|SortedIntDocSet
argument_list|(
name|a
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|smallSetType
operator|==
literal|1
condition|)
block|{
name|Arrays
operator|.
name|sort
argument_list|(
name|a
argument_list|)
expr_stmt|;
return|return
name|loadfactor
operator|!=
literal|0
condition|?
operator|new
name|HashDocSet
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|n
argument_list|,
literal|1
operator|/
name|loadfactor
argument_list|)
else|:
operator|new
name|HashDocSet
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|BitDocSet
argument_list|(
name|obs
argument_list|,
name|n
argument_list|)
return|;
block|}
DECL|method|getRandomSets
specifier|public
name|DocSet
index|[]
name|getRandomSets
parameter_list|(
name|int
name|nSets
parameter_list|,
name|int
name|minSetSize
parameter_list|,
name|int
name|maxSetSize
parameter_list|,
name|int
name|maxDoc
parameter_list|)
block|{
name|DocSet
index|[]
name|sets
init|=
operator|new
name|DocSet
index|[
name|nSets
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
name|nSets
condition|;
name|i
operator|++
control|)
block|{
name|int
name|sz
decl_stmt|;
name|sz
operator|=
name|rand
operator|.
name|nextInt
argument_list|(
name|maxSetSize
operator|-
name|minSetSize
operator|+
literal|1
argument_list|)
operator|+
name|minSetSize
expr_stmt|;
comment|// different distribution
comment|// sz = (maxSetSize+1)/(rand.nextInt(maxSetSize)+1) + minSetSize;
name|sets
index|[
name|i
index|]
operator|=
name|getRandomDocSet
argument_list|(
name|sz
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
block|}
return|return
name|sets
return|;
block|}
comment|/**** needs code insertion into HashDocSet   public void testCollisions() {     loadfactor=.75f;     rand=new Random(12345);  // make deterministic     int maxSetsize=4000;     int nSets=256;     int iter=1;     int[] maxDocs=new int[] {100000,500000,1000000,5000000,10000000};     int ret=0;     long start=System.currentTimeMillis();     for (int maxDoc : maxDocs) {       int cstart = HashDocSet.collisions;       DocSet[] sets = getRandomHashSets(nSets,maxSetsize, maxDoc);       for (DocSet s1 : sets) {         for (DocSet s2 : sets) {           if (s1!=s2) ret += s1.intersectionSize(s2);         }       }       int cend = HashDocSet.collisions;       System.out.println("maxDoc="+maxDoc+"\tcollisions="+(cend-cstart));           }     long end=System.currentTimeMillis();     System.out.println("testIntersectionSizePerformance="+(end-start)+" ms");     if (ret==-1)System.out.println("wow!");     System.out.println("collisions="+HashDocSet.collisions);    }   ***/
DECL|field|smallSetType
specifier|public
specifier|static
name|int
name|smallSetType
init|=
literal|0
decl_stmt|;
comment|// 0==sortedint, 1==hash, 2==openbitset
DECL|field|smallSetCuttoff
specifier|public
specifier|static
name|int
name|smallSetCuttoff
init|=
literal|3000
decl_stmt|;
comment|/***   public void testIntersectionSizePerformance() {     loadfactor=.75f; // for HashDocSet         rand=new Random(1);  // make deterministic      int minBigSetSize=1,maxBigSetSize=30000;     int minSmallSetSize=1,maxSmallSetSize=30000;     int nSets=1024;     int iter=1;     int maxDoc=1000000;       smallSetCuttoff = maxDoc>>6; // break even for SortedIntSet is /32... but /64 is better for performance     // smallSetCuttoff = maxDoc;       DocSet[] bigsets = getRandomSets(nSets, minBigSetSize, maxBigSetSize, maxDoc);     DocSet[] smallsets = getRandomSets(nSets, minSmallSetSize, maxSmallSetSize, maxDoc);     int ret=0;     long start=System.currentTimeMillis();     for (int i=0; i<iter; i++) {       for (DocSet s1 : bigsets) {         for (DocSet s2 : smallsets) {           ret += s1.intersectionSize(s2);         }       }     }     long end=System.currentTimeMillis();     System.out.println("intersectionSizePerformance="+(end-start)+" ms");     System.out.println("ret="+ret);   }    ***/
comment|/****   public void testExistsPerformance() {     loadfactor=.75f;     rand=new Random(12345);  // make deterministic     int maxSetsize=4000;     int nSets=512;     int iter=1;     int maxDoc=1000000;     DocSet[] sets = getRandomHashSets(nSets,maxSetsize, maxDoc);     int ret=0;     long start=System.currentTimeMillis();     for (int i=0; i<iter; i++) {       for (DocSet s1 : sets) {         for (int j=0; j<maxDoc; j++) {           ret += s1.exists(j) ? 1 :0;         }       }     }     long end=System.currentTimeMillis();     System.out.println("testExistsSizePerformance="+(end-start)+" ms");     if (ret==-1)System.out.println("wow!");   }    ***/
comment|/**** needs code insertion into HashDocSet    public void testExistsCollisions() {     loadfactor=.75f;     rand=new Random(12345);  // make deterministic     int maxSetsize=4000;     int nSets=512;     int[] maxDocs=new int[] {100000,500000,1000000,5000000,10000000};     int ret=0;      for (int maxDoc : maxDocs) {       int mask = (BitUtil.nextHighestPowerOfTwo(maxDoc)>>1)-1;       DocSet[] sets = getRandomHashSets(nSets,maxSetsize, maxDoc);       int cstart = HashDocSet.collisions;             for (DocSet s1 : sets) {         for (int j=0; j<maxDocs[0]; j++) {           int idx = rand.nextInt()&mask;           ret += s1.exists(idx) ? 1 :0;         }       }       int cend = HashDocSet.collisions;       System.out.println("maxDoc="+maxDoc+"\tcollisions="+(cend-cstart));     }     if (ret==-1)System.out.println("wow!");     System.out.println("collisions="+HashDocSet.collisions);   }   ***/
DECL|method|dummyIndexReader
specifier|public
name|IndexReader
name|dummyIndexReader
parameter_list|(
specifier|final
name|int
name|maxDoc
parameter_list|)
block|{
comment|// TODO FIXME: THIS IS HEAVY BROKEN AND ILLEGAL TO DO (null delegate):
name|IndexReader
name|r
init|=
operator|new
name|FilterIndexReader
argument_list|(
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|int
name|maxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|numDocs
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasDeletions
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|IndexReader
index|[]
name|getSequentialSubReaders
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
return|return
name|r
return|;
block|}
DECL|method|dummyMultiReader
specifier|public
name|IndexReader
name|dummyMultiReader
parameter_list|(
name|int
name|nSeg
parameter_list|,
name|int
name|maxDoc
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|nSeg
operator|==
literal|1
operator|&&
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
return|return
name|dummyIndexReader
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
return|;
name|IndexReader
index|[]
name|subs
init|=
operator|new
name|IndexReader
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|nSeg
argument_list|)
operator|+
literal|1
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
name|subs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|subs
index|[
name|i
index|]
operator|=
name|dummyIndexReader
argument_list|(
name|rand
operator|.
name|nextInt
argument_list|(
name|maxDoc
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|MultiReader
name|mr
init|=
operator|new
name|MultiReader
argument_list|(
name|subs
argument_list|)
decl_stmt|;
return|return
name|mr
return|;
block|}
DECL|method|doTestIteratorEqual
specifier|public
name|void
name|doTestIteratorEqual
parameter_list|(
name|DocIdSet
name|a
parameter_list|,
name|DocIdSet
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|DocIdSetIterator
name|ia
init|=
name|a
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|DocIdSetIterator
name|ib
init|=
name|b
operator|.
name|iterator
argument_list|()
decl_stmt|;
comment|// test for next() equivalence
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|da
init|=
name|ia
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|int
name|db
init|=
name|ib
operator|.
name|nextDoc
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|da
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ia
operator|.
name|docID
argument_list|()
argument_list|,
name|ib
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|da
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
comment|// test random skipTo() and next()
name|ia
operator|=
name|a
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|ib
operator|=
name|b
operator|.
name|iterator
argument_list|()
expr_stmt|;
name|int
name|doc
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|int
name|da
decl_stmt|,
name|db
decl_stmt|;
if|if
condition|(
name|rand
operator|.
name|nextBoolean
argument_list|()
condition|)
block|{
name|da
operator|=
name|ia
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
name|db
operator|=
name|ib
operator|.
name|nextDoc
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|int
name|target
init|=
name|doc
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// keep in mind future edge cases like probing (increase if necessary)
name|da
operator|=
name|ia
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|db
operator|=
name|ib
operator|.
name|advance
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|da
argument_list|,
name|db
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|ia
operator|.
name|docID
argument_list|()
argument_list|,
name|ib
operator|.
name|docID
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|da
operator|==
name|DocIdSetIterator
operator|.
name|NO_MORE_DOCS
condition|)
break|break;
name|doc
operator|=
name|da
expr_stmt|;
block|}
block|}
block|}
DECL|method|doFilterTest
specifier|public
name|void
name|doFilterTest
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|ReaderContext
name|topLevelContext
init|=
name|reader
operator|.
name|getTopReaderContext
argument_list|()
decl_stmt|;
name|OpenBitSet
name|bs
init|=
name|getRandomSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|rand
operator|.
name|nextInt
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|DocSet
name|a
init|=
operator|new
name|BitDocSet
argument_list|(
name|bs
argument_list|)
decl_stmt|;
name|DocSet
name|b
init|=
name|getIntDocSet
argument_list|(
name|bs
argument_list|)
decl_stmt|;
name|Filter
name|fa
init|=
name|a
operator|.
name|getTopFilter
argument_list|()
decl_stmt|;
name|Filter
name|fb
init|=
name|b
operator|.
name|getTopFilter
argument_list|()
decl_stmt|;
comment|/*** top level filters are no longer supported     // test top-level     DocIdSet da = fa.getDocIdSet(topLevelContext);     DocIdSet db = fb.getDocIdSet(topLevelContext);     doTestIteratorEqual(da, db);     ***/
name|DocIdSet
name|da
decl_stmt|;
name|DocIdSet
name|db
decl_stmt|;
comment|// first test in-sequence sub readers
for|for
control|(
name|AtomicReaderContext
name|readerContext
range|:
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|topLevelContext
argument_list|)
control|)
block|{
name|da
operator|=
name|fa
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|db
operator|=
name|fb
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|doTestIteratorEqual
argument_list|(
name|da
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
name|AtomicReaderContext
index|[]
name|leaves
init|=
name|ReaderUtil
operator|.
name|leaves
argument_list|(
name|topLevelContext
argument_list|)
decl_stmt|;
name|int
name|nReaders
init|=
name|leaves
operator|.
name|length
decl_stmt|;
comment|// now test out-of-sequence sub readers
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nReaders
condition|;
name|i
operator|++
control|)
block|{
name|AtomicReaderContext
name|readerContext
init|=
name|leaves
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|nReaders
argument_list|)
index|]
decl_stmt|;
name|da
operator|=
name|fa
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|db
operator|=
name|fb
operator|.
name|getDocIdSet
argument_list|(
name|readerContext
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|doTestIteratorEqual
argument_list|(
name|da
argument_list|,
name|db
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|testFilter
specifier|public
name|void
name|testFilter
parameter_list|()
throws|throws
name|IOException
block|{
comment|// keeping these numbers smaller help hit more edge cases
name|int
name|maxSeg
init|=
literal|4
decl_stmt|;
name|int
name|maxDoc
init|=
literal|5
decl_stmt|;
comment|// increase if future changes add more edge cases (like probing a certain distance in the bin search)
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|IndexReader
name|r
init|=
name|dummyMultiReader
argument_list|(
name|maxSeg
argument_list|,
name|maxDoc
argument_list|)
decl_stmt|;
name|doFilterTest
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class
end_unit
